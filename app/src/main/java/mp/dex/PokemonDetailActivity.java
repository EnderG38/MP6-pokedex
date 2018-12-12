package mp.dex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class PokemonDetailActivity extends AppCompatActivity {

    private static RequestQueue requestQueue;
    private int id;
    private static final String URL_BASE = "https://pokeapi.co/api/v2/";
    private static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_detail_page);
        id = getIntent().getIntExtra("pokemonId", 0);

        requestQueue = Volley.newRequestQueue(this);
        getPokemonData(id);
    }
    //This method gets the JSONObject for the Pokemon that was tapped on
    //There *shouldn't* be any reason to modify getPokemonData at all
    private void getPokemonData(final int id) {
        try {
            String urlPath = "pokemon/";
            String url = URL_BASE + urlPath + id + "/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            fillView(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    error.printStackTrace();
                }
            }
            );
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //TODO: add moves eventually?
    //The try/catch is to ensure the JSONObject isn't null
    //You can probably drop all your changes in the /try/
    private void fillView(final JSONObject pokemon) {
        try {
            String pkmnName = MainActivity.formatString(pokemon.getString("name"));

            ImageView setSprite = findViewById(R.id.sprite);
            TextView heightText = findViewById(R.id.height);
            double height = pokemon.getInt("height");
            TextView weightText = findViewById(R.id.weight);
            double weight = pokemon.getInt("weight");
            LinearLayout types = findViewById(R.id.detail_types);
            TextView number = findViewById(R.id.detail_id);
            TextView name = findViewById(R.id.detail_name);

            TextView baseHP = findViewById(R.id.hp_value);
            TextView baseAttack = findViewById(R.id.attack_value);
            TextView baseDefense = findViewById(R.id.defense_value);
            TextView baseSpecialAttack = findViewById(R.id.special_attack_value);
            TextView baseSpecialDefense = findViewById(R.id.special_defense_value);
            TextView baseSpeed = findViewById(R.id.speed_value);
            TextView[] stats = {baseSpeed, baseSpecialDefense, baseSpecialAttack, baseDefense, baseAttack, baseHP};
            TextView baseStatTotal = findViewById(R.id.total_value);

            setTitle(pkmnName);

            int w, h;
            w = h = Util.dpToPx(150, this);
            Picasso.get().load(URL_SPRITE_BASE + id + ".png").resize(w, h).into(setSprite);

            String tmp;
            tmp = (String) heightText.getText();
            tmp = tmp.replace("x", "" + height / 10).replace("y", "" + Util.dmToFtAndIn(height));
            heightText.setText(tmp);
            tmp = (String) weightText.getText();
            tmp = tmp.replace("x", ("" + weight / 10)).replace("y", "" + Util.hgToLb(weight));
            weightText.setText(tmp);

            JSONArray jsonArray = pokemon.getJSONArray("types");
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                ImageView iv = new ImageView(this);
                String type = jsonArray.getJSONObject(i).getJSONObject("type").getString("name");
                iv.setImageResource(Util.getDrawable("type_" + type, this));
                iv.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPx(60, this), Util.dpToPx(30, this)));
                types.addView(iv);
            }

            tmp = "#" + id;
            number.setText(tmp);
            name.setText(pkmnName);

            jsonArray = pokemon.getJSONArray("stats");
            int bst = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                int st = jsonArray.getJSONObject(i).getInt("base_stat");
                bst += st;
                tmp = String.valueOf(st);
                stats[i].setText(tmp);
            }
            tmp = String.valueOf(bst);
            baseStatTotal.setText(tmp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
