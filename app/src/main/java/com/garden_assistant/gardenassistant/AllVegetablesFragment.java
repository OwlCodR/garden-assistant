package com.garden_assistant.gardenassistant;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class AllVegetablesFragment extends ListFragment {

    public static final String KEY_info = "info";
    public static final String KEY_image = "image";
    public static final String KEY_small_image = "small_image";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        String[] vegetables = getResources().getStringArray(R.array.vegetables);
        Arrays.sort(vegetables);

        List<HashMap<String,String>> allVegetablesList = new ArrayList<>();
        int images[] = new int[]{R.mipmap.artichoke, R.mipmap.aubergine, R.mipmap.bulgarian_pepper, R.mipmap.broccoli, R.mipmap.peas,
                R.mipmap.zucchini, R.mipmap.cabbage, R.mipmap.potato, R.mipmap.chili_pepper, R.mipmap.onion, R.mipmap.carrot, R.mipmap.cucumber,
                R.mipmap.squash, R.mipmap.tomato, R.mipmap.tomatoes_cherry, R.mipmap.radish, R.mipmap.beet, R.mipmap.pumpkin};
        for (int i = 0; i < images.length; i++)
        {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("keyText", vegetables[i]);
            hashMap.put("keyImage", Integer.toString(images[i]));
            allVegetablesList.add(hashMap);
        }
        String[] from = {"keyImage","keyText"};

        int[] to = {R.id.icon_vegetable, R.id.text_vegetable};

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), allVegetablesList, R.layout.all_vegetables_list, from, to);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Bundle bundle = new Bundle();
        String title = "Овощи";
        String text = getItemText(position, this);
        Resources resource = getActivity().getResources();
        toPlantFragmentFromList(getActivity(), this, bundle, title, text, resource);
    }

    public static void toPlantFragmentFromList(Context context, Fragment fragment, Bundle bundle, String title, String text, Resources resource)
    {
        if (text.equals("Артишок")) {
            fragment = new PlantFragment();
            title  = "Артишок";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.artichoke));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.artichokes_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.artichoke));
        } else if (text.equals("Баклажан")) {
            fragment = new PlantFragment();
            title  = "Баклажан";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.aubergine));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.aubergine_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.aubergine));
        } else if (text.equals("Болгарский Перец")) {
            fragment = new PlantFragment();
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.bulgarian_pepper));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.bulgarian_pepper_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.bulgarian_pepper));
            title  = "Болгарский Перец";
        } else if (text.equals("Брокколи")) {
            fragment = new PlantFragment();
            title  = "Брокколи";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.broccoli));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.broccoli_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.broccoli));
        } else if (text.equals("Горох")) {
            fragment = new PlantFragment();
            title  = "Горох";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.peas));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.peas_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.peas));
        } else if (text.equals("Кабачок")) {
            fragment = new PlantFragment();
            title  = "Кабачок";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.zucchini));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.zucchini_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.zucchini));
        } else if (text.equals("Капуста")) {
            fragment = new PlantFragment();
            title  = "Капуста";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.cabbage));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.cabbage_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.cabbage));
        } else if (text.equals("Картофель")) {
            fragment = new PlantFragment();
            title  = "Картофель";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.potato));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.potato_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.potato));
        } else if (text.equals("Красный Перец")) {
            fragment = new PlantFragment();
            title  = "Красный Перец";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.chili_pepper));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.chili_pepper_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.chili_pepper));
        } else if (text.equals("Лук Репчатый")) {
            fragment = new PlantFragment();
            title  = "Лук Репчатый";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.onion));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.onion_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.onion));
        } else if (text.equals("Морковь")) {
            fragment = new PlantFragment();
            title  = "Морковь";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.carrot));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.carrot_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.carrot));
        } else if (text.equals("Огурец")) {
            fragment = new PlantFragment();
            title  = "Огурец";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.cucumber));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.cucumber_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.cucumber));
        } else if (text.equals("Патиссон")) {
            fragment = new PlantFragment();
            title  = "Патиссон";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.squash));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.squash_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.squash));
        } else if (text.equals("Помидор")) {
            fragment = new PlantFragment();
            title  = "Помидор";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.tomato));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.tomato_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.tomato));
        } else if (text.equals("Помидоры Черри")) {
            fragment = new PlantFragment();
            title  = "Помидоры Черри";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.tomatoes_cherry));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.tomatoes_cherry_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.tomatoes_cherry));
        } else if (text.equals("Редис")) {
            fragment = new PlantFragment();
            title  = "Редис";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.radish));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.radish_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.radish));
        } else if (text.equals("Свекла")) {
            fragment = new PlantFragment();
            title  = "Свекла";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.beet));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.beet_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.beet));
        } else if (text.equals("Тыква")) {
            fragment = new PlantFragment();
            title  = "Тыква";
            bundle.putStringArray(KEY_info, resource.getStringArray(R.array.pumpkin));
            bundle.putInt(KEY_image, (Integer)(R.mipmap.pumpkin_background));
            bundle.putInt(KEY_small_image, (Integer)(R.mipmap.pumpkin));
        }

        if (fragment != null) {
            ((AppCompatActivity) context).getSupportActionBar().setTitle(title);
            fragment.setArguments(bundle);
            FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (user != null) {
            menu.findItem(R.id.action_exit).setVisible(true);   // Пункт "Выйти"
            menu.findItem(R.id.action_sign).setVisible(false);   // Пункт "Войти"
        }
        else {
            menu.findItem(R.id.action_exit).setVisible(false);  // Пункт "Выйти"
            menu.findItem(R.id.action_sign).setVisible(true);   // Пункт "Войти"
        }
        menu.findItem(R.id.action_change_greenhouse_name).setVisible(false);   // Пункт "Изменить Название"
        menu.findItem(R.id.action_delete_greenhouse).setVisible(false);   // Пункт "Удалить"
        menu.findItem(R.id.action_add_greenhouse).setVisible(false);   // Пункт "Добавить"
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.vegetables);
    }

    public static String getItemText(int position, ListFragment this_fragment)
    {
        String s = this_fragment.getListView().getItemAtPosition(position).toString();
        char mass[] = s.toCharArray();
        String name = "";
        for (int i = 0; i < mass.length; i++) {
            if (mass[i] == '=') {
                for (int j = i+1; mass[j] != ','; j++) {
                    name += mass[j];
                }
                break;
            }
        }
        return name;
    }
}