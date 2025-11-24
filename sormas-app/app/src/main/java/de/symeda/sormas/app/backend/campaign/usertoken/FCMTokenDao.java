package de.symeda.sormas.app.backend.campaign.usertoken;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

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
            } else {
                throw new RuntimeException("FCM Token not found with username: " + username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update FCM Token for user: " + username, e);
        }
    }
}
