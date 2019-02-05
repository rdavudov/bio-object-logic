package com.linkedlogics.bio.encryption;

import java.util.HashMap;

/**
 * Interface for encryption of bio objects
 * @author rdavudov
 *
 */
public interface BioEncrypter {
	/**
	 * Initializes encryption classes using properties such as secret key etc.
	 * @param properties
	 * @return
	 */
	public boolean init(HashMap<String, Object> properties) ;
	/**
	 * Encrypts bytes
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(byte[] bytes) ;
	/**
	 * Decrypts bytes
	 * @param bytes
	 * @return
	 */
	public byte[] decrypt(byte[] bytes) ;
}
