package com.linkedlogics.bio;

/**
 * Interface for compression of bio objects
 * @author rdavudov
 *
 */
public interface BioCompressor {
	/**
	 * Compresses bytes 
	 * @param data
	 * @return
	 */
	public byte[] compress(byte[] data) ;
	
	/**
	 * Decompressed bytes 
	 * @param data
	 * @param originalLength
	 * @return
	 */
	public byte[] decompress(byte[] data, int originalLength) ;
}
