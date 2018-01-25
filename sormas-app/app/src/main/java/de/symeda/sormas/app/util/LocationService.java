package de.symeda.sormas.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import de.symeda.sormas.app.R;

/**
 * Created by Mate Strysewske on 16.08.2017.
 *
 * See https://developer.android.com/guide/topics/location/strategies.html for a guide
 */
public final class LocationService {

    private static LocationService instance;
    public static LocationService instance() {
        return instance;
    }

    private Context context;

    private Location bestKnownLocation = null;

    private AlertDialog requestGpsAccessDialog;

    private LocationService() {
    }

    /**
     * Creates a base request for GPS and network every 10 minutes when more than 100m moved
     * @param context
     */
    public static void init(Context context) {
        if (instance != null) {
            throw new UnsupportedOperationException("instance already initialized");
        }
        instance = new LocationService();

        instance.context = context;

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return;
            }

            LocationListener gpsBaseLocationListener = new BestKnownLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*20, 1000, gpsBaseLocationListener, Looper.getMainLooper());

            LocationListener networkBaseLocationListener = new BestKnownLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60*20, 1000, networkBaseLocationListener, Looper.getMainLooper());

        } catch (SecurityException e) {
            Log.e(LocationService.class.getName(), "Error while initializing LocationService", e);
        }
    }

    int activeRequestCount = 0;
    LocationListener gpsActiveLocationListener = null;
    LocationListener networkActiveLocationListener = null;

    /**
     * Request network and GPS once a minute
     */
    public void requestActiveLocationUpdates(Activity callingActivity) {

        activeRequestCount++;

        if (!validateGpsAccessAndEnabled(callingActivity)) {
            return;
        }

        if (!isRequestingActiveLocationUpdates()) {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            networkActiveLocationListener = new BestKnownLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60, 10, networkActiveLocationListener, Looper.getMainLooper());
            gpsActiveLocationListener = new BestKnownLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 10, gpsActiveLocationListener, Looper.getMainLooper());
        }
    }

    public void removeActiveLocationUpdates() {

        if (activeRequestCount < 1) {
            throw new UnsupportedOperationException("removeActiveLocationUpdates was called although no active request exists");
        }

        activeRequestCount--;

        if (activeRequestCount == 0 && isRequestingActiveLocationUpdates()) {

            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            locationManager.removeUpdates(networkActiveLocationListener);
            networkActiveLocationListener = null;
            locationManager.removeUpdates(gpsActiveLocationListener);
            gpsActiveLocationListener = null;
        }
    }

    public boolean isRequestingActiveLocationUpdates() {
        return gpsActiveLocationListener != null;
    }

    public void requestFreshLocation(Activity callingActivity) {

        if (!validateGpsAccessAndEnabled(callingActivity)) {
            return;
        }

        // just for compiler reasons
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new BestKnownLocationListener(), Looper.getMainLooper());
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new BestKnownLocationListener(), Looper.getMainLooper());
    }

    public static final class BestKnownLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, instance.bestKnownLocation)) {
                instance.bestKnownLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            // TODO should we do something about this?
        }
    }

    public Location getLocation(Activity callingActivity) {

        if (!validateGpsAccessAndEnabled(callingActivity)) {
            return null;
        }

        return getLocation();
    }

    public Location getLocation() {

        // just for compiler reasons
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isBetterLocation(location, bestKnownLocation)) {
            bestKnownLocation = location;
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (isBetterLocation(location, bestKnownLocation)) {
                bestKnownLocation = location;
            }
        }

        return bestKnownLocation;
    }

    private static final int SIGNIFICANTLY_NEWER_DELTA = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        if (location == null) {
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > SIGNIFICANTLY_NEWER_DELTA;
        boolean isSignificantlyOlder = timeDelta < -SIGNIFICANTLY_NEWER_DELTA;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Checks whether the SORMAS app has the permission to access the phone's location
     */
    public boolean hasGpsAccess() {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks whether the phone's GPS is turned on
     * @return
     */
    public boolean hasGpsEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean validateGpsAccessAndEnabled(final Activity callingActivity) {

        if (!LocationService.instance().hasGpsAccess()) {
            buildAndShowRequestGpsAccessDialog(callingActivity);
            return false;
        }

        if (!LocationService.instance().hasGpsEnabled()) {
            AlertDialog turnOnGPSDialog = buildEnableGpsDialog(callingActivity);
            turnOnGPSDialog.show();
            return false;
        }

        return true;
    }

    /**
     * Shows a dialog to request GPS access from the user and makes sure that this dialog is only
     * displayed once.
     * @param callingActivity
     */
    private void buildAndShowRequestGpsAccessDialog(final Activity callingActivity) {
        if (requestGpsAccessDialog != null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
        builder.setCancelable(false);
        builder.setMessage(R.string.alert_gps_permission);
        builder.setTitle(R.string.alert_title_gps_permission);
        builder.setIcon(R.drawable.ic_perm_device_information_black_24dp);
        requestGpsAccessDialog = builder.create();

        requestGpsAccessDialog.setButton(AlertDialog.BUTTON_POSITIVE, callingActivity.getString(R.string.action_close_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestGpsAccessDialog = null;
                        Activity finishActivity = callingActivity;
                        do {
                            finishActivity.finish();
                            finishActivity = finishActivity.getParent();
                        } while (finishActivity != null);
                    }
                }
        );
        requestGpsAccessDialog.setButton(AlertDialog.BUTTON_NEGATIVE, callingActivity.getString(R.string.action_allow_gps_access),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(callingActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 9999);
                        requestGpsAccessDialog = null;
                    }
                }
        );

        requestGpsAccessDialog.show();
    }

    public AlertDialog buildEnableGpsDialog(final Activity callingActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
        builder.setCancelable(false);
        builder.setMessage(R.string.alert_gps);
        builder.setTitle(R.string.alert_title_gps);
        builder.setIcon(R.drawable.ic_location_on_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, callingActivity.getString(R.string.action_close_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity finishActivity = callingActivity;
                        do {
                            finishActivity.finish();
                            finishActivity = finishActivity.getParent();
                        } while (finishActivity != null);
                    }
                }
        );
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, callingActivity.getString(R.string.action_turn_on_gps),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        callingActivity.startActivity(gpsOptionsIntent);
                    }
                }
        );

        return dialog;
    }
}