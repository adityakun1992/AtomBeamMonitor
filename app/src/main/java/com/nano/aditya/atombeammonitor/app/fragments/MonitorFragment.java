package com.nano.aditya.atombeammonitor.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nano.aditya.atombeammonitor.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonitorFragment extends Fragment {
    private String domain;
    private static String PREF_NAME = "prefs";
    private final String LOG_TAG = MonitorFragment.class.getSimpleName();
    private String msg = "Intialized, Not running";
    private int c_percent=0, c_point=100, no_of_points=100;
    ArrayList<Integer> point = new ArrayList<Integer>();

    private ProgressBar mProgress;
    private TextView percentText, completedPointsTextView, noofPointsTextView;

    public MonitorFragment() {
        //mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        //domain = settings.getString("contact1", "");


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.monitor, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item){
        int id = Item.getItemId();
        if (id == R.id.action_refresh){
            FetchUpdateTask progressReferesh =new FetchUpdateTask();
            progressReferesh.execute();

            return true;
        }
        return super.onOptionsItemSelected(Item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FetchUpdateTask progressReferesh =new FetchUpdateTask();
        progressReferesh.execute();
        View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);
        Resources res = getResources();
        //Drawable drawable = res.getDrawable(R.drawable.circular);
        mProgress = (ProgressBar) rootView.findViewById(R.id.circularProgressbar);
        mProgress.setProgress(c_percent);   // Main Progress
        percentText = (TextView) rootView.findViewById(R.id.textpercentage);
        percentText.setText(String.valueOf(c_percent));
        completedPointsTextView = (TextView) rootView.findViewById(R.id.currentpoints);
        completedPointsTextView.setText(String.valueOf(c_point));
        noofPointsTextView = (TextView) rootView.findViewById(R.id.maxpoints);
        noofPointsTextView.setText(String.valueOf(no_of_points));
        //mProgress.setSecondaryProgress(50); // Secondary Progress
        mProgress.setMax(10000); // Maximum Progress
        //mProgress.setProgressDrawable(drawable);
        //Log.v(LOG_TAG,)
        return rootView;
    }



    public class FetchUpdateTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchUpdateTask.class.getSimpleName();


        @Override
        protected void onPostExecute(String result) {
            ProgressBarAnimation animate = new ProgressBarAnimation(mProgress, mProgress.getProgress(),c_percent*100);
            animate.setDuration(500);
            mProgress.startAnimation(animate);
            //mProgress.setProgress(c_percent);
            percentText.setText(String.valueOf(c_percent));
            completedPointsTextView.setText(String.valueOf(c_point));
            noofPointsTextView.setText(String.valueOf(no_of_points));
        }

        @Override
        protected String doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String progressDataJSONStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority("192.168.1.7:8080");
                        /*.appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7")
                        .appendQueryParameter("APPID", "e2a4d593fc0429a5c29dd520e4d0db77")*/
                String myUrl = builder.build().toString();

                URL url = new URL(myUrl);


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                progressDataJSONStr = buffer.toString();
                //Log.i(LOG_TAG, progressDataJSONStr);
                updateDataFromJson(progressDataJSONStr);
                //Log.i(LOG_TAG, String.valueOf(progressData));
                return progressDataJSONStr;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally{
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
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }



        private void updateDataFromJson(String progressDataJSONStr)
                throws JSONException {

            final String C_PERCENTAGE = "current_percentage";
            final String MSG = "msg";
            final String POINT = "point";
            final String C_POINT = "current_point";
            final String NO_OF_POINTS = "noofpoints";
            point.clear();
            JSONObject progressDataJson = new JSONObject(progressDataJSONStr);
            JSONArray pointArray = progressDataJson.getJSONArray(POINT);
            c_percent = progressDataJson.getInt(C_PERCENTAGE);
            c_point = progressDataJson.getInt(C_POINT);
            no_of_points = progressDataJson.getInt(NO_OF_POINTS);
            msg = progressDataJson.getString(MSG);
            point.clear();
            for (int i =0; i< pointArray.length(); i++){
                point.add(pointArray.getInt(i));
            }
        }

    }

    public class ProgressBarAnimation extends Animation {
        private final String LOG_TAG = FetchUpdateTask.class.getSimpleName();
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            percentText.setText(String.valueOf((int)value/100));
            progressBar.setProgress((int) value);
        }

    }

}
