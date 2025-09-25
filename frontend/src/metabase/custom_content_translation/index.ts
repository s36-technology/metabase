import { PLUGIN_CONTENT_TRANSLATION } from "metabase/plugins";

import { ContentTranslationConfiguration } from "./components";
import { contentTranslationEndpoints } from "./constants";
import { useTranslateContent } from "./use-translate-content";
import {
  translateDisplayNames,
  useSortByContentTranslation,
  useTranslateFieldValuesInHoveredObject,
  useTranslateSeries,
} from "./utils";

Object.assign(PLUGIN_CONTENT_TRANSLATION, {
  isEnabled: true,
  useSortByContentTranslation,
  useTranslateContent,
  useTranslateFieldValuesInHoveredObject,
  useTranslateSeries,
  setEndpointsForStaticEmbedding: (encodedToken: string) => {
    contentTranslationEndpoints.getDictionary = `/api/custom-content-translation/dictionary/${encodedToken}`;
  },
  translateDisplayNames,
  ContentTranslationConfiguration,
});
