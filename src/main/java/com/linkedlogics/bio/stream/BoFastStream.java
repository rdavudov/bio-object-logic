package com.linkedlogics.bio.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.utility.ByteUtility;
import com.linkedlogics.bio.utility.ConversionUtility;

/**
 * Why it is fast ? because it only allocates one big byte array and when converting values into bytes just passes
 * this big array and appropriate index to mainly ByteUtility class which just adds new bytes without creating new
 * byte array each time for each value
 * @author rdavudov
 *
 */

public class BoFastStream extends OutputStream {
	private byte[] buffer;
	
	private int pos;
	private int length;
	private boolean isLengthAsInt ;
	
	public BoFastStream() {
		length = 256 ;
		buffer = new byte[length] ;
	}
	
	public BoFastStream(int length) {
		this.length = length ;
		buffer = new byte[length] ;
	}
	
	public boolean isLengthAsInt() {
		return isLengthAsInt;
	}

	public void setLengthAsInt(boolean isLengthAsInt) {
		this.isLengthAsInt = isLengthAsInt;
	}

	public byte[] getBytes() {
		if (this.pos == this.length)
			return this.buffer;
		else {
			byte[] res = new byte[this.pos];
			System.arraycopy(this.buffer, 0, res, 0, this.pos);
			return res;
		}
	}
	
	public int getSize() {
		return pos ;
	}
	
	public void reset() {
		pos = 0 ;
	}
	
	private void checkBuffer(int byteCount) {
		if (this.pos + byteCount > this.length) {
			int newLength = this.length * 2;
			if (newLength < this.pos + byteCount)
				newLength = this.pos + byteCount + this.length;
			byte[] newBuf = new byte[newLength];
			System.arraycopy(this.buffer, 0, newBuf, 0, this.buffer.length);
			this.buffer = newBuf;
			this.length = newLength;
		}
	}
	
	@Override
	public void write(int b) {
		this.checkBuffer(1);
		this.buffer[this.pos++] = (byte)b;
	}

	@Override
	public void write(byte[] b, int off, int len) {
		this.checkBuffer(len);
		System.arraycopy(b, off, this.buffer, this.pos, len);
		this.pos += len;
	}
	
	@Override
	public void write(byte[] b) {
		this.write(b, 0, b.length);
	}

	public void writeTag(BioTag tag) {
		checkBuffer(2) ;
		ByteUtility.shortToBytes(this.buffer, this.pos, (short) tag.getCode()) ;
		this.pos += 2 ;
	}
	
	public void writeLength(int length) {
		if (isLengthAsInt) {
			checkBuffer(4) ;
			ByteUtility.intToBytes(this.buffer, this.pos, length) ;
			this.pos += 4 ;
		} else {
			checkBuffer(2) ;
			ByteUtility.shortToBytes(this.buffer, this.pos, (short) length) ;
			this.pos += 2 ;
		}
	}
	
	public void writeIntLength(int length) {
		checkBuffer(4) ;
		ByteUtility.intToBytes(this.buffer, this.pos, length) ;
		this.pos += 4 ;
	}
	
	public void writeByte(BioTag tag, byte value) {
		writeTag(tag) ;
		write(value) ;
	}
	
	public void writeFloat(BioTag tag, float value) {
		writeTag(tag) ;
		writeFloat(value) ;
	}
	
	public void writeShort(BioTag tag, short value) {
		writeTag(tag) ;
		writeShort(value) ;
	}
	
	public void writeByte(Byte value) {
		checkBuffer(1) ;
		this.buffer[this.pos++] = value.byteValue() ;
	}
	
	public void writeInt(BioTag tag, int value) {
		writeTag(tag) ;
		writeInt(value) ;
	}
	
	public void writeInt(int value) {
		checkBuffer(4) ;
		ByteUtility.intToBytes(this.buffer, this.pos, value) ;
		this.pos+=4 ;
	}
	
	public void writeShort(int value) {
		checkBuffer(2) ;
		ByteUtility.shortToBytes(this.buffer, this.pos, (short) value) ;
		this.pos+=2 ;
	}
	
	public void writeFloat(float value) {
		checkBuffer(4) ;
		ByteUtility.floatToBytes(this.buffer, this.pos, (float) value) ;
		this.pos+=4 ;
	}
	
	public void writeLong(BioTag tag, long value) {
		writeTag(tag) ;
		writeLong(value) ;
	}
	
	public void writeLong(long value) {
		checkBuffer(8) ;
		ByteUtility.longToBytes(this.buffer, this.pos, value) ;
		this.pos+=8 ;
	}
	
	public void writeLongArray(BioTag tag, Long[] value) {
		writeTag(tag) ;
		writeLongArray(value) ;
	}
	
	public void writeLongArray(Long[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeLong(value[i]) ;
		}
	}
	
	public void writeDoubleArray(BioTag tag, Double[] value) {
		writeTag(tag) ;
		writeDoubleArray(value) ;
	}
	
	public void writeDoubleArray(Double[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeDouble(value[i]) ;
		}
	}
	
	public void writeIntArray(BioTag tag, Integer[] value) {
		writeTag(tag) ;
		writeIntArray(value) ;
	}
	
	public void writeIntArray(Integer[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeInt(value[i]) ;
		}
	}
	
	public void writeByteArray(BioTag tag, Byte[] value) {
		writeTag(tag) ;
		writeByteArray(value) ;
	}
	
	public void writeShortArray(BioTag tag, Short[] value) {
		writeTag(tag) ;
		writeShortArray(value) ;
	}
	
	public void writeFloatArray(BioTag tag, Float[] value) {
		writeTag(tag) ;
		writeFloatArray(value) ;
	}
	
	public void writeBioBytes(BioTag tag, byte[] value) {
		writeTag(tag) ;
		writeBioBytes(value) ;
	}
	
	public void writeByteArray(Byte[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeByte(value[i].byteValue()) ;
		}
	}
	
	public void writeShortArray(Short[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeShort(value[i].shortValue()) ;
		}
	}
	
	public void writeFloatArray(Float[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeFloat(value[i].floatValue()) ;
		}
	}
	
	public void writeByteArray(byte[] value) {
		writeIntLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeByte(value[i]) ;
		}
	}
	
	public void writeBioBytes(byte[] value) {
		writeIntLength(value.length) ;
		write(value) ;
	}
	
	public void writeBooleanArray(BioTag tag, Boolean[] value) {
		writeTag(tag) ;
		writeBooleanArray(value) ;
	}
	
	public void writeBooleanArray(Boolean[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeBoolean(value[i]) ;
		}
	}
	
	public void writeDouble(BioTag tag, double value) {
		writeTag(tag) ;
		writeDouble(value) ;
	}
	
	public void writeDouble(double value) {
		checkBuffer(8) ;
		ByteUtility.doubleToBytes(this.buffer, this.pos, value) ;
		this.pos+=8 ;
	}
	
	public void writeBoolean(BioTag tag, boolean value) {
		writeTag(tag) ;
		writeBoolean(value) ;
	}
	
	public void writeBoolean(boolean value) {
		writeByte((byte) (value ? 1 : 0)) ;
	}
	
	public void writeAsciiString(BioTag tag, String value) {
		writeTag(tag) ;
		writeAsciiString(value) ;
	}
	
	public void writeAsciiString(String value) {
		if (value == null) {
			writeLength(0) ;
			return ;
		}
		writeLength(value.length()) ;
		checkBuffer(value.length()) ;
		ByteUtility.asciiStringToBytes(value, buffer, pos) ;
		pos+=value.length();
	}
	
	public void writeAsciiStringArray(BioTag tag, String[] value) {
		writeTag(tag) ;
		writeAsciiStringArray(value) ;
	}
	
	public void writeAsciiStringArray(String[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeAsciiString(value[i]) ;
		}
	}
	
	public void writeUtfString(BioTag tag, String value) {
		writeTag(tag) ;
		writeUtfString(value) ;
	}
	
	public void writeUtfString(String value) {
		if (value == null) {
			writeLength(0) ;
			return ;
		}
		byte[] bytes = ByteUtility.utfStringToBytes(value) ;
		writeLength(bytes.length) ;
		write(bytes) ;
	}
	
	public void writeUtfStringArray(BioTag tag, String[] value) {
		writeTag(tag) ;
		writeUtfStringArray(value) ;
	}
	
	public void writeUtfStringArray(String[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeUtfString(value[i]) ;
		}
	}
	
	public void writeObject(BioTag tag, Object object) {
		writeTag(tag) ;
		writeObject(object) ;
	}
	
	public void writeObject(Object object) {
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream() ;
			ObjectOutputStream out = new ObjectOutputStream(bytesOut) ;
			out.writeObject(object) ;
			out.flush() ;
			out.close() ;
			
			byte[] bytes = bytesOut.toByteArray() ;
			writeLength(bytes.length) ;
			write(bytes) ;
		} catch (IOException e) {
			throw new RuntimeException(e) ;
		}
	}
	
	public void writeObjectArray(BioTag tag, Object[] value) {
		writeTag(tag) ;
		writeObjectArray(value) ;
	}
	
	public void writeObjectArray(Object[] value) {
		writeLength(value.length) ;
		for (int i = 0; i < value.length; i++) {
			writeObject(value[i]) ;
		}
	}
	
	public void writeProperties(BioObj object, BioTag tag, BioObject properties) {
		writeTag(tag) ;
		writeProperties(object, properties) ;
	}
	
	public void writeProperties(final BioObj object, BioObject properties) {
		writeLength(properties.size()) ;
		for(Entry<String, Object> e : properties.entries()) {
			String key = e.getKey() ;
			Object value = e.getValue() ;
			boolean isArray = value.getClass().isArray() ;
			BioType primitiveType = ConversionUtility.getType(value) ;
			writeAsciiString(key) ;
			write((byte)(primitiveType.value())) ;
			write(isArray ? (byte) 1 : (byte) 0) ;
			switch (primitiveType) {
			case Long:
				if (isArray) {
					writeLongArray((Long[]) value) ;
				} else {
					writeLong((Long) value) ;
				}
				break;
			case Integer:
				if (isArray) {
					writeIntArray((Integer[]) value) ;
				} else {
					writeInt((Integer) value) ;
				}
				break;
			case Double:
				if (isArray) {
					writeDoubleArray((Double[])value) ;
				} else {
					writeDouble((Double)value) ;
				}
				break;
			case Boolean:
				if (isArray) {
					writeBooleanArray((Boolean[])value) ;
				} else {
					writeBoolean((Boolean)value) ;
				}
				break;
			case Byte:
				if (isArray) {
					writeByteArray((Byte[]) value) ;
				} else {
					writeByte(((Byte) value).byteValue()) ;
				}
				break;
			case Short:
				if (isArray) {
					writeShortArray((Short[]) value) ;
				} else {
					writeShort(((Short) value).shortValue()) ;
				}
				break;
			case Float:
				if (isArray) {
					writeFloatArray((Float[]) value) ;
				} else {
					writeFloat(((Float) value).floatValue()) ;
				}
				break;
			case String:
				if (isArray) {
					writeAsciiStringArray((String[]) value) ;
				} else {
					writeAsciiString((String) value) ;
				}
				break;
			case Unknown:
				break;
			}
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
