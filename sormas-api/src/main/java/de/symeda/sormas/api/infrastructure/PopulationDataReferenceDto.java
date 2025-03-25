package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;

import de.symeda.sormas.api.ReferenceDto;

public class PopulationDataReferenceDto extends ReferenceDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String DISTRICT_ID = "district_id";
	public static final String CAMPAIGN_ID = "campaign_id";
	public static final String SELECTED = "selected";

	

	private Long district_id;
	private Long campaign_id;
	private boolean  selected;
//	private String formname_ps_af;
//	private String formname_fa_af;
	
	
	public PopulationDataReferenceDto() {

}
	
	public PopulationDataReferenceDto(String uuid) {
        this.setUuid(uuid);
    }
	
	
	
	public PopulationDataReferenceDto(Long district_id, Long campaign_id, boolean selected) {
	super();
	this.district_id = district_id;
	this.campaign_id = campaign_id;
	this.selected = selected;
}
	
	public Long getDistrict_id() {
		return district_id;
	}

	public void setDistrict_id(Long district_id) {
		this.district_id = district_id;
	}
	public Long getCampaign_id() {
		return campaign_id;
	}
	public void setCampaign_id(Long campaign_id) {
		this.campaign_id = campaign_id;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}



	

}
