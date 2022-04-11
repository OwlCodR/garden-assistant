package com.garden_assistant.gardenassistant;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class BluetoothConnectedListFragment extends Fragment implements Bluetooth.CommunicationCallback {

    //
    //  Используется библиотека Android Bluetooth Library
    //  https://github.com/OmarAflak/Bluetooth-Library
    //

    private boolean isRegistered = false;
    private List<BluetoothDevice> pairedDevices;
    private Bluetooth bluetooth;
    private ListView listView;
    private View myInflatedView;
    private EditText editTextPhoneNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.bluetooth_list3, container, false);

        listView =  (ListView) myInflatedView.findViewById(R.id.bluetooth_list2);

        bluetooth = new Bluetooth(getActivity());
        bluetooth.enableBluetooth();
        bluetooth.setCommunicationCallback(this);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
        isRegistered = true;

        int pos = getArguments().getInt("pos");
        bluetooth.connectToDevice(bluetooth.getPairedDevices().get(pos));

        addDevicesToList();

        View layout = inflater.inflate(R.layout.dialog_add_number, (ViewGroup) getActivity().findViewById(R.id.constraintLayoutAddNumber));
        editTextPhoneNumber = (EditText) layout.findViewById(R.id.editTextAddPhoneNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                String phoneNumber = editTextPhoneNumber.getText().toString();
                if (editTextPhoneNumber.length() == 11 && bluetooth != null) {
                    bluetooth.send(phoneNumber);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Fragment fragment = new GreenhouseTabsFragment();
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }, 3000);
                        }
                    });
                } else Log.d("NiceLog", "editTextPhoneNumber.length()");
                dialog.cancel();
            }
        }).create();
        builder.show();

        return myInflatedView;
    }

    private void addDevicesToList(){
        pairedDevices = bluetooth.getPairedDevices();

        List<String> names = new ArrayList<>();
        for (BluetoothDevice d : pairedDevices){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, array);

        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered) {
            getActivity().unregisterReceiver(mReceiver);
            isRegistered = false;
        }
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        Log.d("niceLog","Успешно подключено к " + device.getName());
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        bluetooth.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetooth.connectToDevice(device);
                    }
                }, 3000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Fragment fragment = new BluetoothConnectedListFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (isRegistered) {
                            getActivity().unregisterReceiver(mReceiver);
                            isRegistered = false;
                        }

                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (isRegistered) {
                            getActivity().unregisterReceiver(mReceiver);
                            isRegistered = false;
                        }
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                }
            }
        }
    };
}
