package com.garden_assistant.gardenassistant;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class GardenFragment extends ListFragment {

    private MySQLite db;
    private SQLiteDatabase database;
    private Cursor cursor;
    private TreeSet<Plant> treeSet_Plants;
    private DatabaseReference mDatabase;
    private Map<String, Map<String, Object>> vegetablesMap;
    private ArrayList<Plant> arrayListPlants;
    private long countChildren;

    private ArrayList<Plant> mapToArrayList(Map<String, Map<String, Object>> vegetablesMap) {
        ArrayList<Plant> arrayListPlants = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String nowDate = df.format(Calendar.getInstance().getTime());
        for (int i = 0; i < vegetablesMap.size(); i++) {
            String vegetable_id = "vegetable_" + i;
            if (vegetablesMap.containsKey(vegetable_id)) {
                String name = vegetablesMap.get(vegetable_id).get("Name").toString();
                String date = vegetablesMap.get(vegetable_id).get("Date").toString();
                int days = ((Long) (vegetablesMap.get(vegetable_id).get("Days"))).intValue();
                int image_id = ((Long) (vegetablesMap.get(vegetable_id).get("ImageID"))).intValue();
                arrayListPlants.add(new Plant(name, days, Plant.getDaysCount(nowDate, date), image_id, date));
            }
        }
        return arrayListPlants;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        treeSet_Plants = new TreeSet<>();
        db = new MySQLite(getActivity());
        database = db.getWritableDatabase();
        setTreeSetFromSQLite();

        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            // Обращение к БД Firebase //
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    vegetablesMap = (Map<String, Map<String, Object>>) dataSnapshot.child("Vegetables").getValue();
                    countChildren = (long) dataSnapshot.child("countVegetables").getValue();

                    if ((!isFirebaseEqualsToSQLite() && vegetablesMap != null) || (!isFirebaseEqualsToSQLite() && treeSet_Plants.size() != 0))
                        showDialog();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("FirebaseLog", "Произошла ошибка! Не удалось подключиться к Firebase:\n" + error.getMessage());
                }
            });
            // -- Обращение к БД Firebase  -- //
        }

        if (treeSet_Plants.size() == 0)
            showEmpty();

        VegetablesListAdapter adapter = new VegetablesListAdapter(getActivity(), treeSet_Plants);
        setListAdapter(adapter);
    }

    private void showEmpty() {
        Fragment fragment = new EmptyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean isFirebaseEqualsToSQLite() {
        ArrayList<Plant> arrayListPlants = new ArrayList<>(treeSet_Plants);
        if (vegetablesMap != null) {
            if (vegetablesMap.size() == arrayListPlants.size()) {

                for (int i = 0; i < arrayListPlants.size(); i++) {
                    String SQLitePlantName = arrayListPlants.get(i).getName();
                    boolean isVegetable = false;

                    for (int k = 0; k < vegetablesMap.size(); k++) {
                        String vegetable_id = "vegetable_" + k;
                        if (vegetablesMap.containsKey(vegetable_id)) {
                            if (vegetablesMap.get(vegetable_id).get("Name").toString().equals(SQLitePlantName)) {
                                isVegetable = true;
                                String FirebasePlantDate = vegetablesMap.get(vegetable_id).get("Date").toString();
                                String SQLitePlantDate = arrayListPlants.get(i).getDate();

                                if (SQLitePlantDate.equals(FirebasePlantDate)) {
                                    int FirebasePlantImageID = ((Long) (vegetablesMap.get(vegetable_id).get("ImageID"))).intValue();
                                    int SQLitePlantImageID = arrayListPlants.get(i).getImage_id();

                                    if (FirebasePlantImageID != SQLitePlantImageID) {
                                        Log.d("FirebaseLog", "Firebase NOT equals to SqLite:\nНе совпадают ImageID!");
                                        mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("ImageID").setValue(SQLitePlantDate);
                                        Log.d("FirebaseLog", "ImageID в Firebase был изменен");
                                    }
                                } else {
                                    Log.d("FirebaseLog", "Firebase NOT equals to SqLite:\nНе совпадают даты посадки!");
                                    mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).child("Date").setValue(SQLitePlantDate);
                                    Log.d("FirebaseLog", "Дата в Firebase была изменена");
                                }
                                break;
                            }
                        }
                    }
                    if (!isVegetable)
                        return false;
                }
            } else {
                Log.d("FirebaseLog", "Firebase NOT equals to SqLite:\nsize1 != size2");
                return false;
            }
            return true;
        } else {
            Log.d("FirebaseLog", "vegetablesMap == null");
            return false;
        }
    }

    private void showDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_firebase_access, (ViewGroup) getActivity().findViewById(R.id.constraintLayoutFirebaseAccess));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setNegativeButton("Загрузить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (vegetablesMap != null) {
                    Log.d("FirebaseLog", "В Firebase есть овощи, поэтому я загружу их в список");
                    treeSet_Plants.clear();
                    clearSQLiteTable();
                    loadPlantsFromFirebase();
                    Log.d("FirebaseLog", "Закончил загружать список из Firebase\nРазмер списка:" + treeSet_Plants.size());

                    Fragment fragment = new GardenFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Log.d("FirebaseLog", "Firebase не обнаружил овощей, поэтому покажем, что пусто");
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.dialog_empty, (ViewGroup) getActivity().findViewById(R.id.constraintLayoutFirebaseEmpty));

                    AlertDialog.Builder error_builder = new AlertDialog.Builder(getActivity());
                    error_builder.setView(layout);

                    error_builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();
                    error_builder.show();
                }
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (treeSet_Plants.size() != 0) {
                    Log.d("FirebaseLog", "В списке есть овощи, поэтому добавлю их в Firebase");
                    clearFirebase();
                    addAllPlantsToFirebase();
                    treeSet_Plants.clear();


                    Fragment fragment = new GardenFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                dialog.cancel();
            }
        }).create();
        builder.show();
    }

    private void clearFirebase() {
        mDatabase.child("users").child(user.getUid()).child("countVegetables").setValue(0);
        countChildren = 0;
        for (int i = 0; i < vegetablesMap.size(); i++) {
            String vegetable_id = "vegetable_" + i;
            mDatabase.child("users").child(user.getUid()).child("Vegetables").child(vegetable_id).removeValue();
        }
    }

    private void addAllPlantsToFirebase() {
        ArrayList<Plant> arrayListPlants = new ArrayList<>(treeSet_Plants);

        for (int i = 0; i < arrayListPlants.size(); i++) {
            mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + countChildren).child("Name").setValue(arrayListPlants.get(i).getName());
            mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + countChildren).child("Date").setValue(arrayListPlants.get(i).getDate());
            mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + countChildren).child("Days").setValue(arrayListPlants.get(i).getDays());
            mDatabase.child("users").child(user.getUid()).child("Vegetables").child("vegetable_" + countChildren).child("ImageID").setValue(arrayListPlants.get(i).getImage_id());
            countChildren++;
            mDatabase.child("users").child(user.getUid()).child("countVegetables").setValue(countChildren);
        }
    }

    private void loadPlantsFromFirebase() {
        for (int i = 0; i < vegetablesMap.size(); i++) {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String nowDate = df.format(Calendar.getInstance().getTime());

            String vegetable_id = "vegetable_" + i;

            String name = vegetablesMap.get(vegetable_id).get("Name").toString();
            String date = vegetablesMap.get(vegetable_id).get("Date").toString();
            int days = ((Long) (vegetablesMap.get(vegetable_id).get("Days"))).intValue();
            long last_days = Plant.getDaysCount(date, nowDate);
            int image_id = ((Long) (vegetablesMap.get(vegetable_id).get("ImageID"))).intValue();
            treeSet_Plants.add(new Plant(name, days, last_days, image_id, date));
            addPlantToSQLite(name, days, date, image_id);
        }
    }

    private void clearSQLiteTable() {
        database.delete(MySQLite.VEGETABLES_TABLE, null, null);
    }

    private void setTreeSetFromSQLite() {
        cursor = database.query(MySQLite.VEGETABLES_TABLE, null, null, null, null, null, null);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String nowDate = df.format(Calendar.getInstance().getTime());

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(MySQLite.KEY_ID);
            int vegetableIndex = cursor.getColumnIndex(MySQLite.KEY_VEGETABLE_NAME);
            int imageIdIndex = cursor.getColumnIndex(MySQLite.KEY_IMAGE_ID);
            int daysIndex = cursor.getColumnIndex(MySQLite.KEY_DAYS);
            int startDateIndex = cursor.getColumnIndex(MySQLite.KEY_START_DATE);

            do {
                Plant plant = new Plant();
                plant.setImage_id(cursor.getInt(imageIdIndex));
                plant.setName(cursor.getString(vegetableIndex));
                plant.setDays(cursor.getInt(daysIndex));
                plant.setLast_days(cursor.getInt(daysIndex) - plant.getDaysCount(cursor.getString(startDateIndex), nowDate));
                plant.setDate(cursor.getString(startDateIndex));
                treeSet_Plants.add(plant);

                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(vegetableIndex) +
                        ", days = " + cursor.getString(daysIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
    }

    private void addPlantToSQLite(String name, int days, String startDate, int image_id) {
        ContentValues cv = new ContentValues();
        cv.put(MySQLite.KEY_VEGETABLE_NAME, name);
        cv.put(MySQLite.KEY_DAYS, days);
        cv.put(MySQLite.KEY_START_DATE, startDate);
        cv.put(MySQLite.KEY_IMAGE_ID, image_id);
        database.insert(MySQLite.VEGETABLES_TABLE, null, cv);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle bundle = new Bundle();
        String title;
        ArrayList<Plant> arrayListPlants = new ArrayList<>(treeSet_Plants);
        String text = title = arrayListPlants.get(position).getName();
        Resources resource = getActivity().getResources();
        AllVegetablesFragment.toPlantFragmentFromList(getActivity(), this, bundle, title, text, resource);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.garden);
    }
}