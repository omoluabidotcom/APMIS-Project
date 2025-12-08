package de.symeda.sormas.app.backend.campaign.usertoken;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = FCMToken.TABLE_NAME)
@DatabaseTable(tableName = FCMToken.TABLE_NAME)
public class FCMToken extends AbstractDomainObject{

    private static final long serialVersionUID = -629432640970159872L;

    public static final String TABLE_NAME = "fcmtokens";
    public static final String I18N_PREFIX = "fcmtoken";

    public static final String USER_NAME = "userName";

    public static final String TOKEN = "token";

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "token")
    private String token;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
