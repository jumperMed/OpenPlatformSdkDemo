package com.jumper.openplatformdemo.fetalheart.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2017/3/27.
 * <p>
 * 点击保存后回调出来的胎心监测的信息
 * <p>
 */
@DatabaseTable
public class FetalHeartHistoryInfo implements Parcelable {


    @DatabaseField(generatedId = true)
    public int id;

    /**
     * 胎动时间字符串数据
     */
    @DatabaseField
    public String fetalMoveJsonData;
    /**
     * 音频文件 文件路径
     */
    @DatabaseField
    public String audioFilePath;
    /**
     * 胎心数据JSON 文件路径
     */
    @DatabaseField
    public String fetalHeartFilePath;
    /**
     * 宫缩压数据JSON 文件路径
     */
    @DatabaseField
    public String tocoDataFilePath;

    /**
     * 添加时间
     */
    @DatabaseField
    public String addTime;
    /**
     * 平均心率
     */
    @DatabaseField
    public String averageRate;
    /**
     * 胎动次数
     */
    @DatabaseField
    public String fetalMoveCounts;

    public FetalHeartHistoryInfo() {

    }


    protected FetalHeartHistoryInfo(Parcel in) {
        fetalMoveJsonData = in.readString();
        audioFilePath = in.readString();
        fetalHeartFilePath = in.readString();
        tocoDataFilePath = in.readString();
        addTime = in.readString();
        averageRate = in.readString();
        fetalMoveCounts = in.readString();
    }

    public static final Creator<FetalHeartHistoryInfo> CREATOR = new Creator<FetalHeartHistoryInfo>() {
        @Override
        public FetalHeartHistoryInfo createFromParcel(Parcel in) {
            return new FetalHeartHistoryInfo(in);
        }

        @Override
        public FetalHeartHistoryInfo[] newArray(int size) {
            return new FetalHeartHistoryInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fetalMoveJsonData);
        dest.writeString(audioFilePath);
        dest.writeString(fetalHeartFilePath);
        dest.writeString(tocoDataFilePath);
        dest.writeString(addTime);
        dest.writeString(averageRate);
        dest.writeString(fetalMoveCounts);
    }


    @Override
    public String toString() {
        return "FetalHeartCallBackInfo{" +
                "fetalMoveJsonData='" + fetalMoveJsonData + '\'' +
                ", audioFilePath='" + audioFilePath + '\'' +
                ", fetalHeartFilePath='" + fetalHeartFilePath + '\'' +
                ", tocoDataFilePath='" + tocoDataFilePath + '\'' +
                ", addTime='" + addTime + '\'' +
                ", averageRate='" + averageRate + '\'' +
                ", fetalMoveCounts='" + fetalMoveCounts + '\'' +
                '}';
    }
}
