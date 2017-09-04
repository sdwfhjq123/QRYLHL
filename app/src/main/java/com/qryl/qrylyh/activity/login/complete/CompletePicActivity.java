package com.qryl.qrylyh.activity.login.complete;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qryl.qrylyh.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class CompletePicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CompletePicActivity";

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private int choosed_image = 0;

    private HashMap<String, ByteArrayOutputStream> imageMap = new HashMap<>();

    private static final String SFZ_KEY = "syz_key";
    private static final String JKZ_KEY = "jkz_key";
    private static final String ZGZ_KEY = "zgz_key";

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
                //调用相机
                invokeAlbum();
            }
        });
        btnPopCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //打开相机
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

    /**
     * 打开相册
     */
    private void invokeAlbum() {
        //动态申请危险时权限，运行时权限
        if (ContextCompat.checkSelfPermission(CompletePicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CompletePicActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 动态获取到的权限后的重写
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(CompletePicActivity.this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
                        //long l = SystemClock.currentThreadTimeMillis();
                        //Log.i(TAG, "onActivityResult: " + l);
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);//这里压缩options%，把压缩后的数据存放到baos中
                        //long length = baos.toByteArray().length;
                        byte[] bytes = baos.toByteArray();
                        Log.i("wechat", "压缩后图片的大小" + ("字节码：" + " 宽度为:" + bitmap.getWidth() + " 高度为:" + bitmap.getHeight()));
                        if (choosed_image == R.id.sfz_image) {//身份证
                            //sfzImage.setImageBitmap(bitmap);
                            //l = SystemClock.currentThreadTimeMillis();
                            // Log.i(TAG, "onActivityResult: " + l);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(sfzImage);
                            imageMap.put(SFZ_KEY, baos);
                        } else if (choosed_image == R.id.jkz_image) {//健康证
                            //jkzImage.setImageBitmap(bitmap);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(jkzImage);
                            imageMap.put(JKZ_KEY, baos);
                        } else if (choosed_image == R.id.zgz_image) {//从业资格证
                            //zgzImage.setImageBitmap(bitmap);
                            Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(zgzImage);
                            imageMap.put(ZGZ_KEY, baos);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        //Log.i("uri", uri + "");
        if (DocumentsContract.isDocumentUri(CompletePicActivity.this, uri)) {
            //如果是document类型的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            Log.i("type of document", docId);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                Log.i("type of document id", id);
                String selection = MediaStore.Images.Media._ID + "=" + id;
                Log.i("selection", selection);
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，就用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);//这里压缩options%，把压缩后的数据存放到baos中
            byte[] bytes = baos.toByteArray();
            //Log.i("wechat", "压缩后图片的大小" + ("字节码：" + " 宽度为:" + bitmap.getWidth() + " 高度为:" + bitmap.getHeight()));
            if (choosed_image == R.id.sfz_image) {//身份证
                //sfzImage.setImageBitmap(bitmap);
                //l = SystemClock.currentThreadTimeMillis();
                //Log.i(TAG, "onActivityResult: " + l);
                Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(sfzImage);
                imageMap.put(SFZ_KEY, baos);
            } else if (choosed_image == R.id.jkz_image) {//健康证
                //jkzImage.setImageBitmap(bitmap);
                Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(jkzImage);
                imageMap.put(JKZ_KEY, baos);
            } else if (choosed_image == R.id.zgz_image) {//从业资格证
                //zgzImage.setImageBitmap(bitmap);
                Glide.with(this).asBitmap().load(bytes).thumbnail(0.1f).into(zgzImage);
                imageMap.put(ZGZ_KEY, baos);
            }
            //picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(CompletePicActivity.this, "failed to get image ", Toast.LENGTH_SHORT).show();
        }
    }

}
