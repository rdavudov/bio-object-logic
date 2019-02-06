package com.linkedlogics.bio.encryption;

/**
 * Interface for encryption of bio objects
 * @author rdavudov
 *
 */
public interface BioEncrypter {
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
