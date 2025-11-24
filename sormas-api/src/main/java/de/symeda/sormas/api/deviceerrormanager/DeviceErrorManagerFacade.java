/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.deviceerrormanager;

import java.io.IOException;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;


@Remote
public interface DeviceErrorManagerFacade {
	
	List<DeviceErrorManagerDto> getIndexList(DeviceErrorMangerCriteria criteria, Integer first, Integer max,
			List<SortProperty> sortProperties);
	

	DeviceErrorManagerDto saveDeviceErrorFromMobile(@Valid DeviceErrorManagerDto deviceManagerDto);

	List<DeviceErrorManagerDto> getByUuids(List<String> uuids);

	DeviceErrorManagerDto getDeviceDetailsByUuid(String uuid);
	
	DeviceErrorManagerDto getDeviceErrorByUsernameAndDeviceId(String username, String deviceSerial);
	
	List<DeviceErrorManagerDto> getLatestLogs(String username, String deviceSerial, int max);


}