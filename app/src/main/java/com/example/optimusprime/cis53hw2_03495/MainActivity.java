package com.example.optimusprime.cis53hw2_03495;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        if(id == R.id.action_refresh)
        {
            class FetchWeatherTask extends AsyncTask<String, Void, Void> {

                private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


                @Override
                protected Void doInBackground(String...data)
                {

                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Will contain the raw JSON response as a string.
                    String forecastJsonStr = null;
                    String SanJose = "San%20Jose,US";
                    try
                    {
                        URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=\" + SanJose + \"&mode=json&units=metric&cnt=7");

                        // Create the request to OpenWeatherMap, and open the connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        // Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null)
                        {
                            // Nothing to do.
                            //forecastJsonStr = null;
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                            // But it does make debugging a *lot* easier if you print out the completed
                            // buffer for debugging.
                            buffer.append(line + "\n");

                        }

                        if (buffer.length() == 0)
                        {
                            // Stream was empty.  No point in parsing.
                            //forecastJsonStr = null;
                            return null;
                        }
                        forecastJsonStr = buffer.toString();
                        Log.d("JSON COMMAND: ", forecastJsonStr);
                        //weather.add(forecastJsonStr);
                    } catch (MalformedURLException | ProtocolException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error ", e);
                        // If the code didn't successfully get the weather data, there's no point in attempting
                        // to parse it.
                        forecastJsonStr = null;
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                Log.e(LOG_TAG, "Error closing stream", e);
                            }
                        }
                    }
                    return null;
                }
            } // END OF THE FORECAST INNER CLASS

            new FetchWeatherTask().execute("San%20Jose", "7");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
}
