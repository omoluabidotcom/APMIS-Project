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

package de.symeda.sormas.app.backend.campaign.form;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = CampaignFormMetaRegion.TABLE_NAME)
@DatabaseTable(tableName = CampaignFormMetaRegion.TABLE_NAME)
public class CampaignFormMetaRegion extends PseudonymizableAdo {

	public static final String TABLE_NAME = "campaignformmeta_area";
	public static final String I18N_PREFIX = "CampaignFormMetaArea";

	public static final String FORM_ID = "area_id";
	public static final String FORM_NAME = "campaignformmeta_id";

	@Column(name = "area_id")
	private String area_id;  // Change to Long to match server

	@Column(name = "campaignformmeta_id")
	private String campaignformmeta_id;  // Change to Long to match server


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

	// Internationalization prefix
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
