package com.example.gilad.fp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Gilad on 8/4/2015.
 */
public class FP extends ViewGroup {

    private int bgColor;
    private int textColor;
    private int outlineColor;
    private int space;
    private int outlineWidth;
    private String pass = "";
    private String topSelected = "";
    private String bottomSelected = "";
    private enum Batch {FIRST, SECOND, THIRD};
    private Batch curBatch;
    private Resources res;

    public FP(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FP,
                0, 0);
        try {
            bgColor = a.getColor(R.styleable.FP_bgColor, Color.DKGRAY);
            textColor = a.getColor(R.styleable.FP_fontColor, Color.WHITE);
            outlineColor = a.getColor(R.styleable.FP_borderColor, Color.BLACK);
            space = a.getInt(R.styleable.FP_space, 20);
            outlineWidth = a.getInt(R.styleable.FP_borderWidth, 10);


            for (int i = 0; i < 10; i++) {
                addView(new BorderTextView(context, outlineColor, outlineWidth), i);
                ((BorderTextView) getChildAt(i)).setGravity(Gravity.CENTER);
                getChildAt(i).setBackgroundColor(bgColor);
            }

            res = getResources();
            String[] numbers = res.getStringArray(R.array.numbers);

            for (int i = 0 ; i < 4 ; i++) {
                ((BorderTextView) getChildAt(i)).setText(numbers[i]);
            }

            firstBatch();

            colorText();

            OnTouchListener topListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        topSelected = ((BorderTextView) v).getText().toString();
                    }

                    else if (ev.getAction() == MotionEvent.ACTION_UP) {
                        topSelected = ((BorderTextView) v).getText().toString();
                        if (!bottomSelected.equals("")) {
                            nextBatch();
                        }
                    }

                    return true;
                }

            };

            OnTouchListener bottomListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        bottomSelected = ((BorderTextView) v).getText().toString();
                    }
                    if (ev.getAction() == MotionEvent.ACTION_UP) {
                        bottomSelected = ((BorderTextView) v).getText().toString();
                        if (!topSelected.equals("")) {
                            nextBatch();
                        }
                    }

                    return true;
                }
            };

            for (int i = 0 ; i < 4 ; i++)
            {
                getChildAt(i).setOnTouchListener(topListen);
            }

            for (int i = 4 ; i < 10 ; i++)
            {
                getChildAt(i).setOnTouchListener(bottomListen);
            }

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public void setBgColor(int color)
    {
        bgColor = color;
        invalidate();
    }

    public void setOutlineColor (int color)
    {
        outlineColor = color;
        invalidate();
    }

    public void setTextColor(int color) {
        textColor = color;
        colorText();
    }

    public int getBgColor()
    {
        return bgColor;
    }

    public int getTextColor()
    {
        return textColor;
    }

    public int getOutlineColor(){
        return outlineColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float) (getPaddingRight() + getPaddingLeft());
        float ypad = (float) (getPaddingLeft() + getPaddingRight());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad - space;

        // Calculate the size of a cell in a grid of 18 by 9 in the layout.
        // Leave two cells of padding in all directions, and a cell between any two characters.
        float topX = ww / 4;
        float bottomX = ww / 3;
        float yUnit = hh / 3;

        int top = getPaddingTop();
        int bottom = top + (int) yUnit;

        int widthMeasure = MeasureSpec.makeMeasureSpec((int) (topX), MeasureSpec.EXACTLY);
        int heightMeasure = MeasureSpec.makeMeasureSpec((int) (yUnit), MeasureSpec.EXACTLY);

        int fontSize = (int) Math.min(topX, yUnit) / 3;

        int left = 0;
        int right = (int) (getPaddingLeft());

        for (int i = 0 ; i < 4 ; i++)
        {
            left = right;
            right = left + (int) topX;

            getChildAt(i).measure(widthMeasure, heightMeasure);
            getChildAt(i).layout(left, top, right, bottom);
            ((BorderTextView) getChildAt(i)).setTextSize(fontSize);
        }

        widthMeasure = MeasureSpec.makeMeasureSpec((int) (bottomX), MeasureSpec.EXACTLY);

        fontSize = (int) Math.min (bottomX, yUnit) / 3;

        top = bottom + space;
        bottom = top +(int) yUnit;

        right = getPaddingLeft();

        for (int i = 0 ; i < 3 ; i++)
        {
            left = right;
            right = left + (int) bottomX;

            getChildAt(i + 4).measure(widthMeasure, heightMeasure);
            getChildAt(i + 4).layout(left, top, right, bottom);
            ((BorderTextView) getChildAt(i + 4)).setTextSize(fontSize);
        }

        top = bottom;
        bottom += (int) yUnit;

        right = getPaddingLeft();
        for (int i = 0 ; i < 3 ; i++)
        {
            left = right;
            right = left + (int) bottomX;

            getChildAt(i + 7).measure(widthMeasure, heightMeasure);
            getChildAt(i + 7).layout(left, top, right, bottom);
            ((BorderTextView) getChildAt(i + 7)).setTextSize(fontSize);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        float x = ev.getX();
        float y = ev.getY();
        for (int i = 0 ; i < 10 ; i++) {
            View child = getChildAt(i);
            if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                return child.dispatchTouchEvent(ev);
            }
        }
        return true;
    }

    void firstBatch()
    {
        curBatch = Batch.FIRST;
        topSelected = "";
        bottomSelected = "";
        pass = "";

        int[] vehicles = res.getIntArray(R.array.vehicles);

        for (int i = 0; i < 6; i++)
        {
            ((BorderTextView) getChildAt(i + 4)).setText(new String(Character.toChars(vehicles[i])));
        }
    }

    void secondBatch()
    {
        curBatch = Batch.SECOND;
        topSelected = "";
        bottomSelected = "";

        int[] animals = res.getIntArray(R.array.animals);

        for (int i = 0; i < 6; i++)
        {
            ((BorderTextView) getChildAt(i + 4)).setText(new String(Character.toChars(animals[i])));
        }
    }

    void thirdBatch()
    {
        curBatch = Batch.THIRD;
        topSelected = "";
        bottomSelected = "";

        int[] clothes = res.getIntArray(R.array.clothes);

        for (int i = 0; i < 6; i++)
        {
            ((BorderTextView) getChildAt(i + 4)).setText(new String(Character.toChars(clothes[i])));
        }
    }

    private void colorText()
    {
        for (int i = 0 ; i < 10 ; i++)
        {
            ((BorderTextView)getChildAt(i)).setTextColor(textColor);
        }
    }

    private void nextBatch()
    {
        pass += topSelected + bottomSelected;
        topSelected = "";
        bottomSelected = "";
        switch(curBatch)
        {
            case FIRST:
                secondBatch();
                break;
            case SECOND:
                thirdBatch();
                break;
            case THIRD:
                Toast.makeText(getContext(), pass, Toast.LENGTH_SHORT).show();
                pass = "";
                firstBatch();
                break;
        }
    }

    private class BorderTextView extends TextView{

        private int borderColor;
        private int borderWidth;
        private RectF bounds;
        private Paint borderPaint;

        public BorderTextView(Context context) {
            super(context);
            borderColor = Color.BLACK;
            borderWidth = 10;
            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
        }

        public BorderTextView(Context context, int color, int width)
        {
            super(context);
            borderColor = color;
            borderWidth = width;
            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
        }

        public void setBorderColor(int color)
        {
            borderColor = color;
            invalidate();
        }

        public int getBorderColor()
        {
            return borderColor;
        }

        public void setBorderWidth(int width)
        {
            borderWidth = width;
            invalidate();
        }

        public int getBorderWidth()
        {
            return borderWidth;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            float xpad = (float) (getPaddingRight() + getPaddingLeft());
            float ypad = (float) (getPaddingLeft() + getPaddingRight());

            float ww = (float) w - xpad;
            float hh = (float) h - ypad;

            bounds = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + hh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            borderPaint.setColor(borderColor);
            borderPaint.setStrokeWidth(borderWidth);
            canvas.drawRect(bounds, borderPaint);
        }
    }
}







