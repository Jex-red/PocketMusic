package com.example.xsy.pocketmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xsy.pocketmusic.adapter.MyMusicListAdapter;
import com.example.xsy.pocketmusic.utils.MediaUtils;
import com.example.xsy.pocketmusic.vo.Mp3Info;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView_my_music;
    //装载音乐信息
    private ArrayList<Mp3Info> mp3Infos;
    private MainActivity mainActivity;
    private MyMusicListAdapter myMusicListAdapter;
    //更新组件
    private ImageView imageViewAlbum;
    private TextView textViewsinger;
    private TextView textViewsong;
    private ImageView imageViewPlayPause, imageViewNext;
    private int position = 0;//当前播放位置
    private boolean isPause = false;


    public MyMusicListFragment() {
        // Required empty public constructor
    }
    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        return my;
    }
    /**
     * 加载本地音乐列表
     */
    private void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        myMusicListAdapter = new MyMusicListAdapter(mainActivity, mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_myMusic);//初始化ListView
        imageViewAlbum = (ImageView) view.findViewById(R.id.imageView_album);
        imageViewNext = (ImageView) view.findViewById(R.id.iv_next);
        imageViewPlayPause = (ImageView) view.findViewById(R.id.iv_play_pause);
        textViewsinger = (TextView) view.findViewById(R.id.tv_singer);
        textViewsong = (TextView) view.findViewById(R.id.tv_songName);
        listView_my_music.setOnItemClickListener(this);
        imageViewPlayPause.setOnClickListener(this);
        imageViewNext.setOnClickListener(this);
        imageViewAlbum.setOnClickListener(this);
        loadData();
        mainActivity.bindPlayService();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.unbindPlayService();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.playService.play(position);

    }
    /**
     * 回调播放状态下的UI设置
     *
     * @param position
     */
    public void changeUIStatusOnPlay(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            textViewsong.setText(mp3Info.getTitle());
            textViewsinger.setText(mp3Info.getArtist());

            Bitmap smallAlubmImage = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageViewAlbum.setImageBitmap(smallAlubmImage);

            if (mainActivity.playService.isPlaying()) {
                imageViewPlayPause.setImageResource(R.mipmap.player_btn_pause_normal);
            } else {
                imageViewPlayPause.setImageResource(R.mipmap.player_btn_pause_normal);
            }
            this.position = position;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_pause: {
                if (mainActivity.playService.isPlaying()) {
                    imageViewPlayPause.setImageResource(R.mipmap.player_btn_play_normal);
                    mainActivity.playService.pause();
                    isPause = true;
                } else {
                    imageViewPlayPause.setImageResource(R.mipmap.player_btn_pause_normal);
                    if (isPause) {

                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(0);//从头开始播放
                    }
                    isPause = false;
                }
                break;
            }
            case R.id.iv_next: {
                mainActivity.playService.next();
                break;
            }
            case R.id.imageView_album: {
                Intent intent = new Intent(mainActivity, PlayService.class);
                intent.putExtra("isPause", isPause);
                startActivity(intent);
                break;
            }

            default:
                break;
        }

    }
}
