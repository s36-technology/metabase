(ns metabase.custom-content-translation.constants)

(def ^:private http-status-content-too-large 413)
(def ^:private max-content-translation-dictionary-size-mib 1.5)
(def ^:private max-content-translation-dictionary-size-bytes (* max-content-translation-dictionary-size-mib 1024 1024))

(def ^:private sample-translations [{:locale "de" :msgid "Sample translation" :msgstr "Musterübersetzung"}
                                    {:locale "pt_BR" :msgid "Sample translation" :msgstr "Tradução de exemplo"}
                                    {:locale "ja" :msgid "Sample translation" :msgstr "サンプル翻訳"}
                                    {:locale "ko" :msgid "Sample translation" :msgstr "샘플 번역"}])