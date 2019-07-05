package com.example.camera;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;


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
                intent.putExtra("LOCAL","纬度："+slatitude+" "+"经度："+slongitude);
                intent.putExtra("LATITUDE",slatitude);
                intent.putExtra("LONGITUDE",slongitude);
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
    public static String readStreamToString(InputStream inputStream) throws IOException {
        //创建字节数组输出流 ，用来输出读取到的内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //创建读取缓存,大小为1024
        byte[] buffer = new byte[1024];
        //每次读取长度
        int len = 0;
        //开始读取输入流中的文件
        while( (len = inputStream.read(buffer) ) != -1){ //当等于-1说明没有数据可以读取了
            byteArrayOutputStream.write(buffer,0,len); // 把读取的内容写入到输出流中
        }
        //把读取到的字节数组转换为字符串
        String result = byteArrayOutputStream.toString();
        //关闭输入流和输出流
        inputStream.close();
        byteArrayOutputStream.close();
        //返回字符串结果
        return result;
    }
    private String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }
    public static void inputStreamFile(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

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
                File file = new File(mFilePaths);
                //获取选中文件的定位符
                Uri uri = data.getData();
                //使用content的接口
                ContentResolver cr = this.getContentResolver();
                try {
                    //获取图片
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    mimageview.setImageBitmap(bitmap);
                    try {
                        inputStreamFile(cr.openInputStream(uri),file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mFilePaths = file.getPath();
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



