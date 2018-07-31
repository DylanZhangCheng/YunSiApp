package com.lehua.tablet0306.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Czj on 2018/7/31
 */
public class RadarView extends View {
    private int count = 9;                //数据个数
    private float angle = (float) (Math.PI * 2 / count);
    private float radius;                   //网格最大半径
    private int centerX;                  //中心X
    private int centerY;                  //中心Y
    private String[] titles = {"语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理"};  //各维度标签
    //    private double[] data = {3,3,3,3,3,3}; //各维度分值
    private ArrayList<Float> data = new ArrayList<Float>();  //各维度值
    private float maxValue = 5;             //数据最大值
    private Paint mainPaint;                //雷达区画笔
    private Paint innerValuePaint;               //数据区画笔
    private Paint textPaint;                //文本画笔
    private Paint outerValuePaint;                //雷达区边框画笔
    private float circleRadius = 2;                //圆点半径
    private int lableCount = 6;                //Y轴值个数
    private int innerAlpha = 166;                //蜘蛛网局部透明度
    private float strokeWidth = 2;                //蜘蛛网边框宽度
    private boolean drawLabels = false;                //绘制轴值
    private boolean showValueText = false;                //是否显示轴上的值
    private float circleZero = (float) Math.PI / 2;        //初始角度为90度


    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }


    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context) {
        super(context);
        init();
    }


    //初始化
    private void init() {
        count = titles.length;
        angle = (float) (Math.PI * 2 / count);

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Paint.Style.STROKE);

        innerValuePaint = new Paint();
        innerValuePaint.setAntiAlias(true);
        innerValuePaint.setColor(Color.BLUE);
        innerValuePaint.setStyle(Paint.Style.FILL);
        outerValuePaint = new Paint();
        outerValuePaint.setAntiAlias(true);
        outerValuePaint.setColor(Color.BLUE);
        outerValuePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setTextSize(14);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w) / 2 * 0.8f;
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        count = titles.length;
        angle = (float) (Math.PI * 2 / count);

        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
    }

    /**
     * 绘制正多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = radius / (lableCount - 1);
        for (int i = 1; i < lableCount; i++) {
            float curR = r * i;
            path.reset();
            for (int j = 0; j < count; j++) {
                if (j == 0) {
                    path.moveTo(centerX, centerY - curR);
                    if (drawLabels) {
                        String text = String.valueOf(Float.valueOf((maxValue / (lableCount - 1)) * i));
                        float dis = textPaint.measureText(text);//文本长度
                        canvas.drawText(text, centerX, centerY - curR, textPaint);
                    }
                } else {
                    float x = (float) (centerX + curR * Math.cos(angle * j - circleZero));
                    float y = (float) (centerY + curR * Math.sin(angle * j - circleZero));
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i - circleZero));
            float y = (float) (centerY + radius * Math.sin(angle * i - circleZero));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < count; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i - circleZero));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i - circleZero));
            float dis = textPaint.measureText(titles[i]);//文本长度
            if (i < count / 2 && i > 0) {//第12象限
                canvas.drawText(titles[i], x, y, textPaint);
            } else if (i > count / 2) {//第34象限
                canvas.drawText(titles[i], x - dis, y, textPaint);
            } else if (i == 0) {
                canvas.drawText(titles[i], x - dis / 2, y, textPaint);
            } else if (i == count / 2) {
                canvas.drawText(titles[i], x - dis / 2, y + 15, textPaint);
            }
        }
    }

    /**
     * 绘制区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        innerValuePaint.setAlpha(255);
        float x0 = centerX, y0 = centerY;
        for (int i = 0; i < count; i++) {
            double percent = data.get(i) / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i - circleZero) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i - circleZero) * percent);
            if (i == 0) {
                path.moveTo(centerX, y);
                x0 = x;
                y0 = y;
            } else {
                path.lineTo(x, y);
            }
            if (i == count - 1) {
                path.lineTo(x0, y0);
            }
            //绘制小圆点
            canvas.drawCircle(x, y, circleRadius, outerValuePaint);
            if (showValueText) {
                float dis = textPaint.measureText(String.valueOf(data.get(i)));//文本长度
                if (i < count / 2 && i > 0) {//第12象限
                    canvas.drawText(String.valueOf(data.get(i)), x, y, textPaint);
                } else if (i > count / 2) {//第34象限
                    canvas.drawText(String.valueOf(data.get(i)), x - dis, y, textPaint);
                } else if (i == 0) {
                    canvas.drawText(String.valueOf(data.get(i)), x - dis / 2, y, textPaint);
                } else if (i == count / 2) {
                    canvas.drawText(String.valueOf(data.get(i)), x - dis / 2, y + 15, textPaint);
                }
            }
        }
        innerValuePaint.setAlpha(innerAlpha);
        canvas.drawPath(path, innerValuePaint);
        //绘制填充区域
        outerValuePaint.setAlpha(255);
        outerValuePaint.setStrokeWidth(strokeWidth);
        canvas.drawPath(path, outerValuePaint);
    }

    //设置标题
    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    //设置数值
    public void setData(ArrayList<Float> data) {
        this.data = data;
    }

    public float getMaxValue() {
        return maxValue;
    }

    //设置最大数值
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    //设置蜘蛛网颜色
    public void setMainPaintColor(int color) {
        mainPaint.setColor(color);
    }

    //设置蜘蛛网透明度
    public void setMainPaintAlpha(int alpha) {
        mainPaint.setAlpha(alpha);
    }

    //设置蜘蛛网区域内透明度
    public void setValuePaintAlpha(int alpha) {
        innerValuePaint.setAlpha(alpha);
    }

    //设置标题颜色
    public void setTextPaintColor(int color) {
        textPaint.setColor(color);
    }

    //设置覆盖局域颜色
    public void setValuePaintColor(int color) {
        innerValuePaint.setColor(color);
        outerValuePaint.setColor(color);
    }

    //设置字体大小
    public void setTextPaintTextSize(float size) {
        textPaint.setTextSize(size);
    }

    public void setLableCount(int lableCount) {
        this.lableCount = lableCount;
    }

    public void setInnerAlpha(int innerAlpha) {
        this.innerAlpha = innerAlpha;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    public void setShowValueText(boolean showValueText) {
        this.showValueText = showValueText;
    }
}
