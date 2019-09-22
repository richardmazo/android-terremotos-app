package richard.terremotosenelmundo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String strLongitude;
    String strLatitude;
    String strMagnitude;
    String strPlaceDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            strLongitude  = extras.getString(DetailActivity.LONGITUDE);
            strLatitude = extras.getString(DetailActivity.LATITUDE);
            strMagnitude = extras.getString(DetailActivity.MAGNITUDE);
            strPlaceDetail =  extras.getString(DetailActivity.DETAIL_PLACE);
          //  Toast.makeText(getApplicationContext(),"strLongitude " + strLongitude + strLatitude,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng places = new LatLng(Double.parseDouble(strLatitude), Double.parseDouble(strLongitude));
        mMap.addMarker(new MarkerOptions().position(places).title(strPlaceDetail).snippet(strMagnitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places,7));
    }
}
