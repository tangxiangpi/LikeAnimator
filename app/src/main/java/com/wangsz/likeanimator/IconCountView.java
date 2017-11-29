package com.wangsz.likeanimator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by wangsz on 2017/11/29.
 */

public class IconCountView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "IconAnimatorView";

    private static final int QUICK_CLIKC_INTERVAL = 500;
    private long lastClickTime;

    private IconAnimatorView mIconAnimatorView;
    private CountAnimatorView mCountAnimatorView;

    public IconCountView(Context context) {
        this(context, null);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconCountView);
        int xmlId = typedArray.getResourceId(R.styleable.IconCountView_ic_view_id,R.layout.icon_count_view);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(xmlId, this,true);

        init();
    }

    private void init() {

        mIconAnimatorView = (IconAnimatorView) findViewById(R.id.icv_iav);
        mCountAnimatorView = (CountAnimatorView) findViewById(R.id.icv_cav);

        mIconAnimatorView.setClickable(false);

        setInitData(mIconAnimatorView.getIconSelected(),mCountAnimatorView.getCount());

        setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if (System.currentTimeMillis() - lastClickTime < QUICK_CLIKC_INTERVAL){
            Log.e(TAG,"短时间快速无效点击");
            lastClickTime = System.currentTimeMillis();
            return;
        }

        lastClickTime = System.currentTimeMillis();

        if (mIconAnimatorView.getIconSelected()){
            mIconAnimatorView.setIconSelected(false,true);
            mCountAnimatorView.minusOne();
        } else {
            mIconAnimatorView.setIconSelected(true,true);
            mCountAnimatorView.addOne();
        }

    }

    /**
     * 设置初始数据
     * @param selected
     * @param count
     */
    public void setInitData(boolean selected,int count){
        if (count < 0){
            Log.e(TAG,"count 不能小于0");
            return;
        }
        mIconAnimatorView.setIconSelected(selected,false);
        mCountAnimatorView.setCount(count);
    }

}
