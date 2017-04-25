package com.cgbsoft.lib.mvp.ui.video.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cgbsoft.lib.R2;
import com.cgbsoft.lib.mvp.ui.video.listener.VideoDownloadListListener;
import com.cgbsoft.lib.utils.tools.Utils;
import com.cgbsoft.lib.widget.recycler.BaseHolder;

import butterknife.BindView;

/**
 * Created by xiaoyu.zhang on 2016/12/13 16:54
 * Email:zhangxyfs@126.com
 *  
 */
public class VideoDownloadListHolder extends BaseHolder {
    @BindView(R2.id.tv_avd_id)
    public TextView tv_avd_id;

    @BindView(R2.id.ll_avd)
    public LinearLayout ll_avd;

    @BindView(R2.id.fl_avd_check)
    public FrameLayout fl_avd_check;

    @BindView(R2.id.cb_avd)
    public CheckBox cb_avd;

    @BindView(R2.id.fl_avd_cover)
    public FrameLayout fl_avd_cover;

    @BindView(R2.id.iv_avd_cover)
    public ImageView iv_avd_cover;

    @BindView(R2.id.ll_avd_pause)
    public LinearLayout ll_avd_pause;

    @BindView(R2.id.iv_avd_pause)
    public ImageView iv_avd_pause;

    @BindView(R2.id.tv_avd_pause)
    public TextView tv_avd_pause;

    @BindView(R2.id.tv_avd_title)
    public TextView tv_avd_title;

    @BindView(R2.id.pb_avd)
    public ProgressBar pb_avd;

    @BindView(R2.id.tv_avd_progress)
    public TextView tv_avd_progress;

    @BindView(R2.id.tv_avd_speed)
    public TextView tv_avd_speed;

    public VideoDownloadListHolder(View itemView, VideoDownloadListListener listListener) {
        super(itemView);
        int width = Utils.convertDipOrPx(context, 150);
        int height = width * 9 / 16;

        ViewGroup.LayoutParams lp = fl_avd_cover.getLayoutParams();
        lp.width = width;
        lp.height = height;
        fl_avd_cover.setLayoutParams(lp);

        ll_avd.setOnClickListener(v -> listListener.onItemClick(getAdapterPosition(), iv_avd_cover, iv_avd_pause, tv_avd_pause));
        cb_avd.setOnCheckedChangeListener((buttonView, isChecked) -> listListener.onCheck(getAdapterPosition(), isChecked));
        fl_avd_check.setOnClickListener(v -> {
            boolean isCheck = !cb_avd.isChecked();
            cb_avd.setChecked(isCheck);
            listListener.onCheck(getAdapterPosition(), isCheck);
        });
    }
}
