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

package de.symeda.sormas.app.core.device;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
 
import java.util.List;

import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.CampaignDao;
 import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.device.info.DeviceInfo;
import de.symeda.sormas.app.backend.user.User;

/**
 * Service to collect device information
 */
public class DeviceInfoService {

    private static final String TAG = "DeviceInfoService";
    private Context context;

    public DeviceInfoService(Context context) {
        this.context = context;
    }

    /**
     * Collect comprehensive device information for a user
     */
    public DeviceInfo collectDeviceInfo(User user) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setUser(user);
        deviceInfo.setLoginTimestamp(new Date());

 
        try {
            // Device Information
            collectDeviceBasicInfo(deviceInfo);
            
            // Storage Information
            collectStorageInfo(deviceInfo);
            
            // RAM Information
            collectRamInfo(deviceInfo);
            
            // Battery Information
            collectBatteryInfo(deviceInfo);
            
            // Network Information
            collectNetworkInfo(deviceInfo);

            Log.i(TAG, "Device information collected successfully for user: " + user.getUserName());
            return deviceInfo;

        } catch (Exception e) {
            Log.e(TAG, "Error collecting device information", e);
            return deviceInfo; // Return partial information
        }
    }

    /**
     * Collect basic device information
     */
    private void collectDeviceBasicInfo(DeviceInfo deviceInfo) {
        try {
            deviceInfo.setDeviceBrand(Build.MANUFACTURER);
            deviceInfo.setDeviceModel(Build.MODEL);
            deviceInfo.setAndroidVersion(Build.VERSION.RELEASE);
            deviceInfo.setApiLevel(Build.VERSION.SDK_INT);
            deviceInfo.setDeviceId(Build.ID);

            // Device Serial Number (requires permission)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) 
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceInfo.setDeviceSerial(Build.getSerial());
                    } else {
                        deviceInfo.setDeviceSerial(Build.SERIAL);
                    }
                } catch (SecurityException e) {
                    Log.w(TAG, "Could not access device serial number", e);
                    deviceInfo.setDeviceSerial("PERMISSION_DENIED");
                }
            } else {
                final Context ctx = DatabaseHelper.getContext();
                final String deviceSerial = ctx !=
                        null ? Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID) : null;

                deviceInfo.setDeviceSerial(deviceSerial);
            }
    int activecampaignsSize = DatabaseHelper.getCampaignDao().getAllActive().size();

            deviceInfo.setActiveCampaigns(DatabaseHelper.getCampaignDao().getAllActive().size());
            deviceInfo.setActiveFormCount(DatabaseHelper.getCampaignFormDataDao().queryActiveForAll().size());



 
        } catch (Exception e) {
            Log.e(TAG, "Error collecting basic device info", e);
        }
    }

    /**
     * Collect storage information
     */
    private void collectStorageInfo(DeviceInfo deviceInfo) {
        try {
            // Internal Storage
 
            StatFs internalStat = new StatFs(Environment .getDataDirectory().getPath());
             long internalBlockSize = internalStat.getBlockSizeLong();
            long internalTotalBlocks = internalStat.getBlockCountLong();
            long internalAvailableBlocks = internalStat.getAvailableBlocksLong();

            deviceInfo.setInternalStorageTotal(internalTotalBlocks * internalBlockSize);
            deviceInfo.setInternalStorageFree(internalAvailableBlocks * internalBlockSize);

            // External Storage (SD Card)
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                StatFs externalStat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long externalBlockSize = externalStat.getBlockSizeLong();
                long externalTotalBlocks = externalStat.getBlockCountLong();
                long externalAvailableBlocks = externalStat.getAvailableBlocksLong();

                deviceInfo.setExternalStorageTotal(externalTotalBlocks * externalBlockSize);
                deviceInfo.setExternalStorageFree(externalAvailableBlocks * externalBlockSize);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error collecting storage info", e);
        }
    }

    /**
     * Collect RAM information
     */
    private void collectRamInfo(DeviceInfo deviceInfo) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            deviceInfo.setRamTotal(memoryInfo.totalMem);

            // Alternative method to get RAM info from /proc/meminfo
            try {
                File file = new File("/proc/meminfo");
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("MemTotal:")) {
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 2) {
                                long memTotalKB = Long.parseLong(parts[1]);
                                deviceInfo.setRamTotal(memTotalKB * 1024); // Convert KB to bytes
                                break;
                            }
                        }
                    }
                    reader.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "Could not read /proc/meminfo", e);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error collecting RAM info", e);
        }
    }

    /**
     * Collect battery information
     */
    private void collectBatteryInfo(DeviceInfo deviceInfo) {
        try {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);


            
            if (batteryManager != null) {
                int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                deviceInfo.setBatteryLevel(batteryLevel);

                // Alternative method for older Android versions
                if (batteryLevel == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // Use Intent-based battery info for older versions
                    android.content.IntentFilter ifilter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
                    android.content.Intent batteryStatus = context.registerReceiver(null, ifilter);
                    if (batteryStatus != null) {
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


                        batteryLevel = (level * 100) / scale;
                        deviceInfo.setBatteryLevel(batteryLevel);

                        // Charging status
                        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                           status == BatteryManager.BATTERY_STATUS_FULL;
//                        deviceInfo.setIsCharging(isCharging);

                        // Battery status
                        switch (status) {
                            case BatteryManager.BATTERY_STATUS_CHARGING:
                                System.out.println("battery status ======  BATTERY_STATUS_CHARGING");
//                                deviceInfo.setBatteryStatus(DeviceInfo.BatteryStatus.CHARGING);
                                break;
                            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                                System.out.println("battery status ======  BATTERY_STATUS_DISCHARGING");

//                                deviceInfo.setBatteryStatus(DeviceInfo.BatteryStatus.DISCHARGING);
                                break;
                            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                                System.out.println("battery status ======  BATTERY_STATUS_NOT_CHARGING");

//                                deviceInfo.setBatteryStatus(DeviceInfo.BatteryStatus.NOT_CHARGING);
                                break;
                            case BatteryManager.BATTERY_STATUS_FULL:
                                System.out.println("battery status ======  BATTERY_STATUS_FULL");

//                                deviceInfo.setBatteryStatus(DeviceInfo.BatteryStatus.FULL);
                                break;
                            default:
                                System.out.println("battery status ======  default");

//                                deviceInfo.setBatteryStatus(DeviceInfo.BatteryStatus.UNKNOWN);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error collecting battery info", e);
        }
    }

    /**
     * Collect network information
     */
    private void collectNetworkInfo(DeviceInfo deviceInfo) {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
 
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    TelephonyManager telephonyManagerx =
                            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    if(telephonyManagerx != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            deviceInfo.setNetworkProvider(telephonyManagerx.getNetworkOperatorName());
                        }
                    }

                     switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            deviceInfo.setNetworkType(DeviceInfo.NetworkType.WIFI);
                            deviceInfo.setWifiConnected(true);
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            deviceInfo.setNetworkType(DeviceInfo.NetworkType.MOBILE);
                            deviceInfo.setWifiConnected(false);
 
                            break;
                        case ConnectivityManager.TYPE_ETHERNET:
                            deviceInfo.setNetworkType(DeviceInfo.NetworkType.ETHERNET);
                            deviceInfo.setWifiConnected(false);
                            break;
                        default:
                            deviceInfo.setNetworkType(DeviceInfo.NetworkType.UNKNOWN);
                            deviceInfo.setWifiConnected(false);
                    }

            } else {
                    deviceInfo.setNetworkType(DeviceInfo.NetworkType.UNKNOWN);
                    deviceInfo.setWifiConnected(false);
                    deviceInfo.setNetworkStrength(-1);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error collecting network info", e);
        }
    }

    /**
     * Save device information to database
     */
    public boolean saveDeviceInfo(DeviceInfo deviceInfo) {
        try {
            DatabaseHelper.getDeviceInfoDao().updateOrCreateDeviceInfo(deviceInfo.getUser(), deviceInfo);
            Log.i(TAG, "Device information saved successfully for user: " + deviceInfo.getUser().getUserName());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving device information", e);
            return false;
        }
    }
}