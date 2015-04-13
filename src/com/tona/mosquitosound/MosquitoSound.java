package com.tona.mosquitosound;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MosquitoSound {
	private double frequency;
	private int amplifer;

	private final static int BITRATE = 16;
	private final static int SAMPLERATE = 44100; // [Hz]
	private AudioTrack audioTrack = null;

	short buffer[] = null;

	Timer timer;

	public MosquitoSound(double frequency) {
		this.frequency = frequency;
		buffer = new short[SAMPLERATE];
		double signal = 0;
		for (int i = 0; i < buffer.length; i++) {
			signal = generateSignal(i);
			buffer[i] = (short) (signal * Short.MAX_VALUE);
		}
		int bufferSizeInBytes = buffer.length * BITRATE / 8;
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, // [Hz]
		AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, // [bit]
		bufferSizeInBytes, // [byte]
		AudioTrack.MODE_STREAM);
	}

	public double generateSignal(int sample) {
		double t = (double) (sample) / SAMPLERATE;
		// y = a * sin (2PI * f * t), t = i/fs, 0 <= i < TotalSamples
		return Math.sin(2.0 * Math.PI * frequency * t);
	}

	public void play() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// write buffer
				if (audioTrack != null) {
					audioTrack.write(buffer, 0, buffer.length);
					audioTrack.play();
				}
			}
		}, 0, 1000);
	}

	public void stop() {
		if (timer != null && audioTrack != null) {
			audioTrack.pause();
			audioTrack.stop();
			audioTrack = null;
			timer.cancel();
			timer = null;
		}
	}
}
