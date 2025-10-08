/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.devicemanager;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.ClusterFloatStatus;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;


public class DeviceManagerDto extends EntityDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "device_manager";

	public static final String UUID = "uuid";
	public static final String DEVICEMODEL = "device_model";
	public static final String USERNAME = "user_name";
	public static final String USERLOCATION = "user_location";
	public static final String APKVERSION = "apk_version";
	public static final String DEVICEBRAND = "device_brand";
	public static final String DEVICESERIAL = "device_serial";
	public static final String DEVICEID = "device_id";
	public static final String ANDROIDVERSION= "android_version";
	public static final String TOTAL_INT_STORAGE= "total_int_storage";
	public static final String FREE_INT_STORAGE= "free_int_storage";
	public static final String TOTAL_EXT_STORAGE= "total_ext_storage";
	public static final String FREE_EXT_STORAGE= "free_ext_storage";
	public static final String RAM_STORAGE= "ram_storage";
	public static final String BATTERY_LEVEL= "battery_level";
	public static final String CHARGING_STATUS= "charging_status";
	public static final String BATTERY_STATUS= "battery_status";
	public static final String WIFI_CONNECTED= "wifi_connected";
	public static final String NETWORK_STRENGTH= "network_strength";
	public static final String LOGGED_IN_USER= "user_id";
	
	public static final String TOTAL_INT_STORAGE_GB= "total_int_storage_gb";
	public static final String FREE_INT_STORAGE_GB= "free_int_storage_gb";
	public static final String TOTAL_EXT_STORAGE_GB= "total_ext_storage_gb";
	public static final String FREE_EXT_STORAGE_GB= "free_ext_storage_gb";
	public static final String RAM_STORAGE_GB= "ram_storage_gb";



	
	

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String deviceModel;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userNamex;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userLocation;
	private String apkVersion;
	
	private String deviceBrand;
	private String deviceSerial;
	private String deviceId;
	private String androidVersion;    
    private Long internalStorageTotal;
    private Long internalStorageFree;
    private Long externalStorageTotal;
    private Long externalStorageFree;
    private Long ramTotal;
    private Integer batteryLevel;
    private Boolean isCharging;
    private Boolean wifiConnected;
    private Integer networkStrength;
    private UserReferenceDto user;
    
    private Long internalStorageTotalGb;
    private Long internalStorageFreeGb;
    private Long externalStorageTotalGb;
    private Long externalStorageFreeGb;
    private Long ramTotalGb;

	private DistrictReferenceDto district;
	private CommunityReferenceDto cluster;


	
	public DeviceManagerDto() {
		super();
	}


	public static DeviceManagerDto build() {
		DeviceManagerDto dto = new DeviceManagerDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public String getDeviceModel() {
		return deviceModel;
	}


	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}


	public String getUserName() {
		return userNamex;
	}


	public void setUserName(String userName) {
		this.userNamex = userName;
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


	public DistrictReferenceDto getDistrict() {
		return district;
	}


	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}


	public CommunityReferenceDto getCluster() {
		return cluster;
	}


	public void setCluster(CommunityReferenceDto cluster) {
		this.cluster = cluster;
	}
	
	


	public String getUserNamex() {
		return userNamex;
	}


	public void setUserNamex(String userNamex) {
		this.userNamex = userNamex;
	}


	public String getDeviceBrand() {
		return deviceBrand;
	}


	public void setDeviceBrand(String deviceBrand) {
		this.deviceBrand = deviceBrand;
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


	public Boolean getIsCharging() {
		return isCharging;
	}


	public void setIsCharging(Boolean isCharging) {
		this.isCharging = isCharging;
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
	
	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	

	//	public UserReferenceDto getUser() {
//		return user;
//	}
//
//
//	public void setUser(UserReferenceDto user) {
//		this.user = user;
//	}


	public Long getInternalStorageTotalGb() {
		return internalStorageTotalGb;
	}


	public void setInternalStorageTotalGb(Long internalStorageTotalGb) {
		this.internalStorageTotalGb = internalStorageTotalGb;
	}


	public Long getInternalStorageFreeGb() {
		return internalStorageFreeGb;
	}


	public void setInternalStorageFreeGb(Long internalStorageFreeGb) {
		this.internalStorageFreeGb = internalStorageFreeGb;
	}


	public Long getExternalStorageTotalGb() {
		return externalStorageTotalGb;
	}


	public void setExternalStorageTotalGb(Long externalStorageTotalGb) {
		this.externalStorageTotalGb = externalStorageTotalGb;
	}


	public Long getExternalStorageFreeGb() {
		return externalStorageFreeGb;
	}


	public void setExternalStorageFreeGb(Long externalStorageFreeGb) {
		this.externalStorageFreeGb = externalStorageFreeGb;
	}


	public Long getRamTotalGb() {
		return ramTotalGb;
	}


	public void setRamTotalGb(Long ramTotalGb) {
		this.ramTotalGb = ramTotalGb;
	}


	@Override
	public String toString() {
		return getUserName();
	}



}
