package richard.terremotosenelmundo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DownloadEqsAsyncTask extends AsyncTask<Void, Void, ArrayList<Earthquake>> {

    public DownloadEqsInterface delegate;

    private Context context;
    private static final String TAG = "MainActivity";

    public DownloadEqsAsyncTask(Context context) {
        this.context = context;
    }

    public interface DownloadEqsInterface{
        void onEqsDownloaded(ArrayList<Earthquake> eqList);
    }

    @Override
    protected ArrayList<Earthquake> doInBackground(Void... voids) {
        String eqData;
        ArrayList<Earthquake> eqList = null;
        try {
            eqData = downloadData(new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"));
            eqList = parseDataFromJson(eqData);
            saveEqsOnDatabase(eqList);
        }catch (IOException e){
            e.printStackTrace();
        }
        return eqList;
    }

    @Override
    protected void onPostExecute(ArrayList<Earthquake> eqList) {
        super.onPostExecute(eqList);

        delegate.onEqsDownloaded(eqList);

    }

    private void saveEqsOnDatabase(ArrayList<Earthquake> eqList){
        EqDbHelper eqDbHelper = new EqDbHelper(context);
        SQLiteDatabase database = eqDbHelper.getWritableDatabase();

        try{

            for (Earthquake earthquake : eqList){

                ContentValues contentValues = new ContentValues();
                contentValues.put(EqContract.EqColumns.MAGNITUDE, earthquake.getMagnitude());
                contentValues.put(EqContract.EqColumns.PLACE, earthquake.getPlace());
                contentValues.put(EqContract.EqColumns.TIMESTAMP, earthquake.getDateTime());
                contentValues.put(EqContract.EqColumns.LATITUDE, earthquake.getLatitude());
                contentValues.put(EqContract.EqColumns.LONGITUDE, earthquake.getLongitude());

                Log.i(TAG,contentValues.toString());

                database.insert(EqContract.EqColumns.TABLE_NAME, null, contentValues);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private ArrayList<Earthquake> parseDataFromJson(String eqsData){

        ArrayList<Earthquake> eqList = new ArrayList<>();

        try{

            JSONObject jsonObject = new JSONObject(eqsData);
            JSONArray featuresJsonArray = jsonObject.getJSONArray("features");

            for (int i = 0; i < featuresJsonArray.length(); i++){

                JSONObject featuresJsonObject = featuresJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject = featuresJsonObject.getJSONObject("properties");
                double magnitude = propertiesJsonObject.getDouble("mag");
                String place = propertiesJsonObject.getString("place");
                Long timestamp = propertiesJsonObject.getLong("time");
                JSONObject geometryJsonObject = featuresJsonObject.getJSONObject("geometry");
                JSONArray coordinatesJsonArray = geometryJsonObject.getJSONArray("coordinates");
                String longitude = coordinatesJsonArray.getString(0);
                String latitude = coordinatesJsonArray.getString(1);

                eqList.add(new Earthquake(timestamp, magnitude, place, longitude, latitude));

            }
        }catch (JSONException e){

            e.printStackTrace();

        }

        return eqList;
    }

    private String downloadData(URL url) throws IOException {

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        }catch(IOException e){
            e.printStackTrace();
        }finally{

            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }
}
