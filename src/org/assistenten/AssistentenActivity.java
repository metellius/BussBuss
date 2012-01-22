package org.assistenten;

import java.security.InvalidKeyException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AssistentenActivity extends Activity {

	public static final String DEBUG = "BussBuss";
	private SubscriptionHandler subscriptionHandler;
	
	private final int SOLBERG = 2190311;
	private final int OKSENOYVEIEN = 2190040;
	private final int SANDVIKABUSS = 2190401;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.v(DEBUG, "onCreate");
		setContentView(R.layout.main);
		
		
		subscriptionHandler = new SubscriptionHandler() {
			
			@Override
			protected void onSubscriptionsUpdated() {
				Log.v(DEBUG, "onSubscriptionsUpdated");

				final TextView view = (TextView)findViewById(R.id.textView1);
				Route sandvikaBuss = new Route(subscriptionHandler);
				sandvikaBuss.setStartStation(SOLBERG);
				sandvikaBuss.addAlternative(SOLBERG, SANDVIKABUSS, 121, 2);
				sandvikaBuss.addAlternative(SANDVIKABUSS, OKSENOYVEIEN, 151, 3);
				sandvikaBuss.addAlternative(SANDVIKABUSS, OKSENOYVEIEN, 262, 4);
				
				sandvikaBuss.calculateRoute();
			}

			@Override
			protected void onUpdateStarted() {
				Log.v(DEBUG, "onUpdateStarted");

				final TextView view = (TextView)findViewById(R.id.textView1);
				view.setText("Loading...");
			}
		};
		
		subscriptionHandler.addSubcription(SOLBERG);
		subscriptionHandler.addSubcription(OKSENOYVEIEN);
		subscriptionHandler.addSubcription(SANDVIKABUSS);

		subscriptionHandler.updateSubscriptions();



	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.v(DEBUG, "onResume");
		//subscriptionHandler.updateSubscriptions();
	}
}