package mp.dex;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.android.volley.*;
import com.android.volley.toolbox.ImageRequest;

//this class handles the json grabbing and parsing.
public class Contenthandler extends AppCompatActivity {
    private static String urlAppend;
    private static final String URL_BASE = "https://pokeapi.co/api/v2/";
    private static final String URL_SPRITE_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    private static RequestQueue requestQueue;
    protected static Bitmap bitmap = null;

    //TODO: remove Volley from everywhere
    public Bitmap getSpritesTest(final ImageView imageView) {
        final String url = URL_SPRITE_BASE + "1.png";
        final ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        bitmap = response;
                        Log.i("tag", "it shoulda worked");
                    }
                },
                50, 50, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("tag", "shit happend m89");
                    }
                });
        Log.i("tag", "test");
        return bitmap;
    }

    public static void getSpriteTest2(final ImageView imageView1) {
        Picasso.get().load(URL_SPRITE_BASE + "1.png").resize(300, 300).into(imageView1);
    }
}
