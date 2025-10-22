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

package de.symeda.sormas.app.backend.device.errorLog;

import com.j256.ormlite.table.DatabaseTable;

import java.security.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = DeviceErrorLog.TABLE_NAME)
@DatabaseTable(tableName = DeviceErrorLog.TABLE_NAME)
public class DeviceErrorLog extends AbstractDomainObject {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "device_error";
    public static final String I18N_PREFIX = "Device_error";

    public static final String ANDROID_VERSION = "errorMessage";
    public static final String STACK_TRACE = "stackTrace";
    public static final String DEVICE_ID = "deviceId";
    public static final String USERNAME = "userName";
    public static final String ERRORED_ACTION = "errorAction";
    public static final String LAST_UPDATED = "lastUpdated";

    @Column(name = "errorMessage")
    private String errorMessage;

    @Column(name = "stackTrace")
    private String stackTrace;

    @Column(name = "deviceId")
    private String deviceId;

    @Column(name = "errorAction")
    private String errorAction;

    @Column(name = "lastUpdated")
    private Date lastUpdated;

    @Column(name = "userName")
    private String userName;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getErrorAction() {
        return errorAction;
    }

    public void setErrorAction(String errorAction) {
        this.errorAction = errorAction;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}