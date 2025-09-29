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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

/**
 * Data Access Object for DeviceInfo
 */
public class DeviceInfoDao extends AbstractAdoDao<DeviceInfo> {

    public DeviceInfoDao(Dao<DeviceInfo, Long> dao) throws SQLException {
        super(dao);
    }

    @Override
    protected Class<DeviceInfo> getAdoClass() {
        return DeviceInfo.class;
    }

    @Override
    public String getTableName() {
        return DeviceInfo.TABLE_NAME;
    }

    /**
     * Get device info for a specific user
     */
    public DeviceInfo getByUser(User user) throws SQLException {
        if (user == null) {
            return null;
        }
        List<DeviceInfo> deviceInfos = queryForEq(DeviceInfo.USER + "_id", user.getId());
        // Ensure only one record by deleting duplicates
        if (deviceInfos.size() > 1) {
            Log.w(getTableName(), "Multiple DeviceInfo records found for user: " + user.getUserName() + ". Keeping the latest.");
//            deleteDuplicates(user);
            deviceInfos = queryForEq(DeviceInfo.USER + "_id", user.getId());
        }
        return deviceInfos.isEmpty() ? null : deviceInfos.get(0);
    }

    /**
     * Get device info by device ID
     */
    public DeviceInfo getByDeviceId(String deviceId) throws SQLException {
        if (deviceId == null) {
            return null;
        }
        List<DeviceInfo> deviceInfos = queryForEq(DeviceInfo.DEVICE_ID, deviceId);
        return deviceInfos.isEmpty() ? null : deviceInfos.get(0);
    }

    /**
     * Get recent device info entries (last 30 days)
     */
//    public List<DeviceInfo> getRecentDeviceInfo(int days) throws SQLException {
//        Date cutoffDate = new Date(System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L));
//        return queryBuilder()
//                .where()
//                .ge(DeviceInfo.LOGIN_TIMESTAMP, cutoffDate)
//                .orderBy(DeviceInfo.LOGIN_TIMESTAMP, false)
//                .query();
//    }

    /**
     * Create a new DeviceInfo record for a user
     */
    private DeviceInfo createDeviceInfo(User user, DeviceInfo inputDeviceInfo) throws SQLException {
        if (user == null || inputDeviceInfo == null) {
            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
        }
        // Ensure no existing records for the user
//        deleteDuplicates(user);

        DeviceInfo deviceInfo = new DeviceInfo();
        // Initialize fields from AbstractAdoDao
        deviceInfo.setUuid(DataHelper.createUuid());
        deviceInfo.setCreationDate(new Date());
        // Copy fields from input
        deviceInfo.setDeviceBrand(inputDeviceInfo.getDeviceBrand());
        deviceInfo.setDeviceModel(inputDeviceInfo.getDeviceModel());
        deviceInfo.setDeviceSerial(inputDeviceInfo.getDeviceSerial());
        deviceInfo.setAndroidVersion(inputDeviceInfo.getAndroidVersion());
        deviceInfo.setApiLevel(inputDeviceInfo.getApiLevel());
        deviceInfo.setDeviceId(inputDeviceInfo.getDeviceId());
        deviceInfo.setInternalStorageTotal(inputDeviceInfo.getInternalStorageTotal());
        deviceInfo.setInternalStorageFree(inputDeviceInfo.getInternalStorageFree());
        deviceInfo.setExternalStorageTotal(inputDeviceInfo.getExternalStorageTotal());
        deviceInfo.setExternalStorageFree(inputDeviceInfo.getExternalStorageFree());
        deviceInfo.setRamTotal(inputDeviceInfo.getRamTotal());
        deviceInfo.setBatteryLevel(inputDeviceInfo.getBatteryLevel());
//        deviceInfo.setBatteryStatus(inputDeviceInfo.getBatteryStatus());
//        deviceInfo.setIsCharging(inputDeviceInfo.getIsCharging());
        deviceInfo.setNetworkType(inputDeviceInfo.getNetworkType());
        deviceInfo.setWifiConnected(inputDeviceInfo.getWifiConnected());
        deviceInfo.setNetworkStrength(inputDeviceInfo.getNetworkStrength());
        deviceInfo.setUser(user);
        deviceInfo.setLoginTimestamp(new Date());
        deviceInfo.setLastUpdated(new Date());
        deviceInfo.setChangeDate(new Date());
        deviceInfo.setUserLocation(user.getRegion().getName());
        deviceInfo.setApkVersion(ConfigProvider.APPVERSIONNUMBER);
        deviceInfo.setUserName(user.getUserName());
        deviceInfo.setDeviceId(inputDeviceInfo.getDeviceId());

        deviceInfo.setModified(true);

        create(deviceInfo);
        Log.i(getTableName(), "Created DeviceInfo for user: " + user.getUserName());
        return deviceInfo;
    }

    /**
     * Update an existing DeviceInfo record
     */
    private DeviceInfo updateDeviceInfo(DeviceInfo existingDeviceInfo, DeviceInfo inputDeviceInfo) throws SQLException {
        if (existingDeviceInfo == null || inputDeviceInfo == null) {
            throw new IllegalArgumentException("Existing DeviceInfo and input DeviceInfo cannot be null");
        }
        // Update fields from input
        existingDeviceInfo.setDeviceBrand(inputDeviceInfo.getDeviceBrand());
        existingDeviceInfo.setDeviceModel(inputDeviceInfo.getDeviceModel());
        existingDeviceInfo.setDeviceSerial(inputDeviceInfo.getDeviceSerial());
        existingDeviceInfo.setAndroidVersion(inputDeviceInfo.getAndroidVersion());
        existingDeviceInfo.setApiLevel(inputDeviceInfo.getApiLevel());
        existingDeviceInfo.setDeviceId(inputDeviceInfo.getDeviceId());
        existingDeviceInfo.setInternalStorageTotal(inputDeviceInfo.getInternalStorageTotal());
        existingDeviceInfo.setInternalStorageFree(inputDeviceInfo.getInternalStorageFree());
        existingDeviceInfo.setExternalStorageTotal(inputDeviceInfo.getExternalStorageTotal());
        existingDeviceInfo.setExternalStorageFree(inputDeviceInfo.getExternalStorageFree());
        existingDeviceInfo.setRamTotal(inputDeviceInfo.getRamTotal());
        existingDeviceInfo.setBatteryLevel(inputDeviceInfo.getBatteryLevel());
//        existingDeviceInfo.setBatteryStatus(inputDeviceInfo.getBatteryStatus());
//        existingDeviceInfo.setIsCharging(inputDeviceInfo.getIsCharging());
        existingDeviceInfo.setNetworkType(inputDeviceInfo.getNetworkType());
        existingDeviceInfo.setWifiConnected(inputDeviceInfo.getWifiConnected());
        existingDeviceInfo.setNetworkStrength(inputDeviceInfo.getNetworkStrength());
        existingDeviceInfo.setUser(inputDeviceInfo.getUser());
        existingDeviceInfo.setLoginTimestamp(new Date());
        existingDeviceInfo.setLastUpdated(new Date());
        existingDeviceInfo.setChangeDate(new Date());
        existingDeviceInfo.setUserLocation(existingDeviceInfo.getUserLocation());
        existingDeviceInfo.setApkVersion(ConfigProvider.APPVERSIONNUMBER);
        existingDeviceInfo.setUserName(existingDeviceInfo.getUserName());
        existingDeviceInfo.setDeviceId(existingDeviceInfo.getDeviceId());


        existingDeviceInfo.setModified(true);


        update(existingDeviceInfo);
        Log.i(getTableName(), "Updated DeviceInfo for user: " + existingDeviceInfo.getUser().getUserName());
        return existingDeviceInfo;
    }

    /**
     * Update or create DeviceInfo for a user, ensuring only one record per user
     */
    public DeviceInfo updateOrCreateDeviceInfo(User user, DeviceInfo deviceInfo) throws SQLException {
        if (user == null || deviceInfo == null) {
            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
        }
        DeviceInfo existingDeviceInfo = getByUser(user);
        if (existingDeviceInfo != null) {
            return updateDeviceInfo(existingDeviceInfo, deviceInfo);
        } else {
            return createDeviceInfo(user, deviceInfo);
        }
    }

    /**
     * Delete duplicate DeviceInfo records for a user, keeping the latest
     */
    private void deleteDuplicates(User user) throws SQLException {
        QueryBuilder<DeviceInfo, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(DeviceInfo.USER + "_id", user.getId());
        queryBuilder.orderBy(DeviceInfo.LOGIN_TIMESTAMP, false);
        queryBuilder.limit(1L);
        DeviceInfo latest = queryBuilder.queryForFirst();
        if (latest != null) {
            DeleteBuilder<DeviceInfo, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DeviceInfo.USER + "_id", user.getId())
                    .and()
                    .ne("id", latest.getId());
            int deleted = deleteBuilder.delete();
            if (deleted > 0) {
                Log.i(getTableName(), "Deleted " + deleted + " duplicate DeviceInfo records for user: " + user.getUserName());
            }
        }
    }

    @Override
    public DeviceInfo build() {
        DeviceInfo deviceInfo = super.build();
        deviceInfo.setUser(ConfigProvider.getUser());
        deviceInfo.setLoginTimestamp(new Date());
        deviceInfo.setLastUpdated(new Date());
        return deviceInfo;
    }
}