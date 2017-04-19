package com.example.android.booklistingapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.booklistingapp.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private QueryUtils() {
    }

   public static ArrayList<String> fetchData(String requrl){
       URL url = createURL(requrl);
       String jsonResponse = null;

       try{
           jsonResponse = makeHTTPRequest(url);

       } catch(IOException e){
           Log.e(LOG_TAG, "Error closing connection", e);
       }

       ArrayList<String> publishList = extractList(jsonResponse);

       return publishList;

   }

    private static String makeHTTPRequest(URL url) throws IOException{

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static URL createURL(String requrl) {

       URL url = null;

       try {
           url = new URL(requrl);
       } catch(MalformedURLException e){
           Log.e(LOG_TAG, "Error creating url", e);

       }

      return url;
   }

    public static ArrayList<String> extractList(String json) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<String > publishedList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject list = new JSONObject(json);
            JSONArray featuresArray =  list.getJSONArray("items");

            for(int i=0; i<featuresArray.length(); i++){
                JSONObject properties = featuresArray.getJSONObject(i).getJSONObject("volumeInfo");
                String title = properties.getString("title");
                String subtitle = "";

                if(!properties.isNull("subtitle")){
                    subtitle = properties.getString("subtitle");
                } else {
                    subtitle = "No sub title.";
                }
                JSONArray authors = null;
                String authorNames = "";

                if(!properties.isNull("authors")) {
                    authors = properties.getJSONArray("authors");

                    authorNames = "";

                    for (int j = 0; j < authors.length(); j++) {
                        authorNames = authorNames + authors.getString(j) + ", ";
                    }


                }  else {
                    authorNames = "No authors listed";
                }

                publishedList.add("\n"+ title + "\n" + subtitle + "\n" + authorNames+"\n");
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing JSON results", e);
        }

        // Return the list of earthquakes
        return publishedList;
    }


   }