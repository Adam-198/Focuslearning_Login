package com.scut.focuslearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

/**
 * 倒计时控件
 */

@SuppressWarnings("all")
public class CountdownView extends View {

    // 控件宽
    private int width;
    // 控件高
    private int height;
    // 刻度盘半径
    private int dialRadius;
    // 进度条两端高
    private float signHeight = dp2px(6);

    // 定时进度条宽
    private float arcWidth = dp2px(6);
    // 倒计时状态
    private int status = 0;

    public static int COUNTDOWN_INIT = 0;
    public static int COUNTDOWN_START = 1;
    public static int COUNTDOWN_STOP = 2;

    // 初始时间秒数
    private int startTime = 0;
    // 总时间秒数
    private int time = 0;
    // 时间-分
    private int minute = 0;
    // 时间-秒
    private int second = 0;
    // 刻度盘画笔
    private Paint dialPaint;
    // 时间画笔
    private Paint timePaint;
    // 是否移动
    private boolean isMove;
    // 当前旋转的角度
    private float rotateAngle;
    // 当前的角度
    private float currentAngle;
    // 时间改变监听
    private OnCountdownListener onCountdownListener;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 刻度盘画笔
        dialPaint = new Paint();
        dialPaint.setAntiAlias(true);
        dialPaint.setColor(Color.parseColor("#94C5FF"));
        dialPaint.setStyle(Paint.Style.STROKE);
        dialPaint.setStrokeCap(Paint.Cap.ROUND);

        // 时间画笔
        timePaint = new Paint();
        timePaint.setAntiAlias(true);
        timePaint.setColor(Color.parseColor("#94C5FF"));
        timePaint.setTextSize(sp2px(33));
        timePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 控件宽、高
        width = height = Math.min(h, w);
        // 刻度盘半径
        dialRadius = (int) (width / 2 - dp2px(10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制刻度盘
        drawDial(canvas);

        // 绘制定时进度条
        drawArc(canvas);
        // 绘制时间
        drawTime(canvas);
    }

    /**
     * 绘制刻度盘
     *
     * @param canvas 画布
     */
    private void drawDial(Canvas canvas) {
        // 绘制外层圆盘
        dialPaint.setStrokeWidth(dp2px(2));
        canvas.drawCircle(width / 2, height / 2, dialRadius, dialPaint);

        // 将坐标原点移到控件中心
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.save();
    }

    /**
     * 绘制定时进度条
     *
     * @param canvas 画布
     */
    private void drawArc(Canvas canvas) {
        if (minute > 0) {
            // 绘制起始标志
            dialPaint.setStrokeWidth(dp2px(3));
            canvas.drawLine(0, -dialRadius - signHeight, 0, -dialRadius + signHeight, dialPaint);

            // 取消直线圆角设置
            dialPaint.setStrokeCap(Paint.Cap.BUTT);

            // 绘制进度
            for (int i = 0; i <= minute * 3; i++) {
                canvas.drawLine(0, -dialRadius - arcWidth / 2, 0, -dialRadius + arcWidth / 2, dialPaint);
                // 最后一次绘制后不旋转画布
                if (i != minute * 3) {
                    canvas.rotate(1);
                }
            }

            // 绘制结束标志
            dialPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(0, -dialRadius - signHeight, 0, -dialRadius + signHeight, dialPaint);
        }
    }

    /**
     * 绘制时间
     *
     * @param canvas 画布
     */
    private void drawTime(Canvas canvas) {
        canvas.restore();
        String timeText = String.format(Locale.CHINA, "%02d", minute) + " : " + String.format(Locale.CHINA, "%02d", second);
        // 获取时间的宽高
        float timeWidth = timePaint.measureText(timeText);
        float timeHeight = Math.abs(timePaint.ascent() + timePaint.descent());
        // 居中显示
        canvas.drawText(timeText, -timeWidth / 2, timeHeight / 2, timePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(status == CountdownView.COUNTDOWN_START) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下的角度
                currentAngle = calcAngle(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                // 标记正在移动
                isMove = true;
                // 移动的角度
                float moveAngle = calcAngle(event.getX(), event.getY());
                // 滑过的角度偏移量
                float angleOffset = moveAngle - currentAngle;

                // 防止越界
                if (angleOffset < -270) {
                    angleOffset = angleOffset + 360;
                } else if (angleOffset > 270) {
                    angleOffset = angleOffset - 360;
                }

                currentAngle = moveAngle;
                // 计算时间
                calcTime(angleOffset);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (isMove && onCountdownListener != null) {
                    // 回调倒计时改变方法
                    onCountdownListener.countdown(minute);
                    isMove = false;
                }
                break;
            }
        }
        return true;
    }

    /**
     * 以刻度盘圆心为坐标圆点，建立坐标系，求出(targetX, targetY)坐标与x轴的夹角
     *
     * @param targetX x坐标
     * @param targetY y坐标
     * @return (targetX, targetY)坐标与x轴的夹角
     */
    private float calcAngle(float targetX, float targetY) {
        // 以刻度盘圆心为坐标圆点
        float x = targetX - width / 2;
        float y = targetY - height / 2;
        // 滑过的弧度
        double radian;

        if (x != 0) {
            float tan = Math.abs(y / x);
            if (x > 0) {
                if (y >= 0) {
                    // 第四象限
                    radian = Math.atan(tan);
                } else {
                    // 第一象限
                    radian = 2 * Math.PI - Math.atan(tan);
                }
            } else {
                if (y >= 0) {
                    // 第三象限
                    radian = Math.PI - Math.atan(tan);
                } else {
                    // 第二象限
                    radian = Math.PI + Math.atan(tan);
                }
            }
        } else {
            if (y > 0) {
                // Y轴向下方向
                radian = Math.PI / 2;
            } else {
                // Y轴向上方向
                radian = Math.PI + Math.PI / 2;
            }
        }

        // 完整圆的弧度为2π，角度为360度，所以180度等于π弧度
        // 弧度 = 角度 / 180 * π
        // 角度 = 弧度 / π * 180
        return (float) (radian / Math.PI * 180);
    }

    /**
     * 计算时间
     *
     * @param angle 增加的角度
     */
    private void calcTime(float angle) {
        rotateAngle += angle;
        if (rotateAngle < 0) {
            rotateAngle = 0;
        } else if (rotateAngle > 360) {
            rotateAngle = 360;
        }
        //控制时间在0-120分钟以内 3度等于1分钟
        minute = (int) rotateAngle / 3;
        invalidate();
    }

    /**
     * 设置倒计时
     *
     * @param time 总时间秒数
     */
    public void setCountdown(int time) {
        //设置的时间区间段为两小时以内
        if (startTime == 0) {
            startTime = time;
        }
        this.time = time;
        if (time < 0 || time > 7200) {
            return;
        }
        minute = time / 60;
        second = time % 60;
        //设置完时间之后，进度条从最末尾开始
        rotateAngle = time / startTime * 360;
        invalidate();
    }


    /**
     * @param status 倒计时的状态
     */
    public void setCountdownStatus(int status) {
        this.status = status;
    }


    public void setStartTime() {
        onCountdownListener.countdown(startTime / 60);
    }

    /**
     * 设置倒计时监听
     *
     * @param onTempChangeListener 倒计时监听接口
     */
    public void setOnCountdownListener(OnCountdownListener onCountdownListener) {
        this.onCountdownListener = onCountdownListener;
    }

    /**
     * 倒计时监听接口
     */
    public interface OnCountdownListener {
        /**
         * 倒计时
         *
         * @param temp 时间
         */
        void countdown(int minute);
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return px值
     */
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
