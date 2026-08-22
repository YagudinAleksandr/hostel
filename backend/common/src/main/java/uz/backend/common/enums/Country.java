package uz.backend.common.enums;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Список стран
 *
 * @author Aleksandr Yagudin
 */
public enum Country {

    RUSSIAN_FEDERATION("RUS", "Российская Федерация"),
    UZBEKISTAN("UZB", "Узбекистан");

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
    private static final List<Country> SORTED_BY_NAME = Arrays.stream(values())
            .sorted(Comparator.comparing(Country::getName, Collator.getInstance(Locale.of("ru", "RU"))))
            .toList();

    Country(String code, String name) {
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
     * @return неизменяемый список {@link Country}
     */
    public static List<Country> sortedByName() {
        return SORTED_BY_NAME;
    }

    /**
     * Получение объекта страны по коду
     *
     * @param code код страны
     * @return страна {@link Country} или {@code null}
     */
    public static Country fromCode(String code) {
        for (Country country : values()) {
            if (Objects.equals(country.code, code)) {
                return country;
            }
        }
        return null;
    }
}
