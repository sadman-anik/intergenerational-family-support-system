import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


// Lab-style security utility class.
// Static methods are used by both client and server.
public class SecurityUtil {

    public static HashMap ReadinKeys(String keyFile) {
        FileInputStream pfin = null;
        try {
            pfin = new FileInputStream(keyFile);
            ObjectInputStream obin = new ObjectInputStream(pfin);
            HashMap keys = (HashMap) obin.readObject();
            obin.close();
            pfin.close();
            return keys;
        } catch (FileNotFoundException ex) {
            System.out.println("Exception in ReadinKeys(): " + ex.getMessage() + "\n");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Exception in ReadinKeys(): " + ex.getMessage() + "\n");
        } finally {
            try {
                if (pfin != null) {
                    pfin.close();
                }
            } catch (IOException ex) {
                System.out.println("Exception in ReadinKeys(): " + ex.getMessage() + "\n");
            }
        }
        throw new RuntimeException("Key file could not be read.");
    }

    public static String RandomAlphaNumericString(int n) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz"
                + "+/";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    // Asymmetrical encryption; result is Base64 string.
    public static String asyEncrypt(String message, Key pk) {
        String etext = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] cipherData = cipher.doFinal(message.getBytes("UTF-8"));
            etext = Base64.getEncoder().encodeToString(cipherData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | UnsupportedEncodingException |
                 IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in asyEncrypt(): " + ex.getMessage());
        }
        return etext;
    }

    // Asymmetrical decryption; result is normal string.
    public static String asyDecrypt(String message, Key prik) {
        String ptext = "";
        try {
            byte[] msgbytes = Base64.getDecoder().decode(message);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, prik, cipher.getParameters());
            ptext = new String(cipher.doFinal(msgbytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in asyDecrypt(): " + ex.getMessage());
        }
        return ptext;
    }

    public static SecretKey SecretKeyGen() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Exception in SecretKeyGen(): " + ex.getMessage() + "\n");
        }
        throw new RuntimeException();
    }

    public static String keytoB64String(SecretKey sKey) {
        return Base64.getEncoder().encodeToString(sKey.getEncoded());
    }

    public static String EncryptSessionKey(SecretKey sessionKey, PublicKey pubkey) {
        String sessionKeyString = SecurityUtil.keytoB64String(sessionKey);
        return SecurityUtil.asyEncrypt(sessionKeyString, pubkey);
    }

    public static SecretKey DecryptSessionKey(String cipherSessionKeyString, PrivateKey prikey) {
        String sessionKeyString = SecurityUtil.asyDecrypt(cipherSessionKeyString, prikey);
        return SecurityUtil.B64StringTokey(sessionKeyString);
    }

    public static SecretKey B64StringTokey(String kString) {
        byte[] bytekey = Base64.getDecoder().decode(kString);
        return new SecretKeySpec(bytekey, 0, bytekey.length, "AES");
    }

    public static String SymEncryptObj(Object obj, SecretKey sessionKey) {
        byte[] objectBytes = SecurityUtil.convertObjectToBytes(obj);
        return SecurityUtil.SymEncrypt(objectBytes, sessionKey);
    }

    public static byte[] convertObjectToBytes(Object obj) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static String SymEncrypt(byte[] message, Key sk) {
        String ctext = "";
        try {
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, sk);
            ctext = Base64.getEncoder().encodeToString(aesCipher.doFinal(message));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in SymEncrypt(): " + ex.getMessage());
        }
        return ctext;
    }

    public static Object SymDecryptObj(String objectString, SecretKey sessionKey) {
        byte[] objectBytes = SecurityUtil.SymDecrypt(objectString, sessionKey);
        return SecurityUtil.convertBytesToObject(objectBytes);
    }

    public static byte[] SymDecrypt(String message, Key sk) {
        byte[] ptext = null;
        try {
            byte[] msgbytes = Base64.getDecoder().decode(message);
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, sk);
            ptext = aesCipher.doFinal(msgbytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in SymDecrypt(): " + ex.getMessage());
        }
        return ptext;
    }

    public static Object convertBytesToObject(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static String pubKeytoB64String(PublicKey pKey) {
        return Base64.getEncoder().encodeToString(pKey.getEncoded());
    }
}
