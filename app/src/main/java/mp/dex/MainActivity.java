package mp.dex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String urlAppendage = "pokemon/";
    private static final String URL_BASE = "https://pokeapi.co/api/v2/";
    private static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    private static Context mContext;
    private static LinearLayout searchList;
    private static JSONObject searchData;
    private static RequestQueue requestQueue;

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

        final EditText searchbar = findViewById(R.id.pokemon_search_box);
        searchbar.setVisibility(View.GONE);

        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        //TODO: make search work, then add this. or maybe just make search bar hiding work
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "This will eventually be a search button", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                if (searchbar.getVisibility() == View.GONE) {
                    searchbar.setVisibility(View.VISIBLE);
                    searchbar.requestFocus();
                    inputMethodManager.showSoftInput(searchbar, InputMethodManager.SHOW_IMPLICIT);
                    fab.setImageResource(R.drawable.ic_baseline_clear_24px);
                } else {
                    searchbar.setVisibility(View.GONE);
                    searchbar.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    fab.setImageResource(R.drawable.ic_baseline_search_24px);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sp = getSharedPreferences(SettingsActivity.PREFS, 0);
        backToOpenNavDrawer = sp.getBoolean(SettingsActivity.BACK_NAV, false);

        mContext = getApplicationContext();
        requestQueue = Volley.newRequestQueue(mContext);

        searchList = findViewById(R.id.pokemon_search_list);
        updatePokemon();
    }

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

    private static String pokemonName;

    //TODO: remove hardcoded pikachu info
    private void updatePokemon() {
        //the king is dead
        searchList.removeAllViews();
        //long live the king

        for (int i = FIRST_ID; i <= 10; i++) {
            //new everything is required because using existing layouts/etc makes Android unhappy
            //basically you can't add something to a view if it already has a parent
            //and the existing things all had the LinearLayout as a parent
            //so basically we need to make the search item layout from scratch. programmatically. fun.
            ConstraintLayout constraintLayout = new ConstraintLayout(this);
            LinearLayout pokemonList = new LinearLayout(this);
            pokemonList.setGravity(Gravity.CENTER_VERTICAL);
            pokemonList.setClickable(true);
            constraintLayout.addView(pokemonList);

            //here's the actual JSON stuff
            //TODO: make the JSON stuff actually work
            String pokemonName = "name";
            try {
                retrieveData("" + i);
                pokemonName = formatString(searchData.getString("name"));
                //Exceptions for the 5 Pokemon with hyphens in their name cause they're stupid.
                if (i == 250 || i == 474 || i == 782 || i == 783 || i == 784) {
                    pokemonName = pokemonName.replace(' ', '-');
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //these beautiful blocks of code set the layout and constraints and shit
            //It's a lot but damn is this cool
            ImageView setSprite = new ImageView(this);
            Picasso.get().load(URL_SPRITE_BASE + i + ".png").into(setSprite);
            setSprite.setLayoutParams(new LinearLayout.LayoutParams(200, 200));

            TextView setDexNumber = new TextView(this);
            setDexNumber.setText("#" + i);
            setDexNumber.setPadding(10, 0, 10, 0);

            TextView setName = new TextView(this);
            setName.setText(pokemonName);
            setName.setPadding(15, 0, 15, 0);

            pokemonList.addView(setSprite);
            pokemonList.addView(setDexNumber);
            pokemonList.addView(setName);

            searchList.addView(constraintLayout);
        }
    }

    private void updateAbilities() {
        searchList.removeAllViews();
    }

    private void updateMoves() {
        searchList.removeAllViews();
    }

    //it does need to be on a separate thread
    private static void retrieveData(final String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + urlAppendage + id + "/",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Received JSON for id", id);
                        searchData = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
        //Parsing JSON with GSON, appears to not work.
        /*Log.w("This is the Id", id);
        final StringBuilder stringBuilder = new StringBuilder("");
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   try {
                       Log.d("attempting connection", id);
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
        //thread.start();
        return stringBuilder.toString();*/
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
}
