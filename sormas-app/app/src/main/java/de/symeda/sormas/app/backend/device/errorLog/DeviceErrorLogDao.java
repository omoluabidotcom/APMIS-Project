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

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.device.info.DeviceInfo;
import de.symeda.sormas.app.backend.user.User;

/**
 * Data Access Object for DeviceError Logs
 */
public class DeviceErrorLogDao extends AbstractAdoDao<DeviceErrorLog> {

    public DeviceErrorLogDao(Dao<DeviceErrorLog, Long> dao) throws SQLException {
        super(dao);
    }

    @Override
    protected Class<DeviceErrorLog> getAdoClass() {
        return DeviceErrorLog.class;
    }

    @Override
    public String getTableName() {
        return DeviceErrorLog.TABLE_NAME;
    }

    /**
     * Get device info for a specific user
     */


    /**
     * Get device info by device ID
     */
    public DeviceErrorLog getByDeviceId(String deviceId) throws SQLException {
        if (deviceId == null) {
            return null;
        }
        List<DeviceErrorLog> deviceErrors = queryForEq(DeviceErrorLog.DEVICE_ID, deviceId);
        return deviceErrors.isEmpty() ? null : deviceErrors.get(0);
    }


    /**
     * Create a new DeviceInfo record for a user
     */
    private DeviceErrorLog createDeviceErrorLog(String deviceId, DeviceErrorLog inputDeviceError) throws SQLException {
        if (deviceId == null || inputDeviceError == null) {
            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
        }
        // Ensure no existing records for the user
//        deleteDuplicates(user);
        Log.i(getTableName(), "Creating Error Log for user----------------------------: ");

        DeviceErrorLog deviceErrorLog = new DeviceErrorLog();
        // Initialize fields from AbstractAdoDao
        deviceErrorLog.setUuid(DataHelper.createUuid());
        deviceErrorLog.setCreationDate(new Date());
        deviceErrorLog.setChangeDateForNew();
//        deviceErrorLog.setChangeDate(new Date());
//        deviceErrorLog.setLocalChangeDate(new Date());
        // Copy fields from input
        deviceErrorLog.setErrorMessage(inputDeviceError.getErrorMessage());
        deviceErrorLog.setStackTrace(inputDeviceError.getStackTrace());
        deviceErrorLog.setDeviceId(inputDeviceError.getDeviceId());
        deviceErrorLog.setUserName(inputDeviceError.getUserName());
        deviceErrorLog.setErrorAction(inputDeviceError.getErrorAction());
        deviceErrorLog.setLastUpdated(inputDeviceError.getLastUpdated());


        deviceErrorLog.setModified(true);

        create(deviceErrorLog);
        Log.i(getTableName(), "Created DeviceInfo for user: " + deviceErrorLog.getUserName());
        return deviceErrorLog;
    }

    /**
     * Update an existing DeviceInfo record
     */
    private DeviceErrorLog updateDeviceInfo(DeviceErrorLog existingDeviceError, DeviceErrorLog inputDeviceError) throws SQLException {
        if (existingDeviceError == null || inputDeviceError == null) {
            throw new IllegalArgumentException("Existing DeviceInfo and input DeviceInfo cannot be null");
        }
        // Update fields from input

        Log.i(getTableName(), "Creating Error Log fexisting device or user----------------------------: ");

        existingDeviceError.setChangeDate(new Date());
//        existingDeviceError.setLocalChangeDate(new Date());
        existingDeviceError.setErrorMessage(inputDeviceError.getErrorMessage());
        existingDeviceError.setStackTrace(inputDeviceError.getStackTrace());
        existingDeviceError.setDeviceId(inputDeviceError.getDeviceId());
        existingDeviceError.setUserName(inputDeviceError.getUserName());
        existingDeviceError.setLastUpdated(inputDeviceError.getLastUpdated());
        existingDeviceError.setErrorAction(inputDeviceError.getErrorAction());

        existingDeviceError.setModified(true);


        update(existingDeviceError);
        Log.i(getTableName(), "Updated DeviceInfo for user: " + existingDeviceError.getUserName());
        return existingDeviceError;
    }

    /**
     * Update or create DeviceInfo for a user, ensuring only one record per user
     */
//    public DeviceErrorLog updateOrCreateDeviceInfo(User user, DeviceErrorLog deviceError) throws SQLException {
//        if (user == null || deviceError == null) {
//            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
//        }
//        DeviceErrorLog existingDeviceErrorLog = getByDeviceId(deviceError.getDeviceId());
//        if (existingDeviceErrorLog != null) {
//            return updateDeviceInfo(existingDeviceErrorLog, deviceError);
//        } else {
//            return createDeviceErrorLog(deviceError.getDeviceId(), deviceError);
//        }
//    }

    /**
     * Delete duplicate DeviceInfo records for a user, keeping the latest
     */
    private void deleteDuplicates(DeviceErrorLog deviceId) throws SQLException {
        QueryBuilder<DeviceErrorLog, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(DeviceErrorLog.DEVICE_ID , deviceId.getDeviceId());
        queryBuilder.orderBy(DeviceInfo.LOGIN_TIMESTAMP, false);
        queryBuilder.limit(1L);
        DeviceErrorLog latest = queryBuilder.queryForFirst();
        if (latest != null) {
            DeleteBuilder<DeviceErrorLog, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DeviceErrorLog.DEVICE_ID , deviceId.getDeviceId())
                    .and()
                    .ne("id", latest.getId());
            int deleted = deleteBuilder.delete();
            if (deleted > 0) {
                Log.i(getTableName(), "Deleted " + deleted + " duplicate DeviceInfo records for user: " + deviceId.getUserName());
            }
        }
    }


    public synchronized DeviceErrorLog updateOrCreateDeviceInfo(User user, DeviceErrorLog deviceError) throws SQLException {
        if (user == null || deviceError == null) {
            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
        }
        if (deviceError.getDeviceId() == null) {
            deviceError.setDeviceId(user.getUserName() != null ? "NO_DEVICEID_" + user.getUserName() : "NO_DEVICEID");
        }

        // Always insert a new row
        DeviceErrorLog inserted = createDeviceErrorLog(deviceError.getDeviceId(), deviceError);

        // Keep only the 20 newest logs for this (deviceId, userName)
        try {
            pruneOldestLogs(deviceError.getDeviceId(), deviceError.getUserName(), 20);
        } catch (Exception ignore) {}

        return inserted;
    }

    /**
     * Deletes all but the newest `maxKeep` logs for the given device+user.
     */
    private void pruneOldestLogs(String deviceId, String userName, int maxKeep) throws SQLException {
        if (deviceId == null || userName == null || maxKeep < 1) return;

        // keep `maxKeep - 1` existing so that the next insert becomes the `maxKeep`th
        int keepExisting = Math.max(0, maxKeep - 1);

        dao.executeRaw(
                "DELETE FROM device_error " +
                        " WHERE id IN ( " +
                        "   SELECT id FROM device_error " +
                        "    WHERE userName = ? AND deviceId = ? " +
                        "    ORDER BY lastUpdated DESC, id DESC " +
                        "    LIMIT -1 OFFSET " + keepExisting +
                        " )",
                userName, deviceId
        );
    }


    // Make the whole upsert atomic and deterministic
//    public synchronized DeviceErrorLog updateOrCreateDeviceInfo(User user, DeviceErrorLog deviceError) throws SQLException {
//        if (user == null || deviceError == null) {
//            throw new IllegalArgumentException("User and DeviceInfo cannot be null");
//        }
//        // Fallback if deviceId is missing to avoid perpetual inserts
//        if (deviceError.getDeviceId() == null) {
//            deviceError.setDeviceId(user.getUserName() != null ? "NO_DEVICEID_" + user.getUserName() : "NO_DEVICEID");
//        }
//
//        DeviceErrorLog existing = getLatestByDeviceAndUser(deviceError.getDeviceId(), deviceError.getUserName());
//        DeviceErrorLog result = existing != null ? updateDeviceInfo(existing, deviceError)
//                : createDeviceErrorLog(deviceError.getDeviceId(), deviceError);
//
//        // Best-effort cleanup in case duplicates already exist (keeps newest)
//        try {
//            deleteDuplicatesByDeviceAndUser(deviceError.getDeviceId(), deviceError.getUserName());
//        } catch (Exception ignore) {}
//        return result;
//    }

    // Prefer latest if multiple exist
    private DeviceErrorLog getLatestByDeviceAndUser(String deviceId, String userName) throws SQLException {
        if (deviceId == null) return null;
        return queryBuilder()
                .orderBy("id", false)
                .where().eq(DeviceErrorLog.DEVICE_ID, deviceId)
                .and().eq(DeviceErrorLog.USERNAME, userName)
                .queryForFirst();
    }

    private void deleteDuplicatesByDeviceAndUser(String deviceId, String userName) throws SQLException {
        DeviceErrorLog latest = getLatestByDeviceAndUser(deviceId, userName);
        if (latest == null) return;
        DeleteBuilder<DeviceErrorLog, Long> db = dao.deleteBuilder();
        db.where()
                .eq(DeviceErrorLog.DEVICE_ID, deviceId)
                .and().eq(DeviceErrorLog.USERNAME, userName)
                .and().ne("id", latest.getId());
        db.delete();
    }

    @Override
    public DeviceErrorLog build() {
        DeviceErrorLog deviceErrorLog = super.build();
        deviceErrorLog.setUserName(ConfigProvider.getUser().getUserName());
//        deviceErrorLog.setLoginTimestamp(new Date());
        deviceErrorLog.setLastUpdated(new Date());
        return deviceErrorLog;
    }
}