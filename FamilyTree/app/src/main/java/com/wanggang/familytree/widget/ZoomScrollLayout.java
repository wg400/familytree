package com.wanggang.familytree.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
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

    private Integer mLeft, mTop, mRight, mBottom;
    private int centerX, centerY;
    private float mLastScale = 1.0f;
    private float totleScale = 1.0f;

    // childview
    private View mChildView;

    // 拦截滑动事件
    float mDistansX, mDistansY, mTouchSlop;

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

//        mChildView.layout(((FamilyTreeView) mChildView).getLeftBorder() + centerX, ((FamilyTreeView) mChildView).getTopBorder() + DensityUtil.dip2px(getContext(), 64),
//                ((FamilyTreeView) mChildView).getRightBorder() + centerX, ((FamilyTreeView) mChildView).getBottomBorder() + DensityUtil.dip2px(getContext(), 64));

//        mChildView.layout(((com.wanggang.familytree.familytree.FamilyTreeView) mChildView).getLeftBorder() + centerX, ((com.wanggang.familytree.familytree.FamilyTreeView) mChildView).getTopBorder() + centerY,
//                ((com.wanggang.familytree.familytree.FamilyTreeView) mChildView).getRightBorder() + centerX, ((com.wanggang.familytree.familytree.FamilyTreeView) mChildView).getBottomBorder() + centerY);

    }

    public void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mode == MODE.DRAG) {
                    if (mChildView == null) {
                        mChildView = getChildAt(0);

                        centerX = getWidth() / 2;
                        centerY = getHeight() / 2;
                    }
                    if (mLeft == null) {
                        mLeft = mChildView.getLeft();
                        mTop = mChildView.getTop();
                        mRight = mChildView.getRight();
                        mBottom = mChildView.getBottom();
                    }

                    // 防抖动

                    Logger.i("distanceX=" + distanceX + ";distanceY=" + distanceY);
                    Logger.i("mLeft=" + mLeft + ";mTop=" + mTop);

                    mLeft = mLeft - (int) distanceX;
                    mTop = mTop - (int) distanceY;
                    mRight = mRight - (int) distanceX;
                    mBottom = mBottom - (int) distanceY;
                    mChildView.layout(mLeft, mTop, mRight, mBottom);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return super.onDown(e);
            }
        });

        // 系统最小滑动距离
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();
        int currentX = (int) e.getX();
        int currentY = (int) e.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //记录上次滑动的位置
                mDistansX = currentX;
                mDistansY = currentY;

                //将当前的坐标保存为起始点
                mode = MODE.DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDistansX - currentX) >= mTouchSlop || Math.abs(mDistansY - currentY) >= mTouchSlop) { //父容器拦截
                    return true;
                }
                break;
            //指点杆保持按下，并且进行位移
            //有手指抬起，将模式设为NONE
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
                break;
        }
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
        Logger.i("MotionEvent.onScaleBegin");
        mode = MODE.ZOOM;
        if (mode == MODE.ZOOM) {
            if (mChildView == null) {
                mChildView = getChildAt(0);

                centerX = getWidth() / 2;
                centerY = getHeight() / 2;
            }

            mLeft = mChildView.getLeft();
            mTop = mChildView.getTop();
            mRight = mChildView.getRight();
            mBottom = mChildView.getBottom();
        }

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        mLastScale = totleScale;
    }

}
