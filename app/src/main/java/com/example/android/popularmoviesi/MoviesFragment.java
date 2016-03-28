package com.example.android.popularmoviesi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

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
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    //private ArrayAdapter<String> mMoviesAdapter;
    private GridView gridView;
    private int qtyMovies;
    private String []resultTitle;
    private String []resultUrlMovies;
    private String []resultOverview;
    private String []resultAverage;
    private String []resultRelease;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            resultUrlMovies = savedInstanceState.getStringArray("list");

        } else {
            // Probably initialize members with default values for a new instance
        }


        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putStringArray("list", resultUrlMovies);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Log.v("OnItemClick", "Movies position: " + position);

                Intent detail = new Intent(getActivity(), DetailActivity.class);
                detail.putExtra("URL", resultUrlMovies[position] );
                detail.putExtra("TITLE", resultTitle[position]);
                detail.putExtra("OVERVIEW", resultOverview[position]);
                detail.putExtra("AVERAGE", resultAverage[position]);
                detail.putExtra("RELEASE", resultRelease[position]);

                startActivity(detail);
            }
        });

        return rootView;
    }



    public class ImageListAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;

        private String[] imageUrls;

        public ImageListAdapter(Context context, String[] imageUrls) {
            super(context, R.layout.list_item_images_movies, imageUrls);

            this.context = context;
            this.imageUrls = imageUrls;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.list_item_images_movies, parent, false);
            }

            Picasso
                    .with(context)
                    .load(imageUrls[position])
                    .fit() // will explain later
                    .into((ImageView) convertView);


            return convertView;
        }
    }

    public void onResume(){
        super.onResume();
        updateMovies();
    }



    public void updateMovies(){

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(getString(R.string.pref_order_key),"popular");

        moviesTask.execute(order);



    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


       private String[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {


           // These are the names of the JSON objects that need to be extracted.
           final String JSON_LIST = "results";
           final String JSON_POSTER = "poster_path";
           final String JSON_TITLE = "original_title";
           final String JSON_OVERVIEW = "overview";
           final String JSON_AVERAGE = "vote_average";
           final String JSON_RELEASE = "release_date";


           JSONObject moviesJson = new JSONObject(moviesJsonStr);
           JSONArray moviesArray = moviesJson.getJSONArray(JSON_LIST);
           qtyMovies = moviesArray.length();

           String[] resultPoster = new String[qtyMovies];
           resultTitle = new String[qtyMovies];
           resultOverview = new String[qtyMovies];
           resultAverage = new String[qtyMovies];
           resultRelease = new String[qtyMovies];

            for(int i = 0; i < qtyMovies; i++) {


                // Get the JSON object representing the day
                JSONObject movie = moviesArray.getJSONObject(i);
                String overview = movie.getString(JSON_OVERVIEW);
                String release = movie.getString(JSON_RELEASE);
                String poster = movie.getString(JSON_POSTER);
                String title = movie.getString(JSON_TITLE);
                String average = movie.getString(JSON_AVERAGE);


                resultPoster[i] = poster;
                resultTitle[i] = title;
                resultOverview[i] = overview;
                resultAverage[i] = average;
                resultRelease[i] = release;
            }

            return resultPoster;

        }
        @Override
        protected String[] doInBackground(String... params) {

            //Log.v(LOG_TAG, "doInBackground settings: " + params[0]);

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
           String apiKey = "51a87f587e6133114d474587906cf2da";	//add API KEY
            int qtyMovies = 10;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0];
                //final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        //.appendQueryParameter(SORT_PARAM, sortByRates)
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                moviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movies string: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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


            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            resultUrlMovies= new String[qtyMovies];

            if (result != null) {

                for(int i=0; i < qtyMovies; i++) {
                    String moviesPoster = result[i];
                    if (moviesPoster != null) {

                        moviesPoster = "http://image.tmdb.org/t/p/w185/" + moviesPoster;
                        resultUrlMovies[i] = moviesPoster;

                        //Log.i(LOG_TAG, moviesPoster);
                    }
                }
                // New data is back from the server.  Hooray!
            }
            gridView.setAdapter(new ImageListAdapter(getActivity(), resultUrlMovies));
        }
    }
}


