package microphone.toggle;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.content.Context;
import android.media.AudioManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import microphone.toggle.R;

public class Main extends Activity {

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
	}

	public void onCreate(Bundle b) {
		super.onCreate(b);
		init();
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

}