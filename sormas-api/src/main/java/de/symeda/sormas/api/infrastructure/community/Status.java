package de.symeda.sormas.api.infrastructure.community;

public enum Status {

	Additional("Additional"), AdditionalCold("Additional & Cold"), Cold("Cold"), 
	FullCluster("Full Cluster"), HRMPOnly("HRMP Only"), Partial("Partial"), NotTargeted("Not Targeted"), OnHold("On Hold");

	private String displayName;
	
	Status(String displayName) {
        this.displayName = displayName;
    }

	public String toString() {
		return displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public static Status fromValue(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		for (Status status : Status.values()) {
			if (status.name().equalsIgnoreCase(value)
				|| status.getDisplayName().equalsIgnoreCase(value)) {
				return status;
			}
		}

		throw new IllegalArgumentException("Unknown status: " + value);
	}
}
