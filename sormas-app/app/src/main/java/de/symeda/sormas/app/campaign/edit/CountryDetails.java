package de.symeda.sormas.app.campaign.edit;

class CountryDetails {
    private String code;
    private int minLength;
    private int maxLength;

    public CountryDetails(String code, int minLength, int maxLength) {
        this.code = code;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getCode() {
        return code;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public String toString() {
        return "CountryDetails{code='" + code + "', minLength=" + minLength + ", maxLength=" + maxLength + "}";
    }
}
