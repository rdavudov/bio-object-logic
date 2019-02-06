package com.linkedlogics.bio.encryption;

import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.linkedlogics.bio.BioEncrypter;
import com.linkedlogics.bio.exception.EncryptionException;

/**
 * AES implementation of {@link com.linkedlogics.bio.BioEncrypter}
 * @author rdavudov
 *
 */
public class AESEncrypter implements BioEncrypter {
	private Cipher encrypter ;
	private Cipher decrypter ;
	
	/**
	 * Initializes encryption classes using property <b>aes_key</b> provided in properties
	 * @param properties
	 * @return
	 */
	public boolean init(HashMap<String, Object> properties) {
		String key = (String) properties.get("key") ;
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		try {
			encrypter = Cipher.getInstance("AES");
			encrypter.init(Cipher.ENCRYPT_MODE, secretKey);
			
			decrypter = Cipher.getInstance("AES");
			decrypter.init(Cipher.DECRYPT_MODE, secretKey);
			
			return true ;
		} catch (Throwable e) {
			throw new EncryptionException(e) ;
		}
	}
	/**
	 * Encrypts bytes
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(byte[] bytes) {
		try {
			return encrypter.doFinal(bytes);
		} catch (Throwable e) {
			throw new EncryptionException(e) ;
		}
	}
	/**
	 * Decrypts bytes
	 * @param bytes
	 * @return
	 */
	public byte[] decrypt(byte[] bytes) {
		try {
			return decrypter.doFinal(bytes);
		} catch (Throwable e) {
			throw new EncryptionException(e) ;
		}
	}
}
