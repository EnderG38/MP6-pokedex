package mp.dex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        TextView aboutLink = findViewById(R.id.aboutLink);
        Switch toggle = (Switch) findViewById(R.id.backNavSwitch);
        toggle.setChecked(MainActivity.backToOpenNavDrawer);

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
            }
        });
    }
}
