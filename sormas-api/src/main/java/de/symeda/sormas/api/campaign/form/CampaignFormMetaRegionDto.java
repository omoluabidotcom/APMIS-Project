package de.symeda.sormas.api.campaign.form;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignFormMetaRegionDto extends EntityDto {

	private static final long serialVersionUID = -1163673887940552133L;

	public static final String AREA_ID = "area_id";
	public static final String CAMPAIGNFORMELEMENT_ID = "campaignformelements_id";



	private String campaignformmeta_id;
	private String area_id;
	private Date collectionDate;


	public static CampaignFormMetaRegionDto build(Date collectionDate) {

		CampaignFormMetaRegionDto dto = new CampaignFormMetaRegionDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setCollectionDate(collectionDate);
		return dto;
	}
	
	public CampaignFormMetaRegionDto() {
		// TODO Auto-generated constructor stub
	}


	

    public CampaignFormMetaRegionDto(String campaignformmeta_id, String area_id) {
        this.campaignformmeta_id = campaignformmeta_id;
        this.area_id = area_id;
    }

	public String getCampaignformelements_id() {
		return campaignformmeta_id;
	}

	public void setCampaignformelements_id(String campaignformelements_id) {
		this.campaignformmeta_id = campaignformelements_id;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	
}
