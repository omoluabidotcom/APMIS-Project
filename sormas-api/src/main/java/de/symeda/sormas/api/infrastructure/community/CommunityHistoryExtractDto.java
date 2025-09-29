/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.infrastructure.community;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.ClusterFloatStatus;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionHistoryExtractDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;


public class CommunityHistoryExtractDto implements Serializable, Comparable<CommunityHistoryExtractDto> {

	private String uuid_;
	private String name;
	private Long externalId;
	private Long districtexternalId;

	private boolean archived;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	
	private String districtuuid;
	private String districtname;
	private String floating;
	private int clusternumber;
	private Date changedate;


	
	public CommunityHistoryExtractDto(String uuid, String name, Boolean archived, Long externalID, LocalDateTime startDate, LocalDateTime endDate) {
	    this.uuid_ = uuid;
	    this.name = name;
	    this.archived = archived;
	    this.externalId = externalID;
	    this.startDate = startDate;
	    this.endDate = endDate;
	}
    public CommunityHistoryExtractDto(String string, String string2, Boolean boolean1, long l, LocalDateTime localDateTime, Object object) {
        this.uuid_ = string;
        this.name = string2;
        this.archived = boolean1;
        this.externalId = l;
        this.startDate = localDateTime;
        this.endDate = (object instanceof LocalDateTime) ? (LocalDateTime) object : null;
    }
	public CommunityHistoryExtractDto(
			String clusterUuid, 
			String clusterName, 
			long district_id, 
			String districtuuid, 
			String districtname, 
			long externalid,
			String externalname, 
			boolean archived, 
			String floating, 
			int clusternumber, 
			Timestamp changedate) {
		
		
        this.uuid_ = clusterUuid;
	    this.name = clusterName;
	    this.districtexternalId = district_id;
	    this.districtuuid = districtuuid;
	    this.districtname = districtname;
	    this.externalId = externalid;
//	    this.name = externalname;
	    this.archived = archived;
	    this.floating =  floating;
	    this.clusternumber = clusternumber;
	    this.changedate = changedate;
	    


		
	}

	public String getUuid_() {
		return uuid_;
	}
	public void setUuid_(String uuid_) {
		this.uuid_ = uuid_;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getExternalId() {
		return externalId;
	}
	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}
	public boolean isArchived() {
		return archived;
	}
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	
	
	
	public Long getDistrictexternalId() {
		return districtexternalId;
	}
	public void setDistrictexternalId(Long districtexternalId) {
		this.districtexternalId = districtexternalId;
	}
	public String getDistrictuuid() {
		return districtuuid;
	}
	public void setDistrictuuid(String districtuuid) {
		this.districtuuid = districtuuid;
	}
	public String getDistrictname() {
		return districtname;
	}
	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}
	public String getFloating() {
		return floating;
	}
	public void setFloating(String floating) {
		this.floating = floating;
	}
	public int getClusternumber() {
		return clusternumber;
	}
	public void setClusternumber(int clusternumber) {
		this.clusternumber = clusternumber;
	}
	public Date getChangedate() {
		return changedate;
	}
	public void setChangedate(Date changedate) {
		this.changedate = changedate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(archived, changedate, clusternumber, districtexternalId, districtname, districtuuid,
				endDate, externalId, floating, name, startDate, uuid_);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommunityHistoryExtractDto other = (CommunityHistoryExtractDto) obj;
		return archived == other.archived && Objects.equals(changedate, other.changedate)
				&& clusternumber == other.clusternumber && Objects.equals(districtexternalId, other.districtexternalId)
				&& Objects.equals(districtname, other.districtname) && Objects.equals(districtuuid, other.districtuuid)
				&& Objects.equals(endDate, other.endDate) && Objects.equals(externalId, other.externalId)
				&& Objects.equals(floating, other.floating) && Objects.equals(name, other.name)
				&& Objects.equals(startDate, other.startDate) && Objects.equals(uuid_, other.uuid_);
	}
	@Override
	public int compareTo(CommunityHistoryExtractDto o) {
		// TODO Auto-generated method stub
		return 0;
	}

    
    
	
	
	
}
