package de.symeda.sormas.api.campaign.form;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignFormMetaRegionDto extends EntityDto {

	private static final long serialVersionUID = -1163673887940552133L;

	public static final String AREA_ID = "area_id";
	public static final String CAMPAIGNFORMMETA_ID = "campaignformmeta_id";
	public static final String UUID = "uuid";




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


	

    public CampaignFormMetaRegionDto(String campaignformmeta_id, String area_id, String uuid, Date changeDate) {
        this.campaignformmeta_id = campaignformmeta_id;
        this.area_id = area_id;
        this.setUuid(uuid);
        this.setChangeDate(changeDate);
    }
//    campaignformmeta_id

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	
	
	public String getCampaignformmeta_id() {
		return campaignformmeta_id;
	}

	public void setCampaignformmeta_id(String campaignformmeta_id) {
		this.campaignformmeta_id = campaignformmeta_id;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}


	

	
}
