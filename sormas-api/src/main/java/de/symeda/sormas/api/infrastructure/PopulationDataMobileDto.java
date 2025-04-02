package de.symeda.sormas.api.infrastructure;

import java.util.Date;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class PopulationDataMobileDto extends EntityDto {

	private static final long serialVersionUID = -4254008000534611519L;

	public static final String I18N_PREFIX = "PopulationData";

	private String selected;

	private String campaign_id;
	private String district_id;
	private String uuid;
	private Date changeDate;

	public PopulationDataMobileDto() {
		// TODO Auto-generated constructor stub
	}

	public PopulationDataMobileDto(String campaign_id, String district_id, String selected, String uuid, Date changeDate) {
		setUuid(uuid);
		setChangeDate(changeDate);
		this.uuid = uuid;
		this.changeDate = changeDate;
		this.campaign_id = campaign_id;
		this.district_id = district_id;
		this.selected = selected;

		// TODO Auto-generated constructor stub
	}
	
	public PopulationDataMobileDto(String campaign_id, String district_id, String selected) {

		this.campaign_id = campaign_id;
		this.district_id = district_id;
		this.selected = selected;

		// TODO Auto-generated constructor stub
	}

	public static PopulationDataMobileDto build(Date collectionDate) {

		PopulationDataMobileDto dto = new PopulationDataMobileDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	

	public String getCampaign_id() {
		return campaign_id;
	}

	public void setCampaign_id(String campaign_id) {
		this.campaign_id = campaign_id;
	}

	public String getDistrict_id() {
		return district_id;
	}

	public void setDistrict_id(String district_id) {
		this.district_id = district_id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
//
//	public Date getChangeDate() {
//		return changeDate;
//	}
//
//	public void setChangeDate(Date changeDate) {
//		this.changeDate = changeDate;
//	}

}
