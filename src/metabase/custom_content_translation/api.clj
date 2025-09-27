(ns metabase.custom-content-translation.api
  (:require [metabase.api.common :as api]
            [metabase.api.macros :as api.macros]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [metabase.custom-content-translation.models :as ct]
            [metabase.custom-content-translation.constants :as constants]
            [metabase.custom-content-translation.core :as dictionary]
            [metabase.util.i18n :as i18n]
            [metabase.embedding.jwt :as embedding.jwt]
            [metabase.util.malli.schema :as ms]))

(comment metabase.custom-content-translation.api/keep-me)

(api.macros/defendpoint :get "/csv"
  "Provides content translation dictionary in CSV"
  []
  (api/check-superuser)
  (let [translations (ct/get-translations)
        translations (if (empty? translations)
                       constants/sample-translations
                       translations)
        csv-data     (cons ["Locale Code" "String" "Translation"]
                           (map (fn [{:keys [locale msgid msgstr]}]
                                  [locale msgid msgstr])
                                translations))]
    {:status 200
     :headers {"Content-Type" "text/csv; charset=utf-8"
               "Content-Disposition" "attachment; filename=\"metabase-content-translations.csv\""}
     :body (with-out-str
             (csv/write-csv *out* csv-data))}))

(api.macros/defendpoint :post
  "/upload-dictionary"
  "Upload a CSV of content translations"
  {:multipart true}
  [_route_params
   _query-params
   _body
   {:keys [multipart-params], :as _request} :- [:map
                                                [:multipart-params
                                                 [:map
                                                  ["file"
                                                   [:map
                                                    [:filename :string]
                                                    [:tempfile (ms/InstanceOfClass java.io.File)]]]]]]]

  (api/check-superuser)
  (let [file (get-in multipart-params ["file" :tempfile])]
    (when (> (get-in multipart-params ["file" :size]) constants/max-content-translation-dictionary-size-bytes)
      (throw (ex-info (tru "The dictionary should be less than {0}MB." constants/max-content-translation-dictionary-size-mib)
                      {:status-code constants/http-status-content-too-large})))
    (when-not (instance? java.io.File file)
      (throw (ex-info (tru "No file provided") {:status-code 400})))
    (dictionary/read-and-import-csv! file)
    {:success true}))

(api.macros/defendpoint :get "/dictionary/:token"
  "Fetch the content translation dictionary via a JSON Web Token signed with the `embedding-secret-key`."
  [{:keys [token] :as _route-params}
   {:keys [locale] :as _query-params}]
  ;; verify token
  (embedding.jwt/unsign token)
  (if locale
    {:data (ct/get-translations (i18n/normalized-locale-string (str/trim locale)))}
    (throw (ex-info (str (i18n/tru "Locale is required.")) {:status-code 400}))))

(def ^{:arglists '([request respond raise])} custom-content-translation-routes
  "`/api/custom-content-translation` routes."
  (api.macros/ns-handler *ns*))
