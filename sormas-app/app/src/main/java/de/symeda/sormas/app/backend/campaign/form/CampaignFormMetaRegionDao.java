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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.region.PopulationData;

public class CampaignFormMetaRegionDao extends AbstractAdoDao<CampaignFormMetaRegion> {

    public CampaignFormMetaRegionDao(Dao<CampaignFormMetaRegion, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<CampaignFormMetaRegion> getAdoClass() {
        return CampaignFormMetaRegion.class;
    }

    @Override
    public String getTableName() {
        return CampaignFormMetaRegion.TABLE_NAME;
    }

    public List<CampaignFormMetaRegion> getSelectedFormsByRegion(String campaignformUuid, String regionUuid) {
        try {
            QueryBuilder<CampaignFormMetaRegion, Long> queryBuilder = queryBuilder();
            return queryBuilder.where()
                    .eq("campaignformmeta_id", campaignformUuid)
                    .and()
                    .eq("area_id", regionUuid)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

