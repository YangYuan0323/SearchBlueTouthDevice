package com.johnyang.searchbluetouthdevice;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tvDevices;
	private BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		tvDevices = (TextView) findViewById(R.id.tvDevices);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(pairedDevices.size()>0) {
			for (BluetoothDevice device : pairedDevices) {
				 tvDevices.setText(device.getName()+":"+device.getAddress()+"\n");
			}
		}
		
		//搜索到蓝牙的意图过滤器
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);
		//搜索蓝牙结束
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);
		
	}
	public void onClick(View view) {
		setProgressBarIndeterminateVisibility(true);
		setTitle("正在扫描...");
		//先看看是否正在搜索，如果正在搜索则取消掉。然后再开始搜索
		if(mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		//开始搜索
		mBluetoothAdapter.startDiscovery();
	}
	
	/**
	 * 蓝牙广播接收器
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				//判断设备是否被绑定，如果被没有被绑定则需要重新添加
				if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
					tvDevices.append(device.getName()+":"+device.getAddress()+"\n");
				}
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarVisibility(false);
				setTitle("搜索完成");
				
			}
			
			
		}
	};
}
