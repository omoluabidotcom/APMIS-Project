/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.devicemanager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.AccessType;


import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.data.PlatformEnum;
import de.symeda.sormas.api.devicemanager.DeviceManagerReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Access(AccessType.FIELD)
@Entity(name = "device_manager")
@Audited
public class DeviceManager extends AbstractDomainObject{

	public static final String TABLE_NAME = "device_manager";

	public static final String DEVICEMODEL = "device_model";
	public static final String USERNAME = "user_name";
	public static final String LOCATION = "user_location";
	public static final String APKVERSION = "apk_version";
	
	public static final String DEVICEBRAND = "device_brand";
	public static final String DEVICESERIAL = "device_serial";
	public static final String DEVICE_ID = "device_id";

	public static final String ANDROIDVERSION= "android_version";
	public static final String TOTAL_INT_STORAGE= "total_int_storage";
	public static final String FREE_INT_STORAGE= "free_int_storage";
	public static final String TOTAL_EXT_STORAGE= "total_ext_storage";
	public static final String FREE_EXT_STORAGE= "free_ext_storage";
	public static final String RAM_STORAGE= "ram_storage";
	public static final String BATTERY_LEVEL= "battery_level";
	public static final String BATTERY_STATUS= "battery_status";
	public static final String WIFI_CONNECTED= "wifi_connected";
	public static final String NETWORK_STRENGTH= "network_strength";
	public static final String LOGGED_IN_USER= "user_id";

	
	public static final String TOTAL_INT_STORAGE_GB= "total_int_storage_gb";
	public static final String FREE_INT_STORAGE_GB= "free_int_storage_gb";
	public static final String TOTAL_EXT_STORAGE_GB= "total_ext_storage_gb";
	public static final String FREE_EXT_STORAGE_GB= "free_ext_storage_gb";
	public static final String RAM_STORAGE_GB= "ram_storage_gb";
	
	public static final String NETWORK_PROVIDER = "networkProvider";
	public static final String ACTIVE_CAMPAIGNS = "activeCampaigns";
	public static final String ACTIVE_FORM_COUNT = "activeFormCount";



	
	private String device_model ;//VARCHAR(255),
	private String user_name;// VARCHAR(255),
	private String user_location;// VARCHAR(255),
	private String apk_version;// VARCHAR(255)


	private String device_brand;
	private String device_serial;
	private String device_id;
	private String networkProvider;
    private Integer activeCampaigns;
    private Integer activeFormCount;

	private String android_version;    
    private Long total_int_storage;
    private Long free_int_storage;
    private Long total_ext_storage;
    private Long free_ext_storage;
    private Long ram_storage;

    
    

    @Column(name = "total_int_storage_gb", insertable = false, updatable = false)
    private BigDecimal total_int_storage_gb;

    @Column(name = "free_int_storage_gb", insertable = false, updatable = false)
    private BigDecimal free_int_storage_gb;

    @Column(name = "total_ext_storage_gb", insertable = false, updatable = false)
    private BigDecimal total_ext_storage_gb;

    @Column(name = "free_ext_storage_gb", insertable = false, updatable = false)
    private BigDecimal free_ext_storage_gb;

    @Column(name = "ram_storage_gb", insertable = false, updatable = false)
    private BigDecimal ram_storage_gb;

    
//    private Long total_int_storage_gb;
//    private Long free_int_storage_gb;
//    private Long total_ext_storage_gb;
//    private Long free_ext_storage_gb;
//    private Long ram_storage_gb;
    
    private Integer battery_level;
    private Boolean wifi_connected;
    private Integer network_strength;

@ManyToOne
@JoinColumn(name = "user_id") // bigint FK to users.id
    private User user_id;


//	public static final String DISTRICT = "district";
//	public static final String COMMUNITY = "community";


	private static final long serialVersionUID = -8021065433714419288L;


//	private District district;
//	public Community community;



	public DeviceManagerReferenceDto toReference() {
		return new DeviceManagerReferenceDto(getUuid());
	}


	public String getDevice_model() {
		return device_model;
	}


	public void setDevice_model(String device_model) {
		this.device_model = device_model;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getUser_location() {
		return user_location;
	}


	public void setUser_location(String user_location) {
		this.user_location = user_location;
	}


	public String getApk_version() {
		return apk_version;
	}


	public void setApk_version(String apk_version) {
		this.apk_version = apk_version;
	}


	public String getDevice_brand() {
		return device_brand;
	}


	public void setDevice_brand(String device_brand) {
		this.device_brand = device_brand;
	}


	public String getDevice_serial() {
		return device_serial;
	}


	public void setDevice_serial(String device_serial) {
		this.device_serial = device_serial;
	}


	public String getAndroid_version() {
		return android_version;
	}


	public void setAndroid_version(String android_version) {
		this.android_version = android_version;
	}


	public Long getTotal_int_storage() {
		return total_int_storage;
	}


	public void setTotal_int_storage(Long total_int_storage) {
		this.total_int_storage = total_int_storage;
	}


	public Long getFree_int_storage() {
		return free_int_storage;
	}


	public void setFree_int_storage(Long free_int_storage) {
		this.free_int_storage = free_int_storage;
	}


	public Long getTotal_ext_storage() {
		return total_ext_storage;
	}


	public void setTotal_ext_storage(Long total_ext_storage) {
		this.total_ext_storage = total_ext_storage;
	}


	public Long getFree_ext_storage() {
		return free_ext_storage;
	}


	public void setFree_ext_storage(Long free_ext_storage) {
		this.free_ext_storage = free_ext_storage;
	}


	public Long getRam_storage() {
		return ram_storage;
	}


	public void setRam_storage(Long ram_storage) {
		this.ram_storage = ram_storage;
	}


	public Integer getBattery_level() {
		return battery_level;
	}


	public void setBattery_level(Integer battery_level) {
		this.battery_level = battery_level;
	}


	public Boolean getWifi_connected() {
		return wifi_connected;
	}


	public void setWifi_connected(Boolean wifi_connected) {
		this.wifi_connected = wifi_connected;
	}


	public Integer getNetwork_strength() {
		return network_strength;
	}


	public void setNetwork_strength(Integer network_strength) {
		this.network_strength = network_strength;
	}


	public String getDevice_id() {
		return device_id;
	}


	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public BigDecimal getTotal_int_storage_gb() { return total_int_storage_gb; }

	public BigDecimal getFree_int_storage_gb() { return free_int_storage_gb; }

	public BigDecimal getTotal_ext_storage_gb() { return total_ext_storage_gb; }

	public BigDecimal getFree_ext_storage_gb() { return free_ext_storage_gb; }

	public BigDecimal getRam_storage_gb() { return ram_storage_gb; }


	public String getNetworkProvider() {
		return networkProvider;
	}


	public void setNetworkProvider(String networkProvider) {
		this.networkProvider = networkProvider;
	}


	public Integer getActiveCampaigns() {
		return activeCampaigns;
	}


	public void setActiveCampaigns(Integer activeCampaigns) {
		this.activeCampaigns = activeCampaigns;
	}


	public Integer getActiveFormCount() {
		return activeFormCount;
	}


	public void setActiveFormCount(Integer activeFormCount) {
		this.activeFormCount = activeFormCount;
	}
	
	



}
