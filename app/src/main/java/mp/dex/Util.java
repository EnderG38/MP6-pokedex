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

    public static int dmToIn(double dm) {
        return (int) Math.round(dm * 3.93701);
    }

    public static String dmToFtAndIn(double dm) {
        String str = "x' y\"";
        int in = dmToIn(dm);
        int ft = in / 12;
        in %= 12;
        return str.replace("x", "" + ft).replace("y", "" + in);
    }

    public static double hgToLb(double hg) {
        return Math.round(hg / 10d * 2.20462 * 10d) / 10d;
    }

    public static int getDrawable(String id, Context context) {
        return context.getResources().getIdentifier(id, "drawable", context.getPackageName());
    }
}
