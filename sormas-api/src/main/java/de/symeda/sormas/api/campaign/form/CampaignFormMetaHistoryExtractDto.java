package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class CampaignFormMetaHistoryExtractDto implements Serializable, Comparable<CampaignFormMetaHistoryExtractDto> {

	private String uuid;
	private String formname;

//	private Map<String, Object> campaignFormElements = new HashMap<>();
//	 private List<CampaignFormElement> campaignFormElements; // To store JSON data
	private List<CampaignFormElement> campaignFormElements;
	private String formid;
	private LocalDateTime start_date;
	private LocalDateTime end_date;
	private Long version;

//	public CampaignFormMetaHistoryExtractDto(String uuid, String formName, Map<String, Object> campaignFormElements,
//			String formId, LocalDateTime startDate, Object endDate) {
//		this.uuid = uuid;
//		this.formname = formName;
//		 this.campaignFormElements = campaignFormElements != null 
//		            ? campaignFormElements 
//		            : new HashMap<>();																											// it's
//																											// never
//																											// null
//		this.formid = formId;
//		this.start_date = startDate;
//		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
//	}

//	public CampaignFormMetaHistoryExtractDto(String uuid, String formName, List<CampaignFormElement> campaignFormElements, String formId,
//			LocalDateTime startDate, Object endDate) {
//		this.uuid = uuid;
//		this.formname = formName;
//		this.campaignFormElements = campaignFormElements; // Ensure it's never null
//		this.formid = formId;
//		this.start_date = startDate;
//		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
//	}

//	 public CampaignFormMetaHistoryExtractDto(String uuid, String formName, 
//	            List<CampaignFormElement> campaignFormElements, // Change type
//	            String formId, LocalDateTime startDate, Object endDate) {
//	        this.uuid = uuid;
//	        this.formname = formName;
//	        this.campaignFormElements = campaignFormElements != null 
//	            ? campaignFormElements 
//	            : new ArrayList<>(); // Ensure never null
//	        this.formid = formId;
//	        this.start_date = startDate;
//	        this.end_date = (endDate instanceof LocalDateTime) 
//	            ? (LocalDateTime) endDate 
//	            : null;
//	    }

	@Valid
	private List<CampaignFormElement> campaignFormElementsN;

//	public CampaignFormMetaHistoryExtractDto(String uuid, String formName, String campaignFormElements, // Change type
//			String formId, LocalDateTime startDate, Object endDate) {
//		this.uuid = uuid;
//		this.formname = formName;
//		this.campaignFormElements = campaignFormElements; // Ensure never null
//		this.formid = formId;
//		this.start_date = startDate;
//		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
//	}

	public CampaignFormMetaHistoryExtractDto(String uuid, String formName, String campaignFormElements, String formId,
			LocalDateTime startDate, Object endDate, Long version) {
		this.uuid = uuid;
		this.formname = formName;
		this.formid = formId;
		this.start_date = startDate;
		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
		this.version = version;

// JSON conversion for campaignFormElements
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			if (campaignFormElements != null && !campaignFormElements.trim().isEmpty()) {
				this.campaignFormElements = objectMapper.readValue(campaignFormElements,
						new TypeReference<List<CampaignFormElement>>() {
						});
			} else {
				this.campaignFormElements = new ArrayList<>(); // Default to an empty list
			}
		} catch (Exception e) {
// Log and handle the exception if needed
			System.err.println("Error parsing JSON for campaignFormElements: " + e.getMessage());
			this.campaignFormElements = new ArrayList<>(); // Default to an empty list in case of error
		}
	}

//	 public CampaignFormMetaHistoryExtractDto(String uuid, String formName, 
//	            String campaignFormElements, // Change type
//	            String formId, LocalDateTime startDate, Object endDate, Long version) {
//	        this.uuid = uuid;
//	        this.formname = formName;
//	        
//	        
//			ObjectMapper objectMapper = new ObjectMapper();
//
////	        this.campaignFormElements = campaignFormElements;  // Ensure never null
//	        if (campaignFormElements != null && !campaignFormElements.trim().isEmpty()) {
//	            try {
//	                campaignFormElementsN = objectMapper.readValue(
//	                		campaignFormElements, 
//	                    new TypeReference<List<CampaignFormElement>>() {}
//	                );
//	            } catch (Exception e) {
//	                // Handle the exception
//	            }
//	        
//	        
//	        
//	        this.formid = formId;
//	        this.start_date = startDate;
//	        this.end_date = (endDate instanceof LocalDateTime) 
//	            ? (LocalDateTime) endDate 
//	            : null;
//	        this.version = version;
//	    }

	public CampaignFormMetaHistoryExtractDto(String uuid, String formName,
			List<CampaignFormElement> campaignFormElements, // Change type
			String formId, LocalDateTime startDate, Object endDate) {
		this.uuid = uuid;
		this.formname = formName;
		this.campaignFormElementsN = campaignFormElements; // Ensure never null
		this.formid = formId;
		this.start_date = startDate;
		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
	}

	public CampaignFormMetaHistoryExtractDto(String uuid, String formName,
			List<CampaignFormElement> campaignFormElements, // Change type
			String formId, LocalDateTime startDate, Object endDate, Long version) {
		this.uuid = uuid;
		this.formname = formName;
		this.campaignFormElementsN = campaignFormElements; // Ensure never null
		this.formid = formId;
		this.start_date = startDate;
		this.end_date = (endDate instanceof LocalDateTime) ? (LocalDateTime) endDate : null;
		this.version = version;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public List<CampaignFormElement> getCampaignFormElements() {
		return campaignFormElements;
	}

	public void setCampaignFormElements(List<CampaignFormElement> campaignFormElements) {
		this.campaignFormElements = campaignFormElements;
	}

//	// Getters and setters
//	public String getCampaignFormElements() {
//		return campaignFormElements;
//	}
//
//	public void setCampaignFormElements(String campaignFormElements) {
//		this.campaignFormElements = campaignFormElements;
//	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getFormid() {
		return formid;
	}

	public void setFormid(String formid) {
		this.formid = formid;
	}

	public LocalDateTime getStart_date() {
		return start_date;
	}

	public void setStart_date(LocalDateTime start_date) {
		this.start_date = start_date;
	}

	public LocalDateTime getEnd_date() {
		return end_date;
	}

	public void setEnd_date(LocalDateTime end_date) {
		this.end_date = end_date;
	}
//
//	public Map<String, Object> getCampaignFormElements() {
//		return campaignFormElements;
//	}
//
//	public void setCampaignFormElements(Map<String, Object> campaignFormElements) {
//		this.campaignFormElements = campaignFormElements;
//	}
//
//	public String getCampaignFormElementsx() {
//		return campaignFormElementsx;
//	}
//
//	public void setCampaignFormElementsx(String campaignFormElementsx) {
//		this.campaignFormElementsx = campaignFormElementsx;
//	}

	@Override
	public int hashCode() {
		return Objects.hash(campaignFormElements, end_date, formid, formname, start_date, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampaignFormMetaHistoryExtractDto other = (CampaignFormMetaHistoryExtractDto) obj;
		return Objects.equals(campaignFormElements, other.campaignFormElements)
				&& Objects.equals(end_date, other.end_date) && Objects.equals(formid, other.formid)
				&& Objects.equals(formname, other.formname) && Objects.equals(start_date, other.start_date)
				&& Objects.equals(uuid, other.uuid);
	}

	@Override
	public int compareTo(CampaignFormMetaHistoryExtractDto o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
