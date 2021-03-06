package com.easyreader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;


import com.easyreader.base.BaseApplication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * 操作文件相关的方法集合
 */
public class FileUtils {
    private static final String cache_root = getSDcardPath();
    private static final String cache_canDelete = cache_root + "//canDelete";
    private static final String cache_canDelete_image = cache_canDelete + "//image";
    //缓存图片目录，用后可调用deleteAllFile()方法删除目录
    public static final String TEMP_IMAGE = cache_root + "/giveU/TEMP_IMAGE";
    public static final String AD_IMG_PATH = cache_root + "/giveU/AD_IMG";//广告页图片目录
    private static float cacheCanDeleteSize;


    public static File saveBitmap(Bitmap bm, String picName) {
        File f = new File(getCacheCanDeleteImageDir(), picName);
        if (f.exists()) {
            f.delete();
        }

        return compressBmpToFile(bm, f);
    }

    /**
     * @param bm
     * @param path 文件的绝对路径
     * @return
     */
    public static File saveBitmapWithPath(Bitmap bm, String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }

        return compressBmpToFile(bm, f);
    }

    public static File saveBitmapInAction(Bitmap bm, String picName) {
        File f = new File(getDirFile(cache_canDelete_image), picName);
        if (f.exists()) {
            f.delete();
        }

        return compressBmpToFile(bm, f);
    }


    public static File compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 300) {//kb
            baos.reset();
            options -= 5;
            bmp.compress(CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public static void createFile(Context context, File file, int drawableRes) {
        try {
            file.createNewFile();
            Bitmap pic = BitmapFactory.decodeResource(context.getResources(), drawableRes);
            FileOutputStream fos = new FileOutputStream(file);
            pic.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getSDcardPath() {
        String mDir = "";
        try{
            //                    /data/data/com.dfppm.giveu/cache
            mDir = BaseApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath();
            if (isHasSdcard()) {
//                /storage/emulated/0/Android/data/com.giveu.shoppingmall/cache
                try{
                    String externalCacheDir = BaseApplication.getInstance().getApplicationContext().getExternalCacheDir().getAbsolutePath();
                    if ( !TextUtils.isEmpty(externalCacheDir) ){
                        mDir = externalCacheDir;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return mDir;
    }

    public static File getDirFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }


    /**
     * 返回缓存文件夹
     *
     * @return
     */
    public static File getCacheCanDeleteImageDir() {
        return getDirFile(cache_canDelete_image);
    }

    /**
     * 返回头像缓存文件夹
     *
     * @return
     */
    public static File getHeadImageDirFile() {
        return getDirFile(cache_canDelete_image);
    }

    /**
     * 返回广告闪屏图片缓存文件夹
     *
     * @return
     */
    public static File getSplashAdImageDirFile() {
        return getDirFile(AD_IMG_PATH);
    }
    /**
     * sd缓存的根目录
     *
     * @return
     */
    public static File getCacheRootDirFile() {
        return getDirFile(cache_root);
    }


    /**
     * 获取文件大小
     *
     * @param f
     * @return
     */
    public static void getCacheFileSize(File f) {
        try {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    getCacheFileSize(flist[i]);
                } else {
                    cacheCanDeleteSize = cacheCanDeleteSize + flist[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清楚缓存
     */
    public static void clearCacheInSetting() {
        try {
            deleteAllFile(getCacheRootDirFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cacheCanDeleteSize = 0;
    }

    public static void deleteAllFile(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteAllFile(f);
                }
                file.delete();
            }
        }
    }

    /**
     * 将File文件转化为String
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String file2String(String fileName) {
        File file = new File(fileName);
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file));
            String content = "";
            StringBuilder sb = new StringBuilder();
            while (content != null) {
                content = bf.readLine();
                if (content == null) {
                    break;
                }
                sb.append(content.trim());
            }
            bf.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return size
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long size = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
//                Log.w(">>>", file.toString());
            } else if (file.isDirectory()) {
                size += file.length();
//                Log.w(">>>", file.toString());
                size += getDirSize(file); // 递归调用继续统计
            }
        }
        return size;
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dir
     */
    public static void clearDir(File dir) {
        if (dir == null) {
            return;
        }
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                clearDir(file);
            }
        }
    }


    public static void deleteDirInSelectImagesView() {
        File dir = new File(cache_canDelete_image);
        deleteAllFile(dir);
    }

    public static byte[] File2byte(String filePath)
    {
        byte[] buffer = null;
        try
        {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void byte2File(byte[] buf, String filePath, String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory())
            {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }






}
