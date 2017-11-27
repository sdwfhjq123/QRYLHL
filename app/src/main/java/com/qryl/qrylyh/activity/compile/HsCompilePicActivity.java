package com.qryl.qrylyh.activity.compile;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.util.ConstantValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HsCompilePicActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HgCompilePicActivity";

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private int choosed_image = 0;

    private Map<String, Object> dataMap = new HashMap<>();

    private static final String SFZ_KEY = "syz_key";
    private static final String JKZ_KEY = "jkz_key";
    private static final String ZGZ_KEY = "zgz_key";
    private static final String HEAD_KEY = "head_key";

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
    private EditText etMe;
    private File sfzFile;
    private File jkzFile;
    private File zgzFile;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_pic);
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        initView();

        Bundle bundle = getIntent().getExtras();
        String name = (String) bundle.get("name");
        String indentity = (String) bundle.get("identity");
        int gender = (int) bundle.get("gender");
        String age = (String) bundle.get("age");
        String workexperience = (String) bundle.get("workexperience");
        String begoodat = (String) bundle.get("begoodat");
        String localservice = (String) bundle.get("localservice");
        int hospital = bundle.getInt("hospital");
        int office = bundle.getInt("office");

        String introduce = bundle.getString("introduce", "");
        etMe.setText(introduce);
        dataMap.put("name", name);
        dataMap.put("indentity", indentity);
        dataMap.put("gender", gender + "");
        dataMap.put("age", age);
        dataMap.put("workexperience", workexperience);
        dataMap.put("begoodat", begoodat);
        dataMap.put("localservice", localservice);
        dataMap.put("hospital", hospital);
        dataMap.put("office", office);

    }

    private void initView() {
        changeTitle();
        sfzImage = (ImageView) findViewById(R.id.sfz_image);
        jkzImage = (ImageView) findViewById(R.id.jkz_image);
        zgzImage = (ImageView) findViewById(R.id.zgz_image);
        etMe = (EditText) findViewById(R.id.et_me);
        Button btnRegister = (Button) findViewById(R.id.btn_register);
        sfzImage.setOnClickListener(this);
        jkzImage.setOnClickListener(this);
        zgzImage.setOnClickListener(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData();
            }
        });
    }

    /**
     * 向服务器发送请求
     */
    private void postData() {
        dataMap.put("introduce", etMe.getText().toString());
        SharedPreferences pref = getSharedPreferences("image", Context.MODE_PRIVATE);
        String headImage = pref.getString(HEAD_KEY, null);
        String sfzImage = pref.getString(SFZ_KEY, null);
        String jkzImage = pref.getString(JKZ_KEY, null);
        String zgzName = pref.getString(ZGZ_KEY, null);
        //Log.i(TAG, "postData: 头像图片名字" + imageName);
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!headImage.equals("") && !sfzImage.equals("") && !jkzImage.equals("") && !zgzName.equals("")) {
            File headFile = new File(storageDir, headImage);
            File sfzFile = new File(storageDir, sfzImage);
            File jkzFile = new File(storageDir, jkzImage);
            File zgzFile = new File(storageDir, zgzName);
            if (headFile != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), headFile);
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                builder.addFormDataPart("txImg", headFile.getName(), body);
            } else {
                builder.addFormDataPart("txImg", "");
            }
            if (sfzFile != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), sfzFile);
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                builder.addFormDataPart("sfzImg", sfzFile.getName(), body);
            } else {
                builder.addFormDataPart("sfzImg", "");
            }
            if (jkzFile != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), jkzFile);
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                builder.addFormDataPart("jkzImg", jkzFile.getName(), body);
            } else {
                builder.addFormDataPart("jkzImg", "");
            }
            if (zgzFile != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), zgzFile);
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                builder.addFormDataPart("zgzImg", zgzFile.getName(), body);
            } else {
                builder.addFormDataPart("zgzImg", "");
            }
        }
        builder.addFormDataPart("loginId", userId);
        builder.addFormDataPart("roleType", "2");
        builder.addFormDataPart("realName", (String) dataMap.get("name"));
        builder.addFormDataPart("gender", (String) dataMap.get("gender"));
        builder.addFormDataPart("age", (String) dataMap.get("age"));
        builder.addFormDataPart("workYears", (String) dataMap.get("workexperience"));
        builder.addFormDataPart("introduce", (String) dataMap.get("introduce"));
        builder.addFormDataPart("idNum", (String) dataMap.get("indentity"));
        builder.addFormDataPart("hospitalId", String.valueOf(dataMap.get("hospital")));
        builder.addFormDataPart("departmentId", String.valueOf(dataMap.get("office")));
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().url(ConstantValue.URL + "/dn/modify").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: 成功 " + response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
    }

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
        int heightPixels = getResources().getDisplayMetrics().heightPixels / 3;
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
                if (ContextCompat.checkSelfPermission(HsCompilePicActivity.this, android.Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HsCompilePicActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    //打开相机
                    openCarema();
                }
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
        if (ContextCompat.checkSelfPermission(HsCompilePicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HsCompilePicActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    Toast.makeText(HsCompilePicActivity.this, "you denied the permission", Toast.LENGTH_SHORT).show();
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
            imageUri = FileProvider.getUriForFile(HsCompilePicActivity.this, "com.qryl.qrylhl.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        if (choosed_image == R.id.sfz_image) {//身份证
                            //保存file到sp
                            sfzFile = saveMyBitmap(bitmap, "sfz");
                            saveFile(SFZ_KEY, sfzFile.getName());
                            Glide.with(this).asBitmap().load(sfzFile).thumbnail(0.1f).into(sfzImage);
                        } else if (choosed_image == R.id.jkz_image) {//健康证
                            jkzFile = saveMyBitmap(bitmap, "jkz");
                            //保存file到sp
                            saveFile(JKZ_KEY, jkzFile.getName());
                            Glide.with(this).asBitmap().load(jkzFile).thumbnail(0.1f).into(jkzImage);
                        } else if (choosed_image == R.id.zgz_image) {//从业资格证
                            zgzFile = saveMyBitmap(bitmap, "zgz");
                            //保存file到sp
                            saveFile(ZGZ_KEY, zgzFile.getName());
                            Glide.with(this).asBitmap().load(zgzFile).thumbnail(0.1f).into(zgzImage);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        //Log.i("uri", uri + "");
        if (DocumentsContract.isDocumentUri(HsCompilePicActivity.this, uri)) {
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
            bitmap = BitmapFactory.decodeFile(imagePath);
            if (choosed_image == R.id.sfz_image) {//身份证
                //保存file到sp
                sfzFile = saveMyBitmap(bitmap, "sfz");
                saveFile(SFZ_KEY, sfzFile.getName());
                Glide.with(this).asBitmap().load(sfzFile).thumbnail(0.1f).into(sfzImage);
            } else if (choosed_image == R.id.jkz_image) {//健康证
                jkzFile = saveMyBitmap(bitmap, "jkz");
                //保存file到sp
                saveFile(JKZ_KEY, jkzFile.getName());
                Glide.with(this).asBitmap().load(jkzFile).thumbnail(0.1f).into(jkzImage);
            } else if (choosed_image == R.id.zgz_image) {//从业资格证
                zgzFile = saveMyBitmap(bitmap, "zgz");
                //保存file到sp
                saveFile(ZGZ_KEY, zgzFile.getName());
                Glide.with(this).asBitmap().load(zgzFile).thumbnail(0.1f).into(zgzImage);
            }
        } else {
            Toast.makeText(HsCompilePicActivity.this, "failed to get image ", Toast.LENGTH_SHORT).show();
        }
    }


    //将bitmap转化为png格式
    public File saveMyBitmap(Bitmap mBitmap, String prefix) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        try {
            file = File.createTempFile(
                    prefix,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 保存file到sp
     *
     * @param fileName
     */
    private void saveFile(String spKey, String fileName) {
        SharedPreferences sp = getSharedPreferences("image", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(spKey, fileName);
        //提交edit
        edit.apply();
        Log.i(TAG, "saveFile: 保存成功" + sp.getString(spKey, null));
    }

    private void changeTitle() {
        TextView tvReturn = (TextView) findViewById(R.id.return_text);
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("编辑资料");
        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
