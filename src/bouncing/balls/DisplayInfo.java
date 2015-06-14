package bouncing.balls;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayInfo {

    public final long  refreshPeriod;
    public final float refreshRate;
    public final long  heightPixels;
    public final long  widthPixels;
    public final float density;
    public final float scaledDensity;
    public final long  densityDpi;
    public final float xDpi;
    public final float yDpi;

    public DisplayInfo(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        refreshRate = display.getRefreshRate();
        refreshPeriod = Math.round(1000.0f / refreshRate);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
        xDpi = displayMetrics.xdpi;
        yDpi = displayMetrics.xdpi;
        scaledDensity = displayMetrics.scaledDensity;
    }
}
