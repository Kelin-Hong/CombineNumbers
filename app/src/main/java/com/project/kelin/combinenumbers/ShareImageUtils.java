package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by kelin on 14-7-21.
 */
public class ShareImageUtils {
    private final static String APP_ID = "wxa924f725a38c6d74";
    private final static String SAVE_PATH = getSDCardPath() + "/ScreenImages/";
    private final static String IMAGE_FORMAT_POSTFIX = ".png";
    private final static String SHARE_TITLE = "一步都不能错";
    private final static String WX_SHARE_TITLE = "我竟然玩到了第%1$d关,一般人只能玩到第3关,挑战你的智商的数字合并游戏！你也来试试";
    private final static String SHARE_CONTENT = "我竟然玩到了第%1$d关,一般人只能玩到第3关,挑战你的智商的数字合并游戏！你也来试试[一步都不能错]";
    private final static String SHARE_DOWNLOAD_URL = "http://share.weiyun.com/34784165f1273516be46a531982700da";
    private static IWXAPI api;

    /**
     * 获取和保存当前屏幕的截图
     */
    public static void saveImage(Activity context, String fileName) {
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        View decorview = context.getWindow().getDecorView();
        int[] location1 = new int[2];
        decorview.getLocationOnScreen(location1);
        int[] location2 = new int[2];
        //dialogView.getLocationOnScreen(location2);
        decorview.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = decorview.getDrawingCache();
        //Bitmap bitmap2 = getBitmapFromView(dialogView);
//        Canvas canvas = new Canvas(bitmap1);
//        canvas.drawBitmap(bitmap2, location2[0] - location1[0], location2[1] - location1[1], new Paint());
        try {
            File path = new File(SAVE_PATH);
            String filepath = SAVE_PATH + fileName + IMAGE_FORMAT_POSTFIX;
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
                bitmap1.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int px[] = {36, 48, 72, 96};

    public static void saveIconImage(Activity context, String fileName, View iconView) {
        Bitmap bitmap = getBitmapFromView(iconView);
        for (int i = 0; i < 4; i++) {
            //Bitmap bitmap = Bitmap.createScaledBitmap(bap, px[i], px[i], true);
//        Canvas canvas = new Canvas(bitmap1);
//        canvas.drawBitmap(bitmap2, location2[0] - location1[0], location2[1] - location1[1], new Paint());
            try {
                File path = new File(SAVE_PATH);
                String filepath = SAVE_PATH + fileName + "_" + px[i] + IMAGE_FORMAT_POSTFIX;
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
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除没用的屏幕的截图文件夹
     */
    public static void deleteImageFolder(Activity context) {
        deleteDirectory(SAVE_PATH);
    }

    /**
     * 删除没用的屏幕的截图
     */
    public static void deleteImage(Activity context, String fileName) {
        try {
            String filepath = SAVE_PATH + fileName + IMAGE_FORMAT_POSTFIX;
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return 路径
     */
    private static String getSDCardPath() {
        File sdcardDir = null;
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    /**
     * 分享功能
     *
     * @param context  上下文
     * @param fileName 图片名称，不分享图片则传null
     */
    public static void starSystemShare(Context context, String fileName) {
        String imgPath = SAVE_PATH + fileName + IMAGE_FORMAT_POSTFIX;
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain");
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, SHARE_TITLE);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(SHARE_CONTENT, DataConstants.sPassIndex) + SHARE_DOWNLOAD_URL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

    public static void shareToWX(Context context, String imageName) {
        String imagePath = SAVE_PATH + imageName + IMAGE_FORMAT_POSTFIX;
        api = WXAPIFactory.createWXAPI(context, APP_ID, false);
        api.registerApp(APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = SHARE_DOWNLOAD_URL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = String.format(WX_SHARE_TITLE, DataConstants.sPassIndex);
        msg.description = String.format(WX_SHARE_TITLE, DataConstants.sPassIndex);
        try {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            //Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            bmp.recycle();
            msg.setThumbImage(thumbBmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
}
