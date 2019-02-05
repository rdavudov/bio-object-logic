package com.linkedlogics.bio.compression;

import java.util.Arrays;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * LZ4 implementation of {@link com.linkedlogics.bio.compression.BioCompressor}
 * @author rdavudov
 *
 */
public class BioLZ4Compressor implements BioCompressor {
	private LZ4Compressor compressor ;
	private LZ4FastDecompressor decompressor ;
	
	public BioLZ4Compressor() {
		LZ4Factory factory = LZ4Factory.fastestInstance();
		compressor = factory.fastCompressor();
		decompressor = factory.fastDecompressor();
	}
	
	/**
	 * Compresses bytes 
	 * @param data
	 * @return
	 */
	public byte[] compress(byte[] data) {
		int maxCompressedLength = compressor.maxCompressedLength(data.length);
		byte[] compressed = new byte[maxCompressedLength];
		int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, maxCompressedLength);
		byte[] compacted = Arrays.copyOf(compressed, compressedLength) ;
		return compacted ;
	}

	/**
	 * Decompressed bytes 
	 * @param data
	 * @param originalLength
	 * @return
	 */
	public byte[] decompress(byte[] data, int originalLength) {
		byte[] decompressed = new byte[originalLength];
		decompressor.decompress(data, 0, decompressed, 0, originalLength);
		return decompressed ;
	}
	
	public static void main(String[] args) {
		BioLZ4Compressor c = new BioLZ4Compressor() ;
		
		byte[] data = new byte[]{-15, 8, 0, 1, 0, 0, 0, 2, 1, 0, 3, 0, 0, 0, -25, -99, 76, 117, -115, 0, 11, 0, 0, 0, 7, 23, 0, 0, 2, 0, -16, 14, -76, 0, 4, 0, 0, 0, 93, 36, 92, -55, 45, 0, 6, 0, 0, 39, 20, 0, 5, 0, 0, 0, 0, 0, 10, 0, 0, 0, 10} ;
		System.out.println(data.length);
		byte[] compressed = c.compress(data) ;
		System.out.println(compressed.length);
		System.out.println(Arrays.toString(c.decompress(compressed, data.length))) ;
		
		
	}
}
