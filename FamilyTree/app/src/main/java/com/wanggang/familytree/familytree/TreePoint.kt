package com.wanggang.familytree.familytree

import android.graphics.Path
import android.graphics.PathMeasure

class TreePoint(var coordinateX: Int, var coordinateY: Int) {

    // 屏幕坐标系坐标
    var screenX = 0
    var screenY = 0

    // 配偶屏幕坐标系坐标（当有配偶时有效）
    var spouseScreenX = 0

    // 是否包含配偶节点
    var hasSpouseNode = false

    /**
     * 链接点位置，1：左侧节点链接 0：右侧节点链接
     * 当hasSpouseNode = true时有效
     */
    var connectType = 0

    var drawPath: Path? = null

    var mPathMeasure: PathMeasure? = null

    // 父亲节点的位置信息
    var parentPoint: TreePoint? = null



    /**
     * 计算当前节点屏幕坐标
     * */
    fun calculateCoord(itemWidth: Int, itemHeight: Int, lineSpace: Int, colSpace: Int, left: Int, top: Int) {
        screenX = (coordinateX - left + 4) * (itemWidth + colSpace) / 2
        screenY = (- coordinateY - top + 4) * (itemHeight + lineSpace)
    }

    /**
     * 绘制两个点之间的连线
     */
    fun drawConnection() {
        if (this.parentPoint == null) {
            return
        }
        var itemHeight = FamilyTreeAdapter.itemHeight
        var lineSpace = FamilyTreeAdapter.lineSpace
        var radius = FamilyTreeAdapter.radius
        if (drawPath == null) {
            drawPath = Path()
            val thisPoint = this
            val parentPoint = this.parentPoint!!

//            var ponit: TreePoint? = null
//            var connectionPonit: TreePoint? = null
//            val startX: Int
//            // 与父母连线
//            if (thisPoint.screenY < thisConnectionPonit!!.screenY) {
//                ponit = thisPoint
//                connectionPonit = thisConnectionPonit
//            } else {
//                ponit = thisConnectionPonit
//                connectionPonit = thisPoint
//            }

            var connectionPonitX = thisPoint.screenX
            if (thisPoint.hasSpouseNode) {
                if (thisPoint.connectType == 1) {
                    connectionPonitX -= (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
                } else {
                    connectionPonitX += (FamilyTreeAdapter.itemWidth + FamilyTreeAdapter.colSpace) / 2
                }
            }

            if (parentPoint.screenX != connectionPonitX) {
//                drawPath!!.moveTo(ponit.screenX.toFloat(), (ponit.screenY + itemHeight / 2).toFloat())
                if (parentPoint.hasSpouseNode) {
                    drawPath!!.moveTo(parentPoint.screenX.toFloat(), parentPoint.screenY.toFloat())
                } else {
                    drawPath!!.moveTo(parentPoint.screenX.toFloat(), (parentPoint.screenY + itemHeight / 2).toFloat())
                }
                drawPath!!.lineTo(parentPoint.screenX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2 - radius).toFloat())
                if (parentPoint.screenX > connectionPonitX) {
                    // 如果父母的位置在右边，则弧线的方向向左
                    drawPath!!.quadTo(
                        parentPoint.screenX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat(),
                        (parentPoint.screenX - radius).toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat()
                    )
                    drawPath!!.lineTo(
                        (connectionPonitX + radius).toFloat(),
                        (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat()
                    )
                    drawPath!!.quadTo(
                        connectionPonitX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat(),
                        connectionPonitX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2 + radius).toFloat()
                    )
                } else {
                    drawPath!!.quadTo(
                        parentPoint.screenX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat(),
                        (parentPoint.screenX + radius).toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat()
                    )
                    drawPath!!.lineTo(
                        (connectionPonitX - radius).toFloat(),
                        (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat()
                    )
                    drawPath!!.quadTo(
                        connectionPonitX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2).toFloat(),
                        connectionPonitX.toFloat(), (parentPoint.screenY + itemHeight / 2 + lineSpace / 2 + radius).toFloat()
                    )
                }
                drawPath!!.lineTo(connectionPonitX.toFloat(), (thisPoint.screenY - itemHeight / 2).toFloat())
            } else {
                if (parentPoint.hasSpouseNode) {
                    drawPath!!.moveTo(connectionPonitX.toFloat(), parentPoint.screenY.toFloat())
                } else {
                    drawPath!!.moveTo(connectionPonitX.toFloat(), (parentPoint.screenY + itemHeight / 2).toFloat())
                }
                drawPath!!.lineTo(connectionPonitX.toFloat(), (thisPoint.screenY - itemHeight / 2).toFloat())
            }
        }
        if (drawPath != null && mPathMeasure == null) {
            mPathMeasure = PathMeasure(drawPath, false)
        }
    }

    fun getSegment(mPercent: Float): Path? {
        val dst = Path()
        dst.rLineTo(0f, 0f)
        return if (mPathMeasure!!.getSegment(0f, mPercent * mPathMeasure!!.length, dst, true)) {
            dst
        } else drawPath
    }

    override fun toString(): String {
        return "{coordinateX=$coordinateX, coordinateY=$coordinateY, screenX=$screenX, screenY=$screenY, spouseScreenX=$spouseScreenX, connectType=$connectType}"
    }
}