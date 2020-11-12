package com.custom.app.ui.sampleBLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.app.R;
import com.custom.app.util.BitmapActivity;
import com.custom.app.util.DeviceItemAdapter;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.Arrays;
import java.util.UUID;

public class NewDevices extends AppCompatActivity implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback, DeviceItemAdapter.OnAdapterItemClickListener {

    private ProgressBar pgBar;
    private Menu mMenu;
    private RecyclerView mRecyclerView;
    private DeviceItemAdapter mAdapter;
    private BluetoothService mService;
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean mScanning = false;

    private UUID UUID_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_devices);

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.bluetoothServiceClass = BluetoothClassicService.class; //  BluetoothClassicService.class or BluetoothLeService.class

        config.context = getApplicationContext();
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.callListenersInMainThread = true;
        config.uuid = UUID_DEVICE; // For Classic
        config.uuidService = null; // For BLE
        config.uuidCharacteristic = null; // For BLE

        config.connectionPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH; // Automatically request connection priority just after connection is through.
        BluetoothService.init(config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pgBar = (ProgressBar) findViewById(R.id.pg_bar);
        pgBar.setVisibility(View.GONE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new DeviceItemAdapter(this, mBluetoothAdapter.getBondedDevices());
        mAdapter.setOnAdapterItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mService = BluetoothService.getDefaultInstance();
        mService.setOnScanCallback(this);
        mService.setOnEventCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mService.setOnEventCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            startStopScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startStopScan() {
        if (!mScanning) {
            mService.startScan();
        } else {
            mService.stopScan();
        }
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {
        Log.d(UartMainActivity.TAG, "onDataRead");
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(UartMainActivity.TAG, "onStatusChange: $status");
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
        if (status == BluetoothStatus.CONNECTED) {
            CharSequence colors[] = new CharSequence[]{"Try text", "Try picture"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                startActivity(new Intent(NewDevices.this, UartMainActivity.class));
                            } else {
                                startActivity(new Intent(NewDevices.this, BitmapActivity.class));
                            }
                        }
                    }
            );
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(UartMainActivity.TAG, "onDeviceName: $deviceName");
    }

    @Override
    public void onToast(String message) {
        Log.d(UartMainActivity.TAG, "onToast");
    }

    @Override
    public void onDataWrite(byte[] buffer) {
        Log.d(UartMainActivity.TAG, "onDataWrite");
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {

        Log.d(UartMainActivity.TAG, "onDeviceDiscovered: " + device.getName() + " - " + device.getAddress() + " - " + Arrays.toString(device.getUuids()));
        BluetoothDeviceDecorator dv = new BluetoothDeviceDecorator(device, rssi);
        int index = mAdapter.getDevices().indexOf(dv);
        if (index < 0) {
            mAdapter.getDevices().add(dv);
            mAdapter.notifyItemInserted(mAdapter.getDevices().size() - 1);
        } else {
            mAdapter.getDevices().get(index).setDevice(device);
            mAdapter.getDevices().get(index).setRSSI(rssi);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onStartScan() {
        Log.d(UartMainActivity.TAG, "onStartScan");
        mScanning = true;
        pgBar.setVisibility(View.VISIBLE);
        mMenu.findItem(R.id.action_scan).setTitle(R.string.action_stop);
    }

    @Override
    public void onStopScan() {
        Log.d(UartMainActivity.TAG, "onStopScan");
        mScanning = false;
        pgBar.setVisibility(View.GONE);
        mMenu.findItem(R.id.action_scan).setTitle(R.string.action_scan);
    }

    @Override
    public void onItemClick(BluetoothDeviceDecorator device, int position) {
        Bundle b = new Bundle();
        b.putString(BluetoothDevice.EXTRA_DEVICE, device.getDevice().getAddress());
        Intent result = new Intent();
        result.putExtras(b);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}