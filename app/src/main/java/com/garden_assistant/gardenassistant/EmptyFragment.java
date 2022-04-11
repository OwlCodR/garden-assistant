package com.garden_assistant.gardenassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class EmptyFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_empty, container, false);
        setHasOptionsMenu(true);
        return myInflatedView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (user != null) {
            menu.findItem(R.id.action_exit).setVisible(true);
            menu.findItem(R.id.action_sign).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_exit).setVisible(false);
            menu.findItem(R.id.action_sign).setVisible(true);
        }
        menu.findItem(R.id.action_change_greenhouse_name).setVisible(false);
        menu.findItem(R.id.action_delete_greenhouse).setVisible(false);
        menu.findItem(R.id.action_add_greenhouse).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
