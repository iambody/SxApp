package com.cgbsoft.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.cgbsoft.lib.R;


public class BaseDialog extends Dialog {
	public interface OnCustomDialogListener {
		public void back(String name);
	}

	public BaseDialog(Context context, boolean cancelable,
					  OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public BaseDialog(Context context, int theme) {
		super(context,theme);
	}

	public BaseDialog(Context context) {
		this(context, R.style.base_dialog_style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			//透明导航栏
			if (getWindow() != null)
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

}