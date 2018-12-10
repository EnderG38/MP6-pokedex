package mp.dex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//TODO: implement SharedPreferences somewhere so the one setting we have is kept after app close
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String urlAppendage = "pokemon/";
    private static final String URL_BASE = "https://pokeapi.co/api/v2/";
    private static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    private static LinearLayout searchList;

    private static final int POKEMON_MODE = 0;
    private static final int MOVE_MODE = 1;
    private static final int ABILITY_MODE = 2;
    private static final int FIRST_ID = 1;
    private static final int LAST_ID = 802;
    private static final int FIRST_ALTERNATE_ID = 10001;
    private static final int LAST_ALTERNATE_ID = 10147;

    protected static boolean backToOpenNavDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: make search work, then add this. or maybe just make search bar hiding work
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will eventually be a search button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        searchList = findViewById(R.id.pokemon_search_list);
        updatePokemon();
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
            super.onBackPressed();
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

        switch(id) {
            case R.id.nav_pokemon:
                changeMode(POKEMON_MODE);
                break;
            case R.id.nav_abilities:
                changeMode(ABILITY_MODE);
                break;
            case R.id.nav_moves:
                changeMode(MOVE_MODE);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeMode(int m) {
        switch (m) {
            case POKEMON_MODE: {
                urlAppendage = "pokemon/";
                updatePokemon();
                break;
            }
            case ABILITY_MODE: {
                urlAppendage = "abilities/";
                updateAbilities();
                break;
            }
            case MOVE_MODE: {
                urlAppendage = "moves/";
                updateMoves();
                break;
            }
        }
    }
    //Method to convert Json strings to usable UI elements
    public static String formatString(String str) {
        // Create a char array of given String
        str = str.replace('-', ' ');
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {
                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {
                    // Convert into Upper-case
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }
            }
            // If apart from first character
            // Any one is in Upper-case
            else if (ch[i] >= 'A' && ch[i] <= 'Z')
                // Convert into Lower-Case
                ch[i] = (char)(ch[i] + 'a' - 'A');
        }
        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;
    }

    //TODO: replace Pikachu info with placeholder/default values
    private void updatePokemon() {
        searchList.removeAllViews();
        for (int i = FIRST_ID; i <= LAST_ID; i++) {
            LinearLayout obj = new LinearLayout(this);
            obj.setGravity(Gravity.CENTER_VERTICAL);
            JsonParser parser = new JsonParser();
            JsonNull pokemon = (JsonNull) parser.parse(retrieveData("" + i));
            //JsonArray forms = pokemon.getAsJsonArray("forms");
            //JsonObject form = (JsonObject) forms.get(0);
            //String name = formatString(form.get("name").getAsString());
            //Exceptions for Ho-oh, Porygon-Z, and Jangmo-O line
            if (i == 250 || i == 474 || i == 782 || i == 783 || i == 784) {
                //name = name.replace(' ', '-');
            }
            //JsonArray types = pokemon.get("types").getAsJsonArray();

            ImageView setSprite = new ImageView(this);
            Picasso.get().load(URL_SPRITE_BASE + i + ".png").into(setSprite);
            //setSprite.
            TextView setDexNumber = new TextView(this);
            setDexNumber.setText("" + i);
            TextView setName = new TextView(this);
            setName.setText("name");

            obj.addView(setSprite);
            obj.addView(setDexNumber);
            obj.addView(setName);

            searchList.addView(obj);
        }
    }

    private void updateAbilities() {

    }

    private void updateMoves() {

    }

    //this is disgusting but idk if there is a better way
    //basically I just made retrrieveData work on a new Thread since it's not allowed on the main one
    private static String retrieveData(final String id) {
        Log.w("This is the Id", id);
        final StringBuilder stringBuilder = new StringBuilder("");
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        URL url = new URL(URL_BASE + urlAppendage + id + "/");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                        }
                        finally{
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return stringBuilder.toString();
    }

    public void onClick(View v) {
        final int id = v.getId();
        switch(id) {
            case R.id.pokemon_search_item:
                //Navigate to Pokemon Detail Page
                Intent intent = new Intent(MainActivity.this, PokemonDetailActivity.class);
                startActivity(intent);
                break;
        }
    }

    /*//update list of Pokemon/moves/abilities/etc.
    private boolean updateList() {

    }
    */
}
