package org.assistenten;


import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class RealTimeQuery extends AsyncTask<Integer, Integer, QueryResult> {

	public static Long jsonToDate(String jsonString) {                                                                                      
		return new Date(Long.parseLong(jsonString.substring(6, jsonString.indexOf("+")))).getTime();                                        
	}
	
	@Override
	protected QueryResult doInBackground(Integer... params) {
		int stasjonId = params[0];
		Log.v(AssistentenActivity.DEBUG, "starting doInBackground for " + stasjonId);
		QueryResult result = new QueryResult();
		
		HttpClient httpclient = new DefaultHttpClient();
		String resultJson = null;
		try {
			URI uri = new URI("http://api-test.trafikanten.no/RealTime/GetRealTimeData/" + stasjonId);
			Log.v(AssistentenActivity.DEBUG, "http query -> " + uri.toString());

			HttpResponse response = httpclient.execute(new HttpGet(uri));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				resultJson = out.toString();
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONTokener tokener = new JSONTokener(resultJson);
		try {
			JSONArray departures = (JSONArray)tokener.nextValue();
			for (int i = 0; i < departures.length(); i++) {
				JSONObject departure = departures.getJSONObject(i);
				String line = departure.getString("PublishedLineName");
				String directionRef = departure.getString("DirectionRef");
				Long expectedDeparture = jsonToDate(departure.getString("ExpectedDepartureTime"));
				int minutes = (int)((expectedDeparture) - System.currentTimeMillis()) / (1000 * 60);
				result.addResult(Integer.parseInt(line), minutes, Integer.parseInt(directionRef));
				Log.v(AssistentenActivity.DEBUG, stasjonId + ": Line " + line + ": in " + minutes + " min");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	

}
