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

//	private CampaignReferenceDto campaign;
//	private CampaignFormMetaReferenceDto campaignFormMeta;
//	private AreaReferenceDto area;
//	private RegionReferenceDto region;
//	private DistrictReferenceDto district;
//	private CommunityReferenceDto community;
	
	private String campaign;
	private String campaignFormMeta;
	private String area;
	private String region;
	private String district;
	private String cluster;
	
	private boolean archived;
	private Date formDate;
//	private UserReferenceDto creatingUser;
	private String creatingUser;

	private String formType;
	private Integer recordversion;

//	private String formCategory;
	private String source;
	private boolean ispublished;
	private boolean isverified;
	private LocalDateTime changedate;

	
	public CampaignFormDataHistoryExtractDto(String uuid, String formValuesString,
			String campaign, String campaignFormMeta, //String area, 
			String region, String district, String community, boolean archived,	Date formDate, String creatingUser, String formType,
			Integer recordversion,  String source, boolean ispublished, boolean isverified, LocalDateTime changedate) {
		super();
		this.uuid = uuid;
//		this.formValuesString = formValuesString;
		this.campaign = campaign;
		this.campaignFormMeta  = campaignFormMeta;
//		this.area = area;
		this.region  = region;
		this.district  = district;
		this.cluster  = community;
		this.archived = archived;
		this.formDate = formDate;
		this.creatingUser = creatingUser;
		this.formType = formType;
		this.recordversion = recordversion;
//		this.formCategory = formCategory;
		this.source = source;
		this.ispublished = ispublished;
		this.isverified = isverified;
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

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}
	

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(String campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(String creatingUser) {
		this.creatingUser = creatingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}
	

	
	public Integer getRecordversion() {
		return recordversion;
	}

	public void setRecordversion(Integer recordversion) {
		this.recordversion = recordversion;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}





	public String getFormValuesString() {
		return formValuesString;
	}


	public void setFormValuesString(String formValuesString) {
		this.formValuesString = formValuesString;
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
		result = prime * result + Objects.hash(archived, area, campaignFormMeta,
				campaignFormMeta, campaign , changedate, cluster, creatingUser,
				 district,  formDate, formType, formValues, formValuesString, ispublished,
				isverified,  recordversion, region,  source, uuid);
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
//				&& Objects.equals(areaLong, other.areaLong) 
				&& Objects.equals(campaign, other.campaign)
				&& Objects.equals(campaignFormMeta, other.campaignFormMeta)
//				&& Objects.equals(campaignFormMetaLong, other.campaignFormMetaLong)
//				&& Objects.equals(campaignLong, other.campaignLong) 
				&& Objects.equals(changedate, other.changedate)
				&& Objects.equals(cluster, other.cluster) 
//				&& Objects.equals(communityLong, other.communityLong)
				&& Objects.equals(creatingUser, other.creatingUser)
//				&& Objects.equals(creatingUserLong, other.creatingUserLong)
				&& Objects.equals(district, other.district)
//				&& Objects.equals(districtLong, other.districtLong)
				&& Objects.equals(formDate, other.formDate)
				&& Objects.equals(formType, other.formType) && Objects.equals(formValues, other.formValues)
				&& Objects.equals(formValuesString, other.formValuesString) && ispublished == other.ispublished
				&& isverified == other.isverified
				&& Objects.equals(recordversion, other.recordversion) && Objects.equals(region, other.region)
//				&& Objects.equals(regionLong, other.regionLong)
				&& Objects.equals(source, other.source)
				&& Objects.equals(uuid, other.uuid);
	}

	

}
