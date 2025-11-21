package de.symeda.sormas.api.messaging;

import java.io.Serializable;

import de.symeda.sormas.api.EntityDto;

public class FCMTokenDto extends EntityDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String token;

    public FCMTokenDto() {
    }

    public FCMTokenDto(String uuid, String userName, String token) { 
        this.userName = userName;
        this.token = token;
    }

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
}