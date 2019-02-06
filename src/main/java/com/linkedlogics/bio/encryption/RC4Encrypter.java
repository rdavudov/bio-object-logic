package com.linkedlogics.bio.encryption;

import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.linkedlogics.bio.BioEncrypter;
import com.linkedlogics.bio.exception.EncryptionException;

/**
 * RC4 implementation of {@link com.linkedlogics.bio.BioEncrypter}
 * @author rdavudov
 *
 */
public class RC4Encrypter implements BioEncrypter {
	private Cipher encrypter ;
	private Cipher decrypter ;
	
	/**
	 * Initializes encryption classes using property <b>aes_key</b> provided in properties
	 * @param properties
	 * @return
	 */
	public boolean init(HashMap<String, Object> properties) {
		String key = (String) properties.get("key") ;
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "RC4");
		try {
			encrypter = Cipher.getInstance("RC4");
			encrypter.init(Cipher.ENCRYPT_MODE, secretKey);
			
			decrypter = Cipher.getInstance("RC4");
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
