package com.example.camera;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Menu;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FootActivity extends Activity {
    private ImageView mimageview;
    private static int REQ = 1;
    private static int REQ2 = 2;
    private String mFilePaths;
    @RequiresApi(api = Build.VERSION_CODES.M-1)
    void Request() {             //获取相机拍摄读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本判断
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foot);
        mimageview = findViewById(R.id.imageView);
        mFilePaths = Environment.getExternalStorageDirectory().getPath();
        mFilePaths = mFilePaths + "/" + "temp.png";
        Request();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


        public void lookinfo(View view) {
            try {
                //android读取图片EXIF信息
                ExifInterface exifInterface=new ExifInterface(mFilePaths);
                String smodel=exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                String swidth=exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                String sheight=exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                String sdate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                String slatitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String slongitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String smake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                Intent intent=new Intent();
                intent.setClass(FootActivity.this, InfoActivity.class);
                intent.putExtra("MODEL",smodel);
                intent.putExtra("MAKE",smake);
                intent.putExtra("DATE",sdate);
                intent.putExtra("SIZE",swidth+"*"+sheight);
                intent.putExtra("LOCAL","经度："+slatitude+" "+"纬度："+slongitude);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public void takephoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = Uri.fromFile(new File(mFilePaths));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        startActivityForResult(intent,REQ);
    }

    public void select(View view){
        //intent可以应用于广播和发起意图，其中属性有：ComponentName,action,data等
        Intent intent=new Intent();
        intent.setType("image/*");
        //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
        //类型的内容给你选择
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
        startActivityForResult(intent, REQ2);
    }
    /**
     *把用户选择的图片显示在imageview中
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
           FileInputStream fis = null;
            if (requestCode == REQ) {
                try {
                    fis = new FileInputStream(mFilePaths);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    mimageview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == REQ2) {
                //获取选中文件的定位符
                Uri uri = data.getData();
                Log.e("uri", uri.toString());
                //使用content的接口
                ContentResolver cr = this.getContentResolver();
                try {
                    //获取图片
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    mimageview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(),e);
                }
            }else{
                //操作错误或没有选择图片
                Log.i("FootActivity", "operation error");
            }
            }
    }
    }



