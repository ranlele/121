// Generated code from Butter Knife. Do not modify!
package com.fit.run.ui.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.fit.run.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RankFragment_ViewBinding implements Unbinder {
  private RankFragment target;

  @UiThread
  public RankFragment_ViewBinding(RankFragment target, View source) {
    this.target = target;

    target.mRvFriend = Utils.findRequiredViewAsType(source, R.id.rv_friend, "field 'mRvFriend'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RankFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRvFriend = null;
  }
}
