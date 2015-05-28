package com.tona.mosquitosound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
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
		editFrequency.addTextChangedListener(new TextWatcher() {

			private int currentLength;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO 自動生成されたメソッド・スタブ
				currentLength = s.toString().length();
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO 自動生成されたメソッド・スタブ
				if (s.toString().length() < currentLength) {
		            return;
		        }
				boolean unfixed = false;
				Object[] spanned = s.getSpans(0, s.length(), Object.class);
				if (spanned != null) {
					for (Object obj : spanned) {
						// UnderlineSpan での判定から getSpanFlags への判定に変更。
						if ((s.getSpanFlags(obj) & Spanned.SPAN_COMPOSING) == Spanned.SPAN_COMPOSING) {
							unfixed = true;
						}
					}
				}
				if (!unfixed) {
					try {
						int input = Integer.parseInt(editFrequency.getText().toString());
						if (input >= 0 && input <= 20000) {
							seekBar.setProgress(input);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					editFrequency.setSelection(editFrequency.getText().toString().length());
				}
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
