package com.jumper.openplatformdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jumper.jumperopenplatform.DeviceScanActivity;
import com.jumper.jumperopenplatform.JFetalHeartInterface;
import com.jumper.jumperopenplatform.bean.FetalHeartCallBackInfo;
import com.jumper.openplatformdemo.bledevice.BleDeviceDemoActivity_;
import com.jumper.openplatformdemo.fetalheart.HistoryListActivity_;
import com.jumper.openplatformdemo.fetalheart.bean.FetalHeartHistoryInfo;
import com.jumper.openplatformdemo.fetalheart.db.DBHelper;
import com.jumper.openplatformdemo.util.L;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

import java.sql.SQLException;
import java.util.Locale;

@EActivity
public class MainActivity extends AppCompatActivity {

    Dao<FetalHeartHistoryInfo, Integer> fetalHeartHistoryInfoIntegerDao;

    private RadioGroup rgLanguage;

    private TextView tvVersion;

    private RadioGroup rgColor;


    //120
    private EditText etAppid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JFetalHeartInterface.setUserId("");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("fetalheart_intent");
        registerReceiver(broadcastReceiver, intentFilter);

        try {
            fetalHeartHistoryInfoIntegerDao = OpenHelperManager.getHelper(this, DBHelper.class).getDao
                    (FetalHeartHistoryInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        etAppid = (EditText) findViewById(R.id.etAppid);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        rgColor = (RadioGroup)findViewById(R.id.rgColor);
        try {
            tvVersion.setText("版本号：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        rgLanguage = (RadioGroup) findViewById(R.id.rgLanguage);
        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbChina) {
                    language(Locale.CHINESE);
                } else if (checkedId == R.id.rbEnglish) {
                    language(Locale.ENGLISH);
                }
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FetalHeartCallBackInfo info = intent.getParcelableExtra("extra");
            L.e("444444444444444444444");

            if (null == info) {
                return;
            }

            L.e(info.toString());

            FetalHeartHistoryInfo fetalHeartHistoryInfo = new FetalHeartHistoryInfo();
            fetalHeartHistoryInfo.addTime = info.addTime;
            fetalHeartHistoryInfo.audioFilePath = info.audioFilePath;
            fetalHeartHistoryInfo.averageRate = info.averageRate;
            fetalHeartHistoryInfo.fetalHeartFilePath = info.fetalHeartFilePath;
            fetalHeartHistoryInfo.tocoDataFilePath = info.tocoDataFilePath;
            fetalHeartHistoryInfo.fetalMoveCounts = info.fetalMoveCounts;
            fetalHeartHistoryInfo.fetalMoveJsonData = info.fetalMoveJsonData;

            insertInfo(fetalHeartHistoryInfo);

//            startActivity(new Intent(MainActivity.this, PlayRecorderWavActivity_.class).putExtra(EXTRA_RECORDS,
//                    fetalHeartHistoryInfo));
        }
    };

    @Background
    public void insertInfo(FetalHeartHistoryInfo fetalHeartHistoryInfo) {
        try {
            fetalHeartHistoryInfoIntegerDao.create(fetalHeartHistoryInfo);
            L.e("插入数据库成功");
        } catch (SQLException e) {
            L.e("插入数据库异常");
            e.printStackTrace();
        }
    }


    /**
     * 开启sdk界面
     *
     * @param view
     */
    public void enterFetalHeart(View view) {
        // 这块儿为了方便测试，真实环境不支持。仅支持从Manifest 获取。
//        String etAppIdText = etAppid.getText().toString().trim();
//        if (TextUtils.isDigitsOnly(etAppIdText)) {
//            ToastUtil.show("请输入appid");
//            return;
//        } else {
//             AppIdTest.setAppId(etAppIdText);
//        }
//        startActivity(
//                new Intent(this, DeviceScanActivity.class)
//        .putExtra(MacInterceptor.EXTRA_INTERCEPTER_ACTIONS,new MyIntercepter("http://www.baidu.com",null)));

        String url = rgColor.getCheckedRadioButtonId() == R.id.rb?"http://www.baidu.com":"";

        DeviceScanActivity.start(this,new MyIntercepter(url,new String[]{""}),false);

    }

    /**
     * 本地历史数据
     *
     * @param view
     */
    public void historyFetalHeart(View view) {
        startActivity(new Intent(this, HistoryListActivity_.class));
    }

    /**
     * 体温监测等
     *
     * @param view
     */
    public void enterTempreture(View view) {
//        if (TextUtils.isDigitsOnly(etAppid.getText().toString().trim())) {
//            ToastUtil.show("请输入appid");
//            return;
//        } else {
//            Tools.AppId = etAppid.getText().toString().trim();
//        }
        startActivity(new Intent(this, BleDeviceDemoActivity_.class));
    }

    /**
     * 语言切换
     */
    public void language(Locale locale) {
        Resources resources = getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
        unregisterReceiver(broadcastReceiver);
    }





}
