package com.wangsz.likeanimator;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangsz on 2017/11/27.
 */

public class CountAnimatorView extends View {

    private static final int TEXT_COLOR = 0x88e24d3d;
    private static final int TEXT_SIZE = 12;//dp

    private Context mContext;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mCount;

    private String mStrCountNoChange; // 没有变化的字符串部分
    private PointF mPointNoChange = new PointF();
    private int mTextColor;
    private String mStrCountBeforeChange; // 变化部分的原字符串
    private PointF mPointBeforeChange = new PointF();
    private String mStrCountChange; // 变化部分的变化之后的字符串
    private PointF mPointChange = new PointF();
    private int process;

    private float mTextHeight = 0;
    private boolean isUp = true;

    public CountAnimatorView(Context context) {
        this(context, null);
    }

    public CountAnimatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountAnimatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountAnimatorView);
        init(typedArray);
        typedArray.recycle();
    }

    private void init(TypedArray typedArray) {
        float textSize = typedArray.getDimension(R.styleable.CountAnimatorView_ca_textsize, dip2px(TEXT_SIZE));
        mTextColor = typedArray.getColor(R.styleable.CountAnimatorView_ca_text_color, TEXT_COLOR);
        mCount = typedArray.getInt(R.styleable.CountAnimatorView_ca_count, 0);
        mPaint.setTextSize(textSize);
        mPaint.setColor(mTextColor);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = Math.abs(fontMetrics.top);

        analyseCount(0);

        mPointNoChange.set(getPaddingLeft(), getPaddingTop() + mTextHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAlpha(255);
        canvas.drawText(mStrCountNoChange, mPointNoChange.x, mPointNoChange.y, mPaint);
        mPaint.setAlpha(process);
        canvas.drawText(mStrCountBeforeChange, mPointBeforeChange.x, mPointBeforeChange.y, mPaint);
        mPaint.setAlpha(255 - process);
        canvas.drawText(mStrCountChange, mPointChange.x, mPointChange.y, mPaint);
    }

    private void analyseCount(int change) {

        // 数字不变（首次绘制）
        if (change == 0) {
            mStrCountNoChange = String.valueOf(mCount);
            mStrCountBeforeChange = "";
            mStrCountChange = "";
        } else {
            String count = String.valueOf(mCount);
            String countChanged = String.valueOf(mCount + change);
            for (int i = 0; i < count.length(); i++) {
                if (count.charAt(i) != countChanged.charAt(i)) {
                    mStrCountNoChange = countChanged.substring(0, i);
                    mStrCountBeforeChange = count.substring(i);
                    mStrCountChange = countChanged.substring(i);
                    break;
                }
            }
            isUp = change > 0;
            mCount += change;
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "textOffY", 1, 0);
            animator.setDuration(300);
            animator.start();
        }
    }

    private void setTextOffY(float offY) {

        process = (int) (offY * 255);

        if (isUp) {
            mPointBeforeChange.set(getPaddingLeft() + mPaint.measureText(mStrCountNoChange), mTextHeight * offY + getPaddingTop());
            mPointChange.set(getPaddingLeft() + mPaint.measureText(mStrCountNoChange), mTextHeight * (1 + offY) + getPaddingTop());
        } else {
            mPointBeforeChange.set(getPaddingLeft() + mPaint.measureText(mStrCountNoChange), mTextHeight * (2 - offY) + getPaddingTop());
            mPointChange.set(getPaddingLeft() + mPaint.measureText(mStrCountNoChange), mTextHeight * (1 - offY) + getPaddingTop());
        }
        invalidate();
    }

    // 计算尺寸
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //最大宽度 = 左间距 + 扩散圈直径 + 右间距
        int wrapWidth = (int) (getPaddingLeft() + mPaint.measureText(String.valueOf(mCount)) + getPaddingRight());
        //最大高度 = 上间距 + 扩散圈直径 + 下间距
        int wrapHeight = getPaddingTop() + mPaint.getFontMetricsInt().bottom - mPaint.getFontMetricsInt().top + getPaddingBottom();

        //计算wrap_content时的情况
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wrapWidth, wrapHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wrapWidth, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, wrapHeight);
        }
    }

    /**
     * 将dip或dp值转换为px值
     */
    public int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 加1
     */
    public void addOne(){
        analyseCount(1);
    }

    /**
     * 减1
     */
    public void minusOne(){
        analyseCount(-1);
    }

}
