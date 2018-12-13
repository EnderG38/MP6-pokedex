package mp.dex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String urlPath = "pokemon/";
    private static final String URL_BASE = "https://pokeapi.co/api/v2/";
    private static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    private LinearLayout searchList;
    private static RequestQueue requestQueue;

    private static final int POKEMON_MODE = 0;
    private static final int MOVE_MODE = 1;
    private static final int ABILITY_MODE = 2;
    private static int FIRST_ID = 1;
    private static int LAST_ID = 802;
    //private static final int FIRST_ALTERNATE_ID = 10001;
    //private static final int LAST_ALTERNATE_ID = 10147;

    protected static boolean backToOpenNavDrawer = false;

    //TODO: IMPORTANT: add cache and connection checking before full release

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText searchbar = findViewById(R.id.pokemon_search_box);
        searchbar.setVisibility(View.GONE);

        //final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //TODO: make search work, then add this. or maybe just make search bar hiding work
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "This will eventually be a search button", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sp = getSharedPreferences(SettingsActivity.PREFS, 0);
        backToOpenNavDrawer = sp.getBoolean(SettingsActivity.BACK_NAV, false);

        requestQueue = Volley.newRequestQueue(this);

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
    //TODO: disable moves and abilities/ throw a snackbar when tapped instead of changing mode
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        View view = findViewById(R.id.nav_view);
        //Snackbar.make(view, "Navigation is still under construction!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
        searchList.removeAllViews();
        switch (m) {
            case POKEMON_MODE: {
                urlPath = "pokemon/";
                updatePokemon();
                break;
            }
            case ABILITY_MODE: {
                urlPath = "ability/";
                updateAbilities();
                break;
            }
            case MOVE_MODE: {
                urlPath = "move/";
                updateMoves();
                break;
            }
        }
    }
    //Method to convert Json strings to usable UI elements
    public static String formatString(String str) {
        String[] parts = str.split("-");
        for(int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase()
                        + parts[i].substring(1, parts[i].length());
        }
        //This is now compatible with Android Marshmallow and up
        return TextUtils.join(" ", parts);
    }
    //Empties the main content of elements,
    //then makes a call to the URL request method that does the element re-population
    private void updatePokemon() {
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!thread.isInterrupted()) {
                    try {
                        retrieveData(String.valueOf(FIRST_ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();*/
        LAST_ID = 802;
        retrieveData(String.valueOf(FIRST_ID));
    }
    private void updateAbilities() {
        LAST_ID = 232;
        retrieveData(String.valueOf(FIRST_ID));
    }

    private void updateMoves() {
        LAST_ID = 719;
        retrieveData(String.valueOf(FIRST_ID));
    }
    //This takes the place of one iteration of the loop in the previous build
    private void hydratePokemon(final JSONObject pokemon) {
        String pokemonName = null;
        int id = 0;
        try {
            pokemonName = formatString(pokemon.getString("name"));
            id = pokemon.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Exceptions for the 5 Pokemon with hyphens in their name.
        if (id == 250 || id == 474 || id == 782 || id == 783 || id == 784) {
            pokemonName = pokemonName.replace(' ', '-');
        }
        final int detailsId = id;
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        ConstraintSet constraintSet = new ConstraintSet();

        LinearLayout pokemonList = new LinearLayout(this);
        pokemonList.setGravity(Gravity.CENTER_VERTICAL);
        pokemonList.setId(R.id.pokemon_search_item);

        //make each list item animate when clicked
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        constraintLayout.setBackgroundResource(backgroundResource);
        constraintLayout.setClickable(true);
        constraintLayout.addView(pokemonList);

        LinearLayout types = new LinearLayout(this);
        types.setOrientation(LinearLayout.VERTICAL);
        types.setGravity(Gravity.CENTER_VERTICAL);
        types.setHorizontalGravity(Gravity.END);
        try {
            JSONArray typeArray = pokemon.getJSONArray("types");
            for (int i = typeArray.length() - 1; i >= 0; i--) {
                ImageView iv = new ImageView(this);
                String type = typeArray.getJSONObject(i).getJSONObject("type").getString("name");
                iv.setImageResource(Util.getDrawable("type_" + type, this));
                iv.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPx(40, this), Util.dpToPx(20, this)));
                types.addView(iv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ImageView setSprite = new ImageView(this);
        Picasso.get().load(URL_SPRITE_BASE + id + ".png").into(setSprite);
        setSprite.setLayoutParams(new LinearLayout.LayoutParams(200, 200));

        TextView setDexNumber = new TextView(this);
        String textId = "#" + id;
        setDexNumber.setText(textId);
        setDexNumber.setPadding(10, 0, 10, 0);

        TextView setName = new TextView(this);
        setName.setText(pokemonName);
        setName.setPadding(15, 0, 15, 0);

        pokemonList.addView(setSprite);
        pokemonList.addView(setDexNumber);
        pokemonList.addView(setName);
        pokemonList.addView(types);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Navigate to Pokemon Detail Page
                Intent intent = new Intent(MainActivity.this, PokemonDetailActivity.class);
                intent.putExtra("pokemonId", detailsId);
                startActivity(intent);
            }
        });

        searchList.addView(constraintLayout);
    }

    private void hydrateAbility(final JSONObject ability) {

    }

    private void hydrateMove(final JSONObject move) {

    }

    //TODO: modify for compatibility with moves/abilities/items/etc
    //Filling the list occurs concurrently with making the web request
    private void retrieveData(final String id) {
        String url = URL_BASE + urlPath + id + "/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (urlPath.equals("pokemon/")) {
                            hydratePokemon(response);
                            int nextId = 0;
                            try {
                                nextId = response.getInt("id") + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (nextId <= LAST_ID) {
                                retrieveData(String.valueOf(nextId));
                            }
                            Log.d("Received JSON for id", id);
                        } else if (urlPath.equals("ability/")) {
                            hydrateAbility(response);
                            int nextId = 0;
                            try {
                                nextId = response.getInt("id") + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (nextId <= LAST_ID) {
                                retrieveData(String.valueOf(nextId));
                            }
                            Log.d("Received JSON for id", id);
                        } else if (urlPath.equals("move/")) {
                            hydrateMove(response);
                            int nextId = 0;
                            try {
                                nextId = response.getInt("id") + 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (nextId <= LAST_ID) {
                                retrieveData(String.valueOf(nextId));
                            }
                            Log.d("Received JSON for id", id);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
