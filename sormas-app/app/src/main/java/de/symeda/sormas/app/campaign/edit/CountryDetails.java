package de.symeda.sormas.app.campaign.edit;

public class CountryDetails {
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
