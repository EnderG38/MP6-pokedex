package mp.dex;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static String urlPath = "pokemon/";
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
    //TODO : Populate page with necessary Pokemon info
    //TODO : (i.e. name, moves, base stats, image/sprite, dex entry? are probably good for presentation)
    //The try/catch is to ensure the JSONObject isn't null
    //You can probably drop all your changes in the /try/
    private void fillView(final JSONObject pokemon) {
        try {
            setTitle(MainActivity.formatString(pokemon.getString("name")));

            ImageView setSprite = findViewById(R.id.sprite);
            TextView height = findViewById(R.id.height);
            TextView weight = findViewById(R.id.weight);
            TextView number = findViewById(R.id.id);
            TextView name = findViewById(R.id.name);
            LinearLayout types = findViewById(R.id.types_detail);

            int w, h;
            w = h = Util.dpToPx(150, this);
            Picasso.get().load(URL_SPRITE_BASE + id + ".png").resize(w, h).into(setSprite);

            JSONArray typeArray = pokemon.getJSONArray("types");
            for (int i = typeArray.length() - 1; i >= 0; i--) {
                ImageView iv = new ImageView(this);
                String type = typeArray.getJSONObject(i).getJSONObject("type").getString("name");
                iv.setImageResource(Util.getDrawable("type_" + type, this));
                iv.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPx(60, this), Util.dpToPx(30, this)));
                types.addView(iv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
