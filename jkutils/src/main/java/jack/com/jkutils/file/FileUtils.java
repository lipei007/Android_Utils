package jack.com.jkutils.file;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 文件操作工具，非线程安全
 * */
public class FileUtils {

    private static  volatile FileUtils sharedInstance;

    private FileUtils() {

    }

    public static FileUtils sharedUtils() {

        if (sharedInstance == null) {
            synchronized (FileUtils.class) {
                if (sharedInstance == null) {
                    sharedInstance = new FileUtils();
                }
            }
        }
        return sharedInstance;
    }

    public String sandboxCacheDirectory(Context context) {

        if (context == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            context = context.createDeviceProtectedStorageContext();
        }

        File dir = context.getFilesDir();
        return dir.getAbsolutePath();

    }

    public String sandboxTempDirectory(Context context) {

        if (context == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            context = context.createDeviceProtectedStorageContext();
        }
        File dir = context.getCacheDir();
        return dir.getAbsolutePath();

    }

    /**
     * 机身存储的外部存储
     * */
    public String deviceExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * SDCard
     * */
    public String SDCardDirectory(Context context) {
        return null;
    }

    public interface ProgressHandler {
        void progress(long progress);
    }

    public boolean writeData2DiskFromInput(File file, InputStream input, ProgressHandler progressHandler) {

        if (file == null) {
            return false;
        }

        OutputStream out = null;
        boolean result = false;

        try {

            //写数据流
            out =new FileOutputStream(file);

            byte buffer[]=new byte[4*1024];//每次存4K

            int temp;

            long total = 0;

            //写入数据
            while((temp=input.read(buffer))!=-1){
                out.write(buffer,0,temp);

                if (progressHandler != null) {
                    total += temp;
                    progressHandler.progress(total);
                }
            }
            out.flush();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean writeByte2Path(byte[] bytes, String path) {

        File file = new File(path);
        file = createFileInDir(file.getName(), file.getParent());

        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;

        boolean result = false;
        try {
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();

            result = true;
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        return result;
    }

    public boolean writeJson(JSONObject json, String filePath) {

        if (json == null || filePath == null || filePath.length() == 0) {
            return false;
        }

        boolean result = false;

        File f = new File(filePath);
        if (f.exists() && f.isFile()) {
            f.delete();
        }

        Writer writer = null;
        try {

            OutputStream out = new FileOutputStream(new File(filePath));
            writer = new OutputStreamWriter(out);
            writer.write(json.toString());

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private String filterFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        if (fileName.endsWith(")")) {

            int index = fileName.lastIndexOf("(");

            return fileName.substring(0, index);
        }

        return fileName;
    }

    private File createFile(File file, boolean origin, int index) {
        if (file == null) {
            return null;
        }

        if (file.exists()) {

            if (origin) {
                return file;

            } else {

                String name = file.getName();

                if (name.contains(".")) {
                    String[] contents = name.split("\\.");
                    String tmpName = contents[0];
                    String extension = name.substring(tmpName.length() + 1);

                    tmpName = filterFileName(tmpName);
                    name = String.format("%s(%d).%s",tmpName, ++index, extension);

                } else {

                    name = filterFileName(name);
                    name = String.format("%s(%d)",name, ++index);
                }

                String path = file.getParent() + File.separator + name;
                File file1 = new File(path);

                return createFile(file1, origin, index);

            }
        }
        return file;
    }

    private File createFileInDir(String fileName, String dir, boolean isDir) {

        if (dir == null || dir.length() == 0 || fileName == null || fileName.length() == 0) {
            return null;
        }

        File dirF = new File(dir);

        if (dirF.exists()) {

            if (!dirF.isDirectory()) {

                if (!dirF.mkdirs()) {
                    return null;
                }
            }
        }

        String filePath = String.format("%s%s%s",dir, File.separator, fileName);
        File file = new File(filePath);
        file = createFile(file, false, 0);

        try {
            if (isDir) {

                if (file.mkdirs()) {
                    return file;
                }

            } else {

                if (file.createNewFile()) {
                    return file;
                }
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public File createFileInDir(String fileName,String dir) {

        return createFileInDir(fileName, dir, false);
    }

    public File createFolderInDir(String folderName, String dir) {

        return createFileInDir(folderName, dir, true);
    }

    /**
     * 删除文件或文件夹
     * */
    public void deleteFile(File file) {
        if (file == null) {
            return;
        }

        if (file.exists()) {

            if (file.isFile()) { // 文件

                file.delete();

            } else { // 目录

                // 删除目录中所有文件
                File list[] = file.listFiles();
                for (File f : list) {
                    deleteFile(f);
                }

                // 如果目录为空，删除目录
                if (file.listFiles().length == 0) {
                    file.delete();
                }
            }

        }
    }

    /**
     * 移动 文件或文件夹 至 目录
     * @param src 源 文件/文件夹
     * @param destDir 目标目录
     * */
    public boolean moveFile(String src, String destDir) {

        if (src == null || destDir == null) {
            return false;
        }
        File srcFile = new File(src);
        if (!srcFile.exists()) {
            return false;
        }

        String name = srcFile.getName();

        if (srcFile.isDirectory()) {

            String destFolder = destDir + File.separator + name;
            File destFolderF = new File(destFolder);
            destFolderF.mkdirs();

            File[] contents = srcFile.listFiles();
            boolean success = true;
            for (File f : contents) {
                success = success && moveFile(f.getAbsolutePath(), destFolder);
            }

            if (success) {
                deleteFile(srcFile);
            }

            return success;

        } else {

            File destFile = new File(destDir);
            if (destFile.exists() && destFile.isDirectory()) {

            } else {
                destFile.mkdirs();
            }

            File dest = createFileInDir(name, destDir);
            if (dest.exists()) {
                deleteFile(dest);
            }

            boolean move = srcFile.renameTo(dest);

            return move;

        }
    }

    /**
     * 拷贝文件
     * @param file 源文件
     * @param destDir 目标文件夹
     * */
    public boolean copyFile(File file, String destDir, ProgressHandler progressHandler) {

        if (file == null || !file.exists() || destDir == null || destDir.length() == 0) {
            return false;
        }

        if (file.isFile()) {

            try {

                File dest = createFileInDir(file.getName(), destDir);

                FileInputStream is = new FileInputStream(file);
                FileOutputStream os = new FileOutputStream(dest);

                int copy = is.read();
                long totalCopy = 0;

                while (copy != -1) {

                    os.write(copy);
                    copy = is.read();

                    if (progressHandler != null) {
                        totalCopy += copy;
                        progressHandler.progress(totalCopy);
                    }
                }
                is.close();
                os.close();

                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            File dest = createFolderInDir(file.getName(), destDir);
            if (dest != null) {

                File[] files = file.listFiles();
                for (File f : files) {
                    copyFile(f, dest.getAbsolutePath(), progressHandler);
                }

                return true;
            }

        }



        return false;
    }

    /**
     * 文件大小
     * */
    public long sizeOfFile(File file) {
        long size = 0;

        if (file != null && file.exists()) {

            if (file.isFile()) {

                size = file.length();

            } else {

                File list[] = file.listFiles();
                for (File f : list) {
                    size += sizeOfFile(f);
                }

            }

        }

        return size;
    }

    /**
     * 获取文件扩展名
     * */
    public String fileExtension(File file) {

        if (file == null) {
            return null;
        }

        String name = file.getName();

        if (name.contains(".")) {

            String[] contents = name.split("\\.");
            String extension = contents[contents.length - 1];

            return extension;

        } else {
            return "";
        }
    }

    /**
     * 调用系统Share，分析文件
     * */
    public void shareFile(Context context, Uri uri, String type) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(type);
        context.startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    /**
     * 获取文件MIMEType
     * */
    public String filMimeType(File file) {
        if (file == null) {
            return null;
        }
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (mime == null || mime.isEmpty()) {
            mime = "text/plain";
        }
        return mime;
    }

    /**
     * Update MediaStore
     *
     * 由于Android文件系统扫描机制，通常，只会在开机的时候对文件进行扫描(MediaScanner主要用来做这件事情)，
     * 特别是对于媒体文件，在扫描后生成一个媒体文件的索引，便于图片浏览器等类似的软件可以快速的获取系统中的图片等媒体软件。
     * 于是乎，在应用程序运行过程中在内部存储中写入文件后，系统并不知道该文件或者该目录已经存在。
     * 所以，导致用户使用图片浏览器的时候不能找到刚刚拍摄的图片。
     * 或者使用电脑查看时，不能看到创建的文件夹，当然图片也就找不到了。那么如何解决这个问题呢：
     * Android给开发者提供了一组API，用于用户将特定的媒体文件告知Android系统的MediaScanner(媒体扫描器)进行对指定文件的扫描。
     * 让系统发现该文件，以便用户可以快速的读取和查看。
     *
     * */
    public void updateMediaStoreForFile(Context context, String filePath) {

        if (context == null || filePath == null) {
            return;
        }

        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });

    }

}
