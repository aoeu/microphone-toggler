package microphone.toggler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import microphone.toggler.R;

public class Main extends Activity {

	boolean hasNoMicrophoneMuteChangedBroadcaster = android.os.Build.VERSION.SDK_INT < 28;
	boolean shouldPollForMicrophoneMuteChanged = hasNoMicrophoneMuteChangedBroadcaster;

	Handler h = new Handler();
	Receiver r = new Receiver();

	AudioManager am;
	Button b;
	TextView t;

	void init() {
		setContentView(R.layout.main);

		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		t = (TextView)findViewById(R.id.microphoneStatus);
		b = (Button)findViewById(R.id.buttonForMutingMicrophone);

		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMicrophone();
			}
		});
		r.register();
	}

	public void onCreate(Bundle b) {
		super.onCreate(b);
		init();
	}

	void cleanup() {
		r.unregister();
		if (shouldPollForMicrophoneMuteChanged) {
			h.removeCallbacksAndMessages(null);
		}
		if (isMicrophoneMuted()) {
			unmuteMicrophone();
		}
	}

	public void onDestroy() {
		cleanup();
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		updateUI();
	}

	void updateUI() {
		if (isMicrophoneMuted()) {
			t.setText("muted");
			b.setText("unmute");
		} else {
			t.setText("unmuted");
			b.setText("mute");
		}
	}

	void toggleMicrophone() {
		if (isMicrophoneMuted()) {
			unmuteMicrophone();
		} else {
			muteMicrophone();
		}
		updateUI();
	}

	void muteMicrophone() {
		am.setMicrophoneMute(true);
	}

	void unmuteMicrophone() {
		am.setMicrophoneMute(false);
	}

	boolean isMicrophoneMuted() {
		return am.isMicrophoneMute();
	}

	void scheduleNextUIUpdate() {
		h.postDelayed(
			new UIUpdater(),
			randomizeMilliseconds(500, 2000)
		);
	}

	class UIUpdater implements Runnable {
		public void run() {
			updateUI();
			scheduleNextUIUpdate();
		}
	}

	long randomizeMilliseconds(long min, long max) {
		return (long) (java.lang.Math.random() * max) + min;
	}

	class Receiver extends BroadcastReceiver {
		// API-level 28 added AudioManager.ACTION_MICROPHONE_MUTE_CHANGED,
		// use its raw String value so we can still have backwards
		// compatibility for API-Level 25 (N / Android 7.1 / LineageOS 14).
		final String micMuteToggled = "android.media.action.MICROPHONE_MUTE_CHANGED";

		public void onReceive(Context c, Intent i) {
			boolean isMicMuteToggled = micMuteToggled.equals(i.getAction());
			if (isMicMuteToggled) {
				updateUI();
			}
		}

		public void register() {
			IntentFilter i = new IntentFilter();
			i.addAction(micMuteToggled);
			registerReceiver(this, i);
		}

		public void unregister() {
			unregisterReceiver(this);
		}
	};

}
