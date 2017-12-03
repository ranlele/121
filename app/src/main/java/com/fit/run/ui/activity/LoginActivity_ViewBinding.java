// Generated code from Butter Knife. Do not modify!
package com.fit.run.ui.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.fit.run.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  private View view2131165214;

  private View view2131165217;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(final LoginActivity target, View source) {
    this.target = target;

    View view;
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    target.mEdtAccount = Utils.findRequiredViewAsType(source, R.id.edt_account, "field 'mEdtAccount'", EditText.class);
    target.mEdtPassword = Utils.findRequiredViewAsType(source, R.id.edt_password, "field 'mEdtPassword'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btn_login, "method 'onViewClicked'");
    view2131165214 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_to_register, "method 'onViewClicked'");
    view2131165217 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolbar = null;
    target.mEdtAccount = null;
    target.mEdtPassword = null;

    view2131165214.setOnClickListener(null);
    view2131165214 = null;
    view2131165217.setOnClickListener(null);
    view2131165217 = null;
  }
}
