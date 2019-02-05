package com.linkedlogics.bio.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.object.BioObject;
import com.linkedlogics.bio.utility.ByteUtility;

/**
 * Fast streams are not creating additional byte arrays instead they are using original one
 * @author rdavudov
 *
 */
public class BiFastStream extends InputStream {
	private byte[] buffer;
	
	private int pos;
	private int length;
	private boolean isLengthAsInt ;
	
	public BiFastStream(byte[] data) {
		buffer = data ;
		length = data.length ;
		pos = 0 ;
	}
	
	public BiFastStream(byte[] data, int offset, int len) {
		buffer = data ;
		length = offset + len ;
		pos = offset ;
	}
	
	public boolean isLengthAsInt() {
		return isLengthAsInt;
	}

	public void setLengthAsInt(boolean isLengthAsInt) {
		this.isLengthAsInt = isLengthAsInt;
	}

	@Override
	public int available() throws IOException {
		return length - pos;
	}
	
	public int readObjCode() {
		int code = ByteUtility.bytesToShort(buffer, pos) ;
		pos+=2 ;
		return code ;
	}
	
	public int readObjVersion() {
		int code = ByteUtility.bytesToShort(buffer, pos) ;
		pos+=2 ;
		return code ;
	}
	
	public int readTagCode() {
		int code = ByteUtility.bytesToShort(buffer, pos) ;
		pos+=2 ;
		return code ;
	}
	
	@Override
	public int read() throws IOException {
		int value = ByteUtility.bytesToInt(buffer, pos) ;
		pos+=4 ;
		return value ;
	}
	
	@Override
	public int read(byte[] array) throws IOException {
		System.arraycopy(buffer, pos, array, 0, array.length);
		pos+=array.length ;
		return array.length ;
	}
	
	public int readLength() {
		if (isLengthAsInt) {
			int len = ByteUtility.bytesToInt(buffer, pos) ;
			pos+=4 ;
			return len ;
		} else {
			int len = ByteUtility.bytesToShort(buffer, pos) ;
			pos+=2 ;
			return len ;
		}
	}
	
	public int readIntLength() {
		int len = ByteUtility.bytesToInt(buffer, pos) ;
		pos+=4 ;
		return len ;
	}
	
	public Long readLong() {
		long value = ByteUtility.bytesToLong(buffer, pos) ;
		pos+=8 ;
		return value ;
	}
	
	public int readInt() {
		int value = ByteUtility.bytesToInt(buffer, pos) ;
		pos+=4 ;
		return value ;
	}
	
	public short readShort() {
		short value = ByteUtility.bytesToShort(buffer, pos) ;
		pos+=2 ;
		return value ;
	}
	
	public byte readByte() {
		return buffer[pos++] ;
	}
	
	public boolean readBoolean() {
		return readByte() == 1 ;
	}
	
	public double readDouble() {
		double value = ByteUtility.bytesToDouble(buffer, pos) ;
		pos+=8 ;
		return value ;
	}
	
	public float readFloat() {
		float value = ByteUtility.bytesToFloat(buffer, pos) ;
		pos+=4 ;
		return value ;
	}
	
	public String readAsciiString() {
		int length = readLength() ;
		String value = ByteUtility.bytesToAsciiString(buffer, pos, length) ;
		pos+=length ;
		return value ;
	}
	
	public String readUtfString() {
		int length = readLength() ;
		String value = ByteUtility.bytesToUtfString(buffer, pos, length) ;
		pos+=length ;
		return value ;
	}
	
	public Object readObject() {
		int length = readLength() ;
		try {
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(buffer, pos, length) ;
			ObjectInputStream in = new ObjectInputStream(bytesIn) ;
			Object object = in.readObject() ;
			in.close();
			pos+=length ;
			return object ;
		} catch (Throwable e) {
			throw new RuntimeException(e) ; 
		}
	}
	
	public BioObject readProperties(BioObj obj) {
		int size = readLength() ;
		BioObject properties = new BioObject(0) ;
		for (int i = 0; i < size; i++) {
			String key = readAsciiString() ;
			BioType primitiveType = BioType.getType(readByte()) ;
			boolean isArray = readByte() == 1 ;
			Object value = null ;
			switch (primitiveType) {
			case Long:
				if (isArray) {
					value = readLongArray() ;
				} else {
					value = readLong() ;
				}
				break;
			case Integer:
				if (isArray) {
					value = readIntArray() ;
				} else {
					value = readInt() ;
				}
				break;
			case Double:
				if (isArray) {
					value = readDoubleArray() ;
				} else {
					value = readDouble() ;
				}
				break;
			case Boolean:
				if (isArray) {
					value = readBooleanArray() ;
				} else {
					value = readBoolean() ;
				}
				break;
			case Byte:
				if (isArray) {
					value = readByteArray() ;
				} else {
					value = readByte() ;
				}
				break;
			case Short:
				if (isArray) {
					value = readShortArray() ;
				} else {
					value = readShort() ;
				}
				break;
			case Float:
				if (isArray) {
					value = readFloatArray() ;
				} else {
					value = readFloat() ;
				}
				break;
			case String:
				if (isArray) {
					value = readAsciiStringArray() ;
				} else {
					value = readAsciiString() ;
				}
				break;
			case Unknown:
				break;
			}
			properties.put(key, value) ;
		}
		return properties ;
	}
	
	public byte[] readBioBytes() {
		int length = readIntLength() ;
		byte[] bytes = read(length) ;
		return bytes ;
	}
	
	public Long[] readLongArray() {
		int length = readLength() ;
		Long[] longArray = new Long[length] ;
		for (int i = 0; i < length; i++) {
			longArray[i] = readLong() ;
		}
		return longArray ;
	}
	
	public Integer[] readIntArray() {
		int length = readLength() ;
		Integer[] intArray = new Integer[length] ;
		for (int i = 0; i < length; i++) {
			intArray[i] = readInt() ;
		}
		return intArray ;
	}
	
	public Byte[] readByteArray() {
		int length = readLength() ;
		Byte[] byteArray = new Byte[length] ;
		for (int i = 0; i < length; i++) {
			byteArray[i] = readByte() ;
		}
		return byteArray ;
	}
	
	public Short[] readShortArray() {
		int length = readLength() ;
		Short[] shortArray = new Short[length] ;
		for (int i = 0; i < length; i++) {
			shortArray[i] = readShort() ;
		}
		return shortArray ;
	}
	
	public byte[] readPrimitiveByteArray() {
		int length = readIntLength() ;
		byte[] byteArray = new byte[length] ;
		for (int i = 0; i < length; i++) {
			byteArray[i] = readByte() ;
		}
		return byteArray ;
	}
	
	public Boolean[] readBooleanArray() {
		int length = readLength() ;
		Boolean[] booleanArray = new Boolean[length] ;
		for (int i = 0; i < length; i++) {
			booleanArray[i] = readByte() == 1 ;
		}
		return booleanArray ;
	}
	
	public Double[] readDoubleArray() {
		int length = readLength() ;
		Double[] doubleArray = new Double[length] ;
		for (int i = 0; i < length; i++) {
			doubleArray[i] = readDouble() ;
		}
		return doubleArray ;
	}
	
	public Float[] readFloatArray() {
		int length = readLength() ;
		Float[] floatArray = new Float[length] ;
		for (int i = 0; i < length; i++) {
			floatArray[i] = readFloat() ;
		}
		return floatArray ;
	}
	
	public String[] readAsciiStringArray() {
		int size = readLength() ;
		String[] array = new String[size] ;
		for (int i = 0; i < array.length; i++) {
			array[i] = readAsciiString() ;
		}
		return array ;
	}
	
	public String[] readUtfStringArray() {
		int size = readLength() ;
		String[] array = new String[size] ;
		for (int i = 0; i < array.length; i++) {
			array[i] = readUtfString() ;
		}
		return array ;
	}
	
	public Object[] readObjectArray() {
		int size = readLength() ;
		Object[] array = new Object[size] ;
		for (int i = 0; i < array.length; i++) {
			array[i] = readObject() ;
		}
		return array ;
	}
	
	public void skipLength() {
		skip(2) ;
	}
	
	public void skipLong() {
		skip(8) ;
	}
	
	public void skipInt() {
		skip(4) ;
	}
	
	public void skipShort() {
		skip(2) ;
	}

	public void skipByte() {
		skip(1) ;
	}
	
	public void skipBoolean() {
		skip(1) ;
	}
	
	public void skipDouble() {
		skip(8) ;
	}
	
	public void skipAsciiString() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipUtfString() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipObject() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipLongArray() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipIntArray() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipByteArray() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipBioBytes() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipBooleanArray() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipDoubleArray() {
		int length = readLength() ;
		skip(length) ;
	}
	
	public void skipAsciiStringArray() {
		int size = readLength() ;
		for (int i = 0; i < size; i++) {
			skipAsciiString() ;
		}
	}
	
	public void skipUtfStringArray() {
		int size = readLength() ;
		for (int i = 0; i < size; i++) {
			skipUtfString() ;
		}
	}
	
	public void skipObjectArray() {
		int size = readLength() ;
		for (int i = 0; i < size; i++) {
			skipObject() ;
		}
	}
	
	public void skipProperties() {
		int size = readLength() ;
		for (int i = 0; i < size; i++) {
			skipAsciiString() ;
			BioType primitiveType = BioType.getType(readByte()) ;
			boolean isArray = readByte() == 1 ;
			switch (primitiveType) {
			case Long:
				if (isArray) {
					skipLongArray() ;
				} else {
					skipLong() ;
				}
				break;
			case Integer:
				if (isArray) {
					skipIntArray() ;
				} else {
					skipInt() ;
				}
				break;
			case Double:
				if (isArray) {
					skipDoubleArray() ;
				} else {
					skipDouble() ;
				}
				break;
			case Boolean:
				if (isArray) {
					skipBooleanArray() ;
				} else {
					skipBoolean() ;
				}
				break;
			case Byte:
				if (isArray) {
					skipByteArray() ;
				} else {
					skipByte() ;
				}
				break;
			case String:
				if (isArray) {
					skipAsciiStringArray() ;
				} else {
					skipAsciiString() ;
				}
				break;
			case Unknown:
				skipAsciiString() ;
				break;
			}
		}
	}
	
	public byte[] read(int len) {
		if (this.pos + len <= length) {
			byte[] read = new byte[len] ;
			System.arraycopy(this.buffer, this.pos, read, 0, len);
			this.pos += len ;
			return read ;
		} else {
			throw new RuntimeException("missing bytes left " + (length - this.pos) + " but needed " + len + " bytes") ;
		}
	}
	
	public void skip(int len) {
		if (this.pos + len <= length) {
			this.pos += len ;
		} else {
			throw new RuntimeException("missing bytes left " + (length - this.pos) + " but needed " + len + " bytes") ;
		}
	}

	@Override
	public void close()  {
		try {
			super.close();
		} catch (IOException e) {
			throw new RuntimeException(e) ;
		}
	}
}
