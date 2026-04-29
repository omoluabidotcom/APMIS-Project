/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.InfrastructureAdo;

public class DistrictDao extends AbstractInfrastructureAdoDao<District> {

	public DistrictDao(Dao<District, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<District> getAdoClass() {
		return District.class;
	}

	@Override
	public String getTableName() {
		return District.TABLE_NAME;
	}

	public List<District> getByRegion(Region region) {
		return queryActiveForEq(District.REGION + "_id", region, District.NAME, true);
	}

	public List<District> getByName(String districtName) {
		return queryActiveForEq(District.NAME, districtName, District.NAME, true);
	}

//	public List<District> getByDistrictName(String districtName) {
//		try {
//			QueryBuilder<District, String> queryBuilder = queryBuilder();
//			queryBuilder.where().eq("name", districtName);
//			return queryBuilder.query();
//		} catch (SQLException e) {
//			throw new RuntimeException("Error retrieving campaigns for district ID: " + districtId, e);
//		}
//	}

	public List<District> queryActiveForEqANdSelected(String fieldName, Region value, String orderBy, boolean ascending) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(District.REGION + "_id", value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false);
			where.and().eq(InfrastructureAdo.ARCHIVED, false).query();
			where.and().eq(District., false);

			return builder.orderBy(District.NAME, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}



	@Override
	public District saveAndSnapshot(District source) throws DaoException {
		throw new UnsupportedOperationException();
	}
}
