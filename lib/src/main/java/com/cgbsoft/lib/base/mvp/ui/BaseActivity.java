package com.cgbsoft.lib.base.mvp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cgbsoft.lib.Appli;
import com.cgbsoft.lib.R;
import com.cgbsoft.lib.base.mvp.presenter.impl.BasePresenterImpl;
import com.cgbsoft.lib.utils.cache.OtherDataProvider;
import com.cgbsoft.lib.utils.cache.SPreference;
import com.cgbsoft.lib.utils.constant.Constant;
import com.cgbsoft.lib.utils.db.dao.DaoSession;
import com.cgbsoft.lib.utils.tools.DataStatisticsUtils;
import com.cgbsoft.lib.widget.MToast;
import com.cgbsoft.lib.widget.WeakHandler;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * TODO 基础activity 默认开启透明导航条
 * Created by user on 2016/11/4.
 */

public abstract class BaseActivity<P extends BasePresenterImpl> extends RxAppCompatActivity implements Constant {
    protected Activity baseContext;
    private Appli mAppli;//applicaiton
    private WeakHandler mBaseHandler;//handler
    private DaoSession mDaoSession;//数据库
    private Unbinder mUnbinder;//用于butterKnife解绑
    private P mPresenter;//功能调用
    private boolean mIsNeedAdapterPhone = true;
    private boolean mIsNeedGoneNavigationBar;

    private long mExitPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.baseContext=BaseActivity.this;
        if (getIsNightTheme() && savedInstanceState == null) {
            if (SPreference.getIdtentify(getApplicationContext()) == IDS_ADVISER) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        } else {
            before();
            if (layoutID() > 0)
                setContentView(layoutID());
            after();
            init(savedInstanceState);
            data();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OtherDataProvider.addTopActivity(getApplicationContext(), getClass().getName());
    }


    protected void before() {
        mAppli = (Appli) getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected abstract int layoutID();

    protected abstract void init(Bundle savedInstanceState);

    protected abstract P createPresenter();

    protected void after() {
        mUnbinder = ButterKnife.bind(this);
        mBaseHandler = new WeakHandler();
        if (mPresenter == null)
            mPresenter = createPresenter();

        if (mIsNeedAdapterPhone && !isNeedAdapterPhone()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //透明导航栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        if (mIsNeedGoneNavigationBar) {
            toHideNav();
        }
    }

    protected void data() {

    }

    /**
     * 设置是否需要适配手机（必须写在before里，默认为true）
     *
     * @return
     */
    protected void setIsNeedAdapterPhone(boolean b) {
        mIsNeedAdapterPhone = b;
    }

    /**
     * 设置是否需要不显示导航条（必须写子before里，默认为false）
     *
     * @return
     */
    protected void setIsNeedGoneNavigationBar(boolean b) {
        mIsNeedGoneNavigationBar = b;
    }

    /**
     * 获取夜间模式状态
     */
    protected boolean getIsNightTheme() {
        return false;
    }

    /**
     * 是否需要适配手机
     *
     * @return
     */
    protected boolean isNeedAdapterPhone() {
        if (Build.VERSION.SDK_INT > 21) {
            return false;
        } else if (android.os.Build.MODEL.toLowerCase().contains("vivo")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取application
     *
     * @return
     */
    protected Appli getAppli() {
        return mAppli;
    }

    /**
     * 获取数据库
     *
     * @return
     */
    protected DaoSession getmDaoSession() {
        if (mDaoSession == null) {
            mDaoSession = getAppli().getDaoSession();
        }
        return mDaoSession;
    }

    /**
     * 获取presenter
     *
     * @return
     */
    protected P getPresenter() {
        return mPresenter;
    }

    protected void setPresenter() {
        if (mPresenter == null)
            mPresenter = createPresenter();
    }

    /**
     * 获取handler
     *
     * @return
     */
    protected WeakHandler getHandler() {
        return mBaseHandler;
    }


    protected void toHideNav() {
        mBaseHandler.post(mHideRunnable);
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            mBaseHandler.post(mHideRunnable); // hide the navigation bar
        });
    }

    protected void toShowNav() {
        mBaseHandler.removeCallbacks(mHideRunnable);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    protected Runnable mHideRunnable = () -> {
        // must be executed in main thread :)
        getWindow().getDecorView().setSystemUiVisibility(getHideFlags());
    };

    private int getHideFlags() {
        int flags;
        int curApiVersion = Build.VERSION.SDK_INT;
        // This work only for android 4.4+
        if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
            // This work only for android 4.4+
            // hide navigation bar permanently in android activity
            // touch the screen, the navigation bar will not show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

        } else {
            // touch the screen, the navigation bar will show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        return flags;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBaseHandler != null) {
            mBaseHandler.removeCallbacksAndMessages(null);
            mBaseHandler = null;
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        boolean isCurrentRunningForeground = SPreference.isCurrentRunningForeground(this);

//        Observable.just(1).filter(v-> !isCurrentRunningForeground)

    }

    // 判断权限集合
    protected boolean needPermissions(String... permissions) {
        //判断版本是否兼容
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        boolean isNeed;
        for (String permission : permissions) {
            isNeed = needsPermission(permission);
            if (isNeed) {
                return true;
            }
        }
        return false;
    }

    protected List<String> noPermissions(String... permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (needPermissions(permission)) {
                list.add(permission);
            }
        }
        return list;
    }

    // 判断是否缺少权限
    protected boolean needsPermission(String permission) {
        return ContextCompat.checkSelfPermission(Appli.getContext(), permission) != PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 打开activity
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 统计
     *
     * @param grp
     * @param act
     * @param arg1
     */
    protected void toDataStatistics(int grp, int act, String arg1) {
        HashMap<String, String> data = new HashMap<>();
        data.put("grp", String.valueOf(grp));
        data.put("act", String.valueOf(act));
        data.put("arg1", arg1);
        DataStatisticsUtils.push(getApplicationContext(), data);
    }

    protected void toDataStatistics(int grp, int act, String[] args) {
        HashMap<String, String> data = new HashMap<>();
        data.put("grp", String.valueOf(grp));
        data.put("act", String.valueOf(act));
        for (int i = 1; i <= args.length; i++) {
            data.put("arg" + i, args[i - 1]);
        }
        DataStatisticsUtils.push(getApplicationContext().getApplicationContext(), data);
    }

    /**
     * umeng 统计
     *
     * @param umengKey
     * @param mapKey
     * @param mapValue
     */
    protected void toUmengStatistics(String umengKey, String mapKey, String mapValue) {
        Map<String, String> map = new HashMap<>();
        map.put(mapKey, mapValue);
        MobclickAgent.onEvent(getApplicationContext(), umengKey, map);
    }


    /**
     * 双击退出。
     */
    protected boolean exitBy2Click() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mExitPressedTime) > 2000) {//比较两次按键时间差
            MToast.makeText(this, getString(R.string.nav_back_again_finish), Toast.LENGTH_SHORT);
            mExitPressedTime = mNowTime;
        } else {
            finish();
            return true;
        }
        return false;
    }

/**
 * 判断是否拥有运时权限
 */
}

