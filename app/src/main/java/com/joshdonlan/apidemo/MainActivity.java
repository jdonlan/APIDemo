package com.joshdonlan.apidemo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends Activity {

    final String TAG = "API DEMO";

    private MainActivity mMainActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button quoteButton = (Button) findViewById(R.id.quoteButton);
        quoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView symbolView = (TextView) findViewById(R.id.symbolEntry);
                String symbol = symbolView.getText().toString();
                try{
                    String baseURL = "http://query.yahooapis.com/v1/public/yql";
                    String yql = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl1d1t1c1ohgvp2&e=.csv' and columns='symbol,price,date,time,change,open,high,low,volume,chgpct'";
                    String qs = URLEncoder.encode(yql, "UTF-8");
                    URL queryURL = new URL(baseURL + "?q=" + qs + "&format=json");
                    GetQuoteTask getQuoteTask = new GetQuoteTask(mMainActivity);
                    getQuoteTask.execute(queryURL);
                } catch (Exception e) {
                    Log.e(TAG, "Invalid query for symbol: " + symbol);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDisplay(JSONObject apiData){
        try {
            Log.i(TAG,"Updating display.");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON data for display");
        }
    }

}

class GetQuoteTask extends AsyncTask<URL, Integer, JSONObject> {

    final String TAG = "API DEMO ASYNCTASK";

    private MainActivity mMainActivity;

    public GetQuoteTask(MainActivity mainActivity){
        this.mMainActivity = mainActivity;
    }

    @Override
    protected JSONObject doInBackground(URL... urls) {

        //COLLECT STRING RESPONSES FROM API
        StringBuffer responseBuffer = new StringBuffer();

        for(URL queryURL : urls){
            try{
                URLConnection conn = queryURL.openConnection();
                BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());

                byte[] contentBytes = new byte[1024];
                int bytesRead = 0;

                while((bytesRead = bin.read(contentBytes)) != -1){
                    String response = new String(contentBytes,0,bytesRead);
                    responseBuffer.append(response);
                }
                continue;
            } catch (Exception e){
                Log.e(TAG, "Cannot establish URLConnection to " + queryURL.toString());
                return null;
            }
        }
        Log.i(TAG,"Received Data: " + responseBuffer.toString());

        //CONVERT API STRING RESPONSE TO JSONOBJECT
        String json = responseBuffer.toString();
        JSONObject apiData;
        try{
            apiData = new JSONObject(json);
        } catch (JSONException e){
            apiData = null;
            Log.e(TAG, "Could not convert API response to JSON: " + json);
            return apiData;
        }
        try{
            apiData = (apiData != null)? apiData.getJSONObject("query").getJSONObject("results").getJSONObject("row") : null;
            Log.i(TAG, "API JSON data received: " + apiData);
        } catch (Exception e) {
            Log.e(TAG, "Could not parse data record from response: " + apiData.toString());
            apiData = null;
        }

        return apiData;
    }

    protected void onPostExecute(JSONObject apiData) {
        try {
            ((TextView) mMainActivity.findViewById(R.id.data_symbol)).setText(apiData.getString("symbol"));
            ((TextView) mMainActivity.findViewById(R.id.data_price)).setText(apiData.getString("price"));
            ((TextView) mMainActivity.findViewById(R.id.data_updated)).setText(apiData.getString("date") + " " + apiData.getString("time"));
            ((TextView) mMainActivity.findViewById(R.id.data_high)).setText(apiData.getString("high"));
            ((TextView) mMainActivity.findViewById(R.id.data_low)).setText(apiData.getString("low"));
            ((TextView) mMainActivity.findViewById(R.id.data_change)).setText(apiData.getString("change"));
            ((TextView) mMainActivity.findViewById(R.id.data_open)).setText(apiData.getString("open"));
            ((TextView) mMainActivity.findViewById(R.id.data_volume)).setText(apiData.getString("volume"));
            ((TextView) mMainActivity.findViewById(R.id.data_chgpct)).setText(apiData.getString("chgpct"));
        } catch (Exception e){
            Log.e(TAG,"Error updating display");
        }
    }
}
