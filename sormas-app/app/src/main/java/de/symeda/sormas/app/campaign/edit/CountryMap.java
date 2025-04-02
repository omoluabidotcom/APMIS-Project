package de.symeda.sormas.app.campaign.edit;

import java.util.HashMap;
import java.util.Map;

class CountryDetails {
    private String dialCode;
    private int minLength;
    private int maxLength;

    public CountryDetails(String dialCode, int minLength, int maxLength) {
        this.dialCode = dialCode;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getDialCode() {
        return dialCode;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public String toString() {
        return "Dial Code: " + dialCode + ", Min Length: " + minLength + ", Max Length: " + maxLength;
    }

    public String getCode() {
        return dialCode;

    }
}

public class CountryMap {
    private Map<String, CountryDetails> mapvalue = new HashMap<>();

    public void addMapValue() {
        mapvalue.put("Afghanistan", new CountryDetails("+93", 9, 9));
        mapvalue.put("Albania", new CountryDetails("+355", 8, 9));
        mapvalue.put("Algeria", new CountryDetails("+213", 9, 9));
        mapvalue.put("Andorra", new CountryDetails("+376", 6, 9));
        mapvalue.put("Angola", new CountryDetails("+244", 9, 9));
        mapvalue.put("Argentina", new CountryDetails("+54", 10, 10));
        mapvalue.put("Armenia", new CountryDetails("+374", 8, 8));
        mapvalue.put("Australia", new CountryDetails("+61", 9, 9));
        mapvalue.put("Austria", new CountryDetails("+43", 10, 13));
        mapvalue.put("Azerbaijan", new CountryDetails("+994", 9, 9));
    }

    public void printCountryDetails(String country) {
        CountryDetails details = mapvalue.get(country);
        if (details != null) {
            System.out.println(country + " - " + details);
        } else {
            System.out.println("Country not found.");
        }
    }

    public static void main(String[] args) {
        CountryMap countryMap = new CountryMap();
        countryMap.addMapValue();

        // Example usage
        countryMap.printCountryDetails("Albania");
        countryMap.printCountryDetails("Australia");
    }
}

