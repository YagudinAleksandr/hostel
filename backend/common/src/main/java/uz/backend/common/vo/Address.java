package uz.backend.common.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.backend.common.enums.Countries;

import java.util.Objects;

/**
 * Адрес
 *
 * @author Aleksandr Yagudin
 */
@Value
public class Address {
    /**
     * Страна {@link Countries}
     */
    Countries country;

    /**
     * Регион/область
     */
    String region;

    /**
     * Город/населенный пункт
     */
    String city;

    /**
     * Улица
     */
    String street;

    /**
     * Почтовый индекс
     */
    String zip;

    /**
     * Дом/строение
     */
    String house;

    /**
     * Квартира
     */
    String apartment;

    /**
     * Адрес
     *
     * @param country   страна {@link Countries}
     * @param region    регион/область
     * @param city      город/населенный пункт
     * @param street    улица
     * @param zip       почтовый индекс
     * @param house     дом/строение
     * @param apartment квартира
     */
    @JsonCreator
    public Address(@JsonProperty("country") Countries country,
                   @JsonProperty("region") String region,
                   @JsonProperty("city") String city,
                   @JsonProperty("street") String street,
                   @JsonProperty("zip") String zip,
                   @JsonProperty("house") String house,
                   @JsonProperty("apartment") String apartment) {
        this.country = Objects.requireNonNull(country, "Country is required");
        this.city = Objects.requireNonNull(city, "City is required");
        this.street = Objects.requireNonNull(street, "Street is required");
        this.house = Objects.requireNonNull(house, "House is required");
        this.region = region;
        this.zip = zip;
        this.apartment = apartment;
    }
}
