package uni.tirocinio.generatore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCipher {
    private final String ALGORITHM = "AES";
    private byte[] key;
    private SecretKeySpec secretKey;

    public AESCipher(String password) {
        MessageDigest sha = null;
        try {
            key = password.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String data) {
        String enrypted = "";
        try {
            Cipher encCipher = Cipher.getInstance(ALGORITHM);
            encCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            enrypted = Base64.getEncoder().encodeToString(encCipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Errore durante la cifratura: " + e.toString());
        }
        return enrypted;
    }

    public String decrypt(String data) {
        String decrypted = "";
        try {
            Cipher decCipher = Cipher.getInstance(ALGORITHM);
            decCipher.init(Cipher.DECRYPT_MODE, secretKey);
            decrypted = new String(decCipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            System.out.println("Errore durante la decifratura: " + e.toString());
        }
        return decrypted;
    }

    public static void main(String[] args) {
        AESCipher c = new AESCipher("gabrielerundo");

        Scanner sc = new Scanner(System.in);
        System.out.println("Inserire dato da cifrare: ");
        String data = sc.nextLine();
        sc.close();

        String enc = c.encrypt(data);
        System.out.println("Dato cifrato: " + enc);
        System.out.println("Dato decifrato: " + c.decrypt(enc));
    }

}