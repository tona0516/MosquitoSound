package com.tona.mosquitosound;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
/**
 *
 * @author meem
 * @see
 */
public class SoundService extends Service {

	public static SoundService ss = null;
	private MosquitoSound ms;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		ms.stop();
		ms = null;
		if (ss != null) {
			ss = null;
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ss = this;
		double freq = intent.getDoubleExtra("frequency", 0);
		Log.d("freq", "" + freq);
		ms = new MosquitoSound(freq);
		ms.play();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
	}
}
