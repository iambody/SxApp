package com.cgbsoft.lib.base.mvp.model;

import android.text.TextUtils;

/**
 * Created by user on 2016/11/4.
 */

public class BaseResult<T> {
    public String message;//可以为""不能为null,message和code必须有一个
    public String code;//必须有值不能为空

    public T result;//可以没有，不能为null

    public boolean isOk() {
        return TextUtils.equals("200", code);
    }
}
