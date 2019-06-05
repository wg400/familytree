package com.wanggang.familytree.familytree

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wanggang.familytree.Main2Activity
import com.wanggang.familytree.R
import com.wanggang.familytree.dp
import com.wanggang.familytree.model.FamilyMemberModel

class FamilyMemberLayout : ViewGroup {

    // 数据源适配器
    var familyTreeAdapter: FamilyTreeAdapter? = null

    var canMeasure = false
    var canLayout = false

    var mAnimator: ValueAnimator? = null
    var mPercent: Float = 0f
    var mPaint: Paint? = null

    private var mDialog: AlertDialog? = null
    private var mClickModel: FamilyMemberModel? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 1f.dp
        mPaint!!.color = Color.parseColor("#999999")
        mPaint!!.isAntiAlias = true
        setWillNotDraw(false)

        mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator!!.duration = 1000
        mAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            mPercent = valueAnimator.animatedValue as Float
            invalidate()
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (canMeasure) {
            canMeasure = false
            for (i in 0 until childCount) {
                measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
            }
            setMeasuredDimension(familyTreeAdapter!!.realWidth, familyTreeAdapter!!.realHeight)
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (canLayout) {
            canLayout = false
            for (i in 0 until childCount) {
                (getChildAt(i) as FamilyMemberView).layoutSelf()
                getChildAt(i).setOnClickListener(memberClick)
            }

            mAnimator!!.start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mPercent > 0) {
            for (i in 0 until childCount) {
                (getChildAt(i) as FamilyMemberView).drawPath(canvas!!, mPaint!!, mPercent)
            }
        }

    }

    fun displayUI() {

        removeAllViews()

        val childSize = familyTreeAdapter!!.dataList!!.size
        for (i in 0 until childSize) {
            var view = FamilyMemberView(context)
            view.familyMemberModel = familyTreeAdapter!!.dataList!![i]
            addView(view)
//            addViewInLayout(view, i, LayoutParams(FamilyTreeAdapter.itemWidth, FamilyTreeAdapter.itemHeight))
        }
        canMeasure = true
        canLayout = true
        requestLayout()
    }

    fun getLeftBorder(): Int {
        return (familyTreeAdapter!!.left - 2) * (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
    }

    fun getTopBorder(): Int {
        return (familyTreeAdapter!!.top - 1) * (FamilyTreeAdapter.itemHeight + FamilyTreeAdapter.lineSpace)
    }

    fun getRightBorder(): Int {
        return (familyTreeAdapter!!.right + 2) * (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
    }

    fun getBottomBorder(): Int {
        return (familyTreeAdapter!!.bottom + 1) * (FamilyTreeAdapter.itemHeight + FamilyTreeAdapter.lineSpace)
    }

    private var memberClick = OnClickListener {
        mClickModel = (it as FamilyMemberView).familyMemberModel
        println("mClickModel = $mClickModel")
        if (mDialog == null) {
            mDialog = AlertDialog.Builder(context).setItems(
                R.array.dialog_menu
            ) { dialog, which ->
                dialog.dismiss()
                if (which == 3) {

                } else {
                    val intent = Intent(context, Main2Activity::class.java)
                    when (which) {
                        0 -> intent.putExtra("id", mClickModel?.memberEntity?.id)
                        1 -> intent.putExtra("fatherId", mClickModel?.memberEntity?.id)
                        2 -> intent.putExtra("spouseId", mClickModel?.memberEntity?.id)
                    }
                    (context as Activity).startActivityForResult(intent, 111)
                }
            }.create()
        }
        mDialog?.show()
    }


}