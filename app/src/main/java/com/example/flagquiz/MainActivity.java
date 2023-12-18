package com.example.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.core.*;

public class MainActivity extends AppCompatActivity {
    private boolean preferenceChanged = true;
    static final String CHOICES = "pref_numberOfChoices";
    static final String REGIONS = "pref_regions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reading default preferences from preferences file
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preferences, false);

        //Registering for listeners for preferences
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferencesChangedListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferenceChanged) {
            MainFragment quizFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferenceChanged = false;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent preferenceIntent = new Intent(this, PreferencesActivity.class);
        startActivity(preferenceIntent);
        return super.onOptionsItemSelected(menuItem);
    }



    SharedPreferences.OnSharedPreferenceChangeListener preferencesChangedListener = (sharedPreferences, s) -> {
        MainFragment quizFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
        if(s.equals(CHOICES)){
            quizFragment.updateGuessRows (sharedPreferences);
            quizFragment.resetQuiz();
        }
        else if(s.equals(REGIONS)){
            quizFragment.updateRegions(sharedPreferences);
            quizFragment.resetQuiz();
        }
        else {
            Toast.makeText(this, "Default settings were applied", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Restarting quiz with new settings", Toast.LENGTH_SHORT).show();
        };



}