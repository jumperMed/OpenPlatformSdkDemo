package com.jumper.openplatformdemo.fetalheart;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jumper.openplatformdemo.R;
import com.jumper.openplatformdemo.fetalheart.bean.FetalHeartHistoryInfo;
import com.jumper.openplatformdemo.fetalheart.db.DBHelper;
import com.jumper.openplatformdemo.util.ToastUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@EActivity(R.layout.activity_history_list)
public class HistoryListActivity extends Activity {

    @ViewById()
    ListView listView;

    private MyAdapter myAdapter;

    private List<FetalHeartHistoryInfo> infos = new ArrayList<>();
    private Dao<FetalHeartHistoryInfo, Integer> dao;

    @AfterViews
    void afterViews() {

        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FetalHeartHistoryInfo recordersInfo = (FetalHeartHistoryInfo) parent.getItemAtPosition(position);
                startActivity(new Intent(HistoryListActivity.this, PlayRecorderActivity_.class).putExtra
                        (PlayRecorderActivity_.EXTRA_RECORDS, recordersInfo));
            }
        });

        getHistoryInfos();
    }


    @UiThread
    public void showInfos() {
        myAdapter.notifyDataSetChanged();
    }


    @Background
    public void getHistoryInfos() {
        try {
            dao = OpenHelperManager.getHelper(this, DBHelper.class).getDao(FetalHeartHistoryInfo.class);
            infos = dao.queryForAll();
            if (infos == null || infos.size() == 0) {
                ToastUtil.show("本地无数据");
            } else {
                showInfos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private class MyAdapter extends BaseAdapter {

        private Activity context;

        private LayoutInflater inflater;

        public MyAdapter(Activity context) {
            this.context = context;
            inflater = context.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public FetalHeartHistoryInfo getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_recorder, null);
                viewHolder = new ViewHolder();
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvTime.setText(getItem(position).addTime);
            viewHolder.tvContent.setText("平均心率：" + getItem(position).averageRate + "    胎动数：" + getItem(position)
                    .fetalMoveCounts);

            return convertView;
        }


        class ViewHolder {
            TextView tvTime;
            TextView tvContent;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
    }
}
