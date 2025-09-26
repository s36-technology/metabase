(ns metabase.custom-content-translation.api.dictionary
  (:require [metabase.api.common :as api]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [metabase.custom-content-translation.core :as ct]
            [metabase.custom-content-translation.constants :as constants]
            [metabase.custom-content-translation.dictionary :as dictionary]
            [metabase.util.i18n :as i18n]
            [metabase.util.embedding.jwt :as embedding.jwt]))

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

(api.macros/defendpoint :post "/upload-dictionary"
  "Upload a CSV of content translations"
  [{{:strs [file]} :multipart-params}]
  (api/check-superuser)
  (let [file-size (:size file)
        tempfile  (:tempfile file)]
    (when (> file-size constants/max-content-translation-dictionary-size-bytes)
      (throw (ex-info (i18n/tru "The dictionary should be less than {0}MB." constants/max-content-translation-dictionary-size-mib)
                      {:status-code constants/http-status-content-too-large})))
    (when-not (instance? java.io.File tempfile)
      (throw (ex-info (i18n/tru "No file provided") {:status-code 400})))
    (dictionary/read-and-import-csv! tempfile)
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
