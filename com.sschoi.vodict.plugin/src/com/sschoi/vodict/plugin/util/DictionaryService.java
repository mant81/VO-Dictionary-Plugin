package com.sschoi.vodict.plugin.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.json.*;
import org.osgi.framework.Bundle;

public class DictionaryService {

    private static final String PLUGIN_ID = "com.sschoi.vodict.plugin";
    private static final String DICT_PATH = "resources/dictionary.json";

    private static final Map<String, String> dictMap = new HashMap<>();
    private static boolean loaded = false;

    /** 초기화 */
    private static synchronized void loadDictionary() {
        if (loaded) return;

        try {
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            if (bundle == null) {
                System.err.println("⚠ DictionaryService: Plugin bundle not found (" + PLUGIN_ID + ")");
                loaded = true;
                return;
            }

            // ✅ plugin 내부 리소스에서 dictionary.json 읽기
            URL fileURL = bundle.getEntry(DICT_PATH);
            if (fileURL == null) {
                System.err.println("⚠ DictionaryService: dictionary.json not found in " + DICT_PATH);
                loaded = true;
                return;
            }

            try (InputStream is = FileLocator.toFileURL(fileURL).openStream()) {
                String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject obj = new JSONObject(jsonText);
                JSONArray arr = obj.getJSONArray("dictionary");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    dictMap.put(o.getString("en").toLowerCase(), o.getString("ko"));
                }

                System.out.println("📘 Dictionary loaded (" + dictMap.size() + " words)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loaded = true;
        }
    }

    /** 영어 단어가 사전에 있는지 확인 */
    public static boolean exists(String word) {
        loadDictionary();
        return word != null && dictMap.containsKey(word.toLowerCase());
    }

    /** 영어 단어 → 한글 뜻 반환 */
    public static Optional<String> getKoreanMeaning(String word) {
        loadDictionary();
        return word == null ? Optional.empty()
                : Optional.ofNullable(dictMap.get(word.toLowerCase()));
    }

    /** prefix 기반 자동완성 */
    public static List<Map<String, String>> searchPrefix(String prefix) {
        loadDictionary();
        List<Map<String, String>> results = new ArrayList<>();
        if (prefix == null) return results;

        String lowerPrefix = prefix.toLowerCase();
        for (Map.Entry<String, String> entry : dictMap.entrySet()) {
            if (entry.getKey().startsWith(lowerPrefix)) {
                Map<String, String> map = new HashMap<>();
                map.put("en", entry.getKey());
                map.put("ko", entry.getValue());
                results.add(map);
            }
        }

        return results;
    }

    /** 사전에 등록된 모든 영어 단어 반환 */
    public static Set<String> getAllWords() {
        loadDictionary();
        return dictMap.keySet();
    }
}
