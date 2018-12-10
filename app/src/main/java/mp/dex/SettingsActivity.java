package mp.dex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    protected static final String PREFS = "settings";
    protected static final String BACK_NAV = "backNav";
    SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        Switch toggle = (Switch) findViewById(R.id.backNavSwitch);
        toggle.setChecked(MainActivity.backToOpenNavDrawer);
        TextView aboutLink = (TextView) findViewById(R.id.aboutLink);

        aboutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent about = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(about);
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.backToOpenNavDrawer = true;
                } else {
                    MainActivity.backToOpenNavDrawer = false;
                }
                setPref(BACK_NAV, MainActivity.backToOpenNavDrawer);
            }
        });
    }

    public void setPref(String key, boolean pref) {
        settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putBoolean(key, pref);
        settingsEditor.apply();
    }
}
