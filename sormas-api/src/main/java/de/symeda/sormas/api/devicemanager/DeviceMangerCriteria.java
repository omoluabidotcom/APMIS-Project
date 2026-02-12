/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.devicemanager;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto; 
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class DeviceMangerCriteria extends BaseCriteria implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<AreaReferenceDto> area;
	private Set<RegionReferenceDto> region;
	private Set<DistrictReferenceDto> district;	
    
	public DeviceMangerCriteria region(Set<RegionReferenceDto> region) {
		this.region = region;
		return this;
	}
	
	public DeviceMangerCriteria area(Set<AreaReferenceDto> set) {
		this.area = set;
		return this;
	}

	public Set<RegionReferenceDto> getRegion() {
		return region;
	}
	
	public Set<AreaReferenceDto> getArea() {
		return area;
	}
	

	public DeviceMangerCriteria district(Set<DistrictReferenceDto> district) {
		this.district = district;
		return this;
	}

	public Set<DistrictReferenceDto> getDistrict() {
		return district;
	}	
}
