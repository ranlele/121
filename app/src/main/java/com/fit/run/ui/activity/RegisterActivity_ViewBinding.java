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

public class RegisterActivity_ViewBinding implements Unbinder {
  private RegisterActivity target;

  private View view2131165215;

  private View view2131165216;

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RegisterActivity_ViewBinding(final RegisterActivity target, View source) {
    this.target = target;

    View view;
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    target.mEdtAccount = Utils.findRequiredViewAsType(source, R.id.edt_account, "field 'mEdtAccount'", EditText.class);
    target.mEdtPassword = Utils.findRequiredViewAsType(source, R.id.edt_password, "field 'mEdtPassword'", EditText.class);
    target.mEdtRepeat = Utils.findRequiredViewAsType(source, R.id.edt_repeat, "field 'mEdtRepeat'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btn_register, "method 'onViewClicked'");
    view2131165215 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_to_login, "method 'onViewClicked'");
    view2131165216 = view;
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
    RegisterActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolbar = null;
    target.mEdtAccount = null;
    target.mEdtPassword = null;
    target.mEdtRepeat = null;

    view2131165215.setOnClickListener(null);
    view2131165215 = null;
    view2131165216.setOnClickListener(null);
    view2131165216 = null;
  }
}
