package com.lachapelle.christian.diceroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    // Open web browser
    public void gotoWebsite(View view){
        String site = "https://google.com";
        if (view.getId() == R.id.txtGithub){
            site = "https://github.com/Devworks8/DiceRoller";
        }
        else if (view.getId() == R.id.txtLicense){
            site = "https://www.gnu.org/licenses/gpl-3.0.en.html";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site));
        startActivity(browserIntent);
    }
}