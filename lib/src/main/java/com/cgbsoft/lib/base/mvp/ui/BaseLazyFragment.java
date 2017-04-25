package com.cgbsoft.lib.base.mvp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cgbsoft.lib.Appli;
import com.cgbsoft.lib.base.mvp.presenter.impl.BasePresenterImpl;
import com.cgbsoft.lib.utils.cache.OtherDataProvider;
import com.cgbsoft.lib.utils.constant.Constant;
import com.cgbsoft.lib.utils.db.dao.DaoSession;
import com.cgbsoft.lib.utils.tools.DataStatisticsUtils;
import com.cgbsoft.lib.widget.WeakHandler;
import com.trello.rxlifecycle.components.RxFragment;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * desc  懒加载的fragment
 * author wangyongkui  wangyongkui@simuyun.com
 * 日期 17/4/7-10:41
 */
public abstract class BaseLazyFragment<P extends BasePresenterImpl> extends RxFragment implements Constant {
    protected View FBaseView;
    protected Activity FBaseActivity;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;


    //获取参数
    protected abstract void create(Bundle Mybundle);
    //填充fragment

    protected abstract int getContentViewLayoutID();

    //初始化
    protected abstract void initViewsAndEvents(View view);

    //第一次可见
    protected abstract void onFirstUserVisible();

    //fragment可见
    protected abstract void onUserVisible();
    //fragment不可见

    protected abstract void onUserInvisible();

    //fragment销毁
    protected abstract void DetoryViewAndThing();

    /**
     * 获取P
     * @return
     */
    protected abstract P createPresenter();


    private Appli mAppli;
    private WeakHandler mBaseHandler;//handler
    private View mFragmentView;
    private DaoSession mDaoSession;//数据库
    private Unbinder mUnbinder;//用于butterKnife解绑
    private P mPresenter;//功能调用


    private void onFirstUserInvisible() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FBaseActivity= getActivity();
        create(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentViewLayoutID() != 0) {
            FBaseView=inflater.inflate(getContentViewLayoutID(), null);

            return FBaseView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewsAndEvents(view);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            mAppli = (Appli) getActivity().getApplication();
            mBaseHandler = new WeakHandler();
            mPresenter = createPresenter();
            mUnbinder = ButterKnife.bind(this, FBaseView);
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void onDestroy() {
        DetoryViewAndThing();

        super.onDestroy();

    }
    @Override
    public void onResume() {
        super.onResume();
        OtherDataProvider.addTopActivity(FBaseActivity .getApplicationContext(), getClass().getName());
    }
    /**
     * 获取presenter
     *
     * @return
     */
    protected P getPresenter() {
        return mPresenter;
    }

    /**
     * 获取handler
     *
     * @return
     */
    protected WeakHandler getHandler() {
        return mBaseHandler;
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
        DataStatisticsUtils.push(FBaseActivity.getApplicationContext(), data);
    }

    protected void toDataStatistics(int grp, int act, String[] args){
        HashMap<String, String> data = new HashMap<>();
        data.put("grp", String.valueOf(grp));
        data.put("act", String.valueOf(act));
        for (int i = 1; i <= args.length; i++) {
            data.put("arg" + i, args[i - 1]);
        }
        DataStatisticsUtils.push(FBaseActivity.getApplicationContext(), data);
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
        Intent intent = new Intent(FBaseActivity, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }
}
