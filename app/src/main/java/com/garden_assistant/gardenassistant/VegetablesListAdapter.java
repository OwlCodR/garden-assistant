package com.garden_assistant.gardenassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeSet;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class VegetablesListAdapter extends BaseAdapter implements View.OnClickListener {

    public Context context;
    public LayoutInflater lInflater;
    public ArrayList<Plant> arrayList_Plants;
    private MySQLite db;
    private DatabaseReference mDatabase;
    private AlertDialog.Builder builder;
    private ContentValues cv;
    private TextView GardenDays;
    private Map<String, Map<String, Object>> vegetablesMap;
    private long countChildren;

    VegetablesListAdapter(Context context, TreeSet<Plant> treeSet_Plants) {
        this.context = context;
        this.arrayList_Plants = new ArrayList<>(treeSet_Plants);
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.db = new MySQLite(context);
    }

    @Override
    public int getCount() {
        return arrayList_Plants.size();
    }

    @Override
    public Plant getItem(int position) {
        return arrayList_Plants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
                view = lInflater.inflate(R.layout.my_vegetables_list, parent, false);
        }

        Plant plant = getItem(position);
        ((TextView) view.findViewById(R.id.textViewVegetable)).setText(plant.getName());
        String s_days;
        GardenDays = (TextView) view.findViewById(R.id.textViewDays);
        if (plant.getLast_days() <= 0)
            GardenDays.setText("Плод созрел!");
        else if (plant.getLast_days() <= plant.getDays()) {
            s_days = setCorrectDateName(plant.getLast_days());
            GardenDays.setText("До созревания: " + plant.getLast_days() + s_days);
        }
        else {
            s_days = setCorrectDateName(plant.getLast_days() - plant.getDays());
            GardenDays.setText("До посадки: " + (plant.getLast_days() - plant.getDays()) + s_days);
        }
        ((ImageView) view.findViewById(R.id.imageViewVegetable)).setImageResource(plant.getImage_id());

        view.findViewById(R.id.imageButtonPlantDelete).setTag(position);
        view.findViewById(R.id.imageButtonPlantDelete).setOnClickListener(this);
        view.findViewById(R.id.imageButtonEdit).setTag(position);
        view.findViewById(R.id.imageButtonEdit).setOnClickListener(this);

        return view;
    }

    private String setCorrectDateName(Long last_days)
    {
        // WORD DAB
        // 'B' - last_symbol
        // 'A' - second_last_symbol
        String days = Long.toString(last_days);
        char last_symbol = days.charAt((Long.toString(last_days)).length() - 1);
        int last_number = Integer.parseInt(String.valueOf(last_symbol));
        char second_last_symbol;
        if (last_days >= 10.0) {
            second_last_symbol = days.charAt((Long.toString(last_days)).length() - 2);
            int second_last_number = Integer.parseInt(String.valueOf(second_last_symbol));
            if (last_number > 4 || last_number == 0)
                return " дней";
            else if (second_last_number == 1)
                return " дней";
            else if (last_number != 1)
                return " дня";
            else
                return " день";
        } else {
            if (last_number > 4)
                return " дней";
            else if (last_number != 1)
                return " дня";
            else
                return " день";
        }
    }

    public void remove(int position){
        arrayList_Plants.remove(arrayList_Plants.get(position));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonPlantDelete: {
                final int id = (Integer) view.getTag();
                final String name = getItem(id).getName();
                Snackbar.make(view, "Хотите убрать овощ из огорода?", Snackbar.LENGTH_LONG)
                        .setAction("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (user != null) {
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    // Обращение к БД Firebase //
                                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            vegetablesMap = (Map<String, Map<String, Object>>) dataSnapshot.child("Vegetables").getValue();
                                            countChildren = (long) dataSnapshot.child("countVegetables").getValue();

                                            if (vegetablesMap != null) {
                                                for (int i = 0; i < vegetablesMap.size(); i++) {
                                                    String vegetable_id = "vegetable_" + i;
                                                    if (vegetablesMap.get(vegetable_id).get("Name").equals(name)) {

                                                        Log.d("FirebaseLog", vegetable_id + " name = " + name);
                                                        mDatabase.child("users").child(user.getUid()).child("countVegetables").setValue(countChildren - 1);
                                                        if (i + 1 == vegetablesMap.size()) {
                                                            Log.d("FirebaseLog", (i + 1) +" == vegetablesMap.size()");
                                                            mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).removeValue();
                                                        }
                                                        else {
                                                            for (int j = i; j < vegetablesMap.size(); j++) {  // Смещение vegetable_id в бд Firebase
                                                                if (j == 0) j++;
                                                                Log.d("FirebaseLog", "vegetable_" + (j-1) + " = vegetable_" + j);
                                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + (j - 1)).child("Name")
                                                                        .setValue(vegetablesMap.get("vegetable_" + j).get("Name"));
                                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + (j - 1)).child("Days")
                                                                        .setValue(vegetablesMap.get("vegetable_" + j).get("Days"));
                                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + (j - 1)).child("Date")
                                                                        .setValue(vegetablesMap.get("vegetable_" + j).get("Date"));
                                                                mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + (j - 1)).child("ImageID")
                                                                        .setValue(vegetablesMap.get("vegetable_" + j).get("ImageID"));
                                                                if (j + 1 == vegetablesMap.size()) {
                                                                    Log.d("FirebaseLog", (j + 1) +" == vegetablesMap.size()");
                                                                    mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + j).removeValue();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.d("FirebaseLog", "Firebase не обнаружил овощей");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
                                        }
                                    });
                                    // -- Обращение к БД Firebase  -- //
                                }
                                SQLiteDatabase database = db.getWritableDatabase();
                                final int count = database.delete(MySQLite.VEGETABLES_TABLE, MySQLite.KEY_VEGETABLE_NAME + "='" + name + "'", null);
                                Snackbar.make(view,  "Этот овощ успешно убран из огорода", Snackbar.LENGTH_LONG).show();
                                remove(id);
                                notifyDataSetChanged();

                                    ((AppCompatActivity) context).getSupportActionBar()
                                            .setTitle("Мой Огород");

                                if (arrayList_Plants.size() == 0) {
                                    Fragment fragment = new EmptyFragment();
                                    FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }
                        }).show();
                break;
            }
            case R.id.imageButtonEdit:
            {
                final int id = (Integer) view.getTag();
                Snackbar.make(view, "Вы действительно хотите изменить дату посадки?", Snackbar.LENGTH_LONG)
                        .setAction("Да", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                View layout = lInflater.inflate(R.layout.dialog_change_date, (ViewGroup) ((Activity)context).findViewById(R.id.linearLayoutCalendar));

                                DatePicker mDatePicker = (DatePicker) layout.findViewById(R.id.datePicker);
                                builder = new AlertDialog.Builder(context);
                                builder.setView(layout);

                                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                Calendar today = Calendar.getInstance();
                                cv = new ContentValues();
                                final String date = today.get(Calendar.DAY_OF_MONTH) + "." + (today.get(Calendar.MONTH) + 1) + "." + today.get(Calendar.YEAR);
                                cv.put(MySQLite.KEY_START_DATE, date);
                                mDatePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                                        today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                                            @Override
                                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                cv.put(MySQLite.KEY_START_DATE, dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                                            }
                                        });

                                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase database = db.getWritableDatabase();
                                        database.update(MySQLite.VEGETABLES_TABLE, cv, MySQLite.KEY_VEGETABLE_NAME + "='" + getItem(id).getName() + "'", null);

                                        Fragment fragment = new GardenFragment();
                                        FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                }).create();


                                if (user != null) {
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    // Обращение к БД Firebase //
                                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            vegetablesMap = (Map<String, Map<String, Object>>) dataSnapshot.child("Vegetables").getValue();
                                            countChildren = (long) dataSnapshot.child("countVegetables").getValue();

                                            if (vegetablesMap != null) {
                                                for (int i = 0; i < vegetablesMap.size(); i++) {
                                                    String vegetable_id = "vegetable_" + i;
                                                    if (vegetablesMap.get(vegetable_id).containsValue(getItem(id).getName())) {
                                                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                                                        String nowDate = df.format(Calendar.getInstance().getTime());

                                                        long last_days = Plant.getDaysCount(vegetablesMap.get(vegetable_id).get("Date").toString(), nowDate);
                                                        if (last_days != getItem(id).getLast_days()) {
                                                            mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("Date").setValue(date);
                                                        } else
                                                            Log.d("FirebaseLog", "Даты посадки овоща с id = " + vegetable_id + " и " + getItem(id).getName() + " совпадают!");
                                                    } else
                                                        Log.d("FirebaseLog", "Овощ с id = " + vegetable_id + " не " + getItem(id).getName());
                                                }
                                            } else Log.d("FirebaseLog", "Firebase не обнаружил овощей");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
                                        }
                                    });
                                    // -- Обращение к БД Firebase  -- //
                                }
                                builder.show();
                            }
                        }).show();
                break;
            }
        }
    }
}
