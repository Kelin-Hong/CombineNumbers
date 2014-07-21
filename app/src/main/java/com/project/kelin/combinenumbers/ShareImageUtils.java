package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by kelin on 14-7-21.
 */
public class ShareImageUtils {
    /**
     * 获取和保存当前屏幕的截图
     */
    public static void getAndSaveCurrentImage(Activity context, String fileName) {
//构建Bitmap
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//获取屏幕
        View decorview = context.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
//图片存储路径
        String SavePath = getSDCardPath() + "/ScreenImages";
//保存Bitmap
        try {
            File path = new File(SavePath);
//文件
            String filepath = SavePath + "/" + fileName + ".png";

            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
//                Toast.makeText(mContext, "截屏文件已保存至SDCard/ScreenImages/目录下",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private static String getSDCardPath() {
        File sdcardDir = null;
//判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    /**
     * 分享功能
     *
     * @param context       上下文
     * @param activityTitle Activity的名字
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param fileName       图片名称，不分享图片则传null
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String fileName) {
        String SavePath = getSDCardPath() + "/ScreenImages";
        String imgPath = SavePath + "/" + fileName + ".png";
        getAndSaveCurrentImage((Activity) context,fileName);
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }
}
