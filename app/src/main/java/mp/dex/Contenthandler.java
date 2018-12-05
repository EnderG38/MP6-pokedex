package mp.dex;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.android.volley.*;

//this class handles the json grabbing and parsing.
public class Contenthandler extends AppCompatActivity {
    protected static final String URL_BASE = "https://pokeapi.co/api/v2/";
    protected static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    public static void getSpriteTest(final ImageView imageView) {
        Picasso.get().load(URL_SPRITE_BASE + "1.png").resize(300, 300).into(imageView);
    }

    public static TextView getPokemon(final int id) {
        final TextView ret = new TextView(new Contenthandler().getApplicationContext());
        String url = URL_BASE + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ret.setText(response.substring(response.indexOf("\"forms\"") + 1, response.indexOf("\"", response.indexOf("\"forms\"") + 30)));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ret.setText("Error! id: " + id);
            }
        });
        return ret;
    }
}
