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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.deviceerrormanager.DeviceErrorManagerDto;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.device.info.DeviceInfo;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceErrorLogDtoHelper extends AdoDtoHelper<DeviceErrorLog, DeviceErrorManagerDto> {

    @Override
    protected Class<DeviceErrorLog> getAdoClass() {
        return DeviceErrorLog.class;
    }

    @Override
    protected Class<DeviceErrorManagerDto> getDtoClass() {
        return DeviceErrorManagerDto.class;
    }

    @Override
    protected Call<List<DeviceErrorManagerDto>> pullAllSince(long since) throws NoConnectionException {
        // Device info is push-only, no pull needed
        return new Call<List<DeviceErrorManagerDto>>() {
            @Override
            public Response<List<DeviceErrorManagerDto>> execute() throws IOException {
                return Response.success(new ArrayList<>());
            }

            @Override
            public void enqueue(Callback<List<DeviceErrorManagerDto>> callback) {
                callback.onResponse(this, Response.success(new ArrayList<>()));
            }

            @Override
            public boolean isExecuted() { return false; }

            @Override
            public void cancel() {}

            @Override
            public boolean isCanceled() { return false; }

            @Override
            public Call<List<DeviceErrorManagerDto>> clone() { return this; }

            @Override
            public Request request() { return null; }
        };
    }

    @Override
    protected Call<List<DeviceErrorManagerDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        // Device info is push-only, no pull needed
        return new Call<List<DeviceErrorManagerDto>>() {
            @Override
            public Response<List<DeviceErrorManagerDto>> execute() throws IOException {
                return Response.success(new ArrayList<>());
            }

            @Override
            public void enqueue(Callback<List<DeviceErrorManagerDto>> callback) {
                callback.onResponse(this, Response.success(new ArrayList<>()));
            }

            @Override
            public boolean isExecuted() { return false; }

            @Override
            public void cancel() {}

            @Override
            public boolean isCanceled() { return false; }

            @Override
            public Call<List<DeviceErrorManagerDto>> clone() { return this; }

            @Override
            public Request request() { return null; }
        };
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<DeviceErrorManagerDto> deviceErrorDtos) throws NoConnectionException {
        // Note: Device info might not need server synchronization
        // If you want to sync device info to server, implement this
        return RetroProvider.getDeviceErrorFacadeRetro().pushAll(deviceErrorDtos);
    }

    @Override
    protected void fillInnerFromDto(DeviceErrorLog target, DeviceErrorManagerDto source) {
        // Device Information

        target.setDeviceId(source.getDeviceId());
        target.setLastUpdated(source.getLastUpdated());
        target.setErrorMessage(source.getErrorMessage());
        target.setStackTrace(source.getStackTrace());
        target.setUserName(source.getUserName());
        target.setErrorAction(source.getErrorAction());

    }

    @Override
    protected void fillInnerFromAdo(DeviceErrorManagerDto target, DeviceErrorLog source) {
        // Device Information
        target.setDeviceId(source.getDeviceId());
        target.setErrorMessage(source.getErrorMessage());
        target.setStackTrace(source.getStackTrace());
        target.setUserName(source.getUserName());
        target.setErrorAction(source.getErrorAction());
    }

    /**
     * Create a reference DTO for DeviceInfo
     */
    public static DeviceErrorManagerDto toReferenceDto(DeviceErrorLog source) {
        if (source == null) {
            return null;
        }

        DeviceErrorManagerDto target = new DeviceErrorManagerDto();
        target.setUuid(source.getUuid());
        target.setDeviceId(source.getDeviceId());
//        target.setErrorAction(source.getErrorAction());
        target.setUserName(source.getUserName());
//        target.setLoginTimestamp(source.getLoginTimestamp());

        return target;
    }
}
