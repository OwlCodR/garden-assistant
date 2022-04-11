package com.garden_assistant.gardenassistant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.pulltorefresh.PullToRefresh;

public class BluetoothSelectListFragment extends Fragment implements PullToRefresh.OnRefreshListener {

    //
    //  Используется библиотека Android Bluetooth Library
    //  https://github.com/OmarAflak/Bluetooth-Library
    //

    private Bluetooth bluetooth;
    private ListView listView;
    private List<BluetoothDevice> pairedDevices;
    private PullToRefresh pull_to_refresh;
    private boolean isRegistered = false;
    private View myInflatedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.bluetooth_list2, container, false);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
        isRegistered = true;

        bluetooth = new Bluetooth(getActivity());
        bluetooth.enableBluetooth();

        pull_to_refresh = (PullToRefresh) myInflatedView.findViewById(R.id.pull_to_refresh2);
        listView =  (ListView) myInflatedView.findViewById(R.id.bluetooth_list2);

        pull_to_refresh.setListView(listView);
        pull_to_refresh.setOnRefreshListener(this);
        pull_to_refresh.setSlide(500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                Fragment fragment = new BluetoothConnectedListFragment();
                bundle.putInt("pos", position);
                fragment.setArguments(bundle);
                if (isRegistered) {
                    getActivity().unregisterReceiver(mReceiver);
                    isRegistered = false;
                }
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        addDevicesToList();
        return myInflatedView;
    }

    @Override
    public void onRefresh() {
        List<String> names = new ArrayList<>();
        for (BluetoothDevice d : bluetooth.getPairedDevices()){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, array);

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.removeViews(0, listView.getCount());
                listView.setAdapter(adapter);
                pairedDevices = bluetooth.getPairedDevices();
            }
        });
        pull_to_refresh.refreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered) {
            getActivity().unregisterReceiver(mReceiver);
            isRegistered = false;
        }
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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addDevicesToList();
                                listView.setEnabled(true);
                            }
                        });
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setEnabled(false);
                            }
                        });
                        Toast.makeText(getActivity(), "Включите Bluetooth", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        }
    };
}
