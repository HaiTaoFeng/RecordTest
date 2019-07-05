package com.record.recordtest;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.record.recordtest.controller.CameraController;
import com.record.recordtest.manager.PermissionsManager;
import com.record.recordtest.view.AutoFitTextureView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int START_RECORD = 100;
    private final static int CIRCULAR_RECORDING = 101;
    private AutoFitTextureView mTextureview;
    private ImageView mIv_Setting;//设置
    private ImageView mTakePictureBtn;//拍照
    private ImageView mVideoRecodeBtn;//开始录像
    private LinearLayout mVerticalLinear;
    private ImageView mIv_Setting2;//设置
    private ImageView mTakePictureBtn2;//拍照 横,竖屏状态分别设置了一个拍照,录像的按钮
    private ImageView mVideoRecodeBtn2;//开始录像
    private LinearLayout mHorizontalLinear;
    private Button mVHScreenBtn;
    private CameraController mCameraController;
    private boolean mIsRecordingVideo; //开始停止录像
    public static String BASE_PATH = Environment.getExternalStorageDirectory() + "/AAA";
    private PermissionsManager permissionsManager;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case START_RECORD:
                    VedioRecord();
                    break;
                case CIRCULAR_RECORDING:
                    VedioRecord();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            VedioRecord();
                        }
                    },5);
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main);
        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取相机管理类的实例
        mCameraController = CameraController.getmInstance(this);
        mCameraController.setFolderPath(BASE_PATH);
        initView();
        //判断当前横竖屏状态
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVHScreenBtn.setText("竖屏");
        } else {
            mVHScreenBtn.setText("横屏");
        }
    }

    private void initView() {
        mTextureview = (AutoFitTextureView) findViewById(R.id.textureview);
        mIv_Setting = (ImageView) findViewById(R.id.iv_setting);
        mIv_Setting.setOnClickListener(this);
        mTakePictureBtn = (ImageView) findViewById(R.id.take_picture_btn);
        mTakePictureBtn.setOnClickListener(this);
        mVideoRecodeBtn = (ImageView) findViewById(R.id.video_recode_btn);
        mVideoRecodeBtn.setOnClickListener(this);
        mVerticalLinear = (LinearLayout) findViewById(R.id.vertical_linear);
        mIv_Setting2 = (ImageView) findViewById(R.id.iv_setting2);
        mIv_Setting2.setOnClickListener(this);
        mTakePictureBtn2 = (ImageView) findViewById(R.id.take_picture_btn2);
        mTakePictureBtn2.setOnClickListener(this);
        mVideoRecodeBtn2 = (ImageView) findViewById(R.id.video_recode_btn2);
        mVideoRecodeBtn2.setOnClickListener(this);
        mHorizontalLinear = (LinearLayout) findViewById(R.id.horizontal_linear);
        mVHScreenBtn = (Button) findViewById(R.id.v_h_screen_btn);
        mVHScreenBtn.setOnClickListener(this);
        //判断当前屏幕方向
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //竖屏时
            mHorizontalLinear.setVisibility(View.VISIBLE);
            mVerticalLinear.setVisibility(View.GONE);
        } else {
            //横屏
            mVerticalLinear.setVisibility(View.VISIBLE);
            mHorizontalLinear.setVisibility(View.GONE);
        }
        mCameraController.InitCamera(mTextureview);
    }

    private void getPermission(){
        permissionsManager.requestCameraPermission(MainActivity.this);
        permissionsManager.requestVideoPermission(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.take_picture_btn:
                takePicture();
                break;
            case R.id.video_recode_btn:
                VedioRecord();
                break;
            case R.id.take_picture_btn2:
                takePicture();
                break;
            case R.id.video_recode_btn2:
                if (!mIsRecordingVideo) {
                    new Thread(new MyThread()).start();//开启定时器循环录制
                }else{
                    new Thread(new MyThread()).interrupt();
                }
                VedioRecord();
                break;
            case R.id.v_h_screen_btn:
                //判断当前屏幕方向
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Toast.makeText(MainActivity.this, "竖屏了", Toast.LENGTH_SHORT).show();
                } else {
                    //切换横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    Toast.makeText(MainActivity.this, "横屏了", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void takePicture(){
        if (mIsRecordingVideo) {
            VedioRecord();
            mCameraController.takePicture(mHandler,true);
        }else{
            mCameraController.takePicture(mHandler,false);
        }
    }

    private void VedioRecord(){
        if (permissionsManager.checkVideoPermission(MainActivity.this)) {
            if (mIsRecordingVideo) {
                mIsRecordingVideo = !mIsRecordingVideo;
                mCameraController.stopRecordingVideo();
                mVideoRecodeBtn.setImageResource(R.mipmap.ic_record);
                mVideoRecodeBtn2.setImageResource(R.mipmap.ic_record);
                Toast.makeText(this, "录像结束", Toast.LENGTH_SHORT).show();
            } else {
                mVideoRecodeBtn.setImageResource(R.mipmap.ic_pause);
                mVideoRecodeBtn2.setImageResource(R.mipmap.ic_pause);
                mIsRecordingVideo = !mIsRecordingVideo;
                mCameraController.startRecordingVideo();
                Toast.makeText(this, "录像开始", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "权限不足，请先开启权限！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsManager.CAMERA_REQUEST_CODE:
                //权限请求失败
                if (grantResults.length == PermissionsManager.CAMERA_REQUEST.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "拍照权限被拒绝",Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
                break;
            case PermissionsManager.VIDEO_REQUEST_CODE:
                if (grantResults.length == PermissionsManager.VIDEO_PERMISSIONS.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "录像权限被拒绝",Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        startService(new Intent(this,CameraService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        startService(new Intent(this,CameraService.class));
    }

    class MyThread extends Thread {//这里也可用Runnable接口实现
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(20 * 1000);//每隔1s执行一次
                    Message msg = new Message();
                    msg.what = CIRCULAR_RECORDING;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

