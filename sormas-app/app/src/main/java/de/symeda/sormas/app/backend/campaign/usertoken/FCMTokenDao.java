package de.symeda.sormas.app.backend.campaign.usertoken;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class FCMTokenDao extends AbstractAdoDao<FCMToken> {
    public FCMTokenDao(Dao<FCMToken, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<FCMToken> getAdoClass() {
        return null;
    }

    @Override
    public String getTableName() {
        return "";
    }
}
