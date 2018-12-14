package richard.terremotosenelmundo;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTask.DownloadEqsInterface{

    public final static String SELECTED_EARTHQUAKE = "selected_earthquake";
    private ListView earthquakeListView;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        earthquakeListView = (ListView) findViewById(R.id.earthquake_list_view);

        if(Utils.isNetworkAvailable(this)){

            downloadEarthquakes();

        }else{

            getEarthquakesFromDb();

        }
    }

    private void getEarthquakesFromDb(){
        EqDbHelper eqDbHelper = new EqDbHelper(this);
        SQLiteDatabase database = eqDbHelper.getReadableDatabase();

        try{
             cursor = database.query(EqContract.EqColumns.TABLE_NAME, null, null, null, null, null,null);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        ArrayList<Earthquake> eqList = new ArrayList<>();

        while(cursor.moveToNext()){
            Long dateTime = cursor.getLong(EqContract.EqColumns.TIMESTAMP_COLUMN_INDEX);
            Double magnitude = cursor.getDouble(EqContract.EqColumns.MAGNITUDE_COLUMN_INDEX);
            String place = cursor.getString(EqContract.EqColumns.PLACE_COLUMN_INDEX);
            String longitude = cursor.getString(EqContract.EqColumns.LONGITUDE_COLUMN_INDEX);
            String latitude = cursor.getString(EqContract.EqColumns.LATITUDE_COLUMN_INDEX);

            eqList.add(new Earthquake(dateTime, magnitude, place, longitude, latitude));

        }

        cursor.close();
        fillEqList(eqList);

    }

    private void downloadEarthquakes(){
        DownloadEqsAsyncTask downloadEqsAsynTask = new DownloadEqsAsyncTask(this);
        downloadEqsAsynTask.delegate = this;
        downloadEqsAsynTask.execute();
    }

    @Override
    public void onEqsDownloaded(ArrayList<Earthquake> eqList) {
        fillEqList(eqList);
    }

    private void fillEqList(ArrayList<Earthquake> eqList){
        final EqAdapter eqAdapter = new EqAdapter(this, R.layout.eq_list_item, eqList);
        earthquakeListView.setAdapter(eqAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Earthquake selectedEarthquake = eqAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(SELECTED_EARTHQUAKE,selectedEarthquake);
                startActivity(intent);
            }
        });
    }
}
