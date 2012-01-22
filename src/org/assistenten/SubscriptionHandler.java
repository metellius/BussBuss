package org.assistenten;

import java.util.ArrayList;

import android.util.Log;

public abstract class SubscriptionHandler {

	public class Subscription {
		
		private int id;
		private QueryResult result;
		
		public Subscription(int id) {
			this.id = id;
		}

		public QueryResult getResult() {
			return result;
		}

		public int getId() {
			return id;
		}

		public void updateSubscription() {
			RealTimeQuery query = new RealTimeQuery() {
				@Override
				protected void onPostExecute(QueryResult result) {
					super.onPostExecute(result);
					Subscription.this.result = result;
					subscriptionsLeftToUpdate -= 1;
					if (subscriptionsLeftToUpdate == 0)
						onSubscriptionsUpdated();
				}
			};
			query.execute(id);
		}

	}
	
	private ArrayList<Subscription> subscriptions;
	int subscriptionsLeftToUpdate;
	
	protected abstract void onSubscriptionsUpdated();
	protected abstract void onUpdateStarted();
	
	public SubscriptionHandler() {
		super();
		subscriptions = new ArrayList<Subscription>();
		subscriptionsLeftToUpdate = 0;
	}
	
	public void addSubcription(int subscription) {
		if (!subscriptions.contains(subscription))
			subscriptions.add(new Subscription(subscription));
	}
	
	public Subscription getSubscription(int id) {
		for (Subscription subscription : subscriptions) {
			if (subscription.getId() == id) {
				return subscription;
			}
		}
		return null;
	}
	
	public void updateSubscriptions() {
		onUpdateStarted();
		if (subscriptionsLeftToUpdate != 0) {
			Log.e(AssistentenActivity.DEBUG, "Error updateSubscriptions: already updating subscriptions");
			return;
		}
		subscriptionsLeftToUpdate = subscriptions.size();
		
		for (Subscription subscription : subscriptions) {
			subscription.updateSubscription();
		}
	}
	
}
