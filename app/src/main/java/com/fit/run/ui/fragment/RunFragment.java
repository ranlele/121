package com.fit.run.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fit.run.R;
import com.fit.run.event.RefreshStepEvent;
import com.fit.run.ui.base.BaseFragment;
import com.fit.run.widget.StepArcView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class RunFragment extends BaseFragment {
    @BindView(R.id.sav_step)
    StepArcView mSavStep;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_run, null, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onStart() {
        super.onStart();
        registerEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEvent();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshStep(RefreshStepEvent event) {
        mSavStep.setCurrentCount(Config.TOTAL_STEP, event.getStep());
    }
}
