package encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Symmetric encryption/decryption utility using DES algorithm
 * Dependency class for password manager
 */
public class MasterEncryptor {

    public static void main(String[] args) {
    }

    // static 8-byte salt sequence
    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    /**
     * Determine encryption algorithm and generate secret key from given password
     *
     * @param pass password
     * @param decryptMode true if decrypting
     * @return Cipher object
     * @throws GeneralSecurityException
     */
    private static Cipher makeCipher(String pass, Boolean decryptMode) throws GeneralSecurityException{
        // use the KeyFactory library to derive the corresponding key from the given password
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        // create parameters from the salt and an arbitrary number of iterations
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 42);

        // set up the cipher
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

        // set the cipher mode to decryption or encryption
        if (decryptMode){
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
        }

        return cipher;
    }

    /**
     * Encrypts given file with key
     *
     * @param fileName
     * @param key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static void encryptFile(String fileName, String key)
            throws IOException, GeneralSecurityException{
        byte[] decData;
        byte[] encData;
        File inFile = new File(fileName);

        // generate the cipher using key
        Cipher cipher = MasterEncryptor.makeCipher(key, true);

        // read in the file:
        FileInputStream inStream = new FileInputStream(inFile);

        // figure out how many bytes are padded
        int blockSize = 8;
        int paddedCount = blockSize - ((int)inFile.length()  % blockSize );

        // figure out full size including padding
        int padded = (int)inFile.length() + paddedCount;

        decData = new byte[padded];

        inStream.read(decData);
        inStream.close();

        // write out padding bytes as per PKCS5 algorithm
        for (int i = (int)inFile.length(); i < padded; ++i) {
            decData[i] = (byte)paddedCount;
        }

        // encrypt the file data
        encData = cipher.doFinal(decData);


        // write the encrypted data to a new file
        FileOutputStream outStream = new FileOutputStream(new File(fileName));
        outStream.write(encData);
        outStream.close();
    }


    /**
     * Decrypts given file with key
     *
     * @param fileName
     * @param key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void decryptFile(String fileName, String key)
            throws GeneralSecurityException, IOException{
        byte[] encData;
        byte[] decData;
        File inFile = new File(fileName);

        // generate the cipher using key
        Cipher cipher = MasterEncryptor.makeCipher(key, false);

        // read in the file
        FileInputStream inStream = new FileInputStream(inFile );
        encData = new byte[(int)inFile.length()];

        inStream.read(encData);
        inStream.close();

        // decrypt the file data
        decData = cipher.doFinal(encData);

        // figure out how much padding to remove
        int padCount = (int)decData[decData.length - 1];

        // check that padCount bytes at the end have same value
        if (padCount >= 1 && padCount <= 8) {
            decData = Arrays.copyOfRange( decData , 0, decData.length - padCount);
        }

        // write the decrypted data to a new file
        FileOutputStream target = new FileOutputStream(new File(fileName));
        target.write(decData);
        target.close();
    }
}