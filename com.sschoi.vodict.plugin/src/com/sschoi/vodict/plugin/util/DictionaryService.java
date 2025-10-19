package com.sschoi.vodict.plugin.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class DictionaryService {

    private static final String DICT_PATH = "/resources/dictionary.json";

    // static map으로 싱글톤처럼 사용
    private static final Map<String, String> dictMap = new HashMap<>();
    private static boolean loaded = false;

    /** 초기화 */
    private static void loadDictionary() {
        if (loaded) return; // 이미 로드됨

        try (InputStream is = DictionaryService.class.getResourceAsStream(DICT_PATH)) {
            if (is == null) {
                System.err.println("⚠ dictionary.json not found");
                loaded = true;
                return;
            }

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(jsonText);
            JSONArray arr = obj.getJSONArray("dictionary");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                dictMap.put(o.getString("en").toLowerCase(), o.getString("ko"));
            }

            loaded = true;
            System.out.println("📘 Dictionary loaded (" + dictMap.size() + " words)");

        } catch (Exception e) {
            e.printStackTrace();
            loaded = true;
        }
    }

    /** 영어 단어가 사전에 있는지 확인 */
    public static boolean exists(String word) {
        loadDictionary();
        return dictMap.containsKey(word.toLowerCase());
    }

    /** 영어 단어 → 한글 뜻 반환 (없으면 Optional.empty) */
    public static Optional<String> getKoreanMeaning(String word) {
        loadDictionary();
        return Optional.ofNullable(dictMap.get(word.toLowerCase()));
    }

    /** prefix 기반 자동완성: 영어 시작 문자열 → 매칭 단어 리스트 */
    public static List<Map<String, String>> searchPrefix(String prefix) {
        loadDictionary();
        List<Map<String, String>> results = new ArrayList<>();

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
