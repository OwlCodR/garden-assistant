package com.garden_assistant.gardenassistant;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class GreenhouseFragment extends Fragment implements View.OnClickListener {

    public static final String GREENHOUSE_ID = "GREENHOUSE_ID";
    public static final String ADD_GREENHOUSE = "ADD_GREENHOUSE";

    private TextView textViewTemperature, textViewHumidity, textViewMessage;
    private ConstraintLayout constraintLayoutRadar;
    private LinearLayout linearLayoutInfo;

    private DatabaseReference mDatabase;
    private View myInflatedView;
    private FirebaseAuth mAuth;
    private String[] temperature_min, temperature_max, humidity_min, humidity_max;
    private Button buttonConnectToGreenhouse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_greenhouse, container,false);
        textViewTemperature = (TextView) myInflatedView.findViewById(R.id.textViewTemperature);
        textViewHumidity = (TextView) myInflatedView.findViewById(R.id.textViewHumidity);
        textViewMessage = (TextView) myInflatedView.findViewById(R.id.textViewMessage);
        constraintLayoutRadar = (ConstraintLayout)  myInflatedView.findViewById(R.id.constraintLayout_Radar);
        linearLayoutInfo = (LinearLayout)  myInflatedView.findViewById(R.id.linearLayout_Info);
        buttonConnectToGreenhouse = (Button) myInflatedView.findViewById(R.id.buttonConnectTo);
        temperature_min = getResources().getStringArray(R.array.temperature_min);
        temperature_max = getResources().getStringArray(R.array.temperature_max);

        humidity_min = getResources().getStringArray(R.array.humidity_min);
        humidity_max = getResources().getStringArray(R.array.humidity_max);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();

        buttonConnectToGreenhouse.setOnClickListener(this);


        // Обращение к БД Firebase  //
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User person = dataSnapshot.getValue(User.class);
                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.child("Greenhouses").getValue();
                if (map != null) {
                    person.setMapGreenhouses(map);
                }
                if (person.getCountGreenhouse() == 0 || getArguments().getBoolean(ADD_GREENHOUSE)) {
                    linearLayoutInfo.setVisibility(View.GONE);
                    constraintLayoutRadar.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutInfo.setVisibility(View.VISIBLE);
                    constraintLayoutRadar.setVisibility(View.GONE);

                    textViewTemperature.setText(person.getGreenhouseTemperature(getArguments().getString(GREENHOUSE_ID)));
                    textViewHumidity.setText(person.getGreenhouseHumidity(getArguments().getString(GREENHOUSE_ID)));
                }
                check_data();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
            }
        });
        // -- Обращение к БД Firebase  -- //
        return myInflatedView;
    }

    public void check_data()
            // Функция анализирует данные (H - Humidity [Влажность], T - Temperature [Температура])
            // И выводит предупреждение для пользователя, если значения не в норме
    {
        int temperature = 0, humidity = 0;
        DateFormat df = new SimpleDateFormat("HH");
        String date = df.format(Calendar.getInstance().getTime());
        int time = Integer.parseInt(date);
        String text = "";

        if (textViewTemperature.getText().toString().length() == 5) {
            // T - двузначное число
            temperature = Integer.parseInt(textViewTemperature.getText().toString().substring(0, 3));
        } else if (textViewTemperature.getText().toString().length() == 4) {
            // T - двузначное число
            temperature = Integer.parseInt(textViewTemperature.getText().toString().substring(0, 2));
        }
        else if (textViewTemperature.getText().toString().length() == 3) {
            // T состоит из 1 знака
            temperature = Integer.parseInt(textViewTemperature.getText().toString().substring(0, 1));
        }
        if (textViewHumidity.getText().toString().length() == 4) {
            // H - двузначное число
            humidity = Integer.parseInt(textViewHumidity.getText().toString().substring(0, 3));
        } else if (textViewHumidity.getText().toString().length() == 3) {
            // H - двузначное число
            humidity = Integer.parseInt(textViewHumidity.getText().toString().substring(0, 2));
        }
        else if (textViewHumidity.getText().toString().length() == 2) {
            // H состоит из 1 знака
            humidity = Integer.parseInt(textViewHumidity.getText().toString().substring(0, 1));
        }
        if (time >= 22 && time <= 9 && temperature <= 10) {
            // Ночь, холод
            text = temperature_min[(int) (Math.random() * temperature_min.length)];
        } else if (time >= 22 && time <= 9 && temperature >= 25) {
            // Ночь, жара
            text = temperature_max[(int) (Math.random() * temperature_max.length)];
        } else if (time >= 9 && time <= 13 && temperature <= 15) {
            // Утро, холод
            text = temperature_min[(int) (Math.random() * temperature_min.length)];
        } else if (time >= 9 && time <= 13 && temperature >= 25) {
            // Утро, жара
            text = temperature_max[(int) (Math.random() * temperature_max.length)];
        } else if (time >= 13 && time <= 18 && temperature <= 18) {
            // День, холод
            text = temperature_min[(int) (Math.random() * temperature_min.length)];
        } else if (time >= 13 && time <= 18 && temperature >= 28) {
            // День, жара
            text = temperature_max[(int) (Math.random() * temperature_max.length)];
        } else if (time >= 18 && time <= 22 && temperature <= 18) {
            // Вечер, холод
            text = temperature_min[(int) (Math.random() * temperature_min.length)];
        } else if (time >= 18 && time <= 22 && temperature >= 26) {
            // Вечер, жара
            text = temperature_max[(int) (Math.random() * temperature_max.length)];
        }
        textViewMessage.setText(text);

        if (humidity >= 70) {
            textViewMessage.setText(text + "\n" + humidity_max[(int) (Math.random() * humidity_max.length)]);
            return;
        } else if (humidity <= 40) {
            textViewMessage.setText(text + "\n" + humidity_min[(int) (Math.random() * humidity_min.length)]);
            return;
        }
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.greenhouse);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonConnectTo:
            {
                Fragment fragment = new BluetoothScanListFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
