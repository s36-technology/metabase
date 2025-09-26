(ns metabase.custom-content-translation.api
  (:require
   [metabase.api.common :as api]
   [metabase.api.macros :as api.macros]
   [metabase.custom-content-translation.api.dictionary]))

(comment metabase.custom-content-translation.api.dictionary/keep-me)

(def ^{:arglists '([request respond raise])} custom-content-translation-routes
  "`/api/custom-content-translation` routes"
  (api.macros/ns-handler metabase.custom-content-translation.api.dictionary))