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

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends Activity {

    final String TAG = "API DEMO";

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
                try {
                    String baseURL = "http://query.yahooapis.com/v1/public/yql";
                    String yql = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl1d1t1c1ohgvp2&e=.csv' and columns='symbol,price,date,time,change,open,high,low,volume,chgpct'";
                    String qs = URLEncoder.encode(yql, "UTF-8");
                    URL queryURL = new URL(baseURL + "?q=" + qs + "&format=json");
                    new GetQuoteTask().execute(queryURL);
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


    private void updateDisplay(Stock stock){
        ((TextView) findViewById(R.id.data_symbol)).setText(stock.getSymbol());
        ((TextView) findViewById(R.id.data_price)).setText(stock.getPrice().toString());
        ((TextView) findViewById(R.id.data_updated)).setText(stock.getDate());
        ((TextView) findViewById(R.id.data_high)).setText(stock.getHigh().toString());
        ((TextView) findViewById(R.id.data_low)).setText(stock.getLow().toString());
        ((TextView) findViewById(R.id.data_change)).setText(stock.getChange().toString());
        ((TextView) findViewById(R.id.data_open)).setText(stock.getOpen().toString());
        ((TextView) findViewById(R.id.data_volume)).setText(stock.getVolume().toString());
        ((TextView) findViewById(R.id.data_chgpct)).setText(stock.getPercent());
    }

    private class GetQuoteTask extends AsyncTask<URL, Integer, JSONObject> {

        final String TAG = "API DEMO ASYNCTASK";

        @Override
        protected JSONObject doInBackground(URL... urls) {

            String jsonString = "";

            //COLLECT STRING RESPONSES FROM API
            for (URL queryURL : urls) {
                try {
                    URLConnection conn = queryURL.openConnection();
                    jsonString = IOUtils.toString(conn.getInputStream());
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "Cannot establish URLConnection to " + queryURL.toString());
                    return null;
                }
            }
            Log.i(TAG, "Received Data: " + jsonString);

            //CONVERT API STRING RESPONSE TO JSONOBJECT
            JSONObject apiData;

            try {
                apiData = new JSONObject(jsonString);
            } catch (JSONException e) {
                apiData = null;
                Log.e(TAG, "Could not convert API response to JSON: " + jsonString);
                return apiData;
            }
            try {
                apiData = (apiData != null) ? apiData.getJSONObject("query").getJSONObject("results").getJSONObject("row") : null;
                Log.i(TAG, "API JSON data received: " + apiData);
            } catch (Exception e) {
                Log.e(TAG, "Could not parse data record from response: " + apiData.toString());
                apiData = null;
            }

            return apiData;
        }

        protected void onPostExecute(JSONObject apiData) {
            Stock result = new Stock(apiData);
            updateDisplay(result);
        }
    }
}
