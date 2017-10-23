package com.yl.imageselector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 支持缩放和移动的ImageView
 * Created by Luke on 2017/9/15.
 */

public class ZoomImageView extends View {

    public static final int STATUS_INIT = 1;
    public static final int STATUS_ZOOM_OUT = 2;
    public static final int STATUS_ZOOM_IN = 3;
    public static final int STATUS_MOVE = 4;

    private Matrix matrix = new Matrix();
    private Bitmap sourceBitmap;
    private int currentStatus;
    private int width, height;
    // 缩放时的中心点
    private float centerPointX, centerPointY;
    // 当前图片的宽高
    private float currentWidth, currentHeight;
    // 上次手指移动的起点
    private float lastMoveX = -1, lastMoveY = -1;
    // 手指在坐标轴方向的移动距离
    private float movedDistanceX, movedDistanceY;
    // 图片在矩阵上的横纵向偏移值
    private float totalTranslateX, totalTranslateY;
    // 图片在矩阵上的总缩放比例
    private float totalRatio;
    // 手指移动距离所造成的缩放比例
    private float scaleRatio;
    // 图片初始化时的缩放比例
    private float initRatio;
    // 上次两指间的距离
    private double lastFingerDis;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        currentStatus = STATUS_INIT;
    }

    public void setImageBitmap(Bitmap bitmap) {
        sourceBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            width = getWidth();
            height = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 保证原始尺寸下，在ViewPager中可以左右滑动
        if (initRatio == totalRatio) {
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    lastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    float xMove = event.getX();
                    float yMove = event.getY();
                    if (lastMoveX == -1 && lastMoveY == -1) {
                        lastMoveX = xMove;
                        lastMoveY = yMove;
                    }
                    currentStatus = STATUS_MOVE;
                    movedDistanceX = xMove - lastMoveX;
                    movedDistanceY = yMove - lastMoveY;
                    // 边界检查
                    if (totalTranslateX + movedDistanceX > 0) {
                        movedDistanceX = 0;
                    } else if (width - (totalTranslateX + movedDistanceX) >
                            currentWidth) {
                        movedDistanceX = 0;
                    }
                    if (totalTranslateY + movedDistanceY > 0) {
                        movedDistanceY = 0;
                    } else if (height - (totalTranslateY + movedDistanceY) >
                            currentHeight) {
                        movedDistanceY = 0;
                    }
                    invalidate();
                    lastMoveX = xMove;
                    lastMoveY = yMove;
                } else if (event.getPointerCount() == 2) {
                    centerPointBetweenFingers(event);
                    double fingerDis = distanceBetweenFingers(event);
                    if (fingerDis > lastFingerDis) {
                        currentStatus = STATUS_ZOOM_OUT;
                    } else {
                        currentStatus = STATUS_ZOOM_IN;
                    }
                    // 缩放倍数检查
                    if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
                            || (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
                        scaleRatio = (float) (fingerDis / lastFingerDis);
                        totalRatio = totalRatio * scaleRatio;
                        if (totalRatio > 4 * initRatio) {
                            totalRatio = 4 * initRatio;
                        } else if (totalRatio < initRatio) {
                            totalRatio = initRatio;
                        }
                        invalidate();
                        lastFingerDis = fingerDis;
                    }
                }
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    lastMoveX = -1;
                    lastMoveY = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastMoveX = -1;
                lastMoveY = -1;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentStatus) {
            case STATUS_INIT:
                initBitmap(canvas);
                break;
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoom(canvas);
                break;
            case STATUS_MOVE:
                move(canvas);
                break;
            default:
                break;
        }
    }

    private void move(Canvas canvas) {
        matrix.reset();
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        matrix.postScale(totalRatio, totalRatio);
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void zoom(Canvas canvas) {
        matrix.reset();
        matrix.postScale(totalRatio, totalRatio);
        float scaleWidth = sourceBitmap.getWidth() * totalRatio;
        float scaleHeight = sourceBitmap.getHeight() * totalRatio;
        float translateX;
        float translateY;
        if (currentWidth < width) {
            translateX = (width - scaleWidth) / 2;
        } else {
            translateX = totalTranslateX * scaleRatio + centerPointX * (1 - scaleRatio);
            if (translateX > 0) {
                translateX = 0;
            } else if (width - translateX > scaleWidth) {
                translateX = width - scaleWidth;
            }
        }
        if (currentHeight < height) {
            translateY = (height - scaleHeight) / 2;
        } else {
            translateY = totalTranslateY * scaleRatio + centerPointY * (1 - scaleRatio);
            if (translateY > 0) {
                translateY = 0;
            } else if (height - translateY > scaleHeight) {
                translateY = height - scaleHeight;
            }
        }
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        currentWidth = scaleWidth;
        currentHeight = scaleHeight;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            matrix.reset();
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();
            Log.e("ZoomImage", "initBitmap: " + bitmapWidth + ":" + bitmapWidth);
            Log.e("ZoomImage", "initBitmap: " + width + ":" + height);
            if (bitmapWidth > width || bitmapHeight > height) {
                if (bitmapWidth - width > bitmapHeight - height) {
                    float ratio = width / (bitmapWidth * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateY = (height - (bitmapHeight * ratio)) / 2f;
                    matrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                } else {
                    float ratio = height / (bitmapHeight * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    matrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                }
                currentWidth = bitmapWidth * initRatio;
                currentHeight = bitmapHeight * initRatio;
            } else {
                float translateX = (width - bitmapWidth) / 2f;
                float translateY = (height - bitmapHeight) / 2f;
                matrix.postTranslate(translateX, translateY);
                totalTranslateX = translateX;
                totalTranslateY = translateY;
                totalRatio = initRatio = 1f;
                currentWidth = bitmapWidth;
                currentHeight = bitmapHeight;
            }
            canvas.drawBitmap(sourceBitmap, matrix, null);
        }
    }

    // 计算两手指间的中点坐标
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    // 计算两手指间的距离
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }
}
