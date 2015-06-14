package bouncing.balls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class BouncingBalls extends View {

    public static final String TAG = "BouncingBallsView";

    private static final int MIN_DIAMETER = 5;
    private static final int MAX_DIAMETER = 50;
    private static final int MIN_LIFE_TIME = 10;
    private static final int MAX_LIFE_TIME = 100;
    private static final int BALLS_LIMIT = 200;

    public boolean mShowInfo;
    private DisplayInfo mDisplayInfo;
    private Paint mPaint;
    private List<Ball> mBallList;

//    private TimerTask mTimerTask;
//    private Timer mTimer;

    private class Ball {

        private View mParent;

        private boolean mIsAlive;

        private int mLifeTimeCounter;
        private int mAlpha, mColor;
        private int mDiameter;
        private PointF mCurPos;
        private PointF mDelta;
        private Paint mPaint;

        public Ball(View parent, PointF startPos, int color, int diameter, int lifeTime, PointF delta) {
            mParent = parent;
            mCurPos = startPos;
            mColor = color;
            mDiameter = diameter;
            mLifeTimeCounter = lifeTime;
            mDelta = delta;
            mPaint = new Paint();
            mAlpha = 0x07;
            mIsAlive = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (mIsAlive) {
                            move();
                            succeed();

                            synchronized (mParent) {
                                mParent.wait();
                            }
                        }
                    } finally {
                        return;
                    }
                }
            }).start();
        }

        public boolean isAlive() {
            return mIsAlive;
        }

        public void finish() {
            mIsAlive = false;
        }

        public void paint(Canvas canvas) {
            if (!mIsAlive) return;
            mPaint.setColor(mColor);
            canvas.drawCircle(mCurPos.x, mCurPos.y, (float) mDiameter, mPaint);
        }

        private void move() {
            mCurPos.offset(mDelta.x, mDelta.y);

            if ((mCurPos.x <= mDiameter && mDelta.x <= 0)
                    || (mCurPos.x + mDiameter >= mParent.getWidth() && mDelta.x >= 0)) {
                mDelta.x = -mDelta.x;
            }

            if ((mCurPos.y <= mDiameter && mDelta.y <= 0)
                    || (mCurPos.y + mDiameter >= mParent.getHeight() && mDelta.y >= 0)) {
                mDelta.y = -mDelta.y;
            }
        }

        private void succeed() {
            if (mLifeTimeCounter <= 0) {
                //animate ball explosion
                if (mAlpha < 0xFF) {
                    mDiameter += mDiameter >> 2;
                    mColor = 0x00FFFFFF | mAlpha << 24;
                    mAlpha = mAlpha << 2 | mAlpha;
                } else {
                    mIsAlive = false;
                }
            }

            mLifeTimeCounter--;
        }
    } // End of private class Ball

    public BouncingBalls(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public BouncingBalls(Context context) {
        super(context);

        init(context);
    }

    public void spawn(PointF startPos) {
        int diameter = rnd(MIN_DIAMETER, MAX_DIAMETER);
        int massFactor = MAX_DIAMETER / diameter;
        int color = Color.rgb(rnd(0x00, 0xFF), rnd(0x00, 0xFF), rnd(0x00, 0xFF));
        int lifeTime = rnd(MIN_LIFE_TIME, MAX_LIFE_TIME);

        PointF delta = new PointF();

        delta.x = dir() * massFactor * rnd(0x01, 0x04);
        delta.y = dir() * massFactor * rnd(0x01, 0x04);

        spawn(startPos, delta, color, diameter, lifeTime);
    }

    public void spawn(PointF startPos, PointF delta, int color, int diameter, int lifeTime) {
        if (mBallList.size() >= BALLS_LIMIT) return;
        mBallList.add(new Ball(this, startPos, color, diameter, lifeTime, delta));
    }

    public void release(){

        for (Ball ball: mBallList){
            if (ball.isAlive()) ball.finish();
        }

//        mTimerTask.cancel();
//        mTimer.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Ball ball;

        Iterator<Ball> iter = mBallList.iterator();

        while (iter.hasNext()){
            ball = iter.next();
            if (ball.isAlive()) ball.paint(canvas);
            else iter.remove();
        }

        if (mShowInfo) showInfo(canvas);

        synchronized (this){
            notifyAll();
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0, count = event.getPointerCount(); i < count; i++) {
                    spawn(new PointF(event.getX(i), event.getY(i)));
                }
                break;
        }
        return true;
    }

    private void init(Context context) {
        mDisplayInfo = new DisplayInfo(context);
        mPaint = new Paint();
        mBallList = new ArrayList<Ball>();
        mShowInfo = true;

//        mTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                postInvalidate();
//            }
//        };
//
//        mTimer = new Timer();
//        mTimer.schedule(mTimerTask, 0, mDisplayInfo.refreshPeriod);
    }

    private int rnd(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    private int dir() {
        return rnd(0, 65535) >= 32767 ? 1 : -1;
    }

    private void showInfo(Canvas canvas){
        int textSize = 24;
        int textInterval = textSize + 2;
        int line = 1;

        mPaint.setColor(Color.YELLOW);
        mPaint.setTextSize(textSize);

        canvas.drawText("Balls spawned: "   + mBallList.size(), 0, textInterval * line++, mPaint);
        canvas.drawText("Active threads: "  + Thread.activeCount(), 0, textInterval * line++, mPaint);
        canvas.drawText("Time slice: "      + mDisplayInfo.refreshPeriod + "ms", 0, textInterval * line++, mPaint);
        canvas.drawText("Display height: "  + mDisplayInfo.heightPixels  + "px", 0, textInterval * line++, mPaint);
        canvas.drawText("Display width: "   + mDisplayInfo.widthPixels   + "px", 0, textInterval * line++, mPaint);
        canvas.drawText("Display density: " + mDisplayInfo.densityDpi    + "dpi", 0, textInterval * line++, mPaint);
        canvas.drawText("Refresh rate: "    + mDisplayInfo.refreshRate   + "Hz", 0, textInterval * line++, mPaint);
    }
}
