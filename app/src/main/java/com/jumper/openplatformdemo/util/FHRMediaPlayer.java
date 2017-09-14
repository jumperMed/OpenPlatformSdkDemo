package com.jumper.openplatformdemo.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jumper.openplatformdemo.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Terry on 2017/7/28 15:29.
 *
 * @author Terry
 * @version [版本号, YYYY-MM-DD]
 * @since [产品/模块版本]
 */

public class FHRMediaPlayer implements Handler.Callback {


    public static final String Tag = "::FHRMediaPlayer::";

    private static final int PREPARA = 0;

    private static final int CANPLAY = 1;

    private static final int COMPLETE = 2;

    private static final int STOP = 3;

    static final int MSG_REFRESH_PROGRESS = 1;

    private int mediaPlayerState;

    private int maxProgress;

    private IProgress mProgress;


    private MediaPlayer mediaPlayer = null;


    private Handler handler = new Handler(Looper.getMainLooper(), this);


    public FHRMediaPlayer(String filePath, final MediaPlayer.OnErrorListener onErrorListener,
                          final MediaPlayer.OnCompletionListener onCompletionListener) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onErrorListener.onError(mp, what, extra);
                Log.e("Terry_Error", "FHRMediaPlayer --胎心音乐播放失败");
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(BuildConfig.DEBUG){
                    Log.d("Terry",Tag+"onCompletion");
                }
                mediaPlayerState = COMPLETE;
                stop(false);

                if(mProgress != null){
                    mProgress.showProgress(mediaPlayer.getDuration());
                }
                onCompletionListener.onCompletion(mp);
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d("Terry",Tag+"onSeekComplete");
            }
        });

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        try {

            FileInputStream fi = new FileInputStream(new File(filePath));

            mediaPlayer.setDataSource(fi.getFD());
            mediaPlayer.prepare();

            maxProgress = mediaPlayer.getDuration();


        } catch (Exception e) {
            Log.e("Terry_Error", "FHRMediaPlayer --胎心音乐播放失败");
        }

        mediaPlayerState = PREPARA;



    }


    public void setProgress(IProgress progress){
        this.mProgress = progress;
        this.mProgress.setMax(mediaPlayer.getDuration());
    }


    public boolean isComplete(){
        return mediaPlayerState >= COMPLETE;
    }

    public boolean isPrepara(){
        return mediaPlayerState == PREPARA;
    }


    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }


    public void rest() {
        mediaPlayer.reset();
    }


    public void startOrPlay() {
        mediaPlayerState = CANPLAY;
        mediaPlayer.start();
        int next = refreshNow();
        queueNextRefresh(next);

    }


    public void pause() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }


    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    public void setPregress(int progress) {
        if(mediaPlayerState == CANPLAY){
            mediaPlayer.seekTo(progress);
        }

        if (mProgress != null) {
            mProgress.showProgress(progress);
        }
    }


    public void stop(boolean exit){
        // 如果没有点过播放按钮，就不用停止，也不存在停止
        if(mediaPlayerState <= PREPARA){
            return;
        }
        handler.removeMessages(MSG_REFRESH_PROGRESS);
        mediaPlayerState = STOP;
        if(mediaPlayer != null){
            mediaPlayer.stop();
            if(!exit){
                try {
                    mediaPlayer.prepare();//stop后下次重新播放要首先进入prepared状态
                    mediaPlayer.seekTo(0);//须将播放时间设置到0；这样才能在下次播放是重新开始，否则会继续上次播放
                    mediaPlayerState = PREPARA;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop(){
        stop(true);
    }


    public int getMaxProgress() {
        return maxProgress;
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_PROGRESS:
                if(mediaPlayerState < COMPLETE){
                    int next = refreshNow();
                    queueNextRefresh(next);
                }
                break;
        }
        return false;
    }

    // 计算更新时间
    private int refreshNow() {
        if(mProgress != null){
//            Log.d("Terry",Tag +"refreshNow progressMax" + mediaPlayer.getDuration());
//            Log.d("Terry",Tag +"refreshNow progressNow" + getCurrentPosition());
            mProgress.showProgress(getCurrentPosition());
        }
        int position = getCurrentPosition();
        if(position <=0){
            return 500;
        }
        int remaining = 1000 - (position % 1000);

        return remaining;
    }


    // 发出下一次更新。
    private void queueNextRefresh(int delay){
        if(mediaPlayer.isPlaying()){
            Message message = handler.obtainMessage(MSG_REFRESH_PROGRESS);
            handler.removeMessages(MSG_REFRESH_PROGRESS);
            handler.sendMessageDelayed(message,delay);
        }
    }



    public interface IProgress {
        void showProgress(int progress);
        void setMax(int max);
    }


    /**
     * 销毁mediaplayer
     */
    public void destroy(){
        stop(true);
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
