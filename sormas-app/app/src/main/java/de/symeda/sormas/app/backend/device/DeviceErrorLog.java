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

package de.symeda.sormas.app.backend.device;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = DeviceErrorLog.TABLE_NAME)
@DatabaseTable(tableName = DeviceErrorLog.TABLE_NAME)
public class DeviceErrorLog extends AbstractDomainObject {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "device_error";
    public static final String I18N_PREFIX = "Device_error";


    public static final String ANDROID_VERSION = "androidVersion";
    public static final String API_LEVEL = "apiLevel";
    public static final String DEVICE_ID = "deviceId";
    public static final String ERRORED_ACTION = "error_action";
    public static final String LAST_UPDATED = "lastUpdated";

    // Device Information Fields
    @Column
    private String deviceBrand;

    @Column
    private String deviceModel;

    @Column
    private String deviceSerial;

    @Column
    private String deviceId;

    @Column
    private String androidVersion;

    @Column
    private Integer apiLevel;

    // Storage Information Fields
    @Column
    private Long internalStorageTotal;

    @Column
    private Long internalStorageFree;

    @Column
    private Long externalStorageTotal;

    @Column
    private Long externalStorageFree;

    @Column
    private Long ramTotal;

    // Battery Information Fields
    @Column
    private Integer batteryLevel;

//    @Enumerated(EnumType.STRING)
//    private BatteryStatus batteryStatus;
//
//    @Column
//    private Boolean isCharging;

    // Network Information Fields
    @Enumerated(EnumType.STRING)
    private NetworkType networkType;

    @Column
    private Boolean wifiConnected;

    @Column
    private Integer networkStrength;

    // User and Login Information Fields
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;

    @Column(name = "login_timestamp")
    private Date loginTimestamp;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @Column(name = "user_location")
    private String userLocation;

    @Column(name = "apk_version")
    private String apkVersion;

    @Column(name = "user_name")
    private String userName;


    // Enums
//    public enum BatteryStatus {
//        UNKNOWN, CHARGING, DISCHARGING, NOT_CHARGING, FULL
//    }

    public enum NetworkType {
        UNKNOWN, WIFI, MOBILE, ETHERNET, BLUETOOTH, VPN
    }

    // Getters and Setters
    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public Integer getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(Integer apiLevel) {
        this.apiLevel = apiLevel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getInternalStorageTotal() {
        return internalStorageTotal;
    }

    public void setInternalStorageTotal(Long internalStorageTotal) {
        this.internalStorageTotal = internalStorageTotal;
    }

    public Long getInternalStorageFree() {
        return internalStorageFree;
    }

    public void setInternalStorageFree(Long internalStorageFree) {
        this.internalStorageFree = internalStorageFree;
    }

    public Long getExternalStorageTotal() {
        return externalStorageTotal;
    }

    public void setExternalStorageTotal(Long externalStorageTotal) {
        this.externalStorageTotal = externalStorageTotal;
    }

    public Long getExternalStorageFree() {
        return externalStorageFree;
    }

    public void setExternalStorageFree(Long externalStorageFree) {
        this.externalStorageFree = externalStorageFree;
    }

    public Long getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(Long ramTotal) {
        this.ramTotal = ramTotal;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public Boolean getWifiConnected() {
        return wifiConnected;
    }

    public void setWifiConnected(Boolean wifiConnected) {
        this.wifiConnected = wifiConnected;
    }

    public Integer getNetworkStrength() {
        return networkStrength;
    }

    public void setNetworkStrength(Integer networkStrength) {
        this.networkStrength = networkStrength;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(Date loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
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