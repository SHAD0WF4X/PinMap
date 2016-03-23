package robin.pinmapapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TransactionActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap googleMap;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();
        transaction = (Transaction)intent.getSerializableExtra("transaction");

        if(transaction.lon != null && transaction.lat != null) {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            findViewById(R.id.tvFound).setVisibility(View.INVISIBLE);
            findViewById(R.id.map).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.tvFound).setVisibility(View.VISIBLE);
            findViewById(R.id.map).setVisibility(View.INVISIBLE);
        }

        ((TextView)findViewById(R.id.tvName)).setText(transaction.name);
        ((TextView)findViewById(R.id.tvDateTime)).setText(transaction.dateTime);
        ((TextView)findViewById(R.id.tvDescription)).setText(transaction.description);
        ((TextView)findViewById(R.id.tvBedrag)).setText(transaction.bedrag);



    }


    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;


        map.addMarker(new MarkerOptions().position(new LatLng(transaction.lat, transaction.lon)));
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(transaction.lat, transaction.lon));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);

        map.moveCamera(center);
        map.animateCamera(zoom);


    }
}
