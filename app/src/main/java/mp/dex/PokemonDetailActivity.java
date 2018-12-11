package mp.dex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
    //TODO Change title, as well as populate page with necessary Pokemon info
    private void fillView(final JSONObject pokemon) {

    }
}
