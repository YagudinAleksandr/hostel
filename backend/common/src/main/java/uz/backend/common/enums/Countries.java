package uz.backend.common.enums;

import java.text.Collator;
import java.util.*;

/**
 * Список стран
 *
 * @author Aleksandr Yagudin
 */
public enum Countries {
    RUSSIAN_FEDERATION("RUS", "Российская Федерация"),
    UZBEKISTAN("UZB", "Узбекистан"),
    TAJIKISTAN("TJ", "Таджикистан");

    /**
     * Код страны
     */
    private final String code;

    /**
     * Название страны
     */
    private final String name;

    /**
     * Список стран, отсортированный по названию по правилам русского языка.
     * Вычисляется один раз при загрузке класса.
     */
    private static final List<Countries> SORTED_BY_NAME = Arrays.stream(values())
            .sorted(Comparator.comparing(Countries::getName, Collator.getInstance(Locale.of("ru", "RU"))))
            .toList();

    Countries(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * Все страны, отсортированные по названию.
     *
     * @return неизменяемый список {@link Countries}
     */
    public static List<Countries> sortedByName() {
        return SORTED_BY_NAME;
    }

    /**
     * Получение объекта страны по коду
     *
     * @param code код страны
     * @return страна {@link Countries} или {@code null}
     */
    public static Countries fromCode(String code) {
        for (Countries country : values()) {
            if (Objects.equals(country.code, code)) {
                return country;
            }
        }
        return null;
    }
}
