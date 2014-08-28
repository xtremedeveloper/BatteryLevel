package com.bg.batterylevel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by barone on 8/28/14.
 */
public class LevelView extends View {

    private static final int START_ANGLE = -90;

    private RectF mCircleRect = new RectF();
    private Paint mCirclePaint = new Paint();
    private Paint mTextPaint = new Paint();
    private DisplayMetrics mDisplayMetrics;

    private int mStrokeColor = Color.rgb(0x20, 0x65, 0xa0);
    private int mStrokeWidth = 20;
    private int mTextColor =  Color.WHITE;
    private float mTextSize = 24.0f;
    private int mValue = 100;
    private String mUnit = "%";
    private boolean mIsCharging = false;

    private int mViewSize;


    public LevelView(Context context) {
        super(context);
        init();
    }

    public LevelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LevelView, defStyle, 0);
        mStrokeColor = t.getColor(R.styleable.LevelView_strokeColor, mStrokeColor);
        mStrokeWidth = t.getInt(R.styleable.LevelView_strokeWidth, mStrokeWidth);
        mTextColor = t.getColor(R.styleable.LevelView_textColor, mTextColor);
        mTextSize = t.getDimension(R.styleable.LevelView_textSize, mTextSize);
        mValue = t.getInt(R.styleable.LevelView_value, mValue);
        mUnit = t.getString(R.styleable.LevelView_unit);
        t.recycle();

        mDisplayMetrics = getResources().getDisplayMetrics();

        init();
    }

    private void init() {

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(100);
        mCirclePaint.setColor(mStrokeColor);

        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        setTextSize(mTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getImprovedDefaultWidth(widthMeasureSpec);
        int height = getImprovedDefaultHeight(heightMeasureSpec);
        mViewSize = Math.min(width, height);

        setMeasuredDimension(width, height);
    }


    private int getImprovedDefaultHeight(int measureSpec) {
        //int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return getSuggestedMinimumHeight();
        }
        return specSize;
    }

    private int getImprovedDefaultWidth(int measureSpec) {
        //int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return getSuggestedMinimumWidth();
        }
        return specSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCircleRect.set(0, 0, mViewSize, mViewSize);
        mCircleRect.offset((getWidth() - mViewSize ) / 2,
                           (getHeight() - mViewSize) / 2);
        final int halfBorder = (int) (mCirclePaint.getStrokeWidth() / 2f + 8.0f);
        mCircleRect.inset(halfBorder, halfBorder);

        // draw full circle
        mCirclePaint.setAlpha(80);
        canvas.drawArc(mCircleRect, START_ANGLE, 360, false, mCirclePaint);

        // draw the progress
        float angle = 360 * mValue / 100;
        mCirclePaint.setAlpha(255);
        canvas.drawArc(mCircleRect, START_ANGLE, angle, false, mCirclePaint);

        // draw text
        final String text = String.format("%d%s", mValue, mUnit);
        int cx = (int) mCircleRect.centerX();
        int cy = (int) (mCircleRect.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2);
        setTextSize(mTextSize);
        canvas.drawText(text, cx, cy, mTextPaint);

        // draw is charging
        if (mIsCharging) {

            // make the text smaller
            setTextSize(mTextSize * 0.3f);
            cy += (mTextSize * mDisplayMetrics.scaledDensity) / 3;
            canvas.drawText(getResources().getText(R.string.charging).toString(),
                    cx, cy, mTextPaint);

        }


    }

    public void setIsCharging(boolean value) {
        mIsCharging = value;
        postInvalidate();
    }

    public void setValue(int value) {
        mValue = value;
        postInvalidate();
    }

    public void setTextSize(float textSize) {


        float px = mDisplayMetrics.scaledDensity * textSize;
        mTextPaint.setTextSize(px);
    }

}
