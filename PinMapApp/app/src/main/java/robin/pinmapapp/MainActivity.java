package robin.pinmapapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IApiCallback{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;
    private RecyclerView recyclerView;
    private TransactionAdapter ta;
    public static final String BROADCAST_BUFFER_SEND_CODE = "com.example.SEND_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(broadcastBufferReceiver, new IntentFilter(BROADCAST_BUFFER_SEND_CODE));

        recyclerView = (RecyclerView) findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        ta = new TransactionAdapter(new ArrayList<Transaction>(), this);
        recyclerView.setAdapter(ta);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message) + " Our token is: " + sharedPreferences.getString(QuickstartPreferences.TOKEN, "TOKEN_ERROR"));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }

                String url = "http://www.nielslindeboom.me/api_json.php?";
                url += "gcm_id=" + sharedPreferences.getString(QuickstartPreferences.TOKEN, "TOKEN_ERROR");
                RetrieveDataTask t = new RetrieveDataTask(MainActivity.this);
                t.execute(url);


            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastBufferReceiver, new IntentFilter(BROADCAST_BUFFER_SEND_CODE));
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        unregisterReceiver(broadcastBufferReceiver);
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResult(String result) {
        ArrayList<Transaction> list = new ArrayList<>();
        int id;
        String name, description, bedrag, datetime;

        try{
            JSONArray items = new JSONArray(result);

            for (int i = 0; i < items.length(); i++) {
                JSONObject row = items.getJSONObject(i);
                id = row.getInt("id");
                name = row.getString("name");
                description = row.getString("description");
                bedrag = row.getString("amount");
                datetime = row.getString("date");


                Transaction t = new Transaction();
                t.bedrag = bedrag;
                t.id = id;
                t.dateTime = datetime;
                t.description = description;
                t.name = name;


                try{
                    Double lat = row.getDouble("lat");
                    Double lon = row.getDouble("lon");
                    t.lat = lat;
                    t.lon = lon;
                }catch (Exception e){}

                list.add(t);

            }

        }catch (Exception e){
            Log.d(TAG, e.toString());
        }

        ta.setItems(list);
        ta.notifyDataSetChanged();
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            Toast.makeText(context, "Refresh", Toast.LENGTH_SHORT).show();
            ta.clear();

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            String url = "http://www.nielslindeboom.me/api_json.php?";
            url += "gcm_id=" + sharedPreferences.getString(QuickstartPreferences.TOKEN, "TOKEN_ERROR");
            RetrieveDataTask t = new RetrieveDataTask(MainActivity.this);
            t.execute(url);

        }
    };

}
