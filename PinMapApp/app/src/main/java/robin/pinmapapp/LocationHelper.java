package robin.pinmapapp;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Locatie helper
 */
public class LocationHelper {

    private Timer timer;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled;
    private boolean network_enabled;
    public int id = -1;

    // Timer stoppen
    public void cancelTimer() {
        timer.cancel();
        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);
    }

    public LocationHelper(int id){
        this.id=id;
    }

    // Locatie ophalen
    public boolean getLocation(Context context, LocationResult result) {
        locationResult = result;

        // LocationManager check
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // GPS provider check
        try{
            gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        // Network provider check
        try{
            network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        // Geen GPS en geen netwerken: stoppen
        if(!gps_enabled && !network_enabled)
            return false;

        // GPS listener
        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

        // Netwerk listener
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

        // Om de 20 seconden locatie opvragen
        timer = new Timer();
        timer.schedule(new LastLocationTask(), 20000);

        return true;
    }

    // GPS Listener
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    // Network listener
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    // Locatie update
    private class LastLocationTask extends TimerTask {
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null;
            Location gps_loc = null;

            if(gps_enabled)
                gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(network_enabled)
                net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Resultaten combineren indien mogelijk
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            // GPS Locatie gebruiken
            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }

            // Netwerk locatie gebruiken
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    // Locatie resultaat
    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }

}
