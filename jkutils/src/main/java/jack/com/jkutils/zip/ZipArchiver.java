package jack.com.jkutils.zip;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

/**
 * Android 加密 压缩/解压
 * 前往 http://www.lingala.net/zip4j/ 下载 zip4j jar包
 * 我所使用的包是从 下载的Examples eclipse 版本中 的lib目录 抽取出来的。
 * 将jar包拷贝到工程libs目录下，然后在 File/Project Structure/Modules/Dependencies 点击加号选择jar dependency将包导入。
 *
 * */


public class ZipArchiver {

    /**
     * 压缩文件/文件夹 src 至目录 dest
     * @param src 待压缩文件/文件夹
     * @param dest 被压缩文件存放路径，为空则默认为源文件路径.zip
     * @param password 压缩密码，如果为null，则不加密
     * @return 压缩文件，可能为null
     *
     * */
    public static File zipFile(String src, String dest, String password) {

        if (isEmpty(src)) {
            return null;
        }

        File srcF = new File(src);

        if (!srcF.exists()) {
            return null;
        }

        if (isEmpty(dest)) {
            dest = src + ".zip";
        }

        File destF = new File(dest);
        if (destF.exists() && destF.isFile()) {
            deleteFile(destF);
        }


        try {

            ZipFile zipFile = new ZipFile(dest);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (password != null) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                parameters.setPassword(password);
            }

            if (srcF.isDirectory()) {
                zipFile.addFolder(srcF,parameters);
            } else {
                zipFile.addFile(srcF,parameters);
            }

            return destF;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 多目标文件/文件夹 压缩
     * @param srcs 待压缩文件/文件夹 数组
     * @param dest 被压缩文件存放路径，不能为null
     * @param password 压缩密码，如果为null，则不加密
     * @return 压缩文件，可能为null
     *
     * */
    public static File zipFiles(String[] srcs, String dest, String password) {

        if (srcs == null || srcs.length == 0 || isEmpty(dest)) {
            return null;
        }

        File destF = new File(dest);
        if (destF.exists() && destF.isFile()) {
            deleteFile(destF);
        }


        try {

            ZipFile zipFile = new ZipFile(dest);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (password != null) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                parameters.setPassword(password);
            }

            for (String src : srcs) {

                if (!isEmpty(src)) {

                    File srcF = new File(src);

                    if (srcF.exists()) {

                        if (srcF.isDirectory()) {
                            zipFile.addFolder(srcF,parameters);
                        } else {
                            zipFile.addFile(srcF,parameters);
                        }
                    }

                }

            }

            return destF;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解压文件至dir目录
     * @param file 待解压源文件
     * @param dir  解压后文件存放目录，为空与zip文件同目录
     * @param password 解压密码，可为null
     * @return 解压成功/失败，true/false
     * */
    public static boolean unzipFile(File file, String dir, String password) {

        if (file == null) {
            return false;
        }
        if (!file.exists() || file.isDirectory()) {
            return false;
        }

        try {

            ZipFile zipFile = new ZipFile(file);
            if (!zipFile.isValidZipFile()) {
                return false;
            }
            if (zipFile.isEncrypted()) {
                if (password != null) {
                    zipFile.setPassword(password);
                } else {
                    return false;
                }
            }
            
            if (isEmpty(dir)) {
            	dir = file.getParent();
            }

            File dirF = new File(dir);
            if (dirF.exists() && dirF.isDirectory()) {

            } else {
                dirF.mkdirs();
            }
            zipFile.extractAll(dir);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void deleteFile(File file) {
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
    
    private static boolean isEmpty(String str) {
    	return str == null || str.length() == 0;
    }

}
