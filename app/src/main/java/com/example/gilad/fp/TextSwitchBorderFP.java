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
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * Created by Gilad on 8/4/2015.
 */
public class TextSwitchBorderFP extends RelativeLayout {

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

    public TextSwitchBorderFP(Context context, AttributeSet attrs) {
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

            for (int i = 0; i < 10 ; i++)
            {
                BorderTextSwitch child = new BorderTextSwitch(context, outlineColor, outlineWidth, textColor, bgColor);
                child.setId(i + 1);
                addView(child);
            }

            // Manually laying out the views in relation to each other.
            // First item of first row on top.
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(0).setLayoutParams(layout);

            // First item of last row on bottom.
            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(7).setLayoutParams(layout);

            // First item of row above bottom row.
            layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addRule(RelativeLayout.ABOVE, 8);
            layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            getChildAt(4).setLayoutParams(layout);

            // Align rows according to first item.
            for (int i = 1 ; i < 4; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            for (int i = 5 ; i < 7; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            for (int i = 8 ; i < 10; i++)
            {
                layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addRule(RelativeLayout.RIGHT_OF, i);
                layout.addRule(RelativeLayout.ALIGN_TOP, i);
                getChildAt(i).setLayoutParams(layout);
            }

            res = getResources();
            String[] numbers = res.getStringArray(R.array.numbers);

            for (int i = 0 ; i < 4 ; i++) {
                ((BorderTextSwitch) getChildAt(i)).setText(numbers[i]);
            }

            firstBatch();

            OnTouchListener topListen = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        topSelected = ((BorderTextSwitch) v).getText();
                        ((BorderTextSwitch) v).setBgColor(Color.DKGRAY);
                    }

                    else if (ev.getAction() == MotionEvent.ACTION_UP) {
                        topSelected = ((BorderTextSwitch) v).getText();
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
                        bottomSelected = ((BorderTextSwitch) v).getText();
                        ((BorderTextSwitch) v).setBgColor(Color.BLACK);
                    }
                    if (ev.getAction() == MotionEvent.ACTION_UP) {
                        bottomSelected = ((BorderTextSwitch) v).getText();
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

    public void setBgColor(int color)
    {
        bgColor = color;
        for (int i = 0 ; i < 10 ; i++)
        {
            ((BorderTextSwitch) getChildAt(i)).setBgColor(outlineColor);
        }
    }

    public void setOutlineColor (int color)
    {
        outlineColor = color;
        for (int i = 0 ; i < 10 ; i++)
        {
            ((BorderTextSwitch) getChildAt(i)).setBorderColor(outlineColor);
        }
    }

    public void setTextColor(int color) {
        textColor = color;
        for (int i = 0 ; i < 10 ; i++)
        {
            ((BorderTextSwitch) getChildAt(i)).setTextColor(textColor);
        }
    }

    public void setOutlineWidth(int width)
    {
        outlineWidth = width;
        for (int i = 0 ; i < 10 ; i++)
        {
            ((BorderTextSwitch) getChildAt(i)).setBorderWidth(outlineWidth);
        }
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

    public int getOutlineWidth()
    {
        return outlineWidth;
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

        // The are already lain out in relation to each other, only need to fix size.
        for (int i = 0 ; i < 4 ; i++)
        {
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).height = (int) yUnit;
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).width = (int) topX;
        }

        for (int i = 4 ; i < 10 ; i++)
        {
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).height = (int) yUnit;
            ((RelativeLayout.LayoutParams) getChildAt(i).getLayoutParams()).width = (int) bottomX;
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
            ((BorderTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(vehicles[i])));
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
            ((BorderTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(animals[i])));
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
            ((BorderTextSwitch) getChildAt(i + 4)).setText(new String(Character.toChars(clothes[i])));
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

    private class BorderTextSwitch extends TextSwitcher {

        private int borderColor;
        private int borderWidth;
        private int textColor;
        private RectF bounds;
        private Paint borderPaint;
        private int bgColor;
        private int fontSize;

        public BorderTextSwitch(Context context) {
            super(context);
        }

        private BorderTextSwitch(Context context, int borderColor, int borderWidth, final int textColor, int bgColor)
        {
            super(context);
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.textColor = textColor;
            this.bgColor = bgColor;
            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(this.borderColor);
            borderPaint.setStrokeWidth(this.borderWidth);

            setFactory(new ViewFactory() {
                @Override
                public View makeView() {
                    TextView retVal = new TextView(getContext());
                    retVal.setTextColor(textColor);
                    retVal.setGravity(Gravity.CENTER);
                    retVal.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return retVal;
                }
            });

            setInAnimation(context, R.anim.abc_fade_in);
            setOutAnimation(context, R.anim.abc_fade_out);

            setWillNotDraw(false);
        }

        public void setBorderColor(int color)
        {
            borderColor = color;
            borderPaint.setColor(color);
            invalidate();
        }

        public int getBorderColor()
        {
            return borderColor;
        }

        public void setBorderWidth(int width)
        {
            borderWidth = width;
            borderPaint.setStrokeWidth(width);
            invalidate();
        }

        public int getBorderWidth()
        {
            return borderWidth;
        }

        public void setTextColor(int color)
        {
            textColor = color;
            ((TextView) getCurrentView()).setTextColor(color);
            ((TextView) getNextView()).setTextColor(color);
        }

        public int getTextColor()
        {
            return textColor;
        }

        public void setBgColor(int color)
        {
            bgColor = color;
            setBackgroundColor(color);
        }

        public int getBgColor()
        {
            return bgColor;
        }

        public String getText()
        {
            return ((TextView) getCurrentView()).getText().toString();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            float xpad = (float) (getPaddingRight() + getPaddingLeft());
            float ypad = (float) (getPaddingLeft() + getPaddingRight());

            float ww = (float) w - xpad;
            float hh = (float) h - ypad;

            bounds = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + ww, getPaddingTop() + hh);
            // TODO: Check calculation of fontSize.
            fontSize = (int) (Math.min(ww, hh) / 3);
            ((TextView) getCurrentView()).setTextSize(fontSize);
            ((TextView) getNextView()).setTextSize(fontSize);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(bounds, borderPaint);
            setBackgroundColor(bgColor);
        }
    }
}







