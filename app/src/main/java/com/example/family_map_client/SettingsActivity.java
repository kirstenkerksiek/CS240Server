package com.example.family_map_client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import DataManagement.DataCache;

public class SettingsActivity extends AppCompatActivity {
    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseSwitch;
    private Switch motherSwitch;
    private Switch fatherSwitch;
    private Switch maleSwitch;
    private Switch femaleSwitch;
    private LinearLayout logout;
    private Context context = this;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
        familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
        spouseSwitch = findViewById(R.id.spouseSwitch);
        motherSwitch = findViewById(R.id.motherSwitch);
        fatherSwitch = findViewById(R.id.fatherSwitch);
        maleSwitch = findViewById(R.id.maleSwitch);
        femaleSwitch = findViewById(R.id.femaleSwitch);
        logout = findViewById(R.id.logout_switch);
        logout.setClickable(true);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        final DataCache cache = DataCache.getInstance();
        SetSwitches();

        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isLifeStoryLinesVisible()) {
                    cache.setLifeStoryLinesVisible(false);
                }
                else {
                    cache.setLifeStoryLinesVisible(true);
                }
                SetSwitches();
            }
        });

        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isFamilyTreeLinesVisible()) {
                    cache.setFamilyTreeLinesVisible(false);
                }
                else {
                    cache.setFamilyTreeLinesVisible(true);
                }
                SetSwitches();
            }
        });

        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isSpouseLinesVisible()) {
                    cache.setSpouseLinesVisible(false);
                }
                else {
                    cache.setSpouseLinesVisible(true);
                }
                SetSwitches();
            }
        });

        motherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isMotherEventsVisible()) {
                    cache.setMotherEventsVisible(false);
                }
                else {
                    cache.setMotherEventsVisible(true);
                }
                SetSwitches();
            }
        });

        fatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isFatherEventsVisible()) {
                    cache.setFatherEventsVisible(false);
                }
                else {
                    cache.setFatherEventsVisible(true);
                }
                SetSwitches();
            }
        });

        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isMaleEventsVisible()) {
                    cache.setMaleEventsVisible(false);
                }
                else {
                    cache.setMaleEventsVisible(true);
                }
                SetSwitches();
            }
        });

        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cache.isFemaleEventsVisible()) {
                    cache.setFemaleEventsVisible(false);
                }
                else {
                    cache.setFemaleEventsVisible(true);
                }
                SetSwitches();
            }
        });


        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //create asyncLogin
                DataCache cache = DataCache.getInstance();
                cache.logout();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }



    private void SetSwitches() {
        DataCache cache = DataCache.getInstance();
        if (cache.isLifeStoryLinesVisible()) {
            lifeStorySwitch.setChecked(true);
        }
        else {
            lifeStorySwitch.setChecked(false);
        }
        if (cache.isFamilyTreeLinesVisible()) {
            familyTreeSwitch.setChecked(true);
        }
        else {
            familyTreeSwitch.setChecked(false);
        }
        if (cache.isSpouseLinesVisible()) {
            spouseSwitch.setChecked(true);
        }
        else {
            spouseSwitch.setChecked(false);
        }
        if (cache.isMotherEventsVisible()) {
            motherSwitch.setChecked(true);
        }
        else {
            motherSwitch.setChecked(false);
        }
        if (cache.isFatherEventsVisible()) {
            fatherSwitch.setChecked(true);
        }
        else {
            fatherSwitch.setChecked(false);
        }
        if (cache.isMaleEventsVisible()) {
            maleSwitch.setChecked(true);
        }
        else {
            maleSwitch.setChecked(false);
        }
        if (cache.isFemaleEventsVisible()) {
            femaleSwitch.setChecked(true);
        }
        else {
            femaleSwitch.setChecked(false);
        }
    }
}
