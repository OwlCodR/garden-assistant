package com.garden_assistant.gardenassistant;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class GreenhouseTabsFragment extends Fragment {
    private View myInflatedView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private User person;

    private EditText editTextChangeName;
    private TextView textViewChangeName;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_greenhouse_tabs, container, false);
        person = new User();
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        viewPager = (ViewPager) myInflatedView.findViewById(R.id.viewpagerGreenhouse);
        user = mAuth.getCurrentUser();

        tabLayout = (TabLayout) myInflatedView.findViewById(R.id.tableLayoutGreenhouse);
        tabLayout.setupWithViewPager(viewPager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person = dataSnapshot.getValue(User.class);
                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.child("Greenhouses").getValue();
                if (map != null) {
                    person.setMapGreenhouses(map);
                    setupViewPager(viewPager);
                }
                else {
                    setupNullViewPager(viewPager, new GreenhouseFragment(),"Подключиться");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
            }
        });
        return myInflatedView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (user != null) {
            menu.findItem(R.id.action_exit).setVisible(true);   // Пункт "Выйти"
            menu.findItem(R.id.action_sign).setVisible(false);   // Пункт "Войти"
        }
        else {
            menu.findItem(R.id.action_exit).setVisible(false);
            menu.findItem(R.id.action_sign).setVisible(true);   // Пункт "Войти"
        }
        if (person.getCountGreenhouse() != 0) {
            menu.findItem(R.id.action_change_greenhouse_name).setVisible(true);   // Пункт "Изменить Название"
            menu.findItem(R.id.action_delete_greenhouse).setVisible(true);   // Пункт "Удалить"
            menu.findItem(R.id.action_add_greenhouse).setVisible(true);   // Пункт "Добавить"
        } else {
            menu.findItem(R.id.action_change_greenhouse_name).setVisible(false);   // Пункт "Изменить Название"
            menu.findItem(R.id.action_delete_greenhouse).setVisible(false);   // Пункт "Удалить"
            menu.findItem(R.id.action_add_greenhouse).setVisible(false);   // Пункт "Добавить"
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final String tabName = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

        if (id == R.id.action_change_greenhouse_name) {
            View layout = getLayoutInflater().inflate(R.layout.dialog_change_name, (ViewGroup) getActivity().findViewById(R.id.constraintLayoutChangeName));
            editTextChangeName = (EditText) layout.findViewById(R.id.editTextChangeName);
            textViewChangeName = (TextView) layout.findViewById(R.id.textViewPhoneNumber);
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
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    if (tabName != null)
                        textViewChangeName.setText("Измнеить название '" + tabName + "' на:");
                    mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + tabLayout.getSelectedTabPosition()).child("Name").setValue(editTextChangeName.getText().toString());

                    Fragment fragment = new GreenhouseTabsFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }).create();
            builder.show();
            return true;
        } else if (id == R.id.action_delete_greenhouse) {
            Snackbar.make(getView(), "Вы действительно хотите удалить " + "'" + tabName + "'?", Snackbar.LENGTH_LONG)
                    .setAction("Да", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDatabase.child("users").child(user.getUid()).child("countGreenhouse").setValue(person.getCountGreenhouse()-1);
                            if (tabLayout.getSelectedTabPosition() + 1 == person.getCountGreenhouse())
                                mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + tabLayout.getSelectedTabPosition()).removeValue();
                            else {
                                for (int i = tabLayout.getSelectedTabPosition(); i < person.getCountGreenhouse(); i++) {  // Смещение greenhouse_id в бд Firebase
                                    Log.d("bLog", "i == " + i);
                                    Log.d("bLog", "getSelectedTabPosition() == " + tabLayout.getSelectedTabPosition());
                                    if (i == 0) i++;
                                    mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + (i - 1)).child("Humidity").setValue(person.getGreenhouseHumidity("greenhouse_" + i));
                                    mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + (i - 1)).child("Name").setValue(person.getGreenhouseName("greenhouse_" + i));
                                    mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + (i - 1)).child("Temperature").setValue(person.getGreenhouseTemperature("greenhouse_" + i));
                                    if (i + 1 == person.getCountGreenhouse())
                                        mDatabase.child("users").child(user.getUid()).child("Greenhouses").child("greenhouse_" + i).removeValue();
                                }
                            }
                        }
                    }).show();
            return true;
        } else if (id == R.id.action_add_greenhouse) {
            Fragment fragment = new GreenhouseFragment();
            Bundle args = new Bundle();
            args.putString(GreenhouseFragment.GREENHOUSE_ID, "greenhouse_" + person.getCountGreenhouse());
            args.putBoolean(GreenhouseFragment.ADD_GREENHOUSE, true);
            fragment.setArguments(args);

            adapter.addFragment(fragment, "Добавить теплицу");
            viewPager.setAdapter(adapter);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupNullViewPager(ViewPager viewPager, Fragment fragment, String name) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        String greenhouse_id = "greenhouse_0";
        Bundle args = new Bundle();
        args.putString(GreenhouseFragment.GREENHOUSE_ID, greenhouse_id);
        args.putBoolean(GreenhouseFragment.ADD_GREENHOUSE, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Моя Теплица");

        fragment.setArguments(args);
        adapter.addFragment(fragment, name);
        viewPager.setAdapter(adapter);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        for (int i = 0; i < person.getCountGreenhouse(); i++)
        {
            String greenhouse_id = "greenhouse_" + i;

            Fragment fragment = new GreenhouseFragment();

            Bundle args = new Bundle();
            args.putString(GreenhouseFragment.GREENHOUSE_ID, greenhouse_id);
            args.putBoolean(GreenhouseFragment.ADD_GREENHOUSE, false);

            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Мои Теплицы");

            fragment.setArguments(args);

            // Добавление таба
            adapter.addFragment(fragment, person.getGreenhouseName(greenhouse_id));
        }

        viewPager.setAdapter(adapter);
    }
}
