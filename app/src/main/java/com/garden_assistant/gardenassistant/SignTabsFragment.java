package com.garden_assistant.gardenassistant;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class SignTabsFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private View myInflatedView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_sign_tabs, container, false);

        viewPager = (ViewPager) myInflatedView.findViewById(R.id.viewpagerSign);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) myInflatedView.findViewById(R.id.tableLayoutSign);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(this);

        setHasOptionsMenu(true);

        return myInflatedView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_exit).setVisible(false);
        menu.findItem(R.id.action_sign).setVisible(false);
        menu.findItem(R.id.action_change_greenhouse_name).setVisible(false);
        menu.findItem(R.id.action_delete_greenhouse).setVisible(false);
        menu.findItem(R.id.action_add_greenhouse).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Вход");
        adapter.addFragment(new RegisterFragment(), "Регистрация");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition())
        {
            case 0:
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setTitle(R.string.login);
            case 1:
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setTitle(R.string.register);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        return;
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        return;
    }
}
