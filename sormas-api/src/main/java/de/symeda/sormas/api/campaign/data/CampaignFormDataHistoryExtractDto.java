/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.api.campaign.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
//import org.joda.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class CampaignFormDataHistoryExtractDto extends EntityDto { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String uuid;

	private List<CampaignFormDataEntry> formValues;
	private String formValuesString;

	private CampaignReferenceDto campaign;
	private CampaignFormMetaReferenceDto campaignFormMeta;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	
	private Long campaignLong;
	private Long campaignFormMetaLong;
	private Long areaLong;
	private Long regionLong;
	private Long districtLong;
	private Long communityLong;
	
	private boolean archived;
	private Date formDate;
	private UserReferenceDto creatingUser;
	private Long creatingUserLong;

	private String formType;
	private Integer recordversion;
	private String recordgroupuuid;

//	private String formCategory;
	private String source;
	private boolean ispublished;
	private boolean isverified;
	private LocalDateTime changedate;
	
	@Valid
	private List<CampaignFormElement> formValuesN;



	public CampaignFormDataHistoryExtractDto(String uuid, List<CampaignFormDataEntry>  formValuesString,
			Long campaign, Long campaignFormMeta,
//			AreaReferenceDto area,
			Long region, Long district, Long community, boolean archived,
			Date formDate, Long creatingUser, String formType, Integer recordversion,  String source, boolean ispublished, boolean isverified, String recordgroupuuid, LocalDateTime changedate) {
		super();
		this.uuid = uuid;
		this.formValues = formValuesString;
		this.campaignLong = campaign;
		this.campaignFormMetaLong = campaignFormMeta;
//		this.area = area;
		this.regionLong = region;
		this.districtLong = district;
		this.communityLong = community;
		this.archived = archived;
		this.formDate = formDate;
		this.creatingUserLong = creatingUser;
		this.formType = formType;
		this.recordversion = recordversion;
//		this.formCategory = formCategory;
		this.source = source;
		this.ispublished = ispublished;
		this.isverified = isverified;
		this.recordgroupuuid = recordgroupuuid;
		this.changedate = changedate;
		
				
	}
	

	public CampaignFormDataHistoryExtractDto(String uuid, String  formValuesString,
			Long campaign, Long campaignFormMeta,
//			AreaReferenceDto area,
			Long region, Long district, Long community, boolean archived,
			Date formDate, Long creatingUser, String formType, Integer recordversion,  String source, boolean ispublished, boolean isverified, String recordgroupuuid, LocalDateTime changedate) {
		super();
		this.uuid = uuid;
//		this.formValuesString = formValuesString;
		this.campaignLong = campaign;
		this.campaignFormMetaLong = campaignFormMeta;
//		this.area = area;
		this.regionLong = region;
		this.districtLong = district;
		this.communityLong = community;
		this.archived = archived;
		this.formDate = formDate;
		this.creatingUserLong = creatingUser;
		this.formType = formType;
		this.recordversion = recordversion;
//		this.formCategory = formCategory;
		this.source = source;
		this.ispublished = ispublished;
		this.isverified = isverified;
		this.recordgroupuuid = recordgroupuuid;
		this.changedate = changedate;
		
		// JSON conversion for campaignFormElements
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					if (formValuesString != null && !formValuesString.trim().isEmpty()) {
						this.formValues = objectMapper.readValue(formValuesString,
								new TypeReference<List<CampaignFormDataEntry>>() {
								});
					} else {
						this.formValues = new ArrayList<>(); // Default to an empty list
					}
				} catch (Exception e) {
		// Log and handle the exception if needed
					System.err.println("Error parsing JSON for campaignFormElements: " + e.getMessage());
					this.formValues = new ArrayList<>(); // Default to an empty list in case of error
				}
				
	}
		

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<CampaignFormDataEntry> getFormValues() {
		return formValues;
	}

	public void setFormValues(List<CampaignFormDataEntry> formValues) {
		this.formValues = formValues;
	}

	@ImportIgnore
	public CampaignFormMetaReferenceDto getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(CampaignFormMetaReferenceDto campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	@ImportIgnore
	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}

	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}
	
	

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}
	

	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		this.area = area;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public UserReferenceDto getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(UserReferenceDto creatingUser) {
		this.creatingUser = creatingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}


	public Integer getRecordversion() {
		return recordversion;
	}

	public void setRecordversion(Integer recordversion) {
		this.recordversion = recordversion;
	}


	public String getFormValuesString() {
		return formValuesString;
	}


	public void setFormValuesString(String formValuesString) {
		this.formValuesString = formValuesString;
	}


	public Long getCampaignLong() {
		return campaignLong;
	}


	public void setCampaignLong(Long campaignLong) {
		this.campaignLong = campaignLong;
	}


	public Long getCampaignFormMetaLong() {
		return campaignFormMetaLong;
	}


	public void setCampaignFormMetaLong(Long campaignFormMetaLong) {
		this.campaignFormMetaLong = campaignFormMetaLong;
	}


	public Long getAreaLong() {
		return areaLong;
	}


	public void setAreaLong(Long areaLong) {
		this.areaLong = areaLong;
	}


	public Long getRegionLong() {
		return regionLong;
	}


	public void setRegionLong(Long regionLong) {
		this.regionLong = regionLong;
	}


	public Long getDistrictLong() {
		return districtLong;
	}


	public void setDistrictLong(Long districtLong) {
		this.districtLong = districtLong;
	}


	public Long getCommunityLong() {
		return communityLong;
	}


	public void setCommunityLong(Long communityLong) {
		this.communityLong = communityLong;
	}


	public Long getCreatingUserLong() {
		return creatingUserLong;
	}


	public void setCreatingUserLong(Long creatingUserLong) {
		this.creatingUserLong = creatingUserLong;
	}


	public String getRecordgroupuuid() {
		return recordgroupuuid;
	}


	public void setRecordgroupuuid(String recordgroupuuid) {
		this.recordgroupuuid = recordgroupuuid;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public boolean isIspublished() {
		return ispublished;
	}


	public void setIspublished(boolean ispublished) {
		this.ispublished = ispublished;
	}


	public boolean isIsverified() {
		return isverified;
	}


	public void setIsverified(boolean isverified) {
		this.isverified = isverified;
	}


	public LocalDateTime getChangedate() {
		return changedate;
	}


	public void setChangedate(LocalDateTime changedate) {
		this.changedate = changedate;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(archived, area, areaLong, campaign, campaignFormMeta,
				campaignFormMetaLong, campaignLong, changedate, community, communityLong, creatingUser,
				creatingUserLong, district, districtLong, formDate, formType, formValues, formValuesString, ispublished,
				isverified, recordgroupuuid, recordversion, region, regionLong, source, uuid);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampaignFormDataHistoryExtractDto other = (CampaignFormDataHistoryExtractDto) obj;
		return archived == other.archived && Objects.equals(area, other.area)
				&& Objects.equals(areaLong, other.areaLong) && Objects.equals(campaign, other.campaign)
				&& Objects.equals(campaignFormMeta, other.campaignFormMeta)
				&& Objects.equals(campaignFormMetaLong, other.campaignFormMetaLong)
				&& Objects.equals(campaignLong, other.campaignLong) && Objects.equals(changedate, other.changedate)
				&& Objects.equals(community, other.community) && Objects.equals(communityLong, other.communityLong)
				&& Objects.equals(creatingUser, other.creatingUser)
				&& Objects.equals(creatingUserLong, other.creatingUserLong) && Objects.equals(district, other.district)
				&& Objects.equals(districtLong, other.districtLong) && Objects.equals(formDate, other.formDate)
				&& Objects.equals(formType, other.formType) && Objects.equals(formValues, other.formValues)
				&& Objects.equals(formValuesString, other.formValuesString) && ispublished == other.ispublished
				&& isverified == other.isverified && Objects.equals(recordgroupuuid, other.recordgroupuuid)
				&& Objects.equals(recordversion, other.recordversion) && Objects.equals(region, other.region)
				&& Objects.equals(regionLong, other.regionLong) && Objects.equals(source, other.source)
				&& Objects.equals(uuid, other.uuid);
	}

	

}
