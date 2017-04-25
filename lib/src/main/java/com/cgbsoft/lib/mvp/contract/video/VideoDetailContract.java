package com.cgbsoft.lib.mvp.contract.video;

import com.cgbsoft.lib.base.mvp.presenter.BasePresenter;
import com.cgbsoft.lib.base.mvp.view.BaseView;
import com.cgbsoft.lib.mvp.model.video.VideoInfoModel;

/**
 * Created by xiaoyu.zhang on 2016/12/7 18:08
 * Email:zhangxyfs@126.com
 *  
 */
public interface VideoDetailContract {
    interface Presenter extends BasePresenter {
        /**
         * 获取视频信息
         *
         * @param videoId
         */
        void getVideoDetailInfo(String videoId);

        /**
         * 保存播放进度
         *
         * @param playTime
         */
        void updataNowPlayTime(int playTime);

        /**
         * 保存下载视频清晰度
         *
         * @param type
         */
        void updataDownloadType(int type);

        /**
         * 更新最后观看时间
         */
        void updataFinalWatchTime();

        /**
         * 视频点赞
         */
        void toVideoLike();

        /**
         * 获取缓存视频数量
         *
         * @return
         */
        long getCacheVideoNum();

        /**
         * 获取本地视频数据
         *
         * @param videoId
         * @return
         */
        VideoInfoModel getVideoInfo(String videoId);

        /**
         * 添加到下载队列里
         *
         * @param videoId
         */
        void toDownload(String videoId);

        /**
         * 绑定下载监听
         *
         * @param videoId
         */
        void bindDownloadCallback(String videoId);
    }

    interface View extends BaseView {
        void getLocalVideoInfoSucc(VideoInfoModel model);

        void getNetVideoInfoSucc(VideoInfoModel model);

        void toVideoLikeSucc(int likeRes, int likeNum);

        void onDownloadFinish(VideoInfoModel model);

        void onDownloadVideoAdd();
    }
}
