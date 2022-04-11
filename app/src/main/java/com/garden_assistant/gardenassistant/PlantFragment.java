package com.garden_assistant.gardenassistant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class PlantFragment extends Fragment {
    private ImageView imageViewBackground;
    private TextView textViewName, textViewInfo, textViewSmallInfo, textViewRecommendTemperature, textViewDepth, textViewPlantDistance, textViewRowDistance, textViewPlantDays;
    private FloatingActionButton fab;
    private MySQLite db;
    private Bundle bundle;
    private String now_date;
    private DatabaseReference mDatabase;
    private Map<String, Map<String, Object>> vegetablesMap;
    private long countChildren;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            // Обращение к БД Firebase //
            mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    vegetablesMap = (Map<String, Map<String, Object>>) dataSnapshot.child("Vegetables").getValue();
                    countChildren = (long) dataSnapshot.child("countVegetables").getValue();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
                }
            });
            // -- Обращение к БД Firebase  -- //
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_plant, container,false);

        setHasOptionsMenu(true);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        now_date = df.format(Calendar.getInstance().getTime());

        imageViewBackground = (ImageView) myInflatedView.findViewById(R.id.imageViewPlantImage);
        textViewRecommendTemperature = (TextView) myInflatedView.findViewById(R.id.textViewPlantRecommendTemperature);
        textViewName = (TextView) myInflatedView.findViewById(R.id.textViewPlantName);
        textViewInfo = (TextView) myInflatedView.findViewById(R.id.textViewInfo);
        textViewSmallInfo = (TextView) myInflatedView.findViewById(R.id.textViewSmallInfo);
        textViewDepth = (TextView) myInflatedView.findViewById(R.id.textViewPlantDepth);
        textViewPlantDistance = (TextView) myInflatedView.findViewById(R.id.textViewPlantDistance);
        textViewRowDistance = (TextView) myInflatedView.findViewById(R.id.textViewRowDistance);
        textViewPlantDays = (TextView) myInflatedView.findViewById(R.id.textViewPlantDays);
        fab = (FloatingActionButton) myInflatedView.findViewById(R.id.fab_menu);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Добавить овощ в огород?", Snackbar.LENGTH_LONG)
                        .setAction("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SQLiteDatabase database = db.getWritableDatabase();
                                ContentValues contentValues = new ContentValues();

                                if (!isVegetableInGardenExist(database)) {
                                    setContentValues(contentValues);
                                    database.insert(MySQLite.VEGETABLES_TABLE, null, contentValues);
                                    setCorrectText(view);
                                } else {
                                    Snackbar.make(view, textViewName.getText().toString() + " уже есть в огороде!", Snackbar.LENGTH_LONG).show();
                                }

                                if (user != null) {
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    // Обращение к БД Firebase //
                                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            vegetablesMap = (Map<String, Map<String, Object>>) dataSnapshot.child("Vegetables").getValue();
                                            countChildren = (long) dataSnapshot.child("countVegetables").getValue();

                                            if (!isVegetableInGardenExist(vegetablesMap)) {
                                                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                                                String nowDate = df.format(Calendar.getInstance().getTime());

                                                String vegetable_id = "vegetable_" + countChildren;
                                                String name = textViewName.getText().toString();
                                                int imageID = bundle.getInt(AllVegetablesFragment.KEY_small_image);
                                                int days = Integer.parseInt((textViewPlantDays.getText().toString().split(" "))[0]);
                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("Date").setValue(nowDate);
                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("Name").setValue(name);
                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("ImageID").setValue(imageID);
                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("Days").setValue(days);
                                                mDatabase.child("users").child(user.getUid()).child("countVegetables").setValue(countChildren + 1);
                                                Log.d("FirebaseLog", "countChildren = " + countChildren);
                                            } else Log.d("FirebaseLog", "Такой овощ уже есть в Firebase, он не был добавлен");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
                                        }
                                    });
                                    // -- Обращение к БД Firebase  -- //
                                } else Log.d("FirebaseLog", "user == null Овощ не был добавлен в Firebase");
                            }
                        }).show();
            }
        });

        bundle = this.getArguments();
        String[] info = null;
        int id = 0;
        if (bundle != null) {
            id = bundle.getInt(AllVegetablesFragment.KEY_image);
            info = bundle.getStringArray(AllVegetablesFragment.KEY_info);
        }

        if (id != 0) {
            imageViewBackground.setImageResource(id);
        }


        if (info != null) {
            textViewName.setText(info[0]);
            textViewSmallInfo.setText(info[1]);
            textViewInfo.setText(info[2]);
            textViewRecommendTemperature.setText(info[3]);
            textViewDepth.setText(info[4]);
            textViewPlantDistance.setText(info[5]);
            textViewRowDistance.setText(info[6]);
            textViewPlantDays.setText(info[7]);
        }

        db = new MySQLite(getActivity());

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(textViewName.getText().toString());

        return myInflatedView;
    }

    private boolean isVegetableInGardenExist(Map<String, Map<String, Object>> vegetablesMap) {
        if (vegetablesMap != null) {
            for (int i = 0; i < vegetablesMap.size(); i++) {
                String vegetable_id = "vegetable_" + i;
                if (vegetablesMap.containsKey(vegetable_id)) {
                    String name = vegetablesMap.get(vegetable_id).get("Name").toString();
                    if (name.equals(textViewName.getText().toString())) {
                        return true;
                    }
                }
            }
        } else {
            Log.d("FirebaseLog", "В Firebase нет ни одного овоща!");
            return false;
        }
        return false;
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

    private void setContentValues(ContentValues contentValues)
    {
        contentValues.put(MySQLite.KEY_VEGETABLE_NAME, textViewName.getText().toString());
        contentValues.put(MySQLite.KEY_IMAGE_ID, bundle.getInt(AllVegetablesFragment.KEY_small_image));
        contentValues.put(MySQLite.KEY_DAYS, Integer.parseInt((textViewPlantDays.getText().toString().split(" "))[0]));
        contentValues.put(MySQLite.KEY_START_DATE, now_date);
    }

    private void setCorrectText(View view)
    {
        char last_symbol = textViewName.getText().toString().charAt(textViewName.getText().toString().length()-1);
        if (last_symbol == 'а' || textViewName.getText().toString().equals("Морковь"))
            Snackbar.make(view, textViewName.getText().toString() + " успешно добавлена в огород!", Snackbar.LENGTH_LONG).show();
        else if (last_symbol == 'и') Snackbar.make(view, textViewName.getText().toString() + " успешно добавлены в огород!", Snackbar.LENGTH_LONG).show();
        else Snackbar.make(view, textViewName.getText().toString() + " успешно добавлен в огород!", Snackbar.LENGTH_LONG).show();
    }

    public boolean isVegetableInGardenExist(SQLiteDatabase database)
    {
        Cursor cursor = database.query(MySQLite.VEGETABLES_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int name = cursor.getColumnIndex(MySQLite.KEY_VEGETABLE_NAME);
                if (cursor.getString(name).equals(textViewName.getText().toString()))
                    return true;
            } while (cursor.moveToNext());
        } else
            Log.d("sLog","0 rows");
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(textViewName.getText().toString());
    }
}