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

package de.symeda.sormas.backend.deviceerrormanager;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerReferenceDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "devices_error_manager")
@Table(name = "devices_error_manager")

@Audited
public class DeviceErrorManager extends AbstractDomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -404315948897759227L;


	public static final String TABLE_NAME = "devices_error_manager";
	

	@Column(name = "username")
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userName;
	
	@Column(name = "errorAction")
	private String errorAction;
	
	@Column(name = "lastUpdated")
	private Date lastUpdated;
	
	@Column(name = "deviceId")
	private String deviceId;
	
	@Column(name = "stackTrace")
	private String stackTrace;
	
	@Column(name = "errorMessage")
	private String errorMessage;
	
	

	public DeviceErrorManagerReferenceDto toReference() {
		return new DeviceErrorManagerReferenceDto(getUuid());
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

	public void setLastUpdated(Date date) {
		this.lastUpdated = date;
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

}
