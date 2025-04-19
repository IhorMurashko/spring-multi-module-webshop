package com.multimodule.webshop.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameFormatter {

    private static final Pattern WORD_SPLITTER = Pattern.compile("[\\s-]");

    /**
     * Приводит строку имени или фамилии к формату:
     * Каждое слово — с заглавной буквы, остальные — строчные.
     * Работает как с дефисами, так и с пробелами.
     *
     * @param rawName сырая строка имени
     * @return отформатированная строка
     */
    public static String formatName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return rawName;
        }

        rawName = rawName.trim();

        // Проверим — это имя содержит пробел или дефис?
        if (rawName.contains(" ") || rawName.contains("-")) {
            // Разделить с сохранением разделителей
            StringBuilder result = new StringBuilder();
            int last = 0;

            for (int i = 0; i < rawName.length(); i++) {
                char c = rawName.charAt(i);
                if (c == ' ' || c == '-') {
                    result.append(capitalizeWord(rawName.substring(last, i))).append(c);
                    last = i + 1;
                }
            }

            // Добавить последнюю часть
            result.append(capitalizeWord(rawName.substring(last)));
            return result.toString();
        }

        // Одно слово
        return capitalizeWord(rawName);
    }

    private static String capitalizeWord(String word) {
        if (word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase(Locale.ROOT) +
                word.substring(1).toLowerCase(Locale.ROOT);
    }



}
