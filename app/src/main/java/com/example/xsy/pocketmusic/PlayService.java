package com.example.xsy.pocketmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.example.xsy.pocketmusic.utils.MediaUtils;
import com.example.xsy.pocketmusic.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放的服务组件
 * 1.实现的功能有：
 * 2.播放
 * 3.暂停
 * 4.下一首
 * 5.上一首
 * 6.获取当前歌曲进度条
 */

public class PlayService extends Service {

    private MediaPlayer mediaPlayer;
    private int currentPosition;//当前播放歌曲的位置
    ArrayList<Mp3Info> mp3Infos;
    private MusicUpdateListener musicUpdateListener;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isPause = false;


    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);
        es.execute(updateStatusRunnable);
    }
    //更新状态的线程

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //播放
    public void play(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdateListener != null) {
                musicUpdateListener.onChange(currentPosition);
            }

        }

    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }
    public int getCurrentProgress() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    //可以返回playservice对象的内部类
    class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    //暂停
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }

    }

    //下一首
    public void next() {
        if (currentPosition >= mp3Infos.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);

    }

    //上一首
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);

    }

    //MediaPlay的播放方法
    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

    }
    //更新状态的接口

    public interface MusicUpdateListener {

        public void onPublish(int progress);

        public void onChange(int position);
    }
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }
    public boolean isPause() {
        return isPause;
    }
    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

}
