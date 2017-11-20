package com.qryl.qrylyh.activity.compile;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qryl.qrylyh.R;
import com.qryl.qrylyh.activity.BaseActivity;
import com.qryl.qrylyh.activity.login.complete.BeGoodAtWorkActivity;
import com.qryl.qrylyh.activity.login.complete.HospitalActivity;
import com.qryl.qrylyh.activity.login.complete.LocationActivity;
import com.qryl.qrylyh.activity.login.complete.OfficeActivity;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.DialogUtil;
import com.qryl.qrylyh.util.HttpUtil;
import com.qryl.qrylyh.view.MyAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HsCompileInfoActivity extends BaseActivity {

    private static final String TAG = "HsCompileInfoActivity";
    private TextView tvName, tvIdentity, tvGender, tvAge, tvWorkExperience, tvBeGoodAtWork;

    private RelativeLayout myHead, realName, identity, gender, age, workExperience, beGoodAtWork;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int CHOOSE_HOSPITAL = 3;
    private static final int CHOOSE_LOCATION = 4;
    private static final int CHOOSE_WORK = 5;
    private static final int CHOOSE_OFFICE = 6;

    private static final String HEAD_KEY = "head_key";

    private Uri imageUri;
    private Bitmap bitmap;
    private CircleImageView civHead;
    private String[] genderArray;
    private String[] workExperienceArray;
    private String nameDialogText;
    private String identityDialogText;
    private String genderDialogText;
    private String ageDialogText;
    private String workExperienceDialogText;
    private File headFile;
    private int genderNum;
    private RelativeLayout hospital;
    private TextView tvHospital;
    private RelativeLayout location;
    private TextView tvLocation;
    private String locationId;
    private RelativeLayout office;
    private TextView tvOffice;
    private int hospitalId;
    private int officeId;
    private String userId;
    private String introduce;
    private String idImg;
    private String healthCertificateImg;
    private String qualificationCertificateImg;
    private String headshotImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info_max);
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        SharedPreferences sp = getSharedPreferences("image", Context.MODE_PRIVATE);
        sp.edit().putString(HEAD_KEY, "").apply();
        genderArray = getResources().getStringArray(R.array.gender);
        workExperienceArray = getResources().getStringArray(R.array.work_experience);
        initView();
        initData();
        //点击每个条目实现dialog或者activity
        clickItemShowDialog();
    }

    private void initData() {
        postData();
    }

    /**
     * 获取之前编辑的数据
     */
    private void postData() {
        HttpUtil.sendOkHttpRequestInt(ConstantValue.URL + "/dn/getMyDetail", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                handleJson(result);
            }
        }, "loginId", userId);
    }


    private void handleJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String resultCode = jsonObject.getString("resultCode");
            if (resultCode.equals("200")) {
                JSONObject data = jsonObject.getJSONObject("data");
                headshotImg = data.getString("headshotImg");
                final String realName = data.getString("realName");
                final String idNum = data.getString("idNum");
                final int gender = data.getInt("gender");//0男
                final String age = data.getString("age");
                final int workYears = data.getInt("workYears");
                final String professionNames = data.getString("professionNames");
                introduce = data.getString("introduce");
                idImg = data.getString("idImg");
                healthCertificateImg = data.getString("healthCertificateImg");
                qualificationCertificateImg = data.getString("qualificationCertificateImg");
                final String hospitalName = data.getString("hospitalName");
                final String departmentName = data.getString("departmentName");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示
                        Glide.with(HsCompileInfoActivity.this).load(ConstantValue.URL + headshotImg).thumbnail(0.1f).into(civHead);
                        tvName.setText(realName);
                        tvIdentity.setText(idNum);
                        tvGender.setText(gender == 0 ? "男" : "女");
                        tvAge.setText(age);
                        tvWorkExperience.setText(String.valueOf(workYears) );
                        tvHospital.setText(String.valueOf(hospitalName));
                        tvOffice.setText(String.valueOf(departmentName));
                        tvBeGoodAtWork.setText(professionNames);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击每个条目实现dialog或者activity
     */
    private void clickItemShowDialog() {

        //点击更换头像
        myHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        //点击修改姓名
        realName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(HsCompileInfoActivity.this, R.layout.text_item_dialog_text, null);
                TextView tvTitileDialog = (TextView) view.findViewById(R.id.tv_title_dialog);
                final EditText etHintDialog = (EditText) view.findViewById(R.id.et_hint_dialog);
                tvTitileDialog.setText("姓名");
                new MyAlertDialog(HsCompileInfoActivity.this, view)
                        //.setView(view)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nameDialogText = etHintDialog.getText().toString();
                                tvName.setText(nameDialogText);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //身份证
        identity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(HsCompileInfoActivity.this, R.layout.text_item_dialog_num, null);
                TextView tvTitileDialog = (TextView) view.findViewById(R.id.tv_title_dialog);
                final EditText etHintDialog = (EditText) view.findViewById(R.id.et_hint_dialog);
                tvTitileDialog.setText("身份证");
                new MyAlertDialog(HsCompileInfoActivity.this, view)
                        //.setView(view)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                identityDialogText = etHintDialog.getText().toString();
                                tvIdentity.setText(identityDialogText);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //选择性别
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtil.showMultiItemsDialog(HsCompileInfoActivity.this, "选择性别", R.array.gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvGender.setText(genderArray[which]);
                        genderDialogText = tvGender.getText().toString();
                        if (genderDialogText.equals("男")) {
                            genderNum = 1;
                        } else if (genderDialogText.equals("女")) {
                            genderNum = 0;
                        }
                        Log.i(TAG, "onClick: 设置的性别" + genderDialogText);
                        dialog.dismiss();
                    }
                });
            }
        });
        //选择年龄
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(HsCompileInfoActivity.this, R.layout.text_item_dialog_num, null);
                TextView tvTitileDialog = (TextView) view.findViewById(R.id.tv_title_dialog);
                final EditText etHintDialog = (EditText) view.findViewById(R.id.et_hint_dialog);
                tvTitileDialog.setText("请输入年龄");
                new MyAlertDialog(HsCompileInfoActivity.this, view)
                        //.setView(view)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ageDialogText = etHintDialog.getText().toString();
                                tvAge.setText(ageDialogText);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //选择工作经验
        workExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showMultiItemsDialog(HsCompileInfoActivity.this, "选择工作经验", R.array.work_experience, new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvWorkExperience.setText(String.valueOf(workExperienceArray[which]) + "年");
                        workExperienceDialogText = workExperienceArray[which];
                        dialog.dismiss();
                    }
                });
            }
        });
        //擅长的工作
        beGoodAtWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HsCompileInfoActivity.this, BeGoodAtWorkActivity.class);
                intent.putExtra("service_id", 2);
                startActivityForResult(intent, CHOOSE_WORK);
            }
        });

        //选择所在的医院
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HsCompileInfoActivity.this, HospitalActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, CHOOSE_HOSPITAL);
            }
        });
        //选择可服务的区域
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HsCompileInfoActivity.this, LocationActivity.class);
                startActivityForResult(intent, CHOOSE_LOCATION);
            }
        });
        //选择科室
        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HsCompileInfoActivity.this, OfficeActivity.class);
                startActivityForResult(intent, CHOOSE_OFFICE);
            }
        });
    }

    private void initView() {
        changeTitle();
        //点击事件区域
        myHead = (RelativeLayout) findViewById(R.id.my_head);
        realName = (RelativeLayout) findViewById(R.id.real_name);
        identity = (RelativeLayout) findViewById(R.id.identity);
        gender = (RelativeLayout) findViewById(R.id.gender);
        age = (RelativeLayout) findViewById(R.id.age);
        workExperience = (RelativeLayout) findViewById(R.id.work_experience);
        beGoodAtWork = (RelativeLayout) findViewById(R.id.be_good_at_work);
        civHead = (CircleImageView) findViewById(R.id.civ_head);
        hospital = (RelativeLayout) findViewById(R.id.hospital);
        location = (RelativeLayout) findViewById(R.id.location);
        office = (RelativeLayout) findViewById(R.id.office);
        //返回的数据
        tvName = (TextView) findViewById(R.id.tv_name);
        tvIdentity = (TextView) findViewById(R.id.tv_identity);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvWorkExperience = (TextView) findViewById(R.id.tv_work_experience);
        tvBeGoodAtWork = (TextView) findViewById(R.id.tv_be_good_at_work);
        tvHospital = (TextView) findViewById(R.id.tv_hospital);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvOffice = (TextView) findViewById(R.id.tv_office);
        Button btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putExtra();
            }
        });
    }

    /**
     * 传递数据到下个页面
     */
    private void putExtra() {
        Intent intent = new Intent(HsCompileInfoActivity.this, HsCompilePicActivity.class);
        //传递数据
        Bundle bundle = new Bundle();
        bundle.putString("name", tvName.getText().toString());
        bundle.putString("identity", tvIdentity.getText().toString());
        bundle.putInt("gender", genderNum);
        bundle.putString("age", tvAge.getText().toString());
        bundle.putString("workexperience", tvWorkExperience.getText().toString());
        bundle.putString("begoodat", tvBeGoodAtWork.getText().toString());
        bundle.putString("localservice", locationId);
        bundle.putInt("hospital", hospitalId);
        bundle.putInt("office", officeId);
        bundle.putString("introduce", introduce);
        bundle.putString("idImg", idImg);
        bundle.putString("qualificationCertificateImg", qualificationCertificateImg);
        bundle.putString("healthCertificateImg", healthCertificateImg);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

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
            imageUri = FileProvider.getUriForFile(HsCompileInfoActivity.this, "com.qryl.qrylyh.activity.login.complete.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void invokeAlbum() {
        //动态申请危险时权限，运行时权限
        if (ContextCompat.checkSelfPermission(HsCompileInfoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HsCompileInfoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

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
                    Toast.makeText(HsCompileInfoActivity.this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的图片显示出来
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        headFile = saveMyBitmap(bitmap, "head");
                        //保存file到sp
                        saveFile(headFile.getName());
                        Glide.with(this).asBitmap().load(headFile).thumbnail(0.1f).into(civHead);
                        Log.i("wechat", "压缩后图片的大小" + ("字节码：" + " 宽度为:" + bitmap.getWidth() + " 高度为:" + bitmap.getHeight()));
                        Log.i(TAG, "File:" + headFile.getName() + " 路径:" + headFile.getAbsolutePath());
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
            case CHOOSE_LOCATION:
                if (resultCode == RESULT_OK) {
                    locationId = data.getStringExtra("location_id");
                    String locationName = data.getStringExtra("location_name");
                    Log.i(TAG, "onActivityResult: id:" + locationId + ",name:" + locationName);
                    //回显
                    tvLocation.setText(locationName);
                }
                break;
            case CHOOSE_HOSPITAL:
                if (resultCode == RESULT_OK) {
                    hospitalId = data.getIntExtra("hospital_id", 0);
                    String hospitalName = data.getStringExtra("hospital_name");
                    Log.i(TAG, "onActivityResult: 返回回来医院的id" + hospitalId);
                    tvHospital.setText(hospitalName);
                }
                break;
            case CHOOSE_OFFICE:
                if (resultCode == RESULT_OK) {
                    officeId = data.getIntExtra("office_id", 0);
                    String officeName = data.getStringExtra("office_name");
                    Log.i(TAG, "onActivityResult: 返回回来的科室id: " + hospitalId);
                    tvOffice.setText(officeName);
                }
                break;
            case CHOOSE_WORK:
                if (resultCode == RESULT_OK) {
                    String workId = data.getStringExtra("work_id");
                    String workName = data.getStringExtra("work_name");
                    Log.i(TAG, "onActivityResult: 返回回来的擅长的工作的id: " + workId);
                    tvBeGoodAtWork.setText(workName);
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
        if (DocumentsContract.isDocumentUri(HsCompileInfoActivity.this, uri)) {
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
            headFile = saveMyBitmap(bitmap, HEAD_KEY);
            //保存file到sp
            saveFile(headFile.getName());
            Glide.with(this).asBitmap().load(headFile).thumbnail(0.1f).into(civHead);
        } else {
            Toast.makeText(HsCompileInfoActivity.this, "failed to get image ", Toast.LENGTH_SHORT).show();
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
    private void saveFile(String fileName) {
        SharedPreferences sp = getSharedPreferences("image", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(HEAD_KEY, fileName);
        //提交edit
        edit.apply();
        Log.i(TAG, "saveFile: 保存成功" + sp.getString(HEAD_KEY, null));
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
