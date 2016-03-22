package robin.pinmapapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveDataTask extends AsyncTask<String, Void, String> {
    private IApiCallback callback;

    public RetrieveDataTask(IApiCallback callback){
        this.callback=callback;
    }

    private String resultFromUrlConnection(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        return resultFromUrlConnection(urls[0]);
    }

    protected void onPostExecute(String response) {
        try {
            Log.d("INFO", response);
            callback.onResult(response);
        }
        // In case the view was destroyed
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
