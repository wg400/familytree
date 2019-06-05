package com.wanggang.familytree.widget;

import android.graphics.Path;
import android.graphics.PathMeasure;

/**
 * Created by wg on 2017/4/25.
 * Ponit 起始点
 * （point，拼写错了，就这样吧）
 */

public class Ponit {

    // 普通坐标系坐标
    public int coordinateX;

    // 普通坐标系坐标
    public int coordinateY;

    // 屏幕坐标系坐标
    public int x;

    public int y;

    public int exportType; // 出线的方式 0、底部 1、左边、 2、右边

    public Ponit connecPonit;

    Path drawPath;

    PathMeasure mPathMeasure;

    public Ponit(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }


    public boolean contain(Ponit ponit, int pw, int colSpace) {
        if (this.y == ponit.y) {
            return Math.abs(this.x - ponit.x) < pw + colSpace;
        }
        return false;
    }

    /**
     * 绘制两个点之间的连线
     * */
    public void drawConnection(int itemWidth, int itemHeight, int lineSpace, int colSpace, int radius) {
        if (this.connecPonit == null) {
            return;
        }
        if (drawPath == null) {
            drawPath = new Path();
            Ponit thisPoint = this;
            Ponit thisConnectionPonit = this.connecPonit;

            Ponit ponit = null;
            Ponit connectionPonit = null;
            int startX;
            // 与父母连线
            if (thisPoint.y < thisConnectionPonit.y) {
                ponit = thisPoint;
                connectionPonit = thisConnectionPonit;
            } else {
                ponit = thisConnectionPonit;
                connectionPonit = thisPoint;
            }
            if (ponit.x != connectionPonit.x) {
                drawPath.moveTo(ponit.x, ponit.y + itemHeight / 2);
                drawPath.lineTo(ponit.x, ponit.y + itemHeight / 2 + lineSpace / 2 - radius);
                if (ponit.x > connectionPonit.x) {
                    // 如果父母的位置在右边，则弧线的方向向左
                    drawPath.quadTo(ponit.x, ponit.y + itemHeight / 2 + lineSpace / 2,
                            ponit.x - radius, ponit.y + itemHeight / 2 + lineSpace / 2);
                    drawPath.lineTo(connectionPonit.x + radius, ponit.y + itemHeight / 2 + lineSpace / 2);
                    drawPath.quadTo(connectionPonit.x, ponit.y + itemHeight / 2 + lineSpace / 2,
                            connectionPonit.x, ponit.y + itemHeight / 2 + lineSpace / 2 + radius);
                } else {
                    drawPath.quadTo(ponit.x, ponit.y + itemHeight / 2 + lineSpace / 2,
                            ponit.x + radius, ponit.y + itemHeight / 2 + lineSpace / 2);
                    drawPath.lineTo(connectionPonit.x - radius, ponit.y + itemHeight / 2 + lineSpace / 2);
                    drawPath.quadTo(connectionPonit.x, ponit.y + itemHeight / 2 + lineSpace / 2,
                            connectionPonit.x, ponit.y + itemHeight / 2 + lineSpace / 2 + radius);
                }
                drawPath.lineTo(connectionPonit.x, connectionPonit.y - itemHeight / 2);
            } else {
                drawPath.moveTo(connectionPonit.x, ponit.y + itemHeight / 2);
                drawPath.lineTo(connectionPonit.x, connectionPonit.y - itemHeight / 2);
            }
        }
        if (drawPath != null && mPathMeasure == null) {
            mPathMeasure = new PathMeasure(drawPath, false);
        }
    }

    public Path getSegment(float mPercent) {
        Path dst = new Path();
        dst.rLineTo(0, 0);
        if (mPathMeasure.getSegment(0, mPercent * mPathMeasure.getLength(), dst, true)) {
            return dst;
        }
        return drawPath;
    }
}
