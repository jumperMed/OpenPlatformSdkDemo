package com.jumper.openplatformdemo.fetalheart.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.jumper.openplatformdemo.util.FHRMediaPlayer;


/**
 * Created by Terry on 2017/7/28 17:31.
 *
 * @author Terry
 * @version [版本号, YYYY-MM-DD]
 * @since [产品/模块版本]
 */

public class FHRSeekBar extends SeekBar implements FHRMediaPlayer.IProgress {

    public FHRSeekBar(Context context) {
        super(context);
        setEnabled(false);
    }

    public FHRSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnabled(false);
    }

    public FHRSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEnabled(false);
    }



//    android:focusable="true"
//    android:maxHeight="2dp"
//    android:minHeight="2dp"
//    android:max="100"
//    android:progress="5"
//    android:progressDrawable="@drawable/seekbar_mini"
//    android:thumb="@mipmap/fetal_schedule"
//    android:thumbOffset="4dp"


    @Override
    public void showProgress(int progress) {
        setProgress(progress);
    }
}
