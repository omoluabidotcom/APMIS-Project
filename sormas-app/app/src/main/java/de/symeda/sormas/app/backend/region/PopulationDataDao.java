package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class PopulationDataDao extends AbstractAdoDao<PopulationData> {
    public PopulationDataDao(Dao<PopulationData, Long> innerDao) {
        super(innerDao);

    }
    @Override
    protected Class<PopulationData> getAdoClass() {
        return PopulationData.class;
    }
    @Override
    public String getTableName() {
        return PopulationData.TABLE_NAME;
    }

    public List<PopulationData> getDistrictsByCampaignId(Long campaignId) {
        try {
            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
            queryBuilder.where().eq("campaign_id", campaignId);
            return queryBuilder.query();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving districts for campaign ID: " + campaignId, e);
        }
    }

    public List<PopulationData> getCampaignsByDistrictId(Long districtId) {
        try {
            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
            queryBuilder.where().eq("district_id", districtId);
            return queryBuilder.query();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving campaigns for district ID: " + districtId, e);
        }
    }

public List<PopulationData> getSelectedDistrictsByMultipleUuids(List<String> districtUuids, String campaignUuid) {
    if (districtUuids == null || districtUuids.isEmpty()) {
        System.out.println("didtricy uuid is null from backend -------------");
        return new ArrayList<>();
    }
    List<PopulationData> result = new ArrayList<>();
    try {
        QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
        Where<PopulationData, Long> where = queryBuilder.where();
        where.eq("campaign_id", campaignUuid);
        where.and();
        where.eq("selected", true);

        if (districtUuids.size() == 1) {
            where.and();
            where.eq("district_id", districtUuids.get(0));
        } else {
            System.out.println("District uuid size is greater than 0 -----------------");
            where.and();
            where.in("district_id", districtUuids);
        }
        result = queryBuilder.query();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return result;
}

    public List<PopulationData> getSelectedClustersByMultipleUuids(List<String> clusterUuids, String campaignUuid) {

        if (clusterUuids == null || clusterUuids.isEmpty()) {
            System.out.println("clusterUuids is null or empty -------------");
            return new ArrayList<>();
        }

        List<PopulationData> result = new ArrayList<>();

        try {
            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
            Where<PopulationData, Long> where = queryBuilder.where();

            where.eq("campaign_id", campaignUuid)
                    .and()
                    .in("cluster_id", clusterUuids)   // 👈 cluster filter first
                    .and()
                    .eq("selected", true);            // 👈 selected LAST

            result = queryBuilder.query();

            System.out.println("Query result size: " + result.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

//    public List<PopulationData> getSelectedClustersByMultipleUuids(List<String> districtUuids, String campaignUuid) {
//        if (districtUuids == null || districtUuids.isEmpty()) {
//            System.out.println("didtricy uuid is null from backend -------------");
//            return new ArrayList<>();
//        }
//        List<PopulationData> result = new ArrayList<>();
//        try {
//            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
//            Where<PopulationData, Long> where = queryBuilder.where();
//            where.eq("campaign_id", campaignUuid);
//            where.and();
//            where.eq("selected", true);
//
//            if (districtUuids.size() == 1) {
//                where.and();
//                where.eq("cluster_id", districtUuids.get(0));
//            } else {
//                System.out.println("District uuid size is greater than 0 -----------------");
//                where.and();
//                where.in("cluster_id", districtUuids);
//            }
//            result = queryBuilder.query();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

//    public List<PopulationData> getSelectedClustersByMultipleUuidsx(List<String> clusterUuids, String campaignUuid) {
//        if (clusterUuids == null || clusterUuids.isEmpty()) {
//            return new ArrayList<>();
//        }
//        List<PopulationData> result = new ArrayList<>();
//        try {
//            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
//            Where<PopulationData, Long> where = queryBuilder.where();
//
//            // Step 1: test campaign only
//            where.eq("campaign_id", campaignUuid);
//            result = queryBuilder.query();
//            System.out.println("Results with campaign only: " + result.size());
//
//            // Step 2: test cluster only
//            queryBuilder = queryBuilder();
//            where = queryBuilder.where();
//            where.in("cluster_id", clusterUuids);
//            result = queryBuilder.query();
//            System.out.println("Results with cluster only: " + result.size());
//
//            // Step 3: test selected only
//            queryBuilder = queryBuilder();
//            where = queryBuilder.where();
//            where.eq("selected", true);
//            result = queryBuilder.query();
//            System.out.println("Results with selected only: " + result.size());
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public List<PopulationData> getSelectedDistrictByUsersDistrict(String districtUuid, String campaignUuid) {
        try {
            QueryBuilder<PopulationData, Long> queryBuilder = queryBuilder();
            return queryBuilder.where()
                    .eq("campaign_id", campaignUuid)
                    .and()
                    .eq("district_id", districtUuid)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
