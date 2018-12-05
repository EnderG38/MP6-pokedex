package mp.dex;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int POKEMON_MODE = 0;
    private static final int MOVE_MODE = 1;
    private static final int ABILITY_MODE = 2;

    protected static String urlId = ""; //i don't even remember what this is for, but it might end up being used
    private static int mode = 0;
    private static boolean backToOpenNavDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will eventually be a search button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout list = findViewById(R.id.list);
        //TODO: make a less hacky way of getting the current number of pokemon in existence
        for (int i = 1; i < (int) '̢'; i++) {
            //list.addView(getPokemon(i));
            list.addView(getSprite(i));
        }
        //list.
    }

    //TODO: add back to open nav drawer setting (optional/low priority)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backToOpenNavDrawer && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            ImageView a = findViewById(R.id.testImageView);
            Contenthandler.getSpriteTest(a);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //handles selecting options in navigation panel
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_pokemon) {
            changeMode(POKEMON_MODE);
        } else if (id == R.id.nav_moves) {
            changeMode(MOVE_MODE);
        } else if (id == R.id.nav_abilities) {
            changeMode(ABILITY_MODE);
        } /*else if (id == R.id.nav_meta) {
            openMeta();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeMode(int m) {
        mode = m;
        switch (m) {
            case POKEMON_MODE: {
                break;
            }
            case MOVE_MODE: {
                break;
            }
            case ABILITY_MODE: {
                break;
            }
        }
    }

    private TextView getPokemon(final int id) {
        final TextView ret = new TextView(this);
        String url = Contenthandler.URL_BASE + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = "test #" + id;
                        ret.setText(str);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String str = "Error! id: " + id;
                ret.setText(str);
            }
        });
        ret.setHeight(64);
        ret.setTextSize(12);
        return ret;
    }

    private ImageView getSprite(int id) {
        ImageView ret = new ImageView(this);
        ret.setForegroundGravity(GravityCompat.START);
        Picasso.get().load(Contenthandler.URL_SPRITE_BASE + id +".png").resize(150, 150).into(ret);
        return ret;
    }


    /*//update list of Pokemon/moves/abilities/etc.
    private boolean updateList() {

    }
    */
}
