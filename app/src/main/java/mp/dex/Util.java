package mp.dex;

import android.content.Context;

//this class should be used for miscellaneous methods that don't fit anywhere else. Maybe this will be moved into MainActivity.
public class Util {
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
