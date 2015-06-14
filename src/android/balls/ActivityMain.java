package android.balls;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;
import bouncing.balls.BouncingBalls;

public class ActivityMain extends Activity {

    private BouncingBalls viewBallPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBallPane = new BouncingBalls(this);
        setContentView(viewBallPane, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActionBar().hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBallPane.release();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Toast.makeText(this, R.string.start_toast, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO save instance state
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //TODO restore instance state

    }
}

