package com.qryl.qrylyh.activity.login.complete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.ConvertPic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompletePicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CompletePicActivity";

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private int choosed_image = 0;
    /**
     * 身份证
     */
    private ImageView sfzImage;
    /**
     * 健康证
     */
    private ImageView jkzImage;
    /**
     * 资格证
     */
    private ImageView zgzImage;
    private Uri imageUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_pic);
        initView();
    }

    private void initView() {
        sfzImage = (ImageView) findViewById(R.id.sfz_image);
        jkzImage = (ImageView) findViewById(R.id.jkz_image);
        zgzImage = (ImageView) findViewById(R.id.zgz_image);
        sfzImage.setOnClickListener(this);
        jkzImage.setOnClickListener(this);
        zgzImage.setOnClickListener(this);
    }

    /**
     * 点击弹出popupWindow上传照片按钮
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sfz_image:
                showPopupWindow();
                choosed_image = R.id.sfz_image;
                break;
            case R.id.jkz_image:
                showPopupWindow();
                choosed_image = R.id.jkz_image;
                break;
            case R.id.zgz_image:
                showPopupWindow();
                choosed_image = R.id.zgz_image;
                break;
            default:
                break;
        }
    }

    /**
     * 弹出popupWindow的逻辑
     */
    private void showPopupWindow() {
        View popView = View.inflate(this, R.layout.popup_choose_pic, null);
        Button btnPopAlbum = (Button) popView.findViewById(R.id.btn_pop_album);
        Button btnPopCamera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button btnPopCancel = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels * 1 / 3;
        final PopupWindow popupWindow = new PopupWindow(popView, widthPixels, heightPixels);
        popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击popup外部消失
        popupWindow.setOutsideTouchable(true);
        //消失时屏幕变为半透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });
        //出现时屏幕变为透明
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);
        btnPopAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                openAlbum();
            }
        });
        btnPopCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                openCarema();
            }
        });
        btnPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void openAlbum() {
    }

    /**
     * 打开相机
     */
    private void openCarema() {
        //创建File对象，用于存储拍照后的照片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(CompletePicActivity.this, "com.qryl.qrylyh.activity.login.complete.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的图片显示出来
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        long l = SystemClock.currentThreadTimeMillis();
                        Log.i(TAG, "onActivityResult: " + l);
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);//这里压缩options%，把压缩后的数据存放到baos中
                        //long length = baos.toByteArray().length;
                        byte[] bytes = baos.toByteArray();
                        Log.i("wechat", "压缩后图片的大小" + ("字节码：" + " 宽度为:" + bitmap.getWidth() + " 高度为:" + bitmap.getHeight()));
                        if (choosed_image == R.id.sfz_image) {//身份证
                            //sfzImage.setImageBitmap(bitmap);
                            l = SystemClock.currentThreadTimeMillis();
                            Log.i(TAG, "onActivityResult: " + l);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(sfzImage);
                        } else if (choosed_image == R.id.jkz_image) {//健康证
                            //jkzImage.setImageBitmap(bitmap);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(jkzImage);
                        } else if (choosed_image == R.id.zgz_image) {//从业资格证
                            //zgzImage.setImageBitmap(bitmap);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(zgzImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

}
