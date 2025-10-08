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
package de.symeda.sormas.api.deviceerrormanager;

import java.security.Timestamp;
import java.sql.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;


public class DeviceErrorManagerDto extends EntityDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "devices_error_manager";

	public static final String UUID = "uuid";
	    public static final String ANDROID_VERSION = "errorMessage";
    public static final String STACK_TRACE = "stacktrace";
    public static final String DEVICE_ID = "deviceId";
    public static final String USERNAME = "username";
    public static final String ERRORED_ACTION = "errorAction";
    public static final String LAST_UPDATED = "lastUpdated";



	
	

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userName;
	private String errorAction;	
	private Date lastUpdated;
	private String deviceId;	
	private String stackTrace; 
	private String errorMessage; 



	
	public DeviceErrorManagerDto() {
		super();
	}


	public static DeviceErrorManagerDto build() {
		DeviceErrorManagerDto dto = new DeviceErrorManagerDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
 
	

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
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


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public String getStackTrace() {
		return stackTrace;
	}


	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	@Override
	public String toString() {
		return getUserName();
	}



}
