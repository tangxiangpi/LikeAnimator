package com.wangsz.likeanimator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wangsz on 2017/11/24.
 */
public class IconAnimatorView extends View implements View.OnClickListener {

    private static final String TAG = "IconAnimatorView";

    private static final int QUICK_CLIKC_INTERVAL = 500;
    private static final int BITMAP_SELECTED = R.mipmap.ic_messages_like_selected;
    private static final int BITMAP_UNSELECTED = R.mipmap.ic_messages_like_unselected;

    //缩放最小值
    private static final float SCALE_MIN = 0.8f;
    //缩放最大值
    private static final float SCALE_MAX = 1f;
    //缩放动画的时间
    private static final int SCALE_DURING = 150;
    //圆圈扩散动画的时间
    private static final int CICLE_DURING = 300;
    //扩散圆圈大于图标的
    private static final int CICLE_BIGGER = 2;
    //扩散圆圈的颜色
    private static final int CICLE_COLOR = 0x88e24d3d;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //普通画笔
    private Bitmap mBitmapSelected; //选中图标
    private Bitmap mBitmapUnselected; //未选中图标
    private Matrix mMatrixSelected = new Matrix(); //
    private Matrix mMatrixUnSelected = new Matrix(); //
    private PointF mPoint = new PointF(); //图标位置
    private Path mPathCicle = new Path(); //扩散光圈
    private int mMaxRadio; //光圈最大直径
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //圆圈画笔
    private PointF mPointCenter = new PointF(); //图圈圆心位置

    private boolean mSelected;

    private long lastClickTime;

    public IconAnimatorView(Context context) {
        this(context, null);
    }

    public IconAnimatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconAnimatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconAnimatorView);
        init(typedArray);
        typedArray.recycle();
    }

    private void init(TypedArray typedArray) {

        mSelected = typedArray.getBoolean(R.styleable.IconAnimatorView_ia_selected, false);

        //选中、未选中图标
        int select = typedArray.getResourceId(R.styleable.IconAnimatorView_ia_icon_selected, BITMAP_SELECTED);
        int unselect = typedArray.getResourceId(R.styleable.IconAnimatorView_ia_icon_unselected, BITMAP_UNSELECTED);
        mBitmapSelected = BitmapFactory.decodeResource(getResources(), select);
        mBitmapUnselected = BitmapFactory.decodeResource(getResources(), unselect);

        int cicleColor = typedArray.getColor(R.styleable.IconAnimatorView_ia_cicle_color, CICLE_COLOR);
        mCirclePaint.setColor(cicleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(3);

        int w = mBitmapSelected.getWidth() / 2;
        int h = mBitmapSelected.getHeight() / 2;
        mMaxRadio = (int) (Math.sqrt(w * w + h * h) + CICLE_BIGGER);
        mPointCenter.set(getPaddingLeft() + mMaxRadio, getPaddingTop() + mMaxRadio);
        mPoint.set(mPointCenter.x - mBitmapSelected.getWidth() / 2, mPointCenter.y - mBitmapSelected.getHeight() / 2);

        translateMatrix(mMatrixSelected);
        translateMatrix(mMatrixUnSelected);

        setOnClickListener(this);
    }

    private void translateMatrix(Matrix matrix) {
        matrix.setTranslate(mPoint.x, mPoint.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSelected) {
            canvas.drawPath(mPathCicle, mCirclePaint);
            canvas.drawBitmap(mBitmapSelected, mMatrixSelected, mPaint);
        } else {
            canvas.drawBitmap(mBitmapUnselected, mMatrixUnSelected, mPaint);
        }
    }

    @Override
    public void onClick(View view) {

        if (System.currentTimeMillis() - lastClickTime < QUICK_CLIKC_INTERVAL){
            Log.e(TAG,"短时间快速无效点击");
            lastClickTime = System.currentTimeMillis();
            return;
        }

        lastClickTime = System.currentTimeMillis();

        if (mSelected) {
            // 点击切换为未选中
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "iconSelected", SCALE_MAX, SCALE_MIN);
            animator.setDuration(SCALE_DURING);

            ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "iconUnSelected", SCALE_MIN, SCALE_MAX);
            animator1.setDuration(SCALE_DURING);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator1).after(animator);
            animatorSet.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "iconSelected", SCALE_MIN, SCALE_MAX);
            animator.setDuration(SCALE_DURING);

            ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "iconUnSelected", SCALE_MAX, SCALE_MIN);
            animator1.setDuration(SCALE_DURING);

            ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "cicleScale", 0, mMaxRadio);
            animator2.setDuration(CICLE_DURING);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator).after(animator1);
            animatorSet.play(animator).with(animator2);
            animatorSet.start();
        }
    }

    /**
     * 选中图标缩放动画
     * @param scale
     */
    private void setIconSelected(float scale) {

        translateMatrix(mMatrixSelected);
        mMatrixSelected.postScale(scale, scale, mPointCenter.x, mPointCenter.y);
        mSelected = true;
        invalidate();
    }

    /**
     * 未选中图标缩放动画
     *
     * @param scale
     */
    private void setIconUnSelected(float scale) {

        translateMatrix(mMatrixUnSelected);
        mMatrixUnSelected.postScale(scale, scale, mPointCenter.x, mPointCenter.y);
        mSelected = false;
        invalidate();
    }

    /**
     * 圆圈扩散动画
     *
     * @param radio
     */
    private void setCicleScale(float radio) {
        mPathCicle.reset();
        if (radio < mMaxRadio) {
            mPathCicle.addCircle(mPointCenter.x, mPointCenter.y, radio, Path.Direction.CW);
        }
        mSelected = true;
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
        int wrapWidth = getPaddingLeft() + mMaxRadio * 2 + getPaddingRight();
        //最大高度 = 上间距 + 扩散圈直径 + 下间距
        int wrapHeight = getPaddingTop() + mMaxRadio * 2 + getPaddingBottom();

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
     * 设置选中状态
     * @param selected
     * @param isAnimator 是否有动画
     */
    public void setIconSelected(boolean selected,boolean isAnimator){
        if (selected && mSelected){
            return;
        }

        if (isAnimator){
            onClick(this);
        } else {
            mSelected = selected;
            invalidate();
        }
    }

    public boolean getIconSelected(){
        return mSelected;
    }

}
