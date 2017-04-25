package com.cgbsoft.lib.mvp.ui.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cgbsoft.lib.R;
import com.cgbsoft.lib.R2;
import com.cgbsoft.lib.base.mvp.ui.BaseActivity;
import com.cgbsoft.lib.mvp.contract.video.VideoDetailContract;
import com.cgbsoft.lib.mvp.model.video.VideoInfoModel;
import com.cgbsoft.lib.mvp.presenter.video.VideoDetailPresenter;
import com.cgbsoft.lib.utils.cache.SPreference;
import com.cgbsoft.lib.utils.constant.VideoStatus;
import com.cgbsoft.lib.utils.damp.SpringEffect;
import com.cgbsoft.lib.utils.imgNetLoad.Imageload;
import com.cgbsoft.lib.utils.rxjava.RxBus;
import com.cgbsoft.lib.utils.rxjava.RxSubscriber;
import com.cgbsoft.lib.utils.service.FloatVideoService;
import com.cgbsoft.lib.utils.tools.NetUtils;
import com.cgbsoft.lib.utils.tools.Utils;
import com.cgbsoft.lib.widget.LoadingDialog;
import com.cgbsoft.lib.widget.MToast;
import com.cgbsoft.lib.widget.ProgressWheel;
import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.tencent.qcload.playersdk.ui.VideoRootFrame;
import com.tencent.qcload.playersdk.util.PlayerListener;
import com.tencent.qcload.playersdk.util.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.cgbsoft.lib.utils.constant.RxConstant.IS_PLAY_VIDEO_LOCAL_DELETE_OBSERVABLE;
import static com.cgbsoft.lib.utils.constant.RxConstant.NOW_PLAY_VIDEOID_OBSERVABLE;
import static com.cgbsoft.lib.utils.constant.RxConstant.VIDEO_LOCAL_REF_ONE_OBSERVABLE;
import static com.cgbsoft.lib.utils.constant.RxConstant.VIDEO_PLAY5MINUTES_OBSERVABLE;

/**
 * 视频详情
 * 简述：首先获取本地数据（有：判断视频是否下载，如果下载则播放本地视频，否则播放网络视频），然后在获取网络数据（成功：更新本地数据）
 * 推出时候会保存当前视频的播放进度。
 * 如果希望视频在下载列表中出现那么起码得进一次视频详情。
 * Created by xiaoyu.zhang on 2016/12/7 18:07
 * Email:zhangxyfs@126.com
 *  
 */
public class VideoDetailActivity extends BaseActivity<VideoDetailPresenter> implements VideoDetailContract.View, PlayerListener {
    @BindView(R2.id.rl_avd_head)
    RelativeLayout rl_avd_head;

    @BindView(R2.id.vrf_avd)
    VideoRootFrame vrf_avd;

    @BindView(R2.id.iv_mvv_cover)
    ImageView iv_mvv_cover;

    @BindView(R2.id.pw_mvv_wait)
    ProgressWheel pw_mvv_wait;

    @BindView(R2.id.ll_mvv_nowifi)
    LinearLayout ll_mvv_nowifi;

    @BindView(R2.id.tv_mvv_no_wifi)
    TextView tv_mvv_no_wifi;

    @BindView(R2.id.tv_mvv_rich_go)
    TextView tv_mvv_rich_go;

    @BindView(R2.id.iv_avd_back)
    ImageView iv_avd_back;

    @BindView(R2.id.iv_avd_back_play)
    ImageView iv_avd_back_play;

    @BindView(R2.id.sv_avd)
    ScrollView sv_avd;

    @BindView(R2.id.tv_avd_like_num)
    TextView tv_avd_like_num;

    @BindView(R2.id.iv_avd_like)
    ImageView iv_avd_like;

    @BindView(R2.id.ll_avd_cache)
    LinearLayout ll_avd_cache;

    @BindView(R2.id.iv_avd_cache)
    ImageView iv_avd_cache;

    @BindView(R2.id.tv_avd_cache)
    TextView tv_avd_cache;

    @BindView(R2.id.tv_avd_title)
    TextView tv_avd_title;

    @BindView(R2.id.tv_avd_content)
    TextView tv_avd_content;

    @BindView(R2.id.rl_avd_download)
    RelativeLayout rl_avd_download;

    @BindView(R2.id.iv_avd_close)
    ImageView iv_avd_close;

    @BindView(R2.id.tv_avd_hd)
    TextView tv_avd_hd;

    @BindView(R2.id.tv_avd_sd)
    TextView tv_avd_sd;

    @BindView(R2.id.ll_avd_cache_open)
    LinearLayout ll_avd_cache_open;

    @BindView(R2.id.tv_avd_cache_num)
    TextView tv_avd_cache_num;

    private String videoId, videoCoverUrl;
    private boolean isPlayAnim;
    private int comeFrom;
    private LoadingDialog loadingDialog;
    private boolean seekFlag = true, isPlaying, isSetDataSource;//是否播放器设置了数据;
    private int playerCurrentTime;
    private VideoInfoModel videoInfoModel;
    private long startPlayTime;//当前播放时间,用于做任务
    private long allPlayTime;//一共播放时间
    private long fiveMinutes = 5 * 60 * 1000;
    private boolean isSetFullscreenHandler;

    private ConnectionChangeReceiver connectionChangeReceiver;
    private ExitActivityTransition exitTransition;
    private Subscription delaySub, delaySub2;
    private boolean isFullscreen, isDisplayCover;
    private AnimationSet hdAnimationSet, sdAnimationSet, openAnimationSet, closeAnimationSet;
    private Observable<Boolean> isPlayVideoLocalDeleteObservable;

    private boolean isOnPause;
    private int onPausePlayStauts = -1;//默认为-1，没在播放为0 在播放为1

    @Override
    protected void after() {
        super.after();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int layoutID() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        videoId = getIntent().getStringExtra("videoId");
        videoCoverUrl = getIntent().getStringExtra("videoCoverUrl");
        isPlayAnim = getIntent().getBooleanExtra("isPlayAnim", true);
        comeFrom = getIntent().getIntExtra("comeFrom", -1);
        loadingDialog = LoadingDialog.getLoadingDialog(this, false, false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        changeVideoViewSize(Configuration.ORIENTATION_PORTRAIT);
        if (iv_mvv_cover.getVisibility() == View.VISIBLE && videoCoverUrl != null && !TextUtils.isEmpty(videoCoverUrl)) {
            Imageload.display(this, videoCoverUrl, 0, 0, 0, iv_mvv_cover, R.drawable.bg_default, R.drawable.bg_default);
            isDisplayCover = true;
        }
        if (isPlayAnim)
            exitTransition = ActivityTransition.with(getIntent()).duration(200).to(rl_avd_head).start(savedInstanceState);
        else
            sv_avd.setVisibility(View.VISIBLE);

        if (comeFrom == 1)
            FloatVideoService.stopService();

        if (TextUtils.isEmpty(videoId)) {
            loadingDialog.setResult(false, getString(R.string.no_videoid_str), 1000, this::finish);
            return;
        }

        vrf_avd.setListener(this);
        getVideoDetailInfo();
        pw_mvv_wait.setVisibility(View.VISIBLE);
        getPresenter().bindDownloadCallback(videoId);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectionChangeReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(connectionChangeReceiver, filter);

        SpringEffect.doEffectSticky(iv_avd_like, 1.2f, () -> getPresenter().toVideoLike());
        tv_avd_cache_num.setText(String.valueOf(getPresenter().getCacheVideoNum()));

        FloatVideoService.stopService();
    }

    @Override
    protected void data() {
        super.data();
        initAnim();
        isPlayVideoLocalDeleteObservable = RxBus.get().register(IS_PLAY_VIDEO_LOCAL_DELETE_OBSERVABLE, Boolean.class);
        isPlayVideoLocalDeleteObservable.subscribe(new RxSubscriber<Boolean>() {
            @Override
            protected void onEvent(Boolean aBoolean) {
                if (aBoolean) {
                    if (onPausePlayStauts > 0) {
                        vrf_avd.release();
                        playNetData();
                    }
                    if (onPausePlayStauts == 0) {
                        vrf_avd.pause();
                    }
                }
            }

            @Override
            protected void onRxError(Throwable error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        onPausePlayStauts = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        onPausePlayStauts = -1;
    }

    @OnClick(R2.id.iv_avd_back)
    void backClick() {
        toFinish();
    }

    @OnClick(R2.id.iv_avd_back_play)
    void iv_avd_back_play() {
        toDataStatistics(1021, 10102, new String[]{"缩小", SPreference.isColorCloud(this), SPreference.getOrganizationName(this)});
        FloatVideoService.startService(videoId);
        finish();
    }

    @OnClick(R2.id.ll_mvv_nowifi)
    void noWifiClick() {
        play(false);
    }

    @OnClick(R2.id.ll_avd_cache)
    void cacheOpenClick() {
        toDataStatistics(1021, 10103, new String[]{"下载", SPreference.isColorCloud(this), SPreference.getOrganizationName(this)});
        openCacheView();
    }

    @OnClick(R2.id.iv_avd_close)
    void cacheCloseClick() {
        closeCacheView();
    }

    @OnClick(R2.id.tv_avd_sd)
    void sdClick() {
        tv_avd_hd.setEnabled(false);
        tv_avd_sd.setEnabled(false);
        tv_avd_sd.startAnimation(sdAnimationSet);

        //todo 开始后台下载
        getPresenter().updataDownloadType(VideoStatus.SD);
        getPresenter().toDownload(videoId);
    }

    @OnClick(R2.id.tv_avd_hd)
    void hdClick() {
        tv_avd_hd.setEnabled(false);
        tv_avd_sd.setEnabled(false);
        tv_avd_hd.startAnimation(hdAnimationSet);

        //todo 开始后台下载
        getPresenter().updataDownloadType(VideoStatus.HD);
        getPresenter().toDownload(videoId);
    }

    //打开下载列表页面
    @OnClick(R2.id.ll_avd_cache_open)
    void videoDownloadListOpenClick() {
        RxBus.get().post(NOW_PLAY_VIDEOID_OBSERVABLE, videoId);//发送消息，当前正在播放的视频id
        getPresenter().updataNowPlayTime(vrf_avd.getCurrentTime());//更新当前播放视频的位置
        openActivity(VideoDownloadListActivity.class);
    }

    @Override
    public void onBackPressed() {
        toFinish();
    }

    private void toFinish() {
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            sv_avd.setVisibility(View.VISIBLE);
            iv_avd_back.setVisibility(View.VISIBLE);
        } else {
            if (isPlayAnim) {
                sv_avd.setVisibility(View.GONE);
                delaySub2 = Observable.just(1).delay(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new RxSubscriber<Integer>() {
                            @Override
                            protected void onEvent(Integer integer) {
                                exitTransition.exit(VideoDetailActivity.this);
                            }

                            @Override
                            protected void onRxError(Throwable error) {

                            }
                        });
            } else {
                finish();
            }
        }
    }

    private void getVideoDetailInfo() {
        if (isPlayAnim) {
            if (delaySub != null && !delaySub.isUnsubscribed()) {
                return;
            }
            delaySub = Observable.just(1).delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxSubscriber<Integer>() {
                        @Override
                        protected void onEvent(Integer integer) {
                            getPresenter().getVideoDetailInfo(videoId);
                            sv_avd.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onRxError(Throwable error) {

                        }
                    });
        } else {
            getPresenter().getVideoDetailInfo(videoId);
        }
    }

    @Override
    protected VideoDetailPresenter createPresenter() {
        return new VideoDetailPresenter(this, this);
    }

    @Override
    public void getLocalVideoInfoSucc(VideoInfoModel model) {
        videoInfoModel = model;
        playerCurrentTime = videoInfoModel.currentTime;
        setData();
        play(true);

        switch (videoInfoModel.status) {
            case VideoStatus.DOWNLOADING:
                tv_avd_cache.setText(R.string.caching_str);
                iv_avd_cache.setImageResource(R.drawable.ic_caching);

                if (videoInfoModel.downloadtype == 1) {
                    setSdTvBlue();
                } else {
                    setHdTvBlue();
                }
                tv_avd_hd.setEnabled(false);
                tv_avd_sd.setEnabled(false);
                break;
            case VideoStatus.NONE:
                tv_avd_cache.setText(R.string.cache_str);
                iv_avd_cache.setImageResource(R.drawable.ic_cache);
                break;
            case VideoStatus.FINISH:
                if (!TextUtils.isEmpty(videoInfoModel.localVideoPath)) {
                    File file = new File(videoInfoModel.localVideoPath);
                    if (file.isFile() && file.exists()) {
                        tv_avd_cache.setText(R.string.cached_str);
                        iv_avd_cache.setImageResource(R.drawable.ic_cached);

                        if (videoInfoModel.downloadtype == 1) {
                            setSdTvBlue();
                        } else {
                            setHdTvBlue();
                        }

                        tv_avd_hd.setEnabled(false);
                        tv_avd_sd.setEnabled(false);
                        break;
                    }
                }
        }
    }

    @Override
    public void getNetVideoInfoSucc(VideoInfoModel model) {
        videoInfoModel = model;
        toDataStatistics(1020, 10101, new String[]{model.videoName, SPreference.isColorCloud(this), SPreference.getOrganizationName(this)});
        setData();
        play(true);
    }

    @Override
    public void toVideoLikeSucc(int likeRes, int likeNum) {
        iv_avd_like.setImageResource(likeRes);
        tv_avd_like_num.setText(String.valueOf(likeNum));
        toDataStatistics(1021, 10104, new String[]{videoInfoModel.videoName, SPreference.isColorCloud(this), SPreference.getOrganizationName(this)});
    }

    @Override
    public void onDownloadFinish(VideoInfoModel model) {
        videoInfoModel = model;
        if (!TextUtils.isEmpty(videoInfoModel.localVideoPath)) {
            File file = new File(videoInfoModel.localVideoPath);
            if (file.isFile() && file.exists()) {
                tv_avd_cache.setText(R.string.cached_str);
                iv_avd_cache.setImageResource(R.drawable.ic_cached);

                if (videoInfoModel.downloadtype == 1) {
                    setSdTvBlue();
                } else {
                    setHdTvBlue();
                }

                tv_avd_hd.setEnabled(false);
                tv_avd_sd.setEnabled(false);
            }
        }
    }

    @Override
    public void onDownloadVideoAdd() {
        tv_avd_cache_num.setText(String.valueOf(getPresenter().getCacheVideoNum()));
        tv_avd_cache.setText(R.string.caching_str);
        iv_avd_cache.setImageResource(R.drawable.ic_caching);
    }


    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        new MToast(this).showLoginFailure("播放失败，请重新尝试！");
        vrf_avd.pause();
        pw_mvv_wait.setVisibility(View.GONE);
    }


    private void playNetData() {
        if (NetUtils.getNetState() != NetUtils.NetState.NET_WIFI) {
            ll_mvv_nowifi.setVisibility(View.VISIBLE);
            return;
        }
        List<VideoInfo> videos = new ArrayList<>();
        VideoInfo v1 = new VideoInfo();
        VideoInfo v2 = new VideoInfo();
        v1.description = "标清";
        v1.type = VideoInfo.VideoType.MP4;
        v2.description = "高清";
        v2.type = VideoInfo.VideoType.MP4;
        v1.url = videoInfoModel.sdUrl;
        v2.url = videoInfoModel.hdUrl;
        videos.add(v1);
        videos.add(v2);

        vrf_avd.play(videos);
        ll_mvv_nowifi.setVisibility(View.GONE);
        pw_mvv_wait.setVisibility(View.GONE);
        isSetDataSource = true;

        if (!isSetFullscreenHandler) {
            isSetFullscreenHandler = true;
            vrf_avd.setToggleFullScreenHandler(() -> {
                if (vrf_avd.isFullScreen()) { // 竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    sv_avd.setVisibility(View.VISIBLE);
                    iv_avd_back.setVisibility(View.VISIBLE);
                } else { // 横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    sv_avd.setVisibility(View.GONE);
                    iv_avd_back.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void play(boolean isCheckNet) {
        if (!isVideoDownload())
            if (NetUtils.getNetState() != NetUtils.NetState.NET_WIFI && isCheckNet) {
                ll_mvv_nowifi.setVisibility(View.VISIBLE);
                return;
            } else {
                ll_mvv_nowifi.setVisibility(View.GONE);
            }
        if (isPlaying) {
            return;
        }

        int isLocalType = -1;
        boolean isCouldLocalPlay = false;

        if (videoInfoModel.status == VideoStatus.FINISH) {
            File file = new File(videoInfoModel.localVideoPath);
            if (file.isFile() && file.exists()) {
                isLocalType = videoInfoModel.downloadtype;
                isCouldLocalPlay = true;
            }
        }
        List<VideoInfo> videos = new ArrayList<>();
        VideoInfo v1 = new VideoInfo();
        VideoInfo v2 = new VideoInfo();
        v1.description = "标清";
        v1.type = VideoInfo.VideoType.MP4;
        v2.description = "高清";
        v2.type = VideoInfo.VideoType.MP4;

        if (isCouldLocalPlay) {
            if (isLocalType == 0) {//高清
                v2.url = videoInfoModel.localVideoPath;
                videos.add(v2);
            } else if (isLocalType == 1) {//标清
                v1.url = videoInfoModel.localVideoPath;
                videos.add(v1);
            }
        } else {
            v1.url = videoInfoModel.sdUrl;
            v2.url = videoInfoModel.hdUrl;
            videos.add(v1);
            videos.add(v2);
        }

        vrf_avd.play(videos);
        ll_mvv_nowifi.setVisibility(View.GONE);
        pw_mvv_wait.setVisibility(View.GONE);
        isSetDataSource = true;

        if (!isSetFullscreenHandler) {
            isSetFullscreenHandler = true;
            vrf_avd.setToggleFullScreenHandler(() -> {
                if (vrf_avd.isFullScreen()) { // 竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    sv_avd.setVisibility(View.VISIBLE);
                    iv_avd_back.setVisibility(View.VISIBLE);
                } else { // 横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    sv_avd.setVisibility(View.GONE);
                    iv_avd_back.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onStateChanged(int arg0) {
                /*
                 * 1 STATE_IDLE 播放器空闲，既不在准备也不在播放 2 STATE_PREPARING 播放器正在准备 3
				 * STATE_BUFFERING 播放器已经准备完毕，但无法立即播放。此状态
				 * 的原因有很多，但常见的是播放器需要缓冲更多数据才能开始播放 4 STATE_PAUSE 播放器准备好并可以立即播放当前位置
				 * 5 STATE_PLAY 播放器正在播放中 6 STATE_ENDED 播放已完毕
				 */
        switch (arg0) {
            case 5://播放中
                startPlayTime = System.currentTimeMillis();
                iv_mvv_cover.setVisibility(View.GONE);
                pw_mvv_wait.setVisibility(View.GONE);
                seekToPlay(playerCurrentTime);
                isPlaying = true;
                break;
            case 4:
                if (isOnPause) {
                    onPausePlayStauts = 1;
                }

                allPlayTime += System.currentTimeMillis() - startPlayTime;
                if (allPlayTime > fiveMinutes) {
                    RxBus.get().post(VIDEO_PLAY5MINUTES_OBSERVABLE, allPlayTime);
                }
                isPlaying = false;
                break;
            default:
                break;
        }
    }


    protected void seekToPlay(int i) {
        if (seekFlag) {
            vrf_avd.seekTo(i);
        }
        seekFlag = false;
    }

    private void changeVideoViewSize(int orientation) {
        ViewGroup.LayoutParams lp = rl_avd_head.getLayoutParams();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.width = Utils.getRealScreenWidth(this);
        } else {
            lp.width = Utils.getScreenWidth(this);
        }
        lp.height = lp.width * 9 / 16;
        rl_avd_head.setLayoutParams(lp);
    }

    private void openCacheView() {
        if (rl_avd_download.getVisibility() == View.VISIBLE) {
            return;
        }
        rl_avd_download.setVisibility(View.VISIBLE);
        rl_avd_download.startAnimation(openAnimationSet);
    }

    private void closeCacheView() {
        if (rl_avd_download.getVisibility() == View.GONE) {
            return;
        }
        rl_avd_download.setVisibility(View.GONE);
        rl_avd_download.startAnimation(closeAnimationSet);
    }

    private void initAnim() {
        hdAnimationSet = new AnimationSet(true);
        TranslateAnimation hdTranslationAnim_1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, -0.5f);
        hdTranslationAnim_1.setDuration(300);
        TranslateAnimation hdTranslationAnim_2 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_PARENT, 1f);
        hdTranslationAnim_2.setStartOffset(300);
        hdTranslationAnim_2.setDuration(500);
        hdAnimationSet.addAnimation(hdTranslationAnim_1);
        hdAnimationSet.addAnimation(hdTranslationAnim_2);
        hdAnimationSet.setAnimationListener(new AnimListener(2));

        sdAnimationSet = new AnimationSet(true);
        TranslateAnimation sdTranslationAnim_1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, -0.5f);
        sdTranslationAnim_1.setDuration(300);
        TranslateAnimation sdTranslationAnim_2 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_PARENT, 1f);
        sdTranslationAnim_2.setStartOffset(300);
        sdTranslationAnim_2.setDuration(500);
        sdAnimationSet.addAnimation(sdTranslationAnim_1);
        sdAnimationSet.addAnimation(sdTranslationAnim_2);
        sdAnimationSet.setAnimationListener(new AnimListener(1));

        openAnimationSet = new AnimationSet(true);
        TranslateAnimation openTranslateAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f);
        openTranslateAnim.setDuration(800);
        openAnimationSet.addAnimation(openTranslateAnim);


        closeAnimationSet = new AnimationSet(true);
        TranslateAnimation closeTranslateAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f);
        closeTranslateAnim.setDuration(800);
        closeAnimationSet.addAnimation(closeTranslateAnim);
    }

    //高清文字颜色
    private void setHdTvBlue() {
        tv_avd_hd.setBackgroundColor(0x00ffffff);
        tv_avd_hd.setTextColor(0xff5ba8f3);
        tv_avd_sd.setTextColor(0xffe6e6e6);
    }

    private void setSdTvBlue() {
        tv_avd_sd.setBackgroundColor(0x00ffffff);
        tv_avd_sd.setTextColor(0xff5ba8f3);
        tv_avd_hd.setTextColor(0xffe6e6e6);
    }


    @Override
    protected void onDestroy() {
        if (vrf_avd != null) {
            getPresenter().updataNowPlayTime(vrf_avd.getCurrentTime());
            RxBus.get().post(VIDEO_LOCAL_REF_ONE_OBSERVABLE, vrf_avd.getCurrentTime());
            vrf_avd.release();
            vrf_avd = null;
        }
        getPresenter().updataFinalWatchTime();

        if (connectionChangeReceiver != null)
            unregisterReceiver(connectionChangeReceiver);

        if (allPlayTime < fiveMinutes) {
            allPlayTime += System.currentTimeMillis() - startPlayTime;
            if (allPlayTime > fiveMinutes) {
                RxBus.get().post(VIDEO_PLAY5MINUTES_OBSERVABLE, allPlayTime);
            }
        } else {
            RxBus.get().post(VIDEO_PLAY5MINUTES_OBSERVABLE, allPlayTime);
        }

        if (delaySub != null && !delaySub.isUnsubscribed()) {
            delaySub.unsubscribe();
            delaySub = null;
        }

        if (delaySub2 != null && !delaySub2.isUnsubscribed()) {
            delaySub2.unsubscribe();
            delaySub2 = null;
        }

        if (isPlayVideoLocalDeleteObservable != null) {
            RxBus.get().unregister(IS_PLAY_VIDEO_LOCAL_DELETE_OBSERVABLE, isPlayVideoLocalDeleteObservable);
        }

        super.onDestroy();
    }

    private void setData() {
        tv_avd_like_num.setText(String.valueOf(videoInfoModel.likeNum));
        tv_avd_title.setText(videoInfoModel.videoName);
        tv_avd_content.setText(videoInfoModel.content);
        if (!videoInfoModel.isLike) {
            iv_avd_like.setImageResource(R.drawable.ic_like_up);
        } else {
            iv_avd_like.setImageResource(R.drawable.ic_like_down);
            iv_avd_like.setEnabled(false);
        }
        if (!isDisplayCover && videoInfoModel.videoCoverUrl != null && !TextUtils.isEmpty(videoInfoModel.videoCoverUrl)) {
            isDisplayCover = true;
            Imageload.display(this, videoInfoModel.videoCoverUrl, 0, 0, 0, iv_mvv_cover, null, null);
        }
    }

    private boolean isVideoDownload() {
        if (videoInfoModel.status == VideoStatus.FINISH) {
            File file = new File(videoInfoModel.localVideoPath);
            if (file.isFile() && file.exists()) {
                return true;
            }
        }
        return false;
    }

    class AnimListener implements Animation.AnimationListener {
        int which;

        AnimListener(int which) {
            this.which = which;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (which == 1) {
                tv_avd_sd.setBackgroundColor(0x99999999);
            } else {
                tv_avd_hd.setBackgroundColor(0x99999999);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (which == 1) {
                setSdTvBlue();
            } else {
                setHdTvBlue();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {

        private boolean isVideoDownload() {
            if (videoInfoModel == null) {
                VideoInfoModel model = getPresenter().getVideoInfo(videoId);
                if (model != null && model.status == VideoStatus.FINISH) {
                    File file = new File(model.localVideoPath);
                    if (file.isFile() && file.exists()) {
                        return true;
                    }
                }
                return false;
            } else {
                if (videoInfoModel.status == VideoStatus.FINISH) {
                    File file = new File(videoInfoModel.localVideoPath);
                    if (file.isFile() && file.exists()) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            NetUtils.NetState netStatus = NetUtils.getNetState();
            if (netStatus == NetUtils.NetState.NET_NO) {   //无网络
                if (!isVideoDownload()) {
                    ll_mvv_nowifi.setVisibility(View.VISIBLE);
                    tv_mvv_no_wifi.setText(R.string.avd_no_net_str);
                    tv_mvv_rich_go.setText(R.string.avd_ref_str);
                }
                getPresenter().updataNowPlayTime(vrf_avd.getCurrentTime());

            } else if (netStatus == NetUtils.NetState.NET_WIFI) {   //wifi
                ll_mvv_nowifi.setVisibility(View.GONE);
                if (videoInfoModel != null && isSetDataSource) {
                    if (vrf_avd.getCurrentStatus() != 5) {
                        vrf_avd.seekTo(videoInfoModel.currentTime);
                        vrf_avd.play();
                    }
                }
            } else {   //手机流量
                if (videoInfoModel != null && isSetDataSource) {
                    getPresenter().updataNowPlayTime(vrf_avd.getCurrentTime());
                    vrf_avd.pause();
                }
                ll_mvv_nowifi.setVisibility(View.VISIBLE);
                tv_mvv_no_wifi.setText(R.string.avd_no_wifi_str);
                tv_mvv_rich_go.setText(R.string.avd_rich_go_str);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sv_avd.setVisibility(View.GONE);
            isFullscreen = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            sv_avd.setVisibility(View.VISIBLE);
            isFullscreen = false;
        }
        changeVideoViewSize(newConfig.orientation);
    }
}
