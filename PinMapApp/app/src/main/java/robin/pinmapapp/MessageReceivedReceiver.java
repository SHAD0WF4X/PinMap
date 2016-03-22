package robin.pinmapapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class MessageReceivedReceiver extends BroadcastReceiver implements IApiCallback{
    public static final String BROAD = "BROADBROAD";


    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);
        final LocationHelper myLocation = new LocationHelper(id);
        LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult(){
            @Override
            public void gotLocation(Location location){
                if(location != null) {
                    // API call
                    String url = "http://www.nielslindeboom.me/api.php?";
                    url += "id=" + myLocation.id;
                    url += "&lon=" + location.getLongitude();
                    url += "&lat=" + location.getLatitude();

                    RetrieveDataTask t = new RetrieveDataTask(MessageReceivedReceiver.this);
                    t.execute(url);
                }
                myLocation.cancelTimer();
            }
        };

        myLocation.getLocation(context, locationResult);
    }

    @Override
    public void onResult(String result) {
        String r = "prop succes";
    }
}
