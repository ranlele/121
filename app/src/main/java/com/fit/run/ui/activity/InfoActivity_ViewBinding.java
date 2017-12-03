// Generated code from Butter Knife. Do not modify!
package com.fit.run.ui.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.fit.run.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class InfoActivity_ViewBinding implements Unbinder {
  private InfoActivity target;

  private View view2131165212;

  private View view2131165213;

  @UiThread
  public InfoActivity_ViewBinding(InfoActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public InfoActivity_ViewBinding(final InfoActivity target, View source) {
    this.target = target;

    View view;
    target.mTvAccount = Utils.findRequiredViewAsType(source, R.id.tv_account, "field 'mTvAccount'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btn_add_friend, "field 'mBtnAddFriend' and method 'onViewClicked'");
    target.mBtnAddFriend = Utils.castView(view, R.id.btn_add_friend, "field 'mBtnAddFriend'", Button.class);
    view2131165212 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.btn_challenge, "field 'mBtnChallenge' and method 'onViewClicked'");
    target.mBtnChallenge = Utils.castView(view, R.id.btn_challenge, "field 'mBtnChallenge'", Button.class);
    view2131165213 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mTvStep = Utils.findRequiredViewAsType(source, R.id.tv_step, "field 'mTvStep'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    InfoActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvAccount = null;
    target.mBtnAddFriend = null;
    target.mToolbar = null;
    target.mBtnChallenge = null;
    target.mTvStep = null;

    view2131165212.setOnClickListener(null);
    view2131165212 = null;
    view2131165213.setOnClickListener(null);
    view2131165213 = null;
  }
}
