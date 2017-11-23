package com.qryl.qrylyh.activity.H5;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.ConstantValue;
import com.qryl.qrylyh.util.HgxqAndroidToJs;
import com.qryl.qrylyh.view.ProgressWebview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WritePatientsFileActivity extends AppCompatActivity {

    private static final String TAG = "WritePatientsFileActivity";

    private static final String URL_YS = ConstantValue.URL_H5 + "/medical/doctor_patient_details.html";
    private static final String URL_HS = ConstantValue.URL_H5 + "/medical/nurse_patient_details.html";
    private ValueCallback<Uri> mUploadFile;
    private String mCameraFilePath;

    private static final String URL_AM = ConstantValue.URL_H5 + "medical/massager_patient_details.html";

    private ProgressWebview webview;
    private String userId;
    private int roleType;
    private int pubId;
    private int patientId;
    private String orderId;

    //H5调用Android 相册 相机的
    public OpenFileWebChromeClient mOpenFileWebChromeClient = new OpenFileWebChromeClient();
    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调</span>
    private Uri imageUri;
    private String token;

    /**
     * @param context
     * @param pubId     医患端登录id
     * @param patientId 病人的id
     * @param orderId   订单的id
     */
    public static void actionStart(Context context, int pubId, int patientId, String orderId) {
        Intent intent = new Intent(context, WritePatientsFileActivity.class);
        intent.putExtra("pub_id", pubId);
        intent.putExtra("patient_id", patientId);
        intent.putExtra("order_id", orderId);
        context.startActivity(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SharedPreferences prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        roleType = prefs.getInt("role_type", 4);
        token = prefs.getString("token", "");
        Intent intent = getIntent();
        pubId = intent.getIntExtra("pub_id", 0);
        patientId = intent.getIntExtra("patient_id", 0);
        orderId = intent.getStringExtra("order_id");
        Log.i(TAG, "传给H5的类型: " + pubId);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webview = (ProgressWebview) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDatabasePath(WritePatientsFileActivity.this.getApplicationContext().getCacheDir().getAbsolutePath());
        webview.addJavascriptInterface(new HgxqAndroidToJs(this, this), "qrylhg");
        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (roleType == 3) {
                    webview.loadUrl("javascript:getId( " + userId + "," + patientId + "," + orderId + ")");
                } else {
                    webview.loadUrl("javascript:getId( " + userId + "," + pubId + "," + patientId + ",'" + orderId + "','" + token + "'" + ")");
                }
            }

        });
        webview.setWebChromeClient(new OpenFileWebChromeClient());
        if (roleType == 0) {//护工
            //webview.loadUrl(URL_HS);
        } else if (roleType == 2) {//护士
            webview.loadUrl(URL_HS);
        } else if (roleType == 1) {//医生
            webview.loadUrl(URL_YS);
        } else if (roleType == 3) {//按摩师
            webview.loadUrl(URL_AM);
        }
    }

    //重写WebviewChromeClient
    //h5 掉用Android 相册
    public class OpenFileWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //lblCaption.setText(title);
        }

        //android 5.0以后google做了支持
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            take();
            return true;
        }

        //下面的这些方法会根据android的版本自动选择
        //android3.0以下
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            take();
        }

        //android3.0-4.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            take();
        }

        //4.0-4.3  4.4.4(android 4.4无方法）
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            take();
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {

            WebView childView = new WebView(WritePatientsFileActivity.this);
            childView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    webview.loadUrl(url);
                    return true;
                }
            });
            final WebSettings settings = childView.getSettings();
            settings.setJavaScriptEnabled(true);
            childView.setWebChromeClient(this);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childView);
            resultMsg.sendToTarget();
            return true;
        }

    }

    /**
     * 以下是webview的照片上传时候，用于在网页打开android图库文件
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == REQUEST_LOGIN) {
//            if (resultCode == RESULT_OK) {
//                webview.reload();
//            }
//        }
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else if (mUploadMessage != null) {

                if (result != null) {
                    String path = getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mUploadMessage
                            .onReceiveValue(uri);
                } else {
                    //此处要设为null，否则在未选择图片的情况下，依然会出现上传空白文件，
                    imageUri = null;
                    mUploadMessage.onReceiveValue(imageUri);
                }
                mUploadMessage = null;


            }
        }
    }

    //针对5.0的结果回调
    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;

        if (resultCode == Activity.RESULT_OK) {

            if (data == null) {

                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            //此处要设为null，否则在未选择图片的情况下，依然会出现上传空白文件，
            results = null;
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }

//take方法

    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        //设定图片的uri路径
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        //调用相机的intent
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //获取包管理器（就是包含了整个清单文件，其中包括application，activity）
        final PackageManager packageManager = getPackageManager();
        //查询相机intent的activity，ResolveInfo其实就是activity节点
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        //进行遍历
        for (ResolveInfo res : listCam) {
            //获取list中的元素，就activity，就是根据activity拿到相机的包名
            final String packageName = res.activityInfo.packageName;
            //将相机的intent 赋给新的intent
            final Intent i = new Intent(captureIntent);
            //重新设置当前intent的Component  （写全包名）
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        //这个是文档内容，包含image，音视频
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        //添加分类
        i.addCategory(Intent.CATEGORY_OPENABLE);
        //类型为图片
        i.setType("image/*");
        //开始创建跳转应用的对话框，可以自己设置dialog样式，也可以像下面这样，同时创建包含多个
        //intent的对话框，但样式不好控制，只能由系统默认，比如在4.4.4的模拟器上样式，图片见末尾

        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        //添加额外初始相机intent，但要序列化
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        WritePatientsFileActivity.this.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }
    //由于4.4以后content后面不再表示路径，以下方法专为4.4以后路径问题的解决

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
