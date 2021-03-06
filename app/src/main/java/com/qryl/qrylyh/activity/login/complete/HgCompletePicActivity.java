package com.qryl.qrylyh.activity.login.complete;

import android.app.ProgressDialog;
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
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.activity.MainActivity;
import com.qryl.qrylyh.activity.login.LoginActivity;
import com.qryl.qrylyh.util.ConstantValue;

import org.json.JSONException;
import org.json.JSONObject;

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


public class HgCompletePicActivity extends BaseActivity implements View.OnClickListener {

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

    private ProgressDialog dialog;

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

        //dataMap.put("head", head.toString());
        dataMap.put("name", name);
        dataMap.put("indentity", indentity);
        dataMap.put("gender", gender + "");
        dataMap.put("age", age);
        dataMap.put("workexperience", workexperience);
        dataMap.put("begoodat", begoodat);
        dataMap.put("localservice", localservice);

    }


    private void initView() {
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
                nextIfNotNull();
            }
        });
    }

    /**
     * 点击注册除非全部不为空
     */
    private void nextIfNotNull() {

        if (TextUtils.isEmpty(etMe.getText().toString())) {
            Toast.makeText(this, "请填写简介", Toast.LENGTH_SHORT).show();
        } else {
            String introduce = etMe.getText().toString();
            dataMap.put("introduce", introduce);
            if (sfzFile == null) {
                Toast.makeText(this, "请上传身份证", Toast.LENGTH_SHORT).show();
            } else {
                if (jkzFile == null) {
                    Toast.makeText(this, "请上传健康证", Toast.LENGTH_SHORT).show();
                } else {
                    if (zgzFile == null) {
                        Toast.makeText(this, "请上传从业资格证", Toast.LENGTH_SHORT).show();
                    } else {
                        //发送请求
                        for (String key : dataMap.keySet()) {
                            Log.i(TAG, "nextIfNotNull: 上传前获取到的map集合  key:" + key + ", value为:" + dataMap.get(key));
                        }
                        //向服务器发送注册信息
                        postData();
                    }
                }
            }
        }
    }

    /**
     * 向服务器发送请求
     */
    private void postData() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在上传，请稍后...");
        dialog.setCancelable(false);
        dialog.show();
        SharedPreferences pref = getSharedPreferences("image", Context.MODE_PRIVATE);
        String headImage = pref.getString(HEAD_KEY, null);
        String sfzImage = pref.getString(SFZ_KEY, null);
        String jkzImage = pref.getString(JKZ_KEY, null);
        String zgzName = pref.getString(ZGZ_KEY, null);
        //Log.i(TAG, "postData: 头像图片名字" + imageName);
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File headFile = new File(storageDir, headImage);
        File sfzFile = new File(storageDir, sfzImage);
        File jkzFile = new File(storageDir, jkzImage);
        File zgzFile = new File(storageDir, zgzName);
        if (headFile != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), headFile);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            builder.addFormDataPart("txImg", headFile.getName(), body);
        }
        if (sfzFile != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), sfzFile);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            builder.addFormDataPart("sfzImg", sfzFile.getName(), body);
        }
        if (jkzFile != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), jkzFile);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            builder.addFormDataPart("jkzImg", jkzFile.getName(), body);
        }
        if (zgzFile != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), zgzFile);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            builder.addFormDataPart("zgzImg", zgzFile.getName(), body);
        }
        builder.addFormDataPart("loginId", userId);
        builder.addFormDataPart("realName", (String) dataMap.get("name"));
        builder.addFormDataPart("gender", (String) dataMap.get("gender"));
        builder.addFormDataPart("age", (String) dataMap.get("age"));
        builder.addFormDataPart("idNum", (String) dataMap.get("indentity"));
        builder.addFormDataPart("workYears", (String) dataMap.get("workexperience"));
        builder.addFormDataPart("introduce", (String) dataMap.get("introduce"));
        builder.addFormDataPart("professionIds", (String) dataMap.get("begoodat"));
        builder.addFormDataPart("serviceAreaIds", (String) dataMap.get("localservice"));
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().url(ConstantValue.URL + "/carer/addCarer").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    final int roleType = data.getInt("roleType");
                    final String token = data.getString("token");
                    final int loginId = data.getInt("loginId");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                                SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
                                prefs.edit().putInt("role_type", roleType).apply();
                                prefs.edit().putString("token", token).apply();
                                prefs.edit().putString("user_id", String.valueOf(loginId)).apply();
                                prefs.edit().putBoolean("is_force_offline", false).apply();
                                Intent intent = new Intent();
                                intent.setClass(HgCompletePicActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                if (ContextCompat.checkSelfPermission(HgCompletePicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HgCompletePicActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        if (ContextCompat.checkSelfPermission(HgCompletePicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HgCompletePicActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                    Toast.makeText(HgCompletePicActivity.this, "you denied the permission", Toast.LENGTH_SHORT).show();
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
            imageUri = FileProvider.getUriForFile(HgCompletePicActivity.this, "com.qryl.qrylhl.fileprovider", outputImage);
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
        if (DocumentsContract.isDocumentUri(HgCompletePicActivity.this, uri)) {
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
            Toast.makeText(HgCompletePicActivity.this, "failed to get image ", Toast.LENGTH_SHORT).show();
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

}
