package net.yolopago.pago.utilities;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.CRC32;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherHM {

    private static final String ALPHA_NUMERIC_STRING = "0123456789ABCDEF";
    private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String encrypt(byte[] key, byte[] vectorInicializacion, byte[] data) {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/noPadding");//"AES/CBC/PKCS5Padding"
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(vectorInicializacion);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data);
            String sret = bytesToHex(encrypted);
            //sret = URLEncoder.encode(sret, "UTF-8");

            return sret.toUpperCase();

        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (NoSuchPaddingException ex) {
            return "";
        } catch (InvalidKeyException ex) {
            return "";
        } catch (InvalidAlgorithmParameterException ex) {
            return "";
        } catch (IllegalBlockSizeException ex) {
            return "";
        } catch (BadPaddingException ex) {
            return "";
        }
    }
    public static String encryptYLP(byte[] key, String dataStr) {
        try {
            byte[] vectorInicializacion=emptyVector(16);
            if(dataStr.length()<=32){
                dataStr=dataStr+"                                ";
                dataStr=dataStr.substring(0, 32);
            }else {
                dataStr=dataStr+"                                                                ";
                dataStr=dataStr.substring(0, 48);
            }
            byte[] data = dataStr.getBytes();
            Cipher cipher = Cipher.getInstance("AES/CBC/noPadding");//"AES/CBC/PKCS5Padding"
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(vectorInicializacion);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data);
            String sret = bytesToHex(encrypted);
            //sret = URLEncoder.encode(sret, "UTF-8");

            return sret.toUpperCase();

        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (NoSuchPaddingException ex) {
            return "";
        } catch (InvalidKeyException ex) {
            return "";
        } catch (InvalidAlgorithmParameterException ex) {
            return "";
        } catch (IllegalBlockSizeException ex) {
            return "";
        } catch (BadPaddingException ex) {
            return "";
        }
    }

    public static byte[] emptyVector(int size) {
            byte[] emptyVector =new byte[size];
            for(int i=0;i<size;i++) {
                emptyVector[i]=0x00;
            }
            return emptyVector;
    }


    public static String dencrypt(byte[] key, byte[] vectorInicializacion, String data) {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/noPadding");//"AES/CBC/PKCS5Padding"
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(vectorInicializacion);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(hexToBytes(data));
            String sret = bytesToHex(encrypted);
            //sret = URLEncoder.encode(sret, "UTF-8");
            return sret.toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (NoSuchPaddingException ex) {
            return "";
        } catch (InvalidKeyException ex) {
            return "";
        } catch (InvalidAlgorithmParameterException ex) {
            return "";
        } catch (IllegalBlockSizeException ex) {
            return "";
        } catch (BadPaddingException ex) {
            return "";
        }/*catch (UnsupportedEncodingException ex) {
      Logger.getLogger(CipherAES.class.getName()).log(Level.SEVERE, null, ex);
    }return "";*/
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String CRC32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return Long.toHexString(crc32.getValue()).toUpperCase();
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(new SecureRandom().nextDouble() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] hexToBytes(String hexa) {
        char[] hex= hexa.toCharArray();
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127)
                value -= 256;
            raw[i] = (byte) value;
        }
        return raw;
    }
    public static String getSelector(String llavePaso, String serialNumber, String crc32) {
        String selector = bytesToHex(llavePaso.getBytes()) + bytesToHex(serialNumber.getBytes()) + crc32;
        return selector;
    }

}
