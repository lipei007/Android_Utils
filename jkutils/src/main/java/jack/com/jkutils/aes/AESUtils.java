package jack.com.jkutils.aes;

import android.util.Base64;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * AES
 * 需要导入bcprov.jar包
 * */
public class AESUtils {

    // 算法名称
    final static String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    final static String algorithmStr = "AES/CBC/PKCS7Padding";

    private final static String HEX = "0123456789ABCDEF";

    static byte[] iv = new byte[16];


    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }



    public static String encrypt128(String keystring, String content) {

        try {

            Key key= null;
            key = createKey128(keystring);
            byte[] result=encrypt(content.getBytes(),key);
            Log.d(TAG, "encrypt: "+toHex(result));
            return Base64.encodeToString(result,Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String decrypt128(String keystring, String content) {



        try {

            byte[] buffer =Base64.decode(content, Base64.DEFAULT);


            Key key= null;
            key = createKey128(keystring);

            byte[] result = decrypt(buffer,key);

            return new String(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt256(String keystring, String content) {

        try {

            Key key= null;
            key = createKey256(keystring);
            byte[] result=encrypt(content.getBytes(),key);
            Log.d(TAG, "encrypt: "+toHex(result));
            return Base64.encodeToString(result,Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String decrypt256(String keystring, String content) {



        try {

            byte[] buffer =Base64.decode(content, Base64.DEFAULT);


            Key key= null;
            key = createKey256(keystring);

            byte[] result = decrypt(buffer,key);

            return new String(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 加密方法
     *
     * @param content
     *            要加密的字符串
     * @param key
     *            加密密钥
     * @return
     */
    private static byte[] encrypt(byte[] content,  Key key) {
        byte[] encryptedText = null;

        Cipher cipher;

        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");


            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return encryptedText;
    }
    /**
     * 解密方法
     *
     * @param encryptedData
     *            要解密的字符串
     * @param key
     *            解密密钥
     * @return
     */
    private static byte[] decrypt(byte[] encryptedData, Key key) {
        byte[] encryptedText = null;

        Cipher cipher;
        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;

        // 初始化
        Security.addProvider(new BouncyCastleProvider());

        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return encryptedText;
    }


    private static Key createKey128(String keystring) throws UnsupportedEncodingException, NoSuchAlgorithmException {


        byte[] keyBytes=keystring.getBytes();
        Key key;
        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }

        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return key;
    }


    private static Key createKey256(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(key.getBytes("UTF-8"));

        int base = 16;
        if (keyBytes.length % base != 0) {
            Log.d(TAG, "createKey256: "+"key is too short, expanded");
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }
}
