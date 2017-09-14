package com.jumper.openplatformdemo.fetalheart.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jumper.openplatformdemo.R;


/**
 * Created by Terry on 2015/11/18.
 */
public class RecorderDateSmallViewGroup extends LinearLayout {

    UiBean uiBean;

    TextView tvTypeName;

    TextView tvNumber;

    TextView tvUnit;


    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

    }

    public RecorderDateSmallViewGroup(Context context) {
        super(context);
        init(context);
    }

    public RecorderDateSmallViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init(Context context) {
        int padding = dp2px(context, 3);
        setPadding(padding, padding, padding, padding);
        setOrientation(LinearLayout.VERTICAL);


        inflate(context, R.layout.layout_record_item2, this);

        tvTypeName = (TextView) findViewById(R.id.tvTypeName);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvUnit = (TextView) findViewById(R.id.tvUnit);
    }

    public void setView(RecorderDateSmallViewGroup view) {
        UiBean ui = view.uiBean;
        if (ui == null) return;
        setView(ui);
    }


    public void setView(UiBean ui) {
        if (TextUtils.isEmpty(ui.unit)) {
            tvUnit.setVisibility(View.GONE);
        } else {
            tvUnit.setVisibility(View.VISIBLE);
            tvUnit.setText(ui.unit);
        }

        tvTypeName.setText(ui.typeName);
        tvNumber.setTextColor(getContext().getResources().getColor(R.color.color_red));
        tvNumber.setText(ui.numberText);
    }


    public void setWetView(UiBean ui) {
        if (TextUtils.isEmpty(ui.unit)) {
            tvUnit.setVisibility(View.GONE);
        } else {
            tvUnit.setVisibility(View.VISIBLE);
            tvUnit.setText(ui.unit);
        }

        tvUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvUnit.getLayoutParams();

        lp.addRule(RelativeLayout.ALIGN_TOP, R.id.tvNumber);
        lp.bottomMargin = 0;
        tvUnit.setGravity(Gravity.CENTER);
        tvUnit.setTextColor(getResources().getColor(R.color.text_666666));
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        tvTypeName.setPadding(padding, 0, padding, 0);
        tvTypeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvTypeName.setText(ui.typeName);
        setTvNumberTextAndColor(ui.numberText);
    }


    public void setTvNumberTextAndColor(String text) {

        tvNumber.setTextColor(getContext().getResources().getColor(R.color.color_red));
        if (TextUtils.isEmpty(text) || "0".equals(text)) {
            tvNumber.setText("--");
            tvNumber.setTextColor(getResources().getColor(R.color.text_666666));
        } else {
            tvNumber.setText(text);
            tvNumber.setTextColor(getResources().getColor(R.color.color_red));
        }
    }


    public void setTvNumber(String tvNumberString) {
        if (TextUtils.isEmpty(tvNumberString)) {
            tvNumber.setText(tvNumber.getText().toString().length() > 2 ? "--" : "---");
        }
        tvNumber.setText(tvNumberString);
    }

    public static class UiBean {
        public String typeName;
        public String TipText;
        public String numberText;
        public String unit;
        /**
         * state 0 未开始
         * state 1 已结束
         */
        public boolean state;

        public int drawable;


        public UiBean() {
        }

        public UiBean(String typeName, String tipText, String numberText, String unit, boolean state, int drawable) {
            this.typeName = typeName;
            this.TipText = tipText;
            this.numberText = numberText;
            this.unit = unit;
            this.state = state;
            this.drawable = drawable;
        }


        public UiBean(String typeName, String tipText, String numberText, String unit, int drawable) {
            this(typeName, tipText, numberText, unit, false, drawable);
        }

    }

}
