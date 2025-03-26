package de.symeda.sormas.backend.campaign.form;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Table(name = "dialingcode")
public class DialingCode extends AbstractDomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7845268396776481668L;

	public static final String TABLE_NAME = "dialingcode";

	public static final String CODE = "code";
	public static final String COUNTRY = "country";
	public static final String MIN_LENGTH = "min_length";
	public static final String MAX_LENGTH = "max_length";

	String country;
	String code;
	int min_length;
	int max_length;

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
