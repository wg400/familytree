package com.wanggang.familytree.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import com.orhanobut.logger.Logger;
import com.wanggang.familytree.DensityUtil;
import com.wanggang.familytree.familytree.FamilyMemberLayout;

/**
 * Created by wg on 2017/4/21.
 */

public class ZoomScrollLayout extends RelativeLayout implements ScaleGestureDetector.OnScaleGestureListener {

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private static final float MIN_ZOOM = 0.3f;
    private static final float MAX_ZOOM = 3.0f;

    private int centerX, centerY;
    private float mLastScale = 1.0f;
    private float totleScale = 1.0f;

    // childview
    private View mChildView;

    private enum MODE {
        ZOOM, DRAG, NONE
    }

    private MODE mode;

    public ZoomScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public ZoomScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mChildView = getChildAt(0);

        centerX = getWidth() / 2;

        centerY = getHeight() / 2;

        mChildView.layout(((FamilyMemberLayout) mChildView).getLeftBorder() + centerX, ((FamilyMemberLayout) mChildView).getTopBorder() + DensityUtil.dip2px(getContext(), 64),
                ((FamilyMemberLayout) mChildView).getRightBorder() + centerX, ((FamilyMemberLayout) mChildView).getBottomBorder() + DensityUtil.dip2px(getContext(), 64));

    }

    public void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mChildView == null) {
                    mChildView = getChildAt(0);

                    centerX = getWidth() / 2;

                    centerY = getHeight() / 2;
                }
                mChildView.setTranslationX(mChildView.getTranslationX() - distanceX);
                mChildView.setTranslationY(mChildView.getTranslationY() - distanceY);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return super.onDown(e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        Logger.i("MotionEvent.onScale");
        if (mode == MODE.ZOOM) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float tempScale = mLastScale * scaleFactor;
            if (tempScale <= MAX_ZOOM && tempScale >= MIN_ZOOM) {
                totleScale = tempScale;
                applyScale(totleScale);
            }
        }
        return false;
    }

    /**
     * 执行缩放操作
     */
    public void applyScale(float scale) {
        mChildView.setScaleX(scale);
        mChildView.setScaleY(scale);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        mode = MODE.ZOOM;
        if (mChildView == null) {
            mChildView = getChildAt(0);

            centerX = getWidth() / 2;

            centerY = getHeight() / 2;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        mLastScale = totleScale;
    }

}
