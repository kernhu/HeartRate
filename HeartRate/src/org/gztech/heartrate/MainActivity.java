package org.gztech.heartrate;

import org.gztech.heartrate.utils.HeartRateManager;
import org.gztech.heartrate.utils.OnMindsetChangeListener;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();
	private static final int SENSOR_TYPE_HEARTRATE = 65562;

	private TextView mHeartRate, mContext;
	private HeartRateManager mHeartRateManager;
	private SensorManager mSensorManager;
	private Sensor mHrSensor, mGravitySeneor;
	private Bundle bundle;
	private long lastTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {

				mHeartRateManager = new HeartRateManager();
				mHeartRateManager.setOnMindsetChangeListener(mindsetChange);
				mHeartRate = (TextView) stub.findViewById(R.id.text);
				mContext = (TextView) stub.findViewById(R.id.context);

			}
		});

		// get an instance of SensorManager
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// 65562 is lib2
		mHrSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE);
		// get an instance of Gravity Sensor
		mGravitySeneor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		mSensorManager.registerListener(mGravityListener, mGravitySeneor, 3);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mSensorManager.unregisterListener(mGravityListener, mGravitySeneor);
		mSensorManager.unregisterListener(mHeartRateListener, mHrSensor);

	}

	/**
	 * SensorEventListener of Heart Rate Sensor;
	 */
	SensorEventListener mHeartRateListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub

			long current = System.currentTimeMillis();
			if (current - lastTime >= 1000) {

				int rate = (int) event.values[0];

				if (rate > 0) {

					mHeartRateManager.addElement(rate);
					new UiThread(String.valueOf(rate)).start();
				}
				lastTime = current;
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};

	/**
	 * SensorEventListener of Gravity Sensor
	 */
	SensorEventListener mGravityListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub

			if (event.values[2] > (-9.80660) && event.values[2] < 9.80660) {

				mSensorManager.registerListener(mHeartRateListener, mHrSensor,
						3);
			} else {

				mSensorManager
						.unregisterListener(mHeartRateListener, mHrSensor);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};

	class UiThread extends Thread {

		String rate;

		public UiThread(String rate) {
			super();
			this.rate = rate;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			Message msg = new Message();
			msg.what = 0;
			bundle = new Bundle();
			bundle.putString("rate", rate);
			msg.setData(bundle);
			mHandler.sendMessage(msg);

		}
	}

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:

				bundle = msg.getData();
				String rate = bundle.getString("rate");
				mHeartRate.setText(rate);

				break;
			case 1:

				bundle = msg.getData();
				String mindset = bundle.getString("mindset");
				mContext.setText(mindset);

				break;
			}
		};
	};

	/**
	 * 抽象类，监听心情变化
	 */
	OnMindsetChangeListener mindsetChange = new OnMindsetChangeListener() {

		@Override
		public void onMindsetChange(int[] agrs, String mindset) {
			// TODO Auto-generated method stub

			Log.e(TAG, "max=" + agrs[0] + ",min=" + agrs[1] + ",riseCount="
					+ agrs[2] + ",fallCount=" + agrs[3] + ",riseRate="
					+ agrs[4] + ",fallRate=" + agrs[5] + ",mindset=" + mindset);

			Message msg = new Message();
			msg.what = 1;
			bundle = new Bundle();
			bundle.putString("mindset", mindset);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	};

	/*********
	 * 检查是否打开权限,用于测试，没多大用处
	 ***********/
	public boolean hasPermission() {

		PackageManager pm = getPackageManager();
		int permission = pm.checkPermission("android.permission.BODY_SENSORS",
				"org.gztech.heartrate");
		boolean isPer = permission == PackageManager.PERMISSION_GRANTED ? true
				: false;
		return isPer;
	}
}
