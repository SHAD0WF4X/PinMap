package robin.pinmapapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class PermActivity extends AppCompatActivity{
    private ArrayList<String> perms = new ArrayList<>();
    int perm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perm);

        perms.add(Manifest.permission.ACCESS_FINE_LOCATION);


        askPerm();

    }


    private void askPerm() {
        if (ContextCompat.checkSelfPermission(this,
                perms.get(perm))
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{perms.get(perm)},
                    perm);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }else{
            nextPerm();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        nextPerm();
    }

    private void nextPerm(){
        perm++;

        if (perm < perms.size()) {
            askPerm();
        }
        else if (perm == perms.size()) {
            nextActivity();
        }
        //test
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }



    private void nextActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
