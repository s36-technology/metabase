(ns metabase.custom-content-translation.api
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [metabase.api.common :as api]
   [metabase.custom-content-translation.api.dictionary :as dictionary]))

(defroutes custom-content-translation-routes
  "`/api/custom-content-translation` routes"
  (GET "/csv" [] (dictionary/get-csv))
  (POST "/upload-dictionary" [:as {{:keys [file]} :multipart-params}] (dictionary/post-upload-dictionary))
  (GET "/dictionary/:token" [] (dictionary/get-dictionary)))