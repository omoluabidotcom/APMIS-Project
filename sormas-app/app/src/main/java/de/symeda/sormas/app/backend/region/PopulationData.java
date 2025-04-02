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

package de.symeda.sormas.app.backend.region;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = PopulationData.TABLE_NAME)
@DatabaseTable(tableName = PopulationData.TABLE_NAME)
public class PopulationData extends AbstractDomainObject implements Serializable {
//		extends PseudonymizableAdo {

//	private static final long serialVersionUID = -6057113970091470463L;

	// Table name constant
	public static final String TABLE_NAME = "populationdata";
	public static final String I18N_PREFIX = "PopulationData";

	// Column constants
	public static final String COLUMN_CAMPAIGN_ID = "campaign_id";
	public static final String COLUMN_DISTRICT_ID = "district_id";
	public static final String COLUMN_SELECTED = "selected";




	@Column(name = "campaign_id")
	private String campaign_id;  // Change to Long to match server

	@Column(name = "district_id")
	private String district_id;  // Change to Long to match server

	@Column	private String selected;


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

	public String isSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	// Internationalization prefix
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}


}
