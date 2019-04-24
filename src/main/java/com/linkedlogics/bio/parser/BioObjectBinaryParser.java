package com.linkedlogics.bio.parser;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.linkedlogics.bio.BioCompressor;
import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEncrypter;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.stream.BiFastStream;
import com.linkedlogics.bio.stream.BoFastStream;
import com.linkedlogics.bio.utility.ByteUtility;
import com.linkedlogics.bio.utility.ConversionUtility;
import com.linkedlogics.bio.utility.XMLUtility;

/**
 * This is serializer/deserializer class also includes binary compression and encryption if set. It can serialize data which is an array or list or
 * single bio object.
 * @author rdavudov
 *
 */
public class BioObjectBinaryParser {
	/**
	 * indicates whether binary bytes contain compressed data
	 */
	public static final int FLAG_COMPRESSED = 0x01 ;
	/**
	 * indicates whether binary bytes contain an array
	 */
	public static final int FLAG_ARRAY = 0x02;     
	/**
	 * indicates whether binary bytes contain a list
	 */
	public static final int FLAG_LIST = 0x04;
	/**
	 * indicates whether binary bytes contain encrypted data
	 */
	public static final int FLAG_ENCRYPTED = 0x08 ;
	/**
	 * indicates whether binary bytes contain xml parseable bio object
	 */
	public static final int FLAG_XML = 0x10 ;
	
	/**
	 * indicates weather dictionary also needs to be serialized
	 */
	public static final int FLAG_PORTABLE = 0x20 ;
	
	private boolean isCompressed ;
	private boolean isEncrypted ;
	private boolean isLossless ;
	private BioCompressor compressor = BioDictionary.getCompressor() ;
	private BioEncrypter encrypter = BioDictionary.getEncrypter() ;
	private BioObjectXmlParser xmlParser = new BioObjectXmlParser() ;
	private boolean isValidated ;
	
	private HashMap<Integer, BioTag> codeMap = new HashMap<Integer, BioTag>() ;
	private HashMap<String, BioTag> nameMap =new HashMap<String, BioTag>() ;
	private int codeCounter ;
	
	/**
	 * Indicates whether parser will use compression
	 * @return
	 */
	public boolean isCompressed() {
		return isCompressed;
	}
	/**
	 * Changes compression enabled flag
	 * @param isCompressed
	 */
	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}
	/**
	 * Indicates whether parser will use encryption
	 * @return
	 */
	public boolean isEncrypted() {
		return isEncrypted;
	}
	/**
	 * Changes encryption enabled flag
	 * @param isEncrypted
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	
		
	/**
	 * Indicates whether parser will validate unknown objects and throw an exception or just skip them
	 * @return
	 */
	public boolean isValidated() {
		return isValidated;
	}
	
	/**
	 * Changes validated enabled flag
	 * @param isValidated
	 */
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}
	
	public boolean isLossless() {
		return isLossless;
	}
	
	public void setLossless(boolean isLossless) {
		this.isLossless = isLossless;
	}
	
	/**
	 * Encodes bio object or bio object array or list to binary bytes
	 * @param object
	 * @return
	 */
	public byte[] encode(Object object) {
		return encode(object, isCompressed) ;
	}
	
	/**
	 * Encodes bio object with forced compression
	 * @param object
	 * @param isCompressed
	 * @return
	 */
	public byte[] encode(Object object, boolean isCompressed) {
		return encode(object, isCompressed, isLossless, isEncrypted) ;
	}
	
	/**
	 * Encodes bio object with forced compression and lossless
	 * @param object
	 * @param isCompressed
	 * @param isLossless
	 * @return
	 */
	public byte[] encode(Object object, boolean isCompressed, boolean isLossless) {
		return encode(object, isCompressed, isLossless, isEncrypted) ;
	}
	
	/**
	 * Encodes bio object with forced compression and encryption
	 * @param object
	 * @param isCompressed
	 * @param isLossless
	 * @param isEncrypted
	 * @return
	 */
	public byte[] encode(Object object, boolean isCompressed, boolean isLossless, boolean isEncrypted) {
		if (object == null) {
			// nothing to encode
			return null ;
		}
		
		final BoFastStream stream = createOutputStream();
		int flag = 0 ;
		// set up necessary flags need while decoding
		if (isLossless) {
			flag = flag | FLAG_XML ;
		}
		
		if (isEncrypted && encrypter != null) {
			flag = flag | (isEncrypted ? FLAG_ENCRYPTED : 0) ;
		}
		
		if (isCompressed && compressor != null) {
			flag = flag | (isCompressed ? FLAG_COMPRESSED : 0) ;
		}
		
		nameMap.clear(); 
		codeCounter = 0 ;
		
		byte[] encoded = null ;
		if (object instanceof BioObject[]) {
			flag = flag | FLAG_ARRAY ;
			encoded = encodeArray((BioObject[]) object, isLossless) ;
		} else if (object instanceof List) {
			flag = flag | FLAG_LIST ;
			encoded = encodeList((List<BioObject>) object, isLossless) ;
		} else if (object instanceof BioObject) {
			encoded = encode((BioObject) object, isLossless) ;
		} else {
			throw new ParserException("only bio objects can be serializer or their arrays or collections " + object.getClass().getName() + " is not a bio object") ;
		}
		
		// if something happened and we got nothing encoded
		if (encoded == null) {
			return null ;
		}
		
		if (isEncrypted && encrypter != null) {
			encoded = encrypter.encrypt(encoded) ;
		}
		stream.write(flag);
		
		stream.writeShort(nameMap.size());
		for (Entry<String, BioTag> tag : nameMap.entrySet()) {
			stream.writeAsciiString(tag.getKey());
			stream.writeShort(tag.getValue().getCode());
		}
		
		if (isCompressed && compressor != null) {
			byte[] compressed = compressor.compress(encoded) ;
			stream.writeInt(encoded.length); // original length in 4 bytes
			stream.write(compressed);
		} else {
			stream.write(encoded);
		}
		
		stream.close(); 
		return stream.getBytes();
	}
	
	/**
	 * Encodes bio object 
	 * @param bio
	 * @param isLossless
	 * @return
	 */
	private byte[] encode(BioObject bio, boolean isLossless) {
		if (isLossless) {
			return XMLUtility.toXml(bio).getBytes() ;
		} else {
			return writeBio(bio);
		}
	}
	
	/**
	 * Encodes array of bio objects
	 * @param array
	 * @return
	 */
	private byte[] encodeArray(BioObject[] array, boolean isLossless) {
		final BoFastStream stream = createOutputStream();
		stream.write(ByteUtility.shortToBytes((short) array.length));
		for (int i = 0; i < array.length; i++) {
			byte[] bytes = encode(array[i], isLossless);
			if (bytes != null) {
				stream.writeBioBytes(bytes);
			}
		}
		stream.close();
		return stream.getBytes();
	}
	
	/**
	 * Encodes list of bio objects
	 * @param list
	 * @return
	 */
	private byte[] encodeList(List<BioObject> list, boolean isLossless) {
		final BoFastStream stream = createOutputStream();
		stream.write(ByteUtility.shortToBytes((short) list.size()));
		for (int i = 0; i < list.size(); i++) {
			byte[] bytes = encode(list.get(i), isLossless);
			if (bytes != null) {
				stream.writeBioBytes(bytes);
			}
		}
		stream.close();
		return stream.getBytes();
	}
	
	/**
	 * Decodes bytes to bio object, array or list
	 * @param bytes
	 * @return
	 */
	public Object decode(byte[] bytes) {
		if (bytes == null) {
			return null ;
		}
		BiFastStream stream = createInputStream(bytes);
		int flag = stream.readByte() ;
		boolean isCompressed = (int) (flag & FLAG_COMPRESSED) > 0 ;
		boolean isArray = (int) (flag & FLAG_ARRAY) > 0 ;
		boolean isList = (int) (flag & FLAG_LIST) > 0 ;
		boolean isLossless = (int) (flag & FLAG_XML) > 0 ;
		boolean isEncrypted = (int) (flag & FLAG_ENCRYPTED) > 0 ;
		
		codeMap.clear(); 
		codeCounter = 0 ;
		
		int codeCount = stream.readShort() ;
		for (int i = 0; i < codeCount; i++) {
			String key = stream.readAsciiString() ;
			int code = stream.readShort() ;
			BioTag tag = new BioTag() ;
			tag.setCode(code);
			tag.setName(key);
			codeMap.put(code, tag) ;
		}
		try {
			int originalLength = 0 ;
			if (isCompressed) {
				originalLength = stream.readInt() ;
			}
			byte[] decoded = new byte[stream.available()] ;
			stream.read(decoded);
			if (isCompressed) {
				decoded = compressor.decompress(decoded, originalLength) ;
			}
			if (isEncrypted) {
				decoded = encrypter.decrypt(decoded) ;
			}
			
			if (isArray) {
				return decodeArray(decoded, isLossless) ;
			} else if (isList) {
				return decodeList(decoded, isLossless) ;
			} else {
				return decode(decoded, isLossless) ;
			}
			
		} catch (ParserException e) {
			throw e ;
		} catch (Throwable e) {
			throw new ParserException(e) ;
		}
	}
	
	/**
	 * Decodes bio object
	 * @param decoded
	 * @param isLossless
	 * @return
	 */
	private BioObject decode(byte[] decoded, boolean isLossless) {
		if (isLossless) {
			return xmlParser.parse(new ByteArrayInputStream(decoded)) ;
		} else {
			return readBio(decoded) ;
		}
	}
	
	/**
	 * Decodes list of bio objects
	 * @param bytes
	 * @param isLossless
	 * @return
	 */
	private List<BioObject> decodeList(byte[] bytes, boolean isLossless) {
		BiFastStream stream = createInputStream(bytes);
		try {
			int length = stream.readShort();
			
			ArrayList<BioObject> list = new ArrayList<BioObject>(length) ;
			
			for (int i = 0; i < length; i++) {
				byte[] bioBytes = stream.readBioBytes();
				BioObject object = decode(bioBytes, isLossless) ;
				if (object != null) {
					list.add(object) ;
				}
			}
			
			return list ;
		} catch(Throwable e) {
			throw new ParserException(e) ;
		}
	}
	
	/**
	 * Decodes array of bio objects
	 * @param bytes
	 * @param isLossless
	 * @return
	 */
	private BioObject[] decodeArray(byte[] bytes, boolean isLossless) {
		List<BioObject> list = decodeList(bytes, isLossless) ;
		
		if (list.size() > 0) {
			Class bioClass = list.get(0).getClass() ;
			// we try to find a super class that will cover all bio objects
			// for example A, B -> A, C -> B
			// if we have list of A, A, B, B, C, C then result will be A because only covers all instances
			// if we had list of C, C, B, B, C, C then result will be B because only covers all instances
			while (bioClass != BioObject.class) {
				boolean isFine = true ;
				for (int i = 1; i < list.size(); i++) {
					if (!bioClass.isAssignableFrom(list.get(i).getClass())) {
						isFine = false ;
						break ;
					}
				}
				if (isFine) {
					break ;
				}
				bioClass = bioClass.getSuperclass() ;
			}
			
			BioObject[] array = (BioObject[]) Array.newInstance(bioClass, list.size());
			list.toArray(array) ;
			
			return array;	
		} else {
			return null ;
		}
	}

	/**
	 * Writes bio tags and obj information to bytes
	 * @param bio
	 * @return
	 */
	private byte[] writeBio(BioObject bio) {
		if (bio.getBioCode() == 0) {
			final BoFastStream stream = createOutputStream();
			stream.write(bio.getBioDictionary());
			stream.write(ByteUtility.shortToBytes((short) bio.getBioCode()));
			stream.write(ByteUtility.shortToBytes((short) bio.getBioVersion()));
			
			for (Entry<String, Object> e : bio.entries()) {
				writeValue(stream, e.getKey(), e.getValue());
			}
			
			stream.close();

			return stream.getBytes();
			
		} else {
			if (BioDictionary.getDictionary(bio.getBioDictionary()) == null) {
				if (!isValidated)
					return null ;
				else 
					throw new ParserException("bio dictionary " + bio.getBioDictionary() + " is not found");
			}
			final BioObj object = BioDictionary.getDictionary(bio.getBioDictionary()).getObjByCode(bio.getBioCode());
			if (object == null) {
				if (!isValidated)
					return null ;
				else 
					throw new ParserException("bio object " + bio.getBioCode() + "v" + bio.getBioVersion() + " is not found");
			}
			final BoFastStream stream = createOutputStream();
			if (object.isLarge()) {
				stream.setLengthAsInt(true);
			}
			stream.write(object.getDictionary());
			stream.write(ByteUtility.shortToBytes((short) object.getCode()));
			stream.write(ByteUtility.shortToBytes((short) object.getVersion()));
			
			for (Entry<String, Object> e : bio.entries()) {
				writeValue(object, stream, e.getKey(), e.getValue());
			}
			
			stream.close();

			return stream.getBytes();
		}
	}
	
	/**
	 * Encodes single tag data
	 * @param object
	 * @param stream
	 * @param key
	 * @param value
	 */
	private void writeValue(final BioObj object, final BoFastStream stream, String key, Object value) {
		try {
			// We find tag information
			BioTag tag = object.getTag(key);
			if (tag == null) {
				tag = BioDictionary.getDictionary(object.getDictionary()).getSuperTag(key);
			}
			// we encode only if we have tag info and tag is encodable
			if (tag != null) {
				if (!tag.isEncodable()) {
					return ;
				}
				// encode like array (including array length) if tag is 
				if (tag.isArray()) {
					if (!(value instanceof Object[])) {
						throw new ParserException(" for tag " + key + " value " + value + " is not array @ " + object.getBioClass().getName()) ;
					}
					// writing tag info
					stream.write(tag.getType().value());
					// writing array indicator
					stream.write((byte) 1);
					switch (tag.getType()) {
					case Long:
						stream.writeLongArray(tag, (Long[]) value);
						break;
					case Time:
						stream.writeLongArray(tag, (Long[]) value);
						break;
					case BioEnum:
						BioEnum[] bioEnumArray = (BioEnum[]) value;
						Integer[] intArray = new Integer[bioEnumArray.length];
						for (int i = 0; i < bioEnumArray.length; i++)
							intArray[i] = bioEnumArray[i].getOrdinal();
						stream.writeIntArray(tag, intArray);
						break;
					case Integer:
						stream.writeIntArray(tag, (Integer[]) value);
						break;
					case Byte:
						stream.writeByteArray(tag, (Byte[]) value);
						break;
					case Short:
						stream.writeShortArray(tag, (Short[]) value);
						break;
					case Float:
						stream.writeFloatArray(tag, (Float[]) value);
						break;
					case Boolean:
						stream.writeBooleanArray(tag, (Boolean[]) value);
						break;
					case Double:
						stream.writeDoubleArray(tag, (Double[]) value);
						break;
					case String:
						stream.writeAsciiStringArray(tag, (String[]) value);
						break;
					case UtfString:
						stream.writeUtfStringArray(tag, (String[]) value);
						break;
					case JavaObject:
						stream.writeObjectArray(tag, (Object[]) value);
						break;
					case BioObject:
						BioObject[] bioArray = (BioObject[]) value;
						stream.writeTag(tag);
						stream.writeLength(bioArray.length);
						for (int i = 0; i < bioArray.length; i++) {
							byte[] bioBytes = writeBio(bioArray[i]);
							if (bioBytes != null) {
								stream.writeBioBytes(bioBytes);
							}
						}
						break;
					default:
						throw new ParserException(tag.getType() + " arrays are not supported");
					}
				} else if (tag.isList()) {
					if (!(value instanceof List)) {
						throw new ParserException(" for tag " + key + " value " + value + " is not list @ " + object.getBioClass().getName()) ;
					}
					// writing tag info
					stream.write(tag.getType().value());
					// writing list indicator
					stream.write((byte) 2);
					List<Object> list = (List) value;
					switch (tag.getType()) {
					case Long:
						Long[] longArray = new Long[list.size()];
						list.toArray(longArray);
						stream.writeLongArray(tag, longArray);
						break;
					case Time:
						longArray = new Long[list.size()];
						list.toArray(longArray);
						stream.writeLongArray(tag, longArray);
						break;
					case BioEnum:
						List<BioEnum> bioEnumArray = (List<BioEnum>) value;
						Integer[] intCodeArray = new Integer[list.size()];
						for (int i = 0; i < bioEnumArray.size(); i++)
							intCodeArray[i] = bioEnumArray.get(i).getOrdinal();
						stream.writeIntArray(tag, intCodeArray);
						break;
					case Integer:
						Integer[] intArray = new Integer[list.size()];
						list.toArray(intArray);
						stream.writeIntArray(tag, intArray);
						break;
					case Byte:
						Byte[] byteArray = new Byte[list.size()];
						list.toArray(byteArray);
						stream.writeByteArray(tag, byteArray);
						break;
					case Short:
						Short[] shortArray = new Short[list.size()];
						list.toArray(shortArray);
						stream.writeShortArray(tag, shortArray);
						break;
					case Float:
						Float[] floatArray = new Float[list.size()];
						list.toArray(floatArray);
						stream.writeFloatArray(tag, floatArray);
						break;
					case Boolean:
						Boolean[] booleanArray = new Boolean[list.size()];
						list.toArray(booleanArray);
						stream.writeBooleanArray(tag, booleanArray);
						break;
					case Double:
						Double[] doubleArray = new Double[list.size()];
						list.toArray(doubleArray);
						stream.writeDoubleArray(tag, doubleArray);
						break;
					case String:
						String[] stringArray = new String[list.size()];
						list.toArray(stringArray);
						stream.writeAsciiStringArray(tag, stringArray);
						break;
					case UtfString:
						stringArray = new String[list.size()];
						list.toArray(stringArray);
						stream.writeUtfStringArray(tag, stringArray);
						break;
					case JavaObject:
						Object[] objectArray = new Object[list.size()];
						list.toArray(objectArray);
						stream.writeObjectArray(tag, objectArray);
						break;
					case BioObject:
						BioObject[] bioArray = new BioObject[list.size()];
						list.toArray(bioArray);
						stream.writeTag(tag);
						stream.writeLength(bioArray.length);
						for (int i = 0; i < bioArray.length; i++) {
							byte[] bioBytes = writeBio(bioArray[i]);
							if (bioBytes != null) {
								stream.writeBioBytes(bioBytes);
							}
						}
						break;
					default:
						throw new ParserException(tag.getType() + " lists are not supported");
					}
				} else {
					// If value is a dynamic expression we skip
					if (value instanceof BioExpression) {
						return ;
					}
					// If value is bio object of a different dictionary and we don't have we skip
					if (value instanceof BioObject && BioDictionary.getDictionary(((BioObject) value).getBioDictionary()) == null) {
						return ;
					}
					// write tag info
					stream.write(tag.getType().value());
					// write single object indicator
					stream.write((byte) 0);
					switch (tag.getType()) {
					case Long:
						stream.writeLong(tag, ((Number) value).longValue());
						break;
					case Time:
						stream.writeLong(tag, ((Number) value).longValue());
						break;
					case Byte:
						stream.writeByte(tag, ((Number) value).byteValue());
						break;
					case Short:
						stream.writeShort(tag, ((Number) value).shortValue());
						break;
					case Float:
						stream.writeFloat(tag, ((Number) value).floatValue());
						break;
					case Integer:
						stream.writeInt(tag, ((Number) value).intValue());
						break;
					case BioEnum:
						BioEnum bioEnum = (BioEnum) value;
						stream.writeInt(tag, bioEnum.getOrdinal());
						break;
					case Double:
						stream.writeDouble(tag, (Double) value);
						break;
					case String:
						stream.writeAsciiString(tag, (String) value);
						break;
					case UtfString:
						stream.writeUtfString(tag, (String) value);
						break;
					case Boolean:
						stream.writeBoolean(tag, (Boolean) value);
						break;
					case JavaObject:
						stream.writeObject(tag, value);
						break;
					case BioObject:
						byte[] bioBytes = writeBio((BioObject) value);
						if (bioBytes != null) {
							stream.writeBioBytes(tag, bioBytes);
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new ParserException("exception " + e.getClass().getName() + " for tag " + key + " value " + value + " object " + object.getBioClass().getName());
		}
	}
	
	private void writeValue(final BoFastStream stream, String key, Object value) {
		try {
			BioTag tag = null ;
			// We find tag information
			if (nameMap.containsKey(key)) {
				tag = nameMap.get(key) ;
			} else {
				tag = new BioTag() ;
				tag.setCode(++codeCounter);
				tag.setName(key);
				nameMap.put(key, tag) ;
			}

			boolean isArray = value instanceof Object[] ;
			boolean isList = value instanceof List ;
			BioType type = ConversionUtility.getType(value) ;

			// encode like array (including array length) if tag is 
			if (isArray) {
				// writing tag info
				stream.write(type.value());
				// writing array indicator
				stream.write((byte) 1);
				switch (type) {
				case Long:
					stream.writeLongArray(tag, (Long[]) value);
					break;
				case Time:
					stream.writeLongArray(tag, (Long[]) value);
					break;
				case BioEnum:
					BioEnum[] bioEnumArray = (BioEnum[]) value;
					Integer[] intArray = new Integer[bioEnumArray.length];
					for (int i = 0; i < bioEnumArray.length; i++)
						intArray[i] = bioEnumArray[i].getOrdinal();
					stream.writeIntArray(tag, intArray);
					break;
				case Integer:
					stream.writeIntArray(tag, (Integer[]) value);
					break;
				case Byte:
					stream.writeByteArray(tag, (Byte[]) value);
					break;
				case Short:
					stream.writeShortArray(tag, (Short[]) value);
					break;
				case Float:
					stream.writeFloatArray(tag, (Float[]) value);
					break;
				case Boolean:
					stream.writeBooleanArray(tag, (Boolean[]) value);
					break;
				case Double:
					stream.writeDoubleArray(tag, (Double[]) value);
					break;
				case String:
					stream.writeAsciiStringArray(tag, (String[]) value);
					break;
				case UtfString:
					stream.writeUtfStringArray(tag, (String[]) value);
					break;
				case JavaObject:
					stream.writeObjectArray(tag, (Object[]) value);
					break;
				case BioObject:
					BioObject[] bioArray = (BioObject[]) value;
					stream.writeTag(tag);
					stream.writeLength(bioArray.length);
					for (int i = 0; i < bioArray.length; i++) {
						byte[] bioBytes = writeBio(bioArray[i]);
						if (bioBytes != null) {
							stream.writeBioBytes(bioBytes);
						}
					}
					break;
				default:
					throw new ParserException(tag.getType() + " arrays are not supported");
				}
			} else if (isList) {
				// writing tag info
				stream.write(type.value());
				// writing list indicator
				stream.write((byte) 2);
				List<Object> list = (List) value;
				switch (type) {
				case Long:
					Long[] longArray = new Long[list.size()];
					list.toArray(longArray);
					stream.writeLongArray(tag, longArray);
					break;
				case Time:
					longArray = new Long[list.size()];
					list.toArray(longArray);
					stream.writeLongArray(tag, longArray);
					break;
				case BioEnum:
					List<BioEnum> bioEnumArray = (List<BioEnum>) value;
					Integer[] intCodeArray = new Integer[list.size()];
					for (int i = 0; i < bioEnumArray.size(); i++)
						intCodeArray[i] = bioEnumArray.get(i).getOrdinal();
					stream.writeIntArray(tag, intCodeArray);
					break;
				case Integer:
					Integer[] intArray = new Integer[list.size()];
					list.toArray(intArray);
					stream.writeIntArray(tag, intArray);
					break;
				case Byte:
					Byte[] byteArray = new Byte[list.size()];
					list.toArray(byteArray);
					stream.writeByteArray(tag, byteArray);
					break;
				case Short:
					Short[] shortArray = new Short[list.size()];
					list.toArray(shortArray);
					stream.writeShortArray(tag, shortArray);
					break;
				case Float:
					Float[] floatArray = new Float[list.size()];
					list.toArray(floatArray);
					stream.writeFloatArray(tag, floatArray);
					break;
				case Boolean:
					Boolean[] booleanArray = new Boolean[list.size()];
					list.toArray(booleanArray);
					stream.writeBooleanArray(tag, booleanArray);
					break;
				case Double:
					Double[] doubleArray = new Double[list.size()];
					list.toArray(doubleArray);
					stream.writeDoubleArray(tag, doubleArray);
					break;
				case String:
					String[] stringArray = new String[list.size()];
					list.toArray(stringArray);
					stream.writeAsciiStringArray(tag, stringArray);
					break;
				case UtfString:
					stringArray = new String[list.size()];
					list.toArray(stringArray);
					stream.writeUtfStringArray(tag, stringArray);
					break;
				case JavaObject:
					Object[] objectArray = new Object[list.size()];
					list.toArray(objectArray);
					stream.writeObjectArray(tag, objectArray);
					break;
				case BioObject:
					BioObject[] bioArray = new BioObject[list.size()];
					list.toArray(bioArray);
					stream.writeTag(tag);
					stream.writeLength(bioArray.length);
					for (int i = 0; i < bioArray.length; i++) {
						byte[] bioBytes = writeBio(bioArray[i]);
						if (bioBytes != null) {
							stream.writeBioBytes(bioBytes);
						}
					}
					break;
				default:
					throw new ParserException(tag.getType() + " lists are not supported");
				}
			} else {
				// write tag info
				stream.write(type.value());
				// write single object indicator
				stream.write((byte) 0);
				switch (type) {
				case Long:
					stream.writeLong(tag, ((Number) value).longValue());
					break;
				case Time:
					stream.writeLong(tag, ((Number) value).longValue());
					break;
				case Byte:
					stream.writeByte(tag, ((Number) value).byteValue());
					break;
				case Short:
					stream.writeShort(tag, ((Number) value).shortValue());
					break;
				case Float:
					stream.writeFloat(tag, ((Number) value).floatValue());
					break;
				case Integer:
					stream.writeInt(tag, ((Number) value).intValue());
					break;
				case BioEnum:
					BioEnum bioEnum = (BioEnum) value;
					stream.writeInt(tag, bioEnum.getOrdinal());
					break;
				case Double:
					stream.writeDouble(tag, (Double) value);
					break;
				case String:
					stream.writeAsciiString(tag, (String) value);
					break;
				case UtfString:
					stream.writeUtfString(tag, (String) value);
					break;
				case Boolean:
					stream.writeBoolean(tag, (Boolean) value);
					break;
				case JavaObject:
					stream.writeObject(tag, value);
					break;
				case BioObject:
					byte[] bioBytes = writeBio((BioObject) value);
					if (bioBytes != null) {
						stream.writeBioBytes(tag, bioBytes);
					}
					break;
				}
			}
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}
	
	/**
	 * Decodes bio object
	 * @param bytes
	 * @return
	 */
	private BioObject readBio(byte[] bytes) {
		BiFastStream stream = createInputStream(bytes);
		try {
			int dictionary = stream.readByte() ;
			int objCode = stream.readObjCode();
			int objVersion = stream.readObjVersion();
			
			if (objCode == 0 && objVersion == 0) {
				BioObject bio = new BioObject();
				
				BioTag tag = null;
				while (stream.available() > 0) {
					byte tagInfo = stream.readByte();
					BioType tagType = BioType.getType(tagInfo);
					byte typeInfo = stream.readByte() ;
					boolean isArray = typeInfo == 1 ;
					boolean isList = typeInfo == 2 ;
					int tagCode = stream.readTagCode();
					tag = codeMap.get(tagCode) ;
					Object value = readValue(tagType, isArray, isList, tag, stream);
					if (value != null) {
						bio.put(tag.getName(), value);
					}
				}
				
				return bio ;
			} else {
				if (BioDictionary.getDictionary(dictionary) == null) {
					if (!isValidated)
						return null ;
					else 
						throw new ParserException("bio dictionary " + dictionary + " is not found");
				}
				BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(objCode);
				if (obj == null) {
					if (!isValidated)
						return null ;
					else 
						throw new ParserException("bio obj with " + objCode + " is not found");
				}
				if (obj.isLarge()) {
					stream.setLengthAsInt(true);
				}
				// we create an instance of bio object
				BioObject bio = null;
				if (obj.getBioClass() != null) {
					// why empty() because otherwise if bio obj has initial fields they will auto generated
					// but actually during serialization they were not present
					// so here we empty object and only add serialized tag values
					bio = (BioObject) obj.getBioClass().getConstructor().newInstance().empty();
					bio.setBioCode(objCode);
					bio.setBioName(obj.getName());
					bio.setBioVersion(objVersion);
				} else {
					bio = new BioObject(objCode, null, objVersion);
				}
				// we parse tags one by one
				BioTag tag = null;
				while (stream.available() > 0) {
					byte tagInfo = stream.readByte();
					BioType tagType = BioType.getType(tagInfo);
					byte typeInfo = stream.readByte() ;
					boolean isArray = typeInfo == 1 ;
					boolean isList = typeInfo == 2 ;
					int tagCode = stream.readTagCode();
					tag = obj.getTag(tagCode);
					// if couldn't find tag, may be it is a super tag ???
					if (tag == null) {
						tag = BioDictionary.getDictionary(dictionary).getSuperTag(tagCode);
					}

					if (tag != null) {
						Object value = readValue(tagType, isArray, isList, tag, stream);
						if (value != null) {
							bio.put(tag.getName(), value);
						}
					} else {
						// there is something encoded but we don't have it in dictionary
						// so we just ignore it
						readValue(tagType, isArray, isList, null, stream);
					}
				}
				return bio;
			}
			
			
			
			
			
			
			
		} catch (Throwable e) {
			throw new ParserException(e);
		} finally {
			stream.close();
		}
	}
	
	/**
	 * Decodes tag value
	 * @param obj
	 * @param type
	 * @param isArray
	 * @param isList
	 * @param tag
	 * @param stream
	 * @return
	 */
	private Object readValue(BioType type, boolean isArray, boolean isList, BioTag tag, BiFastStream stream) {
		if (isArray) {
			switch (type) {
			case Long:
				return stream.readLongArray();
			case Integer:
				return stream.readIntArray();
			case BioEnum:
				BioEnumObj bioEnumObj = tag.getEnumObj();
				Integer intArray[] = stream.readIntArray();
				BioEnum[] bioEnumArray = null;
				if (tag != null && tag.getEnumObj() != null && tag.getEnumObj().getBioClass() != null) {
					bioEnumArray = (BioEnum[]) Array.newInstance(tag.getEnumObj().getBioClass(), intArray.length);
					for (int i = 0; i < intArray.length; i++)
						bioEnumArray[i] = bioEnumObj.getBioEnum(intArray[i]);
					return bioEnumArray;
				}
				return null;
			case Double:
				return stream.readDoubleArray();
			case Byte:
				return stream.readByteArray();
			case Short:
				return stream.readShortArray();
			case Float:
				return stream.readFloatArray();
			case Boolean:
				return stream.readBooleanArray();
			case String:
				return stream.readAsciiStringArray();
			case UtfString:
				return stream.readUtfStringArray();
			case JavaObject:
				return stream.readObjectArray();
			case Time:
				return stream.readLongArray();
			case BioObject:
				int size = stream.readLength();
				
				ArrayList<BioObject> list = new ArrayList<BioObject>() ;
				for (int i = 0; i < size; i++) {
					BioObject bio = readBio(stream.readBioBytes());
					if (bio != null) {
						list.add(bio) ;
					}
				}
				
				BioObject[] bioArray = null;
				if (tag != null && tag.getObj() != null && tag.getObj().getBioClass() != null) {
					bioArray = (BioObject[]) Array.newInstance(tag.getObj().getBioClass(), list.size());
				} else {
					bioArray = new BioObject[list.size()];
				}
				list.toArray(bioArray) ;
				
				return bioArray;
			}
		} else if (isList) {
			switch (type) {
			case Long:
				return new ArrayList(Arrays.asList(stream.readLongArray()));
			case Integer:
				return new ArrayList(Arrays.asList(stream.readIntArray()));
			case BioEnum:
				BioEnumObj bioEnumObj = tag.getEnumObj();
				Integer intArray[] = stream.readIntArray();
				BioEnum[] bioEnumArray = null;
				if (tag != null && tag.getEnumObj() != null && tag.getEnumObj().getBioClass() != null) {
					bioEnumArray = (BioEnum[]) Array.newInstance(tag.getEnumObj().getBioClass(), intArray.length);
					for (int i = 0; i < intArray.length; i++)
						bioEnumArray[i] = bioEnumObj.getBioEnum(intArray[i]);
					return new ArrayList(Arrays.asList(bioEnumArray));
				}
				return null;
			case Double:
				return new ArrayList(Arrays.asList(stream.readDoubleArray()));
			case Byte:
				return new ArrayList(Arrays.asList(stream.readByteArray()));
			case Short:
				return new ArrayList(Arrays.asList(stream.readShortArray()));
			case Float:
				return new ArrayList(Arrays.asList(stream.readFloatArray()));
			case Boolean:
				return new ArrayList(Arrays.asList(stream.readBooleanArray()));
			case String:
				return new ArrayList(Arrays.asList(stream.readAsciiStringArray()));
			case UtfString:
				return new ArrayList(Arrays.asList(stream.readUtfStringArray()));
			case JavaObject:
				return new ArrayList(Arrays.asList(stream.readObjectArray()));
			case Time:
				return new ArrayList(Arrays.asList(stream.readLongArray()));
			case BioObject:
				int size = stream.readLength();
				ArrayList<BioObject> list = new ArrayList<BioObject>() ;
				for (int i = 0; i < size; i++) {
					BioObject bio = readBio(stream.readBioBytes());
					if (bio != null) {
						list.add(bio) ;
					}
				}
				return list ;
			}
		} else {
			switch (type) {
			case Long:
				return stream.readLong();
			case Integer:
				return stream.readInt();
			case BioEnum:
				BioEnumObj bioEnumObj = tag.getEnumObj();
				return bioEnumObj.getBioEnum(stream.readInt());
			case Double:
				return stream.readDouble();
			case Time:
				return stream.readLong();
			case Boolean:
				return stream.readBoolean();
			case Byte:
				return stream.readByte();
			case Short:
				return stream.readShort();
			case Float:
				return stream.readFloat();
			case String:
				return stream.readAsciiString();
			case UtfString:
				return stream.readUtfString();
			case JavaObject:
				return stream.readObject();
			case BioObject:
				return readBio(stream.readBioBytes());
			}
		}
		return null;
	}
	
	/**
	 * Creates stream for decoding
	 * @param data
	 * @return
	 */
	private BiFastStream createInputStream(byte[] data) {
		return new BiFastStream(data);
	}

	/**
	 * Creates stream for encoding
	 * @return
	 */
	private BoFastStream createOutputStream() {
		return new BoFastStream();
	}
}