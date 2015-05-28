package com.tona.mosquitosound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	private SeekBar seekBar;
	private TextView textFrequency;
	private TextView textStatus;
	private EditText editFrequency;
	private Button buttonPlay;
	private Button buttonStop;
	private Button buttonBackgroundPlay;
	private Button buttonBackgroundStop;
	private Spinner spinnerHighFrequency;

	private MosquitoSound ms;

	RelativeLayout layout_ad;// 広告表示用スペース
	AdView adView;
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

	private int status;// 0:停止,1:アプリ再生,2:バックグラウンド再生
	private final static String strStatus = "status:";
	private String[] strStatuses = {"stop", "play", "play in background"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-4176998183155624/9926503990"); // 注1
		adView.setAdSize(AdSize.BANNER);

		layout_ad = (RelativeLayout) findViewById(R.id.root);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(WC, WC);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL, 2);
		layout_ad.addView(adView, param);

		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		textFrequency = (TextView) findViewById(R.id.text_freqency);
		textStatus = (TextView) findViewById(R.id.text_status);
		editFrequency = (EditText) findViewById(R.id.edit_frequency);
		editFrequency.setRawInputType(Configuration.KEYBOARD_QWERTY);
		editFrequency.setImeOptions(EditorInfo.IME_ACTION_GO);
		editFrequency.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO 自動生成されたメソッド・スタブ
				if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					try {
						int input = Integer.parseInt(editFrequency.getText().toString());
						if (input >= 0 && input <= 20000) {
							seekBar.setProgress(input);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				int len = editFrequency.getText().toString().length();
				if (len > 0) {
					editFrequency.setSelection(len);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});

		seekBar = (SeekBar) findViewById(R.id.seekbar_freqency);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				editFrequency.setText("" + progress);
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.freq_list, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerHighFrequency = (Spinner) findViewById(R.id.spinner);
		spinnerHighFrequency.setAdapter(adapter);
		spinnerHighFrequency.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				String item = (String) spinnerHighFrequency.getItemAtPosition(arg2);
				seekBar.setProgress(Integer.parseInt(item));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自動生成されたメソッド・スタブ

			}
		});

		buttonPlay = (Button) findViewById(R.id.button_play);
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SoundService.ss == null) {
					ms = new MosquitoSound((double) seekBar.getProgress());
					ms.play();
					buttonPlay.setEnabled(false);
					buttonStop.setEnabled(true);
					buttonBackgroundPlay.setEnabled(false);
					buttonBackgroundStop.setEnabled(false);
					status = 1;
					textStatus.setText(strStatus + strStatuses[status]);
				} else {
					Toast.makeText(getApplicationContext(), "playing sound in background.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		buttonStop = (Button) findViewById(R.id.button_stop);
		buttonStop.setClickable(false);
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SoundService.ss == null) {
					ms.stop();
					ms = null;
					buttonPlay.setEnabled(true);
					buttonStop.setEnabled(false);
					buttonBackgroundPlay.setEnabled(true);
					buttonBackgroundStop.setEnabled(false);
					status = 0;
					textStatus.setText(strStatus + strStatuses[status]);
				} else {
					Toast.makeText(getApplicationContext(), "playing sound in background.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		buttonBackgroundPlay = (Button) findViewById(R.id.button_play_background);
		buttonBackgroundPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SoundService.ss == null) {
					Intent i = new Intent();
					i.putExtra("frequency", (double) seekBar.getProgress());
					i.setClass(getBaseContext(), SoundService.class);
					startService(i);
					buttonPlay.setEnabled(false);
					buttonStop.setEnabled(false);
					buttonBackgroundPlay.setEnabled(false);
					buttonBackgroundStop.setEnabled(true);
					status = 2;
					textStatus.setText(strStatus + strStatuses[status]);
				} else {
					Toast.makeText(getApplicationContext(), "failed to start sound.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		buttonBackgroundStop = (Button) findViewById(R.id.button_stop_background);
		buttonBackgroundStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SoundService.ss != null) {
					stopService(new Intent(getBaseContext(), SoundService.class));
					buttonPlay.setEnabled(true);
					buttonStop.setEnabled(false);
					buttonBackgroundPlay.setEnabled(true);
					buttonBackgroundStop.setEnabled(false);
					status = 0;
					textStatus.setText(strStatus + strStatuses[status]);
				} else {
					Toast.makeText(getApplicationContext(), "failed to stop sound.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (SoundService.ss != null) {
			buttonPlay.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonBackgroundPlay.setEnabled(false);
			buttonBackgroundStop.setEnabled(true);
			status = 2;
			textStatus.setText(strStatus + strStatuses[status]);
		}
	}
	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}
}
