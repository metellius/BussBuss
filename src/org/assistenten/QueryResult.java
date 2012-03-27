package org.assistenten;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;

public class QueryResult {

	private HashMap<Integer, ArrayList<Departure>> data;
	
	private class Departure {
		public ArrayList<Integer> departures;
		public int minutesToDeparture;
		public int directionRef;
		
	}
	
	public QueryResult() {
		super();
		data = new HashMap<Integer, ArrayList<Departure>>();
		Log.v(AssistentenActivity.DEBUG, "new QueryResult");
	}

	public void addResult(int lineId, int minutesToDeparture, int directionRef) {
		ArrayList<Departure> departures = data.get(lineId);
		if (departures == null)
			departures = new ArrayList<Departure>();
		
		Departure departure = new Departure();
		departure.minutesToDeparture = minutesToDeparture;
		departure.directionRef = directionRef;
		departures.add(departure);
		data.put(lineId, departures); //possibly redundant
	}
	
	public int getNext(int bussId, int fromMinutes, int directionRef) throws InvalidKeyException {
		Log.v(AssistentenActivity.DEBUG, "getNext: " + bussId + " fromMinutes " + fromMinutes);
		ArrayList<Departure> departures = data.get(bussId);
		if(departures == null) throw new InvalidKeyException();
		for (Departure departure : departures) {
			Log.v(AssistentenActivity.DEBUG, "getNext: considering " + departure);
			if (departure.minutesToDeparture >= fromMinutes && departure.directionRef == directionRef)
				return departure.minutesToDeparture;
		}
		return -1;
	}

	@Override
	public String toString() {
		String result = "";
		for (Integer bussId : data.keySet()) {
			result += bussId.toString() + " ";
			for (Departure departure : data.get(bussId)) {
				result += departure.toString() + " ";
			}
			result += "\n";
		}
		return result;
	}


}
