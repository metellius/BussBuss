package org.assistenten;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;

public class QueryResult {

	private HashMap<Integer, ArrayList<Integer>> data;
	
	public QueryResult() {
		super();
		data = new HashMap<Integer, ArrayList<Integer>>();
		Log.v(AssistentenActivity.DEBUG, "new QueryResult");
	}

	public void addResult(int bussId, ArrayList<Integer> departures) {
		data.put(bussId, departures);
	}
	
	public void addResult(int bussId, int departure) {
		ArrayList<Integer> departures = data.get(bussId);
		if (departures == null)
			departures = new ArrayList<Integer>();
		
		departures.add(departure);
		data.put(bussId, departures);
	}
	
	public int getNext(int bussId, int fromMinutes) throws InvalidKeyException {
		Log.v(AssistentenActivity.DEBUG, "getNext: " + bussId + " fromMinutes " + fromMinutes);
		ArrayList<Integer> departures = data.get(bussId);
		if(departures == null) throw new InvalidKeyException();
		for (Integer departure : departures) {
			Log.v(AssistentenActivity.DEBUG, "getNext: considering " + departure);
			if (departure >= fromMinutes)
				return departure;
		}
		return -1;
	}

	@Override
	public String toString() {
		String result = "";
		for (Integer bussId : data.keySet()) {
			result += bussId.toString() + " ";
			for (Integer minutes : data.get(bussId)) {
				result += minutes.toString() + " ";
			}
			result += "\n";
		}
		return result;
	}


}
