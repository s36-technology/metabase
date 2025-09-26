(ns metabase.custom-content-translation.api
  (:require
   [metabase.api.macros :as api.macros]
   [metabase.custom-content-translation.api.dictionary]))

(def ^{:arglists '([request respond raise])} custom-content-translation-routes
  "`/api/custom-content-translation` routes"
  (api.macros/ns-handler 'metabase.custom-content-translation.api.dictionary))