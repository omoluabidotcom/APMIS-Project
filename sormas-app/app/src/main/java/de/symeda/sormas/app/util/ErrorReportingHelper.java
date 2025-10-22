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

package de.symeda.sormas.app.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Date;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.device.errorLog.DeviceErrorLog;
import de.symeda.sormas.app.backend.device.info.DeviceInfo;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.FirebaseParameter;
import de.symeda.sormas.app.core.device.DeviceInfoService;
import de.symeda.sormas.app.rest.RetroProvider;

public class ErrorReportingHelper {

	/**
	 * Sends an exception report to Firebase Analytics.
	 */
	public static void sendCaughtException(Exception e) {
		FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
		crashlytics.setCustomKey(FirebaseParameter.CONNECTION_ID, String.valueOf(RetroProvider.getLastConnectionId()));
		crashlytics.setCustomKey(FirebaseParameter.SERVER_URL, ConfigProvider.getServerRestUrl());
		crashlytics.recordException(e);
	}

	/**
	 * Sends an exception report to Firebase Analytics.
	 * 
	 * @param entity
	 *            The entity object (e.g. a case or contact) if this error is associated with one
	 */
	public static void sendCaughtException(Exception e, AbstractDomainObject entity) {
		if (entity != null) {
			FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
			crashlytics.setCustomKey(FirebaseParameter.ENTITY_TYPE, entity.getClass().getSimpleName());
			crashlytics.setCustomKey(FirebaseParameter.ENTITY_UUID, entity.getUuid());
		}
		sendCaughtException(e);
	}


	/**
	 * Persist the latest device error in the local DB (keeps only the latest per device).
	 * 'action' should describe where it happened, e.g. "sync", "open_form".
	 */
	public static void logAndStoreDeviceError(String action, Exception e) {
		try {

			System.out.println("Log.i(getTableName(), Creating Error Log foffffffr user----------------------------: ");
			final Context ctx = DatabaseHelper.getContext();

			System.out.println("Context in Device Error Log----------------------------: " +  ctx) ;

			final User user = ConfigProvider.getUser();

			String deviceSerial = "";

			if (user != null) {

				DeviceInfoService deviceInfoService = new DeviceInfoService(ctx);
				DeviceInfo deviceInfo = deviceInfoService.collectDeviceInfo(user);
				deviceSerial =  deviceInfo.getDeviceId();

				System.out.println(deviceInfo.getDeviceId() + "gggggggggggggg"+  deviceInfo.getDeviceSerial());

				}

			final DeviceErrorLog log = DatabaseHelper.getDeviceErrorLogDao().build();
			log.setErrorMessage(e != null ? String.valueOf(e.getMessage()) : "Unknown error");
			log.setStackTrace(e != null ? Log.getStackTraceString(e) : null);
			log.setDeviceId(deviceSerial);
			log.setUserName(user != null ? user.getUserName() : null);
			log.setErrorAction(action);
			log.setLastUpdated(new Date());

			DatabaseHelper.getDeviceErrorLogDao().updateOrCreateDeviceInfo(user, log);
		} catch (Exception ignore) {
			// never fail the app due to logging
		}
	}

	/**
	 * Convenience method: report to Crashlytics and store as latest device error.
	 */
	public static void reportAndStore(String action, Exception e) {
		sendCaughtException(e);
		logAndStoreDeviceError(action, e);
	}
}
