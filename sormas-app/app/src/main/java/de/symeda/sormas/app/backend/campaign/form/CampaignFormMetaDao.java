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
import java.util.Objects;
import java.util.stream.Collectors;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class CampaignFormMetaDao extends AbstractAdoDao<CampaignFormMeta> {

    public CampaignFormMetaDao(Dao<CampaignFormMeta, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<CampaignFormMeta> getAdoClass() {
        return CampaignFormMeta.class;
    }

    @Override
    public String getTableName() {
        return CampaignFormMeta.TABLE_NAME;
    }

    // This method should NOT be static
    public List<String> getAllFormCategories() {
        try {
            QueryBuilder<CampaignFormMeta, Long> queryBuilder = this.queryBuilder();  // 'this' is important

            queryBuilder.selectColumns("formCategory").distinct();

            List<CampaignFormMeta> results = queryBuilder.query();

            return results.stream()
                    .map(CampaignFormMeta::getFormCategory)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}

