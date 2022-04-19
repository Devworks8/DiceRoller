package com.lachapelle.christian.diceroller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private TextView txtHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyList = new ArrayList<>();
        currentHistoryList = new ArrayList<>();
        lstHistoryRef = (ListView) findViewById(R.id.lstHistory);

        dieList = new ArrayList<>();
        currentDieList = new ArrayList<>();
        lstSelectedRef = (ListView) findViewById(R.id.lstSelected);

        ImageButton iBtnD4 = (ImageButton) findViewById(R.id.imgBtnD4);
        iBtnD4.setOnClickListener(view -> diceManager("d4", false));

        ImageButton iBtnD6 = (ImageButton) findViewById(R.id.imgBtnD6);
        iBtnD6.setOnClickListener(view -> diceManager("d6", false));

        ImageButton iBtnD8 = (ImageButton) findViewById(R.id.imgBtnD8);
        iBtnD8.setOnClickListener(view -> diceManager("d8", false));

        ImageButton iBtnD10 = (ImageButton) findViewById(R.id.imgBtnD10);
        iBtnD10.setOnClickListener(view -> diceManager("d10", false));

        ImageButton iBtnD12 = (ImageButton) findViewById(R.id.imgBtnD12);
        iBtnD12.setOnClickListener(view -> diceManager("d12", false));

        ImageButton iBtnD20 = (ImageButton) findViewById(R.id.imgBtnD20);
        iBtnD20.setOnClickListener(view -> diceManager("d20", false));

        ImageButton iBtnCustom = (ImageButton) findViewById(R.id.imgBtnCustom);
        iBtnCustom.setOnClickListener(view -> diceManager("custom", true));

        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(view -> reset(false));
        btnReset.setOnLongClickListener(view -> {
            reset(true);

            return true;
        });

        Button btnRoll = (Button) findViewById(R.id.btnRoll);
        btnRoll.setOnClickListener(view -> diceManager("roll", false));

        lstSelectedAdapter = new ArrayAdapter<>(this, R.layout.selected_layout, R.id.txtSelected, currentDieList);
        lstSelectedRef.setAdapter(lstSelectedAdapter);

        lstSelectedRef.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> updateImages());

        lstHistoryAdapter = new ArrayAdapter<>(this, R.layout.history_layout, R.id.txtHistory, currentHistoryList);
        lstHistoryRef.setAdapter(lstHistoryAdapter);

        lstHistoryRef.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String historyItem = (String) adapterView.getItemAtPosition(i);

                historyList.remove(historyItem);
                lstHistoryAdapter.notifyDataSetChanged();

                updateScreen();

                return true;
            }
        });
    }

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
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateScreen(){
        lstHistoryAdapter.clear();
        lstHistoryAdapter.addAll(historyList);
        lstHistoryRef.post(() -> lstHistoryRef.smoothScrollToPosition(0));

        lstSelectedAdapter.clear();
        lstSelectedAdapter.addAll(dieList);
    }

    private void updateImages(){
        TextView dieType;
        ImageView dieImage;
        View v;

        for (int i = 0; i < lstSelectedRef.getChildCount(); i++){
            v = lstSelectedRef.getChildAt(i);
            dieType = (TextView) v.findViewById(R.id.txtSelected);
            dieImage = (ImageView) v.findViewById(R.id.ivDie);

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

    private void diceManager(String die, boolean custom){
        if (die.equals("roll")){
            Spinner numDice;
            TextView dieType;
            View v;
            StringBuilder diceString = new StringBuilder();

            for (int i = 0; i < lstSelectedRef.getChildCount(); i++){
                v = lstSelectedRef.getChildAt(i);
                numDice = (Spinner) v.findViewById(R.id.spnSelectedTotal);
                dieType = (TextView) v.findViewById(R.id.txtSelected);

                if (diceString.length() != 0){
                    diceString.append(";").append(numDice.getSelectedItem()).append(dieType.getText().toString().replace(" added.", ""));
                }else{
                    diceString = new StringBuilder(numDice.getSelectedItem() + dieType.getText().toString().replace(" added.", ""));
                }
            }

            new Die.DieBuilder(diceString.toString(), false).build();
            Die.DieBuilder.rollAll();

            historyList.add(0, getRollString(diceString.toString()).replace(";", " + "));
            currentHistoryList.add(0, getRollString(diceString.toString()).replace(";", " + "));
            lstHistoryAdapter.notifyDataSetChanged();
        }
        else if (custom){
            final EditText txtDieType = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Custom Die")
                    .setMessage("Enter desired die.\nExample: d100")
                    .setView(txtDieType)
                    .setPositiveButton("Add", (dialogInterface, i) -> {
                        String die1 = txtDieType.getText().toString();

                        if (!currentDieList.contains(die1)) {
                            currentDieList.add(die1);
                            dieList.add(die1);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();

        }
        else {
            if (!currentDieList.contains(die)) {
                currentDieList.add(die);
                dieList.add(die);
            }
        }

        updateScreen();
    }

    private String getRollString(String diceString){
        return diceString + " = " + Die.DieBuilder.getTotal();
    }

    public void deleteDie(View view){
        View parent = (View) view.getParent();
        TextView txtDie = (TextView) parent.findViewById(R.id.txtSelected);
        String die = String.valueOf(txtDie.getText());

        dieList.remove(die);

        updateScreen();
    }

    public void reroll(View view){
        View parent = (View) view.getParent();
        TextView txtDie = (TextView) parent.findViewById(R.id.txtHistory);
        String historyString = String.valueOf(txtDie.getText()).replace(" + ", ";").split(" = ")[0];

        new Die.DieBuilder(historyString, false).build();
        Die.DieBuilder.rollAll();

        historyList.add(0, getRollString(historyString).replace(";", " + "));
        currentHistoryList.add(0, getRollString(historyString).replace(";", " + "));
        lstHistoryAdapter.notifyDataSetChanged();

        updateScreen();
    }

    private void reset(boolean all){
        if (all){
            historyList.clear();
        }

        dieList.clear();

        updateScreen();
    }
}