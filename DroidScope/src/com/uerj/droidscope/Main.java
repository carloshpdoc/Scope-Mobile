package com.uerj.droidscope;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.uerj.droidscope.core.BluetoothService;
import com.uerj.droidscope.screens.GraphSensor;
import com.uerj.droidscope.utils.DeviceListActivity;
import com.uerj.droidscope.utils.Globals;


public class Main extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private String address;
	private BluetoothService mBluetoothService;
	private ToggleButton led;
	private Button graph;
	private SeekBar pwm;
	private TextView sensor;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(Globals.TAG, "+ ON CREATE +");
		setContentView(R.layout.main);
		pwm = (SeekBar)findViewById(R.id.pwm);
		led = (ToggleButton)findViewById(R.id.led);
		sensor = (TextView)findViewById(R.id.txt);
		graph = (Button)findViewById(R.id.btGraph);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e(Globals.TAG, "+ ON START +");
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, Globals.REQUEST_ENABLE_BT);
		} else {
			Log.i(Globals.TAG,"Criando o service!!");
			if(mBluetoothService==null) mBluetoothService = new BluetoothService(this,handler);
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Log.e(Globals.TAG, "+ ON RESUME +");
		if (mBluetoothService != null) {
			Log.i(Globals.TAG,"Entrou aqui o service!!");
			if (mBluetoothService.getState() == Globals.STATE_NONE) {
				mBluetoothService.start();
			}

			led.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						mBluetoothService.write("A".getBytes());
					}else{
						mBluetoothService.write("C".getBytes());
					}				
				}
			});

			pwm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					int value = seekBar.getProgress();
					String valueStr;
					if(value<10){
						valueStr = "R00"+String.valueOf(value);
					}else if(value<100){
						valueStr = "R0"+String.valueOf(value);
					}else{
						valueStr = "R"+String.valueOf(value);
					}
					mBluetoothService.write(valueStr.getBytes());
					Log.i(Globals.TAG,valueStr);
				}
			});

			graph.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), GraphSensor.class);
					startActivity(intent);
					finish();
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        if (mBluetoothService != null) mBluetoothService.stop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Globals.REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				address = data.getStringExtra(Globals.EXTRA_DEVICE_ADDRESS);
				if(address!=null){
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
					mBluetoothService.connect(device);
				}
			}
		case Globals.REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				mBluetoothService = new BluetoothService(this,handler);
			} else {
				Log.d(Globals.TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, Globals.REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	private void ensureDiscoverable() {
		Log.d(Globals.TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private final Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Globals.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.i(Globals.TAG,readMessage);
				if(msg.arg1>5)
					sensor.setText("Sensor: "+readMessage);
				break;
			}
		};
	};
}
