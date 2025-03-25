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

package de.symeda.sormas.api.campaign.form;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.user.FormAccess;

public class CampaignFormMetaRegionReferenceDto extends ReferenceDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String AREA_ID = "area_id";
	public static final String CAMPAIGNFORMELEMENT_ID = "campaignformelements_id";


//	public static final String UUID = "uuid";


	private String campaignformelements_id;
	private String area_id;
	private Date collectionDate;
//	private String uuid;


	public CampaignFormMetaRegionReferenceDto() {
	}
	
	public CampaignFormMetaRegionReferenceDto(String uuid) {
        this.setUuid(uuid);
    }
	
	
	public CampaignFormMetaRegionReferenceDto(String campaignformelements_id, String area_id) {
		this.area_id = area_id;
		this.campaignformelements_id = campaignformelements_id;
	}

	public String getCampaignformelements_id() {
		return campaignformelements_id;
	}

	public void setCampaignformelements_id(String campaignformelements_id) {
		this.campaignformelements_id = campaignformelements_id;
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
