package com.nano.aditya.atombeammonitor.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;

/**
 * Created by adity on 8/31/2016.
 */
public class GraphFragment extends Fragment {
    private final String LOG_TAG = GraphFragment.class.getSimpleName();
    boolean initialize;
    //Animator myAnimation = new Animator();
    //Animator myAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_view);
    //Animation myAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_view);
    private final String dot = ".";
    RelativeLayout layout;
    //FetchPointUpdateTask pointData;
    ArrayList<ArrayList<Float>> doneArray = new ArrayList<ArrayList<Float>>();
    ArrayList<ArrayList<Float>> yetArray = new ArrayList<ArrayList<Float>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_graphical,container,false);
        layout = (RelativeLayout) rootView.findViewById(R.id.graph_layout);
        /*TextView a = new TextView(getActivity());
        a.setText(dot);
        layout.addView(a);*/
        FetchPointUpdateTask pointData = new FetchPointUpdateTask(layout);
        pointData.execute();
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.monitor, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            FetchPointUpdateTask pointData = new FetchPointUpdateTask(layout);
            pointData.execute();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class FetchPointUpdateTask extends AsyncTask<Void,String,String>{
        RelativeLayout layout;
        AnimatorSet s = new AnimatorSet();
        ArrayList<TextView> textVIEWS = new ArrayList<TextView>();
        ArrayList<TextView> yetText = new ArrayList<TextView>();

        public FetchPointUpdateTask(RelativeLayout layout) {
            this.layout = layout;
            initialize = true;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d(LOG_TAG,"doInBackground");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String pointDataJSONStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority("192.168.1.13:8080")
                        .appendPath("points");
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
                pointDataJSONStr = buffer.toString();
                //Log.i(LOG_TAG,pointDataJSONStr);
                updateData(pointDataJSONStr);
                return pointDataJSONStr;
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

        private void updateData(String pointDataJSONString)
            throws JSONException{
            ArrayList<Float> point;
            Log.d(LOG_TAG,"updateData");
            final String YET = "yet";
            final String DONE = "done";
            doneArray.clear();
            yetArray.clear();
            JSONObject progressDataJson = new JSONObject(pointDataJSONString);
            JSONArray doneJSONArray = progressDataJson.getJSONArray(DONE);
            JSONArray yetJSONArray = progressDataJson.getJSONArray(YET);//.getJSONArray(1);
            for (int i =0; i< doneJSONArray.length(); i++){
                point = new ArrayList<Float>();
                point.add((float)doneJSONArray.getJSONArray(i).getDouble(0));
                point.add((float)(-1*doneJSONArray.getJSONArray(i).getDouble(1)));
                doneArray.add(point);
            }
            for (int i =0; i< yetJSONArray.length(); i++){
                point = new ArrayList<Float>();
                point.add((float)yetJSONArray.getJSONArray(i).getDouble(0));
                point.add((float)(-1*yetJSONArray.getJSONArray(i).getDouble(1)));
                yetArray.add(point);
            }
            /*for (int i = 0; i <doneArray.size(); i++)
                Log.i(LOG_TAG,"["+String.valueOf(doneArray.get(i).get(0))+","+String.valueOf(doneArray.get(i).get(1))+"]");*/


        }

        /*@Override
        protected void onProgressUpdate(String... values) {
            Log.d(LOG_TAG,"onProgressUpdate");
            //super.onProgressUpdate(values);
        }*/

        @Override
        protected void onPostExecute(String s) {
            Log.d(LOG_TAG,"onPostExecute");
            if (initialize == true){
                Animation myAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_view);
                if (yetArray.size()!=0)
                    initialize = false;
                for(int i = 0; i<doneArray.size();i++){
                    textVIEWS.add(new TextView(getActivity()));
                    textVIEWS.get(i).setText(dot);
                    textVIEWS.get(i).setTextColor(Color.parseColor("#00FF00"));
                    textVIEWS.get(i).setX(doneArray.get(i).get(0)*10);
                    textVIEWS.get(i).setY(doneArray.get(i).get(1)*10);
                    textVIEWS.get(i).setTextSize(45);
                    layout.addView(textVIEWS.get(i));
                }
                Log.d(LOG_TAG,"YET::::");
                for(int i = doneArray.size(); i<doneArray.size()+yetArray.size()-1;i++){
                    textVIEWS.add(new TextView(getActivity()));
                    textVIEWS.get(i).setText(dot);
                    textVIEWS.get(i).setTextColor(Color.parseColor("#FF0000"));
                    textVIEWS.get(i).setX(yetArray.get(i-doneArray.size()).get(0)*10);
                    textVIEWS.get(i).setY(yetArray.get(i-doneArray.size()).get(1)*10);
                    textVIEWS.get(i).setTextSize(45);
                    //textVIEWS.get(i).setAlpha(0);
                    layout.addView(textVIEWS.get(i));
                }
                for(int i =0; i<textVIEWS.size();i++){
                    textVIEWS.get(i).startAnimation(myAnimation);
                }
            }

            super.onPostExecute(s);
        }

    }


    /*public class ProgressBarAnimation extends Animation {
        ArrayList<TextView> TextVIEW = new ArrayList<TextView>();
        //ArrayList<TextView> yetText = new ArrayList<TextView>();
        int i = 0;
        private RelativeLayout layout;
        private float from;
        private float  to;

        public ProgressBarAnimation(RelativeLayout layout, ArrayList doneText, ArrayList yetText) {
            super();
            this.layout = layout;
            doneText.addAll(yetText);
            this.TextVIEW = doneText;
            //this.yetText = yetText;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            if (i<TextVIEW.size()){
                layout.addView(TextVIEW.get(i));
                i++;
            }

            //percentText.setText(String.valueOf((int)value/100));
            //progressBar.setProgress((int) value);
        }

    }*/

}