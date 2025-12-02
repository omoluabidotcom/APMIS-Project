package de.symeda.sormas.app.backend.campaign.usertoken;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;

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
            QueryBuilder<FCMToken, Long> builder = dao.queryBuilder();
            Where<FCMToken, Long> where = builder.where();
            where.eq(FCMToken.USER_NAME, username.toLowerCase());
            where.and().eq(AbstractDomainObject.SNAPSHOT, false);
            PreparedQuery<FCMToken> preparedQuery = builder.prepare();
            FCMToken fcmtoken = dao.queryForFirst(preparedQuery);

            if (fcmtoken != null) {
                fcmtoken.setToken(token);
//				user.setChangeDate(new Date());
//				user.setModified(true);
                dao.update(fcmtoken);
                System.out.println("NOTNULLLLLLLLLLLNULLLLLLLLLLLLLLLLLLLLLLLLL 111111111111111");
            } else {
                FCMToken fcmToken = new FCMToken();
                fcmToken.setUserName(username.toLowerCase());
                fcmToken.setToken(token);

                // Required ADO fields
                Date now = new Date();
                fcmToken.setUuid(java.util.UUID.randomUUID().toString());
                fcmToken.setCreationDate(now);
                fcmToken.setChangeDate(now);
                fcmToken.setLocalChangeDate(now);
                fcmToken.setLastOpenedDate(now);
                fcmToken.setModified(true);
                fcmToken.setSnapshot(false);

                dao.create(fcmToken);
//                fcmToken.

                dao.create(fcmToken);
                System.out.println("NOTNULLLLLLLLLLLNULLLLLLLLLLLLLLLLLLLLLLLLL 22222222222222222");
//                throw new RuntimeException("FCM Token not found with username: " + username);
            }
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
