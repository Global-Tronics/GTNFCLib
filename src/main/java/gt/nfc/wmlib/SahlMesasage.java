package gt.nfc.wmlib;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class SahlMesasage {
    String type;
    String cardBuff;
    public byte[] DecryptBuffer(  String value ) {
        AesDecryptor _AesDecryptor = new AesDecryptor() ;
       try {
            byte[] cleartext = Base64.decode(value ,Base64.DEFAULT ); //value.getBytes("UTF-8");
           byte[] crypted = _AesDecryptor.Decrypt(cleartext  ) ;
            return crypted;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;//"Encrypt Error";
        }
    }

    public String EncryptBuffer(  String value ) {
        AesDecryptor _AesDecryptor = new AesDecryptor() ;
        try {
            //byte[] cleartext = Base64.decode(value ,Base64.DEFAULT ); //value.getBytes("UTF-8");
            String crypted = _AesDecryptor.encrypt(value  ) ;
            byte[] DecryptBuffer = DecryptBuffer(crypted) ;
            return crypted;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;//"Encrypt Error";
        }
    }

    public String bytesToHex(byte[] bytes)
    {
        String str = "" ;
        for (byte b : bytes)
        {
            str += String.format("%02X", b);
            System.out.print(str+"\t");
        }
        return str ;
    }
}

class AesDecryptor {
    public byte[]  Decrypt(byte[] encryptedBytes) {
        byte[] defaultSystemKey = { 0x03, (byte) 0xE3, (byte)0xF1, 0x77, 0x01, 0x00, (byte)0xC3, 0x0B,
                0x27, 0x68, 0x49, 0x20, 0x00, (byte)0xFF, (byte)0xE5, 0x0C };

        byte[] defaultSystemInitVectorKey = { 0x08, 0x42, 0x00, 0x6F, (byte)0x8E, 0x03, 0x33, (byte)0xBC,
                (byte)0xF6, (byte)0x91, 0x05, 0x43, 0x00, (byte)0xEC ,   0x0A , 0x00 };

        try {
            //byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);key.getBytes("UTF-8")iv.getBytes("UTF-8")
            SecretKeySpec secretKeySpec = new SecretKeySpec(defaultSystemKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(defaultSystemInitVectorKey);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");//PKCS5
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return ConvertfromHextoByteArr( new String(decryptedBytes, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String  Encrypt(byte[] encryptedBytes) {
        byte[] defaultSystemKey = { 0x03, (byte) 0xE3, (byte)0xF1, 0x77, 0x01, 0x00, (byte)0xC3, 0x0B,
                0x27, 0x68, 0x49, 0x20, 0x00, (byte)0xFF, (byte)0xE5, 0x0C };

        byte[] defaultSystemInitVectorKey = { 0x08, 0x42, 0x00, 0x6F, (byte)0x8E, 0x03, 0x33, (byte)0xBC,
                (byte)0xF6, (byte)0x91, 0x05, 0x43, 0x00, (byte)0xEC ,   0x0A , 0x00 };

        try {
            //byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);key.getBytes("UTF-8")iv.getBytes("UTF-8")
            SecretKeySpec secretKeySpec = new SecretKeySpec(defaultSystemKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(defaultSystemInitVectorKey);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");//PKCS5
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            //String ret = Base64.decode(decryptedBytes ,Base64.DEFAULT );
            return   new String(decryptedBytes, StandardCharsets.UTF_8  ) ;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String encrypt(String plainText ) {
        byte[] defaultSystemKey = { 0x03, (byte) 0xE3, (byte)0xF1, 0x77, 0x01, 0x00, (byte)0xC3, 0x0B,
                0x27, 0x68, 0x49, 0x20, 0x00, (byte)0xFF, (byte)0xE5, 0x0C };

        byte[] defaultSystemInitVectorKey = { 0x08, 0x42, 0x00, 0x6F, (byte)0x8E, 0x03, 0x33, (byte)0xBC,
                (byte)0xF6, (byte)0x91, 0x05, 0x43, 0x00, (byte)0xEC ,   0x0A , 0x00 };

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(defaultSystemKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(defaultSystemInitVectorKey);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedBytes ,Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static byte[]  ConvertfromHextoByteArr(String HexString)
    {

        //String s = "2f4a33";
        byte[] ans = new byte[HexString.length() / 2];

         for (int i = 0; i < ans.length; i++) {
            int index = i * 2;

            // Using parseInt() method of Integer class
            int val = Integer.parseInt(HexString.substring(index, index + 2), 16);
            ans[i] = (byte)val;
        }
         return ans ;
    }


}





