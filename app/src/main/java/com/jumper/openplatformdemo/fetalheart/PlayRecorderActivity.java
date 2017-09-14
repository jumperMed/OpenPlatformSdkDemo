package com.jumper.openplatformdemo.fetalheart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jumper.chart.ADFetalHeartPlayChart;
import com.jumper.chart.FHRAndTocoPlayChartView;
import com.jumper.chart.ScrollListener;
import com.jumper.chart.detail.FetalRecords;
import com.jumper.fetalheart.line.HeadSetUnit;
import com.jumper.help.Mp3RecordTimeCreater;
import com.jumper.help.TimeScheduleCallBack;
import com.jumper.jumperopenplatform.util.FileTools;
import com.jumper.openplatformdemo.R;
import com.jumper.openplatformdemo.fetalheart.bean.FetalHeartHistoryInfo;
import com.jumper.openplatformdemo.fetalheart.bean.FetalRecord;
import com.jumper.openplatformdemo.fetalheart.widget.FHRSeekBar;
import com.jumper.openplatformdemo.fetalheart.widget.RecorderDateSmallViewGroup;
import com.jumper.openplatformdemo.util.FHRMediaPlayer;
import com.jumper.openplatformdemo.util.L;
import com.jumper.openplatformdemo.util.ToastUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.media.AudioManager.MODE_NORMAL;

/**
 * 播放胎音记录
 *
 * @author Terry
 */
@EActivity(R.layout.activity_play_recoder_v1)
public class PlayRecorderActivity extends Activity implements HeadSetUnit.HeadsetListerner {


    public static final String TAG = "::PlayRecorderActivity::";


    /**
     * 音频文件
     */
    public static final String EXTRA_AUDIO_FILE_PATH = "audio_file_path";

    /**
     * 胎心数据JSON 文件
     */
    public static final String EXTRA_FHR_JSON_FILE_PATH = "fhr_json_path";

    /**
     * 胎动数据
     */
    public static final String EXTRA_FM_DATA = "fm_data";

    /**
     * 胎动数据JSON 文件
     */
    public static final String EXTRA_TOCO_JSON_FILE_PATH = "toco_json_path";

    /**
     * 添加时间
     */
    public static final String EXTRA_ADD_TIME = "add_time";

    /**
     * 分享网址
     */
    public static final String EXTRA_SHARE_URL = "share_url";


    public static final String EXTRA_DOCTOR_ASK = "doctor_ask";

    /**
     * 直接传一个Records
     */
    public static final String EXTRA_RECORDS = "record_info";




    @ViewById
    TextView buttonFetal;
    @ViewById
    TextView tvRangeTime, fetalTime;
    @ViewById
    LinearLayout llViewGruop;
    @ViewById
    TextView tvTopFhrText;
    @ViewById
    TextView tvTotalTime;
    @ViewById
    FHRSeekBar sbProgress;
    @ViewById
    TextView tvRecordTime;
    @ViewById
    ADFetalHeartPlayChart adFetalHeartPlayChart;


    private FHRAndTocoPlayChartView chartView;
    private HorizontalScrollView scroll;

    // //用以接收数据
    private String filePath;
    private String jsonPath;

    private String addTime, shareUrl;

    PowerManager pm;
    WakeLock mWakeLock;

    private String fmJson;




    private int audioManagerMode;


    private HeadSetUnit mHeadSetUnit;

    private String tocoDataJsonFilePath;

    Mp3RecordTimeCreater mp3RecordTimeCreater;


    private int maxProgress = 0;

    private boolean isHaveToco = true;




    private FHRMediaPlayer fhrMediaPlayer = null;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "My Tag");

        mHeadSetUnit = new HeadSetUnit(this);
        mHeadSetUnit.setHeadsetListerner(this);
        mHeadSetUnit.register();


        mp3RecordTimeCreater = new Mp3RecordTimeCreater();
        mp3RecordTimeCreater.setTimeScheduleCallBack(new TimeScheduleCallBack() {
            @Override
            public void run() {
                on5SecondArrival();
            }
        });

    }

    @AfterViews
    void afterView() {

        getIntents();

        initTopView();
        initChartView();
        initNumberView();

        toNormal();

        getData();
    }


    /**
     * 设置顶部View
     */
    void initTopView() {


        // 设置大小不一样的文字
        String text = getString(R.string.record_fhr_title);
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_878787))
                , 3, text.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(0.6f), 3, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTopFhrText.setText(ss);


    }

    /**
     * 初始化画线
     */
    void initChartView() {
        // chartLayout.removeAllViews();

        chartView = adFetalHeartPlayChart.getPlayChartView();
        scroll = adFetalHeartPlayChart.getHorizontalView();

        chartView.setHaveToco(!TextUtils.isEmpty(tocoDataJsonFilePath));

        adFetalHeartPlayChart.setScrollListener(new ScrollListener() {

            @Override
            public void onScroll(float progress) {
                if (data == null) {
                    return;
                }

                mp3RecordTimeCreater.cancelTimer();
                mDataPosition = (int)(data.size() *progress);

                Log.d("Terry",TAG +"onScroll::" + mDataPosition);
                showUiText(mDataPosition);

            }

            @Override
            public void onScrollStop(float progress) {
                if (data == null) {
                    return;
                }

                L.d("progress--->"+progress);
                mDataPosition = (int)(data.size() * progress);
                Log.d("Terry",TAG +"onScrollStop::" + mDataPosition);
                showUiText(mDataPosition);

                // 从拖动位置开始播放
                int audioProgress = (int)(maxProgress * progress);
                fhrMediaPlayer.setPregress(audioProgress);
                if(mDataPosition < data.size()
                        && fhrMediaPlayer.isPlaying()){
                    mp3RecordTimeCreater.initTimer();
                }
            }
        });

        fetalTime.setText(getDataString());
    }

    private static String getDataString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(c.getTime());
    }



    /**
     * 接受数据
     */
    void getIntents() {

        Intent intent = getIntent();
        FetalHeartHistoryInfo recordersInfo = intent.getParcelableExtra(EXTRA_RECORDS);
        if (recordersInfo == null) {
            L.e("传值失败");
        } else {
            fmJson = recordersInfo.fetalMoveJsonData;
            filePath = recordersInfo.audioFilePath;
            jsonPath = recordersInfo.fetalHeartFilePath;
            tocoDataJsonFilePath = recordersInfo.tocoDataFilePath;
            addTime = recordersInfo.addTime;
        }


    }


    @UiThread
    void initMedialPlayer() {
        mp3RecordTimeCreater.clear();

        fhrMediaPlayer = new FHRMediaPlayer(filePath,
                new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return false;
                    }
                },
                new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 播放完成了。将界面更新
                        setPlayButtonUi(false);
                        mp3RecordTimeCreater.cancelTimer();
                        scrollInPlaying(data.size());
                    }
                });
        fhrMediaPlayer.setProgress(sbProgress);

        maxProgress = fhrMediaPlayer.getMaxProgress();

        setPlayButtonUi(false);
        buttonFetal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonFetal.isSelected()){
                    // 从已结束了中恢复
                    if(fhrMediaPlayer.isComplete()
                            || fhrMediaPlayer.isPrepara()){

                        if (!chartView.isDraw()) {
                            drawLine(data, FMdata, tocoData);
                        }
                        mDataPosition = 0 ;
                        scrollToBegin();
                    }
                    fhrMediaPlayer.startOrPlay();
                    mp3RecordTimeCreater.initTimer();
                    setPlayButtonUi(true);
                }else{

                    mp3RecordTimeCreater.cancelTimer();
                    fhrMediaPlayer.pause();
                    setPlayButtonUi(false);
                }
            }
        });

    }





    @UiThread
    void on5SecondArrival() {
        if (mDataPosition >= data.size()) {
            stopAudio();
            return;
        }
        changeUi5Seconds( mDataPosition);
        mDataPosition++;
    }


    // 停止播放音乐
    private void stopAudio(){
        fhrMediaPlayer.stop(false);
        setPlayButtonUi(false);
    }





    // 显示数据
    void changeUi5Seconds(int dataPosition) {
        scrollInPlaying(dataPosition);

        showUiText(dataPosition);

    }



    public void showUiText(int position){
        int fixPosition = 0;
        if(position >= data.size() ) {
            position = data.size();
            fixPosition = position - 1;
        }else{
            fixPosition = position;
        }
        setFetalHeart("" + data.get(fixPosition));
        showTimeText(position);
    }

    

    private void setPlayButtonUi(boolean start){
        buttonFetal.setSelected(!start);
        buttonFetal.setCompoundDrawablesWithIntrinsicBounds(
                start? R.mipmap.fetal_start: R.mipmap.fetal_stop,0,0,0);
    }


    @Background
    void getData() {

        boolean isSuccess = true;

        // 判断是否有宫缩
        if (TextUtils.isEmpty(tocoDataJsonFilePath)) {
            isHaveToco = false;
        }



        if (!isSuccess) {
            ToastUtil.show("数据异常");
            return;
        }

        if (filePath != null) {
            File file = new File(filePath);
            if (!file.exists() || file.length() <= 0) {
                ToastUtil.show("胎心音频文件异常");
                return;
            }
        }


        if (TextUtils.isEmpty(jsonPath)) {
            ToastUtil.show("数据异常");
            return;
        }


        String json = FileTools.ReadJsonFile(jsonPath);
        if (TextUtils.isEmpty(json)) {
            ToastUtil.show("数据异常");
        }

        String tocoJsonData = null;
        if (!TextUtils.isEmpty(tocoDataJsonFilePath)) {
            tocoJsonData = FileTools.readJsonFile(tocoDataJsonFilePath);
        }


        try {
            Gson gson = new Gson();
            data = gson.fromJson(json, new TypeToken<ArrayList<Integer>>() {
            }.getType());

            if (tocoJsonData != null) {
                tocoData = gson.fromJson(tocoJsonData, new TypeToken<ArrayList<Integer>>() {
                }.getType());
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            data = null;
        }

        try {
            ArrayList<FetalRecord> temList = new Gson().fromJson(fmJson,
                    new TypeToken<ArrayList<FetalRecord>>() {
                    }.getType());
            if (temList != null) {
                FMdata.addAll(temList);
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if ( data != null) {
            initMedialPlayer();
            drawLine(data, FMdata, tocoData);
        } else {
            ToastUtil.show("数据异常");
        }
    }


    public static String formatNumber(int num) {
        return num >= 10 ? (num + "") : ("0" + num);
    }

    @UiThread
    void drawLine(ArrayList<Integer> data, ArrayList<FetalRecords> list,
                  ArrayList<Integer> tocoList) {


        chartView.addData(data, tocoList);
        chartView.addFetalMoveData(list);
        chartView.setHaveToco(isHaveToco);
        int size = data.size() / 2;
        String times = formatNumber(size / 60) + ":" + formatNumber(size % 60);
        setTestTime(times);
        tvTotalTime.setText(times);

        if (list != null) {
            setFetalMovement(list.size() + "");
        }
    }

    @UiThread
    void drawFm(ArrayList<FetalRecord> FMdata) {
        if (FMdata != null && !FMdata.isEmpty()) {
        }
    }

    private void initNumberView() {
        tvRecordTime.setText(getString(R.string.play_time, addTime));

        RecorderDateSmallViewGroup.UiBean fetalMoveBean = new RecorderDateSmallViewGroup.UiBean(
                getString(R.string.fetal_movement), getString(R.string.record_fetalMove_tip), "0", "次",
                R.mipmap.fetal);

        RecorderDateSmallViewGroup.UiBean hateRateBean = new RecorderDateSmallViewGroup.UiBean(
                getString(R.string.fetal_heart_rate), getString(R.string.record_fhr_range),
                "0", "BPM", R.mipmap.fetal_heart_rate);

        RecorderDateSmallViewGroup.UiBean testTimeBean = new RecorderDateSmallViewGroup.UiBean(
                getString(R.string.record_test_Time), "", "00:00", "",
                R.mipmap.fetal_time);

        RecorderDateSmallViewGroup.UiBean[] uiBeans = new RecorderDateSmallViewGroup.UiBean[]{
                testTimeBean, hateRateBean, fetalMoveBean
        };
        for (int i = 0; i < uiBeans.length; i++) {
            ((RecorderDateSmallViewGroup) llViewGruop.getChildAt(i)).setView(uiBeans[i]);
        }
        showTimeText(0);

        sbProgress.setProgress(0);

    }


    /**
     * 测试时间
     */
    private static final int TEST_TIME = 0;

    /**
     * 心率
     */
    private static final int FETAL_HEART = 1;

    /**
     * 胎动
     */
    private static final int FETAL_MOVEMENT = 2;


    /**
     * 0 测试时间   1 心率   2 胎动
     */
    private void setHarteRate(int fetalNumber, String title) {
        RecorderDateSmallViewGroup recorderDateSmallViewGroup = (RecorderDateSmallViewGroup)
                llViewGruop.getChildAt(fetalNumber);
        recorderDateSmallViewGroup.setTvNumber(title);
    }


    private void setTestTime(String title) {
        setHarteRate(TEST_TIME, title);
    }

    private void setFetalHeart(String title) {
        if (title.equals("0")) {
            title = "--";
        }
        setHarteRate(FETAL_HEART, title);
    }

    private void setFetalMovement(String title) {
        setHarteRate(FETAL_MOVEMENT, title);
    }


    @Override
    public void headsetIsIn(boolean in) {
        if (in){
            setSpeakerOn();
        }
    }

    @Override
    public void headsetOut() {
        toNormal();
    }

    @Override
    public void blueHeadsetOut() {

    }







    // //////////////////////////////////////
    // 对scrollView 相关的操作
    // /////////////////////////////////////
    private void scrollToBegin() {
        scroll.scrollTo(0, scroll.getScrollY());
    }

    private void scrollInPlaying(int count) {
        scroll.scrollTo((int) (Math.ceil(count * chartView.getPerX())),
                scroll.getScrollY());
    }





    @UiThread
    void showTimeText(int count) {

        tvRangeTime.setText(mp3RecordTimeCreater.getTimeString(count));
    }



    private ArrayList<Integer> data;

    private ArrayList<Integer> tocoData;

    private ArrayList<FetalRecords> FMdata = new ArrayList<>();

    private int mDataPosition = 0;






    /**
     * 插入耳机时，设置成扬声器外放
     */
    private void setSpeakerOn() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        audioManagerMode = audioManager.getMode();
        L.d("--------------------" + audioManagerMode);

        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(true);//使用扬声器外放，即使已经插入耳机
        setVolumeControlStream(AudioManager.STREAM_MUSIC);//控制声音的大小
        audioManager.setMode(AudioManager.STREAM_MUSIC);

    }

    @Override
    public void onBackPressed() {
        back();
    }

    void back() {
        mp3RecordTimeCreater.cancelTimer();
        fhrMediaPlayer.stop();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkHeadSet()) {
            setSpeakerOn();
        }
        mWakeLock.acquire();
    }

    boolean checkHeadSet() {
        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
    }

    void toNormal() {
        L.d("将声音模式复原。。。");

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManagerMode == 0) {
            audioManagerMode = MODE_NORMAL;
        }
        audioManager.setMode(audioManagerMode);
        audioManager.setSpeakerphoneOn(false);
        //audioManager.setMode(AudioManager.STREAM_SYSTEM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toNormal();
        mHeadSetUnit.unregister();
//		unregisterReceiver(headSetBroadCaseReciever);
        fhrMediaPlayer.destroy();
        mp3RecordTimeCreater.cancelTimer();

    }




}
