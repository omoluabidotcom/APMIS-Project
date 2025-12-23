package de.symeda.sormas.app.backend.campaign.usertoken;

import com.j256.ormlite.dao.Dao;
 
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class FCMTokenDao extends AbstractAdoDao<FCMToken> {
    public FCMTokenDao(Dao<FCMToken, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<FCMToken> getAdoClass() {
        return FCMToken.class;
    }

    @Override
    public String getTableName() {
        return FCMToken.TABLE_NAME;
    }

    public void updateFcmToken(String username, String token) {
        try {

            Date now = new Date();
            FCMToken fcmtoken = dao.queryBuilder()
                    .where()
                    .eq(FCMToken.USER_NAME, username.toLowerCase())
                    .and()
                    .eq(AbstractDomainObject.SNAPSHOT, false)
                    .queryForFirst();

            if (fcmtoken == null) {
                fcmtoken = new FCMToken();
                fcmtoken.setUuid(UUID.randomUUID().toString());
                fcmtoken.setCreationDate(now);
            }

            fcmtoken.setUserName(username.toLowerCase());
            fcmtoken.setToken(token);
            fcmtoken.setChangeDate(now);
            fcmtoken.setLocalChangeDate(now);
            fcmtoken.setLastOpenedDate(now);
            fcmtoken.setModified(true);
            fcmtoken.setSnapshot(false);

            dao.createOrUpdate(fcmtoken);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update FCM Token for user: " + username, e);
        }
    }

    public void saveOrUpdateToken(String username, String newToken) throws SQLException {
        FCMToken token = dao.queryBuilder()
                .where()
                .eq(FCMToken.USER_NAME, username)
                .queryForFirst();

        if (token == null) {
            token = new FCMToken();
            token.setUserName(username);
        }

        token.setToken(newToken);

        dao.createOrUpdate(token);
    }
}
