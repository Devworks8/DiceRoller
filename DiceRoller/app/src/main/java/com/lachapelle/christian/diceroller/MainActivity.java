package com.lachapelle.christian.diceroller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> historyList;
    private ArrayList<String> currentHistoryList;
    private ArrayList<String> dieList;
    private ArrayList<String> currentDieList;
    private ArrayAdapter<String> lstHistoryAdapter;
    private ListView lstHistoryRef;
    private ArrayAdapter<String> lstSelectedAdapter;
    private ListView lstSelectedRef;
    SharedPreferences prefs;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fix the screen orientation to portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Disable Night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize Shared Preferences
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Setup History ListView
        historyList = new ArrayList<>();
        currentHistoryList = new ArrayList<>();
        lstHistoryRef = findViewById(R.id.lstHistory);

        lstHistoryAdapter = new ArrayAdapter<>(this, R.layout.history_layout, R.id.txtHistory, currentHistoryList);
        lstHistoryRef.setAdapter(lstHistoryAdapter);

        lstHistoryRef.setOnItemLongClickListener((adapterView, view, i, l) -> {
            String historyItem = (String) adapterView.getItemAtPosition(i);

            historyList.remove(historyItem);
            lstHistoryAdapter.notifyDataSetChanged();

            updateScreen();

            return true;
        });

        // Setup Selected die ListView
        dieList = new ArrayList<>();
        currentDieList = new ArrayList<>();
        lstSelectedRef = findViewById(R.id.lstSelected);

        lstSelectedAdapter = new ArrayAdapter<>(this, R.layout.selected_layout, R.id.txtSelected, currentDieList);
        lstSelectedRef.setAdapter(lstSelectedAdapter);

        // Update associated die image on ListView layout change
        lstSelectedRef.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> updateImages());

        // Setup button click listeners
        ImageButton iBtnD4 = findViewById(R.id.imgBtnD4);
        iBtnD4.setOnClickListener(view -> diceManager("d4", false));

        ImageButton iBtnD6 = findViewById(R.id.imgBtnD6);
        iBtnD6.setOnClickListener(view -> diceManager("d6", false));

        ImageButton iBtnD8 = findViewById(R.id.imgBtnD8);
        iBtnD8.setOnClickListener(view -> diceManager("d8", false));

        ImageButton iBtnD10 = findViewById(R.id.imgBtnD10);
        iBtnD10.setOnClickListener(view -> diceManager("d10", false));

        ImageButton iBtnD12 = findViewById(R.id.imgBtnD12);
        iBtnD12.setOnClickListener(view -> diceManager("d12", false));

        ImageButton iBtnD20 = findViewById(R.id.imgBtnD20);
        iBtnD20.setOnClickListener(view -> diceManager("d20", false));

        ImageButton iBtnCustom = findViewById(R.id.imgBtnCustom);
        iBtnCustom.setOnClickListener(view -> diceManager("custom", true));

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(view -> reset(false));
        btnReset.setOnLongClickListener(view -> {
            reset(true);

            return true;
        });

        Button btnRoll = findViewById(R.id.btnRoll);
        btnRoll.setOnClickListener(view -> diceManager("roll", false));
    }

    // Setup Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.about) {
            Toast toast = Toast.makeText(getApplicationContext(), "Created by: Christian Lachapelle\n" +
                    "Student #: A00230066\n" +
                    "Course Code: IOT-1009", Toast.LENGTH_SHORT);
            toast.show();

            return true;
        }else if (item.getItemId() == R.id.settings){
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Load saved data from Shared Preferences
    @Override
    protected void onStart(){
        super.onStart();

        for (int item = 0;; ++item){
            final String historyItem = prefs.getString(String.valueOf(item), "");
            if (!historyItem.equals("")){
                historyList.add(historyItem);
            }else{
                break;
            }
        }
        updateScreen();
    }

    // Save or Clear data to Shared Preferences
    @Override
    protected void onStop(){
        SharedPreferences.Editor editor = prefs.edit();
        boolean saveHistory = prefs.getBoolean("save_history_pref", false);
        if (saveHistory){
            for (int item = 0; item < historyList.size(); ++item){
                editor.putString(String.valueOf(item), historyList.get(item));
            }
        }else{
            editor.clear();
            editor.putBoolean("save_history_pref", false);
        }
        editor.apply();

        super.onStop();
    }

    // Update the ViewLists
    private void updateScreen(){
        lstHistoryAdapter.clear();
        lstHistoryAdapter.addAll(historyList);
        lstHistoryRef.post(() -> lstHistoryRef.smoothScrollToPosition(0));

        lstSelectedAdapter.clear();
        lstSelectedAdapter.addAll(dieList);
    }

    // Update the Image of added die to Selection List to correspond to the correct die
    private void updateImages(){
        TextView dieType;
        ImageView dieImage;
        View v;

        for (int i = 0; i < lstSelectedRef.getChildCount(); i++){
            v = lstSelectedRef.getChildAt(i);
            dieType = v.findViewById(R.id.txtSelected);
            dieImage = v.findViewById(R.id.ivDie);

            switch (dieType.getText().toString()){
                case "d4":
                    dieImage.setImageResource(R.drawable.d4);
                    break;
                case "d6":
                    dieImage.setImageResource(R.drawable.d6);
                    break;
                case "d8":
                    dieImage.setImageResource(R.drawable.d8);
                    break;
                case "d10":
                    dieImage.setImageResource(R.drawable.d10);
                    break;
                case "d12":
                    dieImage.setImageResource(R.drawable.d12);
                    break;
                case "d20":
                    dieImage.setImageResource(R.drawable.d20);
                    break;
                default:
                    dieImage.setImageResource(R.drawable.custom);
                    break;
            }
        }
    }

    // Perform all Die class related functions
    private void diceManager(String die, boolean custom){
        // Roll the selected dice
        if (die.equals("roll")){
            Spinner numDice;
            TextView dieType;
            View v;
            StringBuilder diceString = new StringBuilder();

            for (int i = 0; i < lstSelectedRef.getChildCount(); i++){
                v = lstSelectedRef.getChildAt(i);
                numDice = v.findViewById(R.id.spnSelectedTotal);
                dieType = v.findViewById(R.id.txtSelected);

                if (diceString.length() != 0){
                    diceString.append(";").append(numDice.getSelectedItem()).append(dieType.getText().toString().replace(" added.", ""));
                }else{
                    diceString = new StringBuilder(numDice.getSelectedItem() + dieType.getText().toString().replace(" added.", ""));
                }
            }

            if (diceString.length() != 0) {
                new Die.DieBuilder(diceString.toString(), false).build();
                Die.DieBuilder.rollAll();

                // Update the Roll History
                historyList.add(0, getRollString(diceString.toString()).replace(";", " + "));
                currentHistoryList.add(0, getRollString(diceString.toString()).replace(";", " + "));
                lstHistoryAdapter.notifyDataSetChanged();
            }
        }
        // Ask for requested die type and add it to the Selected die list
        else if (custom){
            final EditText txtDieType = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Custom Die")
                    .setMessage("Enter desired die.\nExample: d100")
                    .setView(txtDieType)
                    .setPositiveButton("Add", (dialogInterface, i) -> {
                        String customDie = txtDieType.getText().toString();

                        // Do not allow duplicates or a die count greater than 7
                        if (!currentDieList.contains(customDie) && lstSelectedRef.getCount() < 7) {
                            currentDieList.add(customDie);
                            dieList.add(customDie);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "The die already exists or the maximum die count of 7 has been reached.",
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();

        }
        // Add a die to the Selected die list
        else {
            // Do not allow duplicates or a die count greater than 7
            if (!currentDieList.contains(die) && lstSelectedRef.getCount() < 7) {
                currentDieList.add(die);
                dieList.add(die);
            }else{
                Toast.makeText(getApplicationContext(),
                        "The die already exists or the maximum die count of 7 has been reached.",
                        Toast.LENGTH_LONG).show();
            }
        }

        updateScreen();
    }

    // Return formatted result string
    private String getRollString(String diceString){
        return diceString + " = " + Die.DieBuilder.getTotal();
    }

    // Remove die from Selected die list
    public void deleteDie(View view){
        View parent = (View) view.getParent();
        TextView txtDie = parent.findViewById(R.id.txtSelected);
        String die = String.valueOf(txtDie.getText());

        dieList.remove(die);

        updateScreen();
    }

    // Reroll previously rolled dice from History list
    public void reroll(View view){
        View parent = (View) view.getParent();
        TextView txtDie = parent.findViewById(R.id.txtHistory);
        String historyString = String.valueOf(txtDie.getText()).replace(" + ", ";").split(" = ")[0];

        new Die.DieBuilder(historyString, false).build();
        Die.DieBuilder.rollAll();

        historyList.add(0, getRollString(historyString).replace(";", " + "));
        currentHistoryList.add(0, getRollString(historyString).replace(";", " + "));
        lstHistoryAdapter.notifyDataSetChanged();

        updateScreen();
    }

    // Reset Selected dice list or all
    private void reset(boolean all){
        if (all){
            historyList.clear();
        }

        dieList.clear();

        updateScreen();
    }
}