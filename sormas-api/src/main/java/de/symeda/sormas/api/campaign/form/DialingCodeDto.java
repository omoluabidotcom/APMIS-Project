package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;
import java.util.Objects;

public class DialingCodeDto implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -188082255757123990L;

	public static final String TABLE_NAME = "dialingcode";

	public static final String CODE = "code";
	public static final String COUNTRY = "country";
	public static final String MIN_LENGTH = "min_length";
	public static final String MAX_LENGTH = "max_length";

	String country;
	String code;
	int min_length;
	int max_length;
	
	public DialingCodeDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DialingCodeDto(String country, String code, int min_length, int max_length) {
		super();
		this.country = country;
		this.code = code;
		this.min_length = min_length;
		this.max_length = max_length;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getMin_length() {
		return min_length;
	}

	public void setMin_length(int min_length) {
		this.min_length = min_length;
	}

	public int getMax_length() {
		return max_length;
	}

	public void setMax_length(int max_length) {
		this.max_length = max_length;
	}

	
}
