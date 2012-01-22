package org.assistenten;

import java.security.InvalidKeyException;
import java.util.ArrayList;

import android.util.Log;

public class Route {

	private class RouteAlternative {
		public int toStation;
		public int expectedTravelTime;
		public int fromStation;
		public int bussTogId;
		
		@Override
		public String toString() {
			return "[ " + fromStation + " -> " + toStation + " ] " + bussTogId + " (" + expectedTravelTime + "min)";
		}
	}
	private SubscriptionHandler subscriptionHandler;
	private int startStation;
	private ArrayList<RouteAlternative> routeAlternatives;
	
	public int getStartStation() {
		return startStation;
	}

	public void setStartStation(int startStation) {
		this.startStation = startStation;
	}

	public Route(SubscriptionHandler handler) {
		this.subscriptionHandler = handler;
		this.routeAlternatives = new ArrayList<Route.RouteAlternative>();
	}
	
	public void addAlternative(int fromStation, int toStation, int bussTogId, int expectedTravelTime) {
		RouteAlternative ra = new RouteAlternative();
		ra.toStation = toStation;
		ra.expectedTravelTime = expectedTravelTime;
		ra.fromStation = fromStation;
		ra.bussTogId = bussTogId;
		routeAlternatives.add(ra);
	}
	
	private ArrayList<RouteAlternative> getAlternativesFrom(int station) {
		ArrayList<RouteAlternative> ret = new ArrayList<RouteAlternative>();
		for (RouteAlternative ra : routeAlternatives) {
			if (ra.fromStation == station)
				ret.add(ra);
		}
		return ret;
	}
	
	
	public void calculateRoute() {
		Log.v(AssistentenActivity.DEBUG, "calculateRoute");
		for (RouteAlternative alternative : getAlternativesFrom(startStation)) {
			traverseAlternatives(alternative, alternative.toString() + " --- ", alternative.expectedTravelTime);
		}
	}
	
	public void traverseAlternatives(RouteAlternative ra, String currentRoute, int totalTravelTime) {
		ArrayList<RouteAlternative> choices = getAlternativesFrom(ra.toStation);
		if (choices.isEmpty())
			Log.v(AssistentenActivity.DEBUG, totalTravelTime + " min: " + currentRoute);
		else {
			for (RouteAlternative alternative : choices) {
				Log.v(AssistentenActivity.DEBUG, "considering " + alternative);
				int nextSuitableDeparture = -1;
				try {
					nextSuitableDeparture = subscriptionHandler.getSubscription(alternative.fromStation).getResult().getNext(alternative.bussTogId, totalTravelTime);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				}
				String newRoute = new String(currentRoute);
				newRoute += alternative.toString() + "nexsuit" + nextSuitableDeparture + " --- ";
				traverseAlternatives(alternative, newRoute, totalTravelTime + alternative.expectedTravelTime);
			}
		}
	}
	
}
