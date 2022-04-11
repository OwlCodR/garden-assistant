package com.garden_assistant.gardenassistant;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import me.aflak.bluetooth.Bluetooth;

public class BluetoothScanListFragment extends Fragment implements AdapterView.OnItemClickListener, Bluetooth.DiscoveryCallback {

    //
    //  Используется библиотека Android Bluetooth Library
    //  https://github.com/OmarAflak/Bluetooth-Library
    //

    private Bluetooth bluetooth;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private TextView state;
    private ProgressBar progress;
    private List<BluetoothDevice> devices;
    private View myInflatedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.bluetooth_list, container, false);
        listView = (ListView) myInflatedView.findViewById(R.id.bluetooth_list);
        state = (TextView) myInflatedView.findViewById(R.id.bluetooth_state);
        progress = (ProgressBar) myInflatedView.findViewById(R.id.bluetooth_progress);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bluetooth = new Bluetooth(getActivity());
        bluetooth.setDiscoveryCallback(this);

        bluetooth.scanDevices();
        progress.setVisibility(View.VISIBLE);
        state.setText("Сканирование...");
        listView.setEnabled(false);

        devices = new ArrayList<>();
        return myInflatedView;
    }

    private void setText(final String txt){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                state.setText(txt);
            }
        });
    }

    private void setProgressVisibility(final int id){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(id);
            }
        });
    }

    @Override
    public void onFinish() {
        setProgressVisibility(View.INVISIBLE);
        setText("Сканирование завершено!");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setEnabled(true);
            }
        });

        Fragment fragment = new BluetoothSelectListFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDevice(final BluetoothDevice device) {
        devices.add(device);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(device.getAddress() + " : " + device.getName());
            }
        });
    }

    @Override
    public void onPair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Paired!");

        Fragment fragment = new BluetoothSelectListFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUnpair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Paired!");
    }

    @Override
    public void onError(String message) {
        setProgressVisibility(View.INVISIBLE);
        setText("Error: "+message);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setProgressVisibility(View.VISIBLE);
        setText("Pairing...");
        bluetooth.pair(devices.get(position));
    }
}
