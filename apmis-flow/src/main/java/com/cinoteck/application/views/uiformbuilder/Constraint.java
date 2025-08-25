package com.cinoteck.application.views.uiformbuilder;

public enum Constraint {

	EXPRESSION,
	RANGE;

	

	private final String[] allowedValues;

	Constraint(String... allowedValues) {
		this.allowedValues = allowedValues;
	}

	public String[] getAllowedValues() {
		return allowedValues;
	}

	public String toString() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	public static Constraint fromString(String stringValue) {
		return valueOf(stringValue.toUpperCase().replaceAll("-", "_"));
	}

}

