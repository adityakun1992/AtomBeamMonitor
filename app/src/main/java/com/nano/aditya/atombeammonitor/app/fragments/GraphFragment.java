package com.nano.aditya.atombeammonitor.app.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nano.aditya.atombeammonitor.app.MainActivity;
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
    ArrayList<TextView> textVIEWS = new ArrayList<TextView>();
    private final String dot = ".";
    RelativeLayout layout;
    //FetchPointUpdateTask pointData;
    private ArrayList<ArrayList<Float>> olddoneArray = new ArrayList<ArrayList<Float>>();
    private ArrayList<ArrayList<Float>> oldyetArray = new ArrayList<ArrayList<Float>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graphical,container,false);
        layout = (RelativeLayout) rootView.findViewById(R.id.graph_layout);
        /*TextView a = new TextView(getActivity());
        a.setText(dot);
        layout.addView(a);*/
        FetchPointUpdateTask pointData = new FetchPointUpdateTask(layout);
        pointData.execute(((MainActivity)getActivity()).getURL,"new");
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
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
            pointData.execute(((MainActivity)getActivity()).getURL,"old");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class FetchPointUpdateTask extends AsyncTask<String,String,String>{
        RelativeLayout layout;
        ArrayList<ArrayList<Float>> doneArray;
        ArrayList<ArrayList<Float>> yetArray;

        boolean newFragment;
        ImageView imageView = new ImageView(getActivity());
        public FetchPointUpdateTask(RelativeLayout layout) {
            this.layout = layout;

            doneArray = new ArrayList<ArrayList<Float>>();
            yetArray = new ArrayList<ArrayList<Float>>();
            imageView.setImageResource(R.drawable.border);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (params[1]=="new"){
                newFragment = true;
            }
            // Will contain the raw JSON response as a string.
            String pointDataJSONStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority(params[0])
                        .appendPath("points");
                String myUrl = builder.build().toString();

                URL url = new URL(myUrl);


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
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
                return params[1];
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
            /*for (int i = 0; i <yetArray.size(); i++)
                Log.i(LOG_TAG,"["+String.valueOf(yetArray.get(i).get(0))+","+String.valueOf(yetArray.get(i).get(1))+"]");*/


        }

        /*@Override
        protected void onProgressUpdate(String... values) {
            Log.d(LOG_TAG,"onProgressUpdate");
            //super.onProgressUpdate(values);
        }*/

        @Override
        protected void onPostExecute(String newFragment) {

            Log.d(LOG_TAG,"OLD DONE ARRAY SIZE:"+ String.valueOf(olddoneArray.size()));
            Log.d(LOG_TAG,"OLD YET ARRAY SIZE:"+ String.valueOf(oldyetArray.size()));
            Log.d(LOG_TAG,"NEW DONE ARRAY SIZE:"+ String.valueOf(doneArray.size()));
            Log.d(LOG_TAG,"NEW YET ARRAY SIZE:"+ String.valueOf(yetArray.size()));

            if (newFragment == "new" || (olddoneArray.size()+oldyetArray.size()) != doneArray.size()+yetArray.size() ||
                    olddoneArray.size()>doneArray.size() || oldyetArray.size()<yetArray.size()){
                ArrayList<Animation> myAnimation = new ArrayList<Animation>();
                textVIEWS = new ArrayList<TextView>();
                layout.removeAllViews();
                int offsetTime = 0;
                try{
                    offsetTime = 2000/(doneArray.size()+yetArray.size());
                    //offsetTime=25;
                    for(int i = 0; i<doneArray.size();i++){
                        textVIEWS.add(new TextView(getActivity()));
                        textVIEWS.get(i).setText(dot);
                        textVIEWS.get(i).setTextColor(Color.parseColor("#00FF00"));
                        textVIEWS.get(i).setX(doneArray.get(i).get(0)*13);
                        textVIEWS.get(i).setY(doneArray.get(i).get(1)*13);
                        textVIEWS.get(i).setTextSize(45);
                        layout.addView(textVIEWS.get(i));
                        myAnimation.add(AnimationUtils.loadAnimation(getActivity(), R.anim.text_view));
                        //myAnimation.get(i).setDuration(offsetTime+10);
                        myAnimation.get(i).setStartOffset(i*offsetTime);
                    }
                    for(int i = doneArray.size(); i<doneArray.size()+yetArray.size();i++){
                        textVIEWS.add(new TextView(getActivity()));
                        textVIEWS.get(i).setText(dot);
                        textVIEWS.get(i).setTextColor(Color.parseColor("#FF0000"));
                        textVIEWS.get(i).setX(yetArray.get(i-doneArray.size()).get(0)*13);
                        textVIEWS.get(i).setY(yetArray.get(i-doneArray.size()).get(1)*13);
                        textVIEWS.get(i).setTextSize(45);
                        layout.addView(textVIEWS.get(i));
                        myAnimation.add(AnimationUtils.loadAnimation(getActivity(), R.anim.text_view));
                        //myAnimation.get(i).setDuration(offsetTime+100);
                        myAnimation.get(i).setStartOffset(i*offsetTime);
                    }
                    for(int i =0; i<textVIEWS.size();i++){
                        textVIEWS.get(i).startAnimation(myAnimation.get(i));
                    }
                }catch(ArithmeticException e){
                    Log.e(LOG_TAG,"Error", e);
                }
            }
            else{
                int new_printed = doneArray.size()-olddoneArray.size();
                Log.i(LOG_TAG,"new_printed="+String.valueOf(new_printed));
                if (new_printed>0){
                    for (int i=olddoneArray.size();i<new_printed+olddoneArray.size(); i++){
                        Log.i(LOG_TAG,"i="+String.valueOf(i));
                        textVIEWS.get(i).setTextColor(Color.parseColor("#00FF00"));
                        ObjectAnimator colorAnim = ObjectAnimator.ofInt(textVIEWS.get(i), "textColor",
                                Color.RED, Color.GREEN);
                        colorAnim.setDuration(500);
                        colorAnim.setEvaluator(new ArgbEvaluator());
                        colorAnim.start();

                    }
                }
            }

            olddoneArray = new ArrayList<ArrayList<Float>>(doneArray);
            oldyetArray = new ArrayList<ArrayList<Float>>(yetArray);
            //int offsetTime = 15;
            //Log.i(LOG_TAG,String.valueOf(offsetTime));


        }

    }
}
