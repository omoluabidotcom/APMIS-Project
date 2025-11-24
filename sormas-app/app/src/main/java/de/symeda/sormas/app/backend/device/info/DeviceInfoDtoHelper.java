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

package de.symeda.sormas.app.backend.device.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.devicemanager.DeviceManagerDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceInfoDtoHelper extends AdoDtoHelper<DeviceInfo, DeviceManagerDto> {

    @Override
    protected Class<DeviceInfo> getAdoClass() {
        return DeviceInfo.class;
    }

    @Override
    protected Class<DeviceManagerDto> getDtoClass() {
        return DeviceManagerDto.class;
    }

    @Override
    protected Call<List<DeviceManagerDto>> pullAllSince(long since) throws NoConnectionException {
        // Device info is push-only, no pull needed
        return new Call<List<DeviceManagerDto>>() {
            @Override
            public Response<List<DeviceManagerDto>> execute() throws IOException {
                return Response.success(new ArrayList<>());
            }

            @Override
            public void enqueue(Callback<List<DeviceManagerDto>> callback) {
                callback.onResponse(this, Response.success(new ArrayList<>()));
            }

            @Override
            public boolean isExecuted() { return false; }

            @Override
            public void cancel() {}

            @Override
            public boolean isCanceled() { return false; }

            @Override
            public Call<List<DeviceManagerDto>> clone() { return this; }

            @Override
            public Request request() { return null; }
        };
    }

    @Override
    protected Call<List<DeviceManagerDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        // Device info is push-only, no pull needed
        return new Call<List<DeviceManagerDto>>() {
            @Override
            public Response<List<DeviceManagerDto>> execute() throws IOException {
                return Response.success(new ArrayList<>());
            }

            @Override
            public void enqueue(Callback<List<DeviceManagerDto>> callback) {
                callback.onResponse(this, Response.success(new ArrayList<>()));
            }

            @Override
            public boolean isExecuted() { return false; }

            @Override
            public void cancel() {}

            @Override
            public boolean isCanceled() { return false; }

            @Override
            public Call<List<DeviceManagerDto>> clone() { return this; }

            @Override
            public Request request() { return null; }
        };
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<DeviceManagerDto> deviceInfoDtos) throws NoConnectionException {
        // Note: Device info might not need server synchronization
        // If you want to sync device info to server, implement this
         return RetroProvider.getDeviceInfoFacadeRetro().pushAll(deviceInfoDtos);
    }

    @Override
    protected void fillInnerFromDto(DeviceInfo target, DeviceManagerDto source) {
        // Device Information
        target.setDeviceBrand(source.getDeviceBrand());
        target.setDeviceModel(source.getDeviceModel());
        target.setDeviceSerial(source.getDeviceSerial());
        target.setAndroidVersion(source.getAndroidVersion());

        // Storage Information
        target.setInternalStorageTotal(source.getInternalStorageTotal());
        target.setInternalStorageFree(source.getInternalStorageFree());
        target.setExternalStorageTotal(source.getExternalStorageTotal());
        target.setExternalStorageFree(source.getExternalStorageFree());
        target.setRamTotal(source.getRamTotal());

        // Battery Information
        target.setBatteryLevel(source.getBatteryLevel());
//        target.setIsCharging(source.getIsCharging());
        


        // Network Information
        target.setWifiConnected(source.getWifiConnected());
        
        // Convert network type string to enum
//        if (source.getNetworkType() != null) {
//            try {
//                target.setNetworkType(DeviceInfo.NetworkType.valueOf(source.getNetworkType()));
//            } catch (IllegalArgumentException e) {
//                target.setNetworkType(DeviceInfo.NetworkType.UNKNOWN);
//            }
//        }
        
        target.setNetworkStrength(source.getNetworkStrength());

        // User Information
//        target.setUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getUser()));
        target.setUserName(source.getUserName());


        // Timestamps
//        target.setLoginTimestamp(source.getLoginTimestamp() != null ? source.getLoginTimestamp() : new Date());
//        target.setLastUpdated(source.getLastUpdated() != null ? source.getLastUpdated() : new Date());

        //APPVERSION AND USER LOCATION
        target.setApkVersion(source.getApkVersion());
        target.setUserLocation(source.getUserLocation());
        target.setDeviceId(source.getDeviceId());
    }

    @Override
    protected void fillInnerFromAdo(DeviceManagerDto target, DeviceInfo source) {
        // Device Information
        target.setDeviceBrand(source.getDeviceBrand());
        target.setDeviceModel(source.getDeviceModel());
        target.setDeviceSerial(source.getDeviceSerial());
        target.setAndroidVersion(source.getAndroidVersion());
//        target.setApiLevel(source.getApiLevel());
//        target.setDeviceId(source.getDeviceId());

        // Storage Information
        target.setInternalStorageTotal(source.getInternalStorageTotal());
        target.setInternalStorageFree(source.getInternalStorageFree());
        target.setExternalStorageTotal(source.getExternalStorageTotal());
        target.setExternalStorageFree(source.getExternalStorageFree());
        target.setRamTotal(source.getRamTotal());
        target.setDeviceId(source.getDeviceId());


        // Battery Information
        target.setBatteryLevel(source.getBatteryLevel());
//        target.setIsCharging(source.getIsCharging());
//        target.setBatteryStatus(source.getBatteryStatus() != null ? source.getBatteryStatus().toString() : null);

        // Network Information
        target.setWifiConnected(source.getWifiConnected());
//        target.setNetworkType(source.getNetworkType() != null ? source.getNetworkType().toString() : null);
        target.setNetworkStrength(source.getNetworkStrength());

        // User Information
//        target.setUser(UserDtoHelper.toReferenceDto(source.getUser()));
        target.setUserName(source.getUserName());


        target.setApkVersion(source.getApkVersion());
        target.setUserLocation(source.getUserLocation());

        // Timestamps
//        target.setLoginTimestamp(source.getLoginTimestamp());
//        target.setLastUpdated(source.getLastUpdated());
    }

    /**
     * Create a reference DTO for DeviceInfo
     */
    public static DeviceManagerDto toReferenceDto(DeviceInfo source) {
        if (source == null) {
            return null;
        }

        DeviceManagerDto target = new DeviceManagerDto();
        target.setUuid(source.getUuid());
        target.setDeviceBrand(source.getDeviceBrand());
        target.setDeviceModel(source.getDeviceModel());
//        target.setLoginTimestamp(source.getLoginTimestamp());
        
        return target;
    }
}
