package com.fit.run.pedometer;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.fit.run.pedometer.callback.StepCallBack;


/**
 * 计步模式分为 加速度传感器 google内置计步器
 * <p/>
 */
public abstract class StepMode implements SensorEventListener {
    private Context context;
    public StepCallBack stepCallBack;
    public SensorManager sensorManager;
    public static int CURRENT_SETP = 0;
    public boolean isAvailable = false;

    public StepMode(Context context, StepCallBack stepCallBack) {
        this.context = context;
        this.stepCallBack = stepCallBack;
    }

    public boolean getStep() {
        prepareSensorManager();
        registerSensor();
        return isAvailable;
    }

    protected abstract void registerSensor();

    private void prepareSensorManager() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
//        getLock(this);
//        android4.4以后可以使用计步传感器
//        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
//        if (VERSION_CODES >= 19) {
//            addCountStepListener();
//        } else {
//            addBasePedoListener();
//        }

    }


}
