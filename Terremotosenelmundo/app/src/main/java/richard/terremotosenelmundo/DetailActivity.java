package richard.terremotosenelmundo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public final static String LONGITUDE = "longitude";
    public final static String LATITUDE = "latitude";
    public final static String MAGNITUDE = "magnitude";
    public final static String DETAIL_PLACE = "detail_place";

    Button btnMapa;
    TextView txtLongitude;
    TextView txtLatitude;
    TextView txtMagnitude;
    TextView txtDetailPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        Earthquake earthquake = extras.getParcelable(MainActivity.SELECTED_EARTHQUAKE);

        btnMapa = (Button) findViewById(R.id.btn_mapa);
        txtLongitude = (TextView) findViewById(R.id.eq_detail_longitude);
        txtLatitude = (TextView) findViewById(R.id.eq_detail_latitude);
        txtMagnitude = (TextView)findViewById(R.id.eq_detail_magnitude);
        txtDetailPlace = (TextView)findViewById(R.id.eq_detail_place);

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strLongitude = txtLongitude.getText().toString();
                String strLatitude = txtLatitude.getText().toString();
                String strMagnitude = txtMagnitude.getText().toString();
                String strDetailPlace = txtDetailPlace.getText().toString();

                //Toast.makeText(getApplicationContext(),"Latitude " + strLongitude + strLatitude,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra(LONGITUDE,strLongitude);
                intent.putExtra(LATITUDE,strLatitude);
                intent.putExtra(MAGNITUDE,strMagnitude);
                intent.putExtra(DETAIL_PLACE,strDetailPlace);


                startActivity(intent);
            }
        });

        if(earthquake != null){

            TextView magnitudeTextView = (TextView) findViewById(R.id.eq_detail_magnitude);
            TextView longitudeTextView = (TextView) findViewById(R.id.eq_detail_longitude);
            TextView latitudeTextView = (TextView) findViewById(R.id.eq_detail_latitude);
            TextView placeTextView = (TextView) findViewById(R.id.eq_detail_place);
            TextView dateTimeTextView = (TextView) findViewById(R.id.eq_detail_date_time);

            magnitudeTextView.setText(String.valueOf(earthquake.getMagnitude()));
            longitudeTextView.setText(String.valueOf(earthquake.getLongitude()));
            latitudeTextView.setText(String.valueOf(earthquake.getLatitude()));
            placeTextView.setText(String.valueOf(earthquake.getPlace()));
            dateTimeTextView.setText(getStringDateFromTimestamp(earthquake.getDateTime()));

        }
    }

    private String getStringDateFromTimestamp(long timestamp){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMMM/yyyy - H:mm:ss", Locale.getDefault());

        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);

    }

}
