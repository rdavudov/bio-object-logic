package com.linkedlogics.bio.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * This utility handles all byte to object and vice versa operation. Very useful !!!
 *
 * @author rdavudov
 */
public class ByteUtility {
    private static final char[] hex_lowercase = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] hex_uppercase = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final long SECOND_SHIFT = 2208988800L;
    private static final int INT_INET4 = 1;
    private static final int INT_INET6 = 2;

    public static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    public static byte[] longToBytes(byte[] result, int offset, long value) {
        for (int i = offset + 7; i >= offset; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    public static byte[] intToBytes(byte[] result, int offset, int value) {
        for (int i = offset + 3; i >= offset; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    public static byte[] shortToBytes(short value) {
        byte[] lenBytes = new byte[2];

        lenBytes[1] = (byte) (value & 0xFF);
        lenBytes[0] = (byte) ((value >> 8) & 0xFF);

        return lenBytes;
    }

    public static byte[] shortToBytes(byte[] result, int offset, short value) {
        result[offset + 1] = (byte) (value & 0xFF);
        result[offset] = (byte) ((value >> 8) & 0xFF);

        return result;
    }

    public static byte[] doubleToBytes(double value) {
        return longToBytes(Double.doubleToRawLongBits(value));
    }

    public static byte[] floatToBytes(float value) {
        return intToBytes(Float.floatToRawIntBits(value));
    }

    public static byte[] doubleToBytes(byte[] bytes, int offset, double value) {
        return longToBytes(bytes, offset, Double.doubleToRawLongBits(value));
    }
    
    public static byte[] floatToBytes(byte[] bytes, int offset, float value) {
        return intToBytes(bytes, offset, Float.floatToRawIntBits(value));
    }

    public static byte[] asciiStringToBytes(String value) {
        return getBytesFast(value);
    }

    public static byte[] asciiStringToBytes(String value, byte[] result, int offset) {
        return getBytesFast(value, result, offset);
    }

    public static byte[] getBytesFast(String str) {
        final byte b[] = new byte[str.length()];
        for (int j = 0; j < b.length; j++)
            b[j] = (byte) str.charAt(j);
        return b;
    }

    public static byte[] getBytesFast(String str, byte[] result, int offset) {
        for (int j = 0; j < str.length(); j++)
            result[offset + j] = (byte) str.charAt(j);
        return result;
    }

    public static byte[] utfStringToBytes(String value) {
        try {
            return value.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        long result = 0;
        for (int i = offset; i < offset + 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static short bytesToShort(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 2; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return (short) result;
    }

    public static short bytesToShort(byte[] bytes, int offset) {
        int result = 0;
        for (int i = offset; i < offset + 2; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return (short) result;
    }

    public static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        int result = 0;
        for (int i = offset; i < offset + 4; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static double bytesToDouble(byte[] bytes) {
        long value = bytesToLong(bytes);
        return Double.longBitsToDouble(value);
    }

    public static float bytesToFloat(byte[] bytes) {
        int value = bytesToInt(bytes);
        return Float.intBitsToFloat(value);
    }

    public static double bytesToDouble(byte[] bytes, int offset) {
        long value = bytesToLong(bytes, offset);
        return Double.longBitsToDouble(value);
    }
    
    public static float bytesToFloat(byte[] bytes, int offset) {
        int value = bytesToInt(bytes, offset);
        return Float.intBitsToFloat(value);
    }

    public static String bytesToAsciiString(byte[] bytes) {
        try {
            return new String(bytes, "us-ascii");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToAsciiString(byte[] bytes, int offset, int length) {
        try {
            return new String(bytes, offset, length, "us-ascii");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToUtfString(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToUtfString(byte[] bytes, int offset, int length) {
        try {
            return new String(bytes, offset, length, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = hex_lowercase[(0xF0 & data[i]) >>> 4];
            out[j++] = hex_lowercase[0x0F & data[i]];
        }
        return new String(out);
    }

    public static String bytesToHex(final byte[] data, int offset, int length) {
        final char[] out = new char[(length - offset) << 1];
        for (int i = offset, j = 0; i < length; i++) {
            out[j++] = hex_lowercase[(0xF0 & data[i]) >>> 4];
            out[j++] = hex_lowercase[0x0F & data[i]];
        }
        return new String(out);
    }

    public static byte[] hexToBytes(String hex) {
        char[] data = hex.toCharArray();
        final int len = data.length;

        final byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = Character.digit(data[j], 16) << 4;
            j++;
            f = f | Character.digit(data[j], 16);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    public static Byte[] bytesToBytes(byte[] bytes) {
        Byte[] b = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            b[i] = bytes[i];
        }
        return b;
    }

    public static byte[] bytesToBytes(Byte[] bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            b[i] = bytes[i];
        }
        return b;
    }

    public static byte[] objectToBytes(Object object) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytesOut);
            out.writeObject(object);
            out.flush();
            out.close();

            byte[] bytes = bytesOut.toByteArray();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e) ;
        }
    }

    public static Object bytesToObject(byte[] bytes) {
        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bytesIn);
            Object object = in.readObject();
            in.close();
            return object;
        } catch (Throwable e) {
        	  throw new RuntimeException(e) ;
        }
    }

    public static Date bytesToDate(byte[] rawData) {
        byte[] tmp = new byte[8];
        System.arraycopy(rawData, 0, tmp, 4, 4);
        return new Date((bytesToLong(tmp) - SECOND_SHIFT) * 1000L);
    }

    public static InetAddress bytesToAddress(byte[] rawData) {
        InetAddress inetAddress;
        byte[] address;
        try {
            if (rawData[1] == INT_INET4) {
                address = new byte[4];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet4Address.getByAddress(address);
            } else {
                address = new byte[16];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet6Address.getByAddress(address);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return inetAddress;
    }

    public static byte[] addressToBytes(InetAddress address) {
        byte addressBytes[] = address.getAddress();
        byte[] data = new byte[addressBytes.length + 2];
        int addressType = address instanceof Inet4Address ? INT_INET4 : INT_INET6;
        data[0] = (byte) ((addressType >> 8) & 0xFF);
        data[1] = (byte) (addressType & 0xFF);
        System.arraycopy(addressBytes, 0, data, 2, addressBytes.length);
        return data;
    }

    public static byte[] dateToBytes(Date date) {
        byte[] data = new byte[4];
        System.arraycopy(longToBytes((date.getTime() / 1000L) + SECOND_SHIFT), 4, data, 0, 4);
        return data;
    }

    public static byte[] unsignedInt32ToBytes(long value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        buffer.flip();
        buffer.get(bytes);
        buffer.get(bytes);
        return bytes;
    }

    public static void main(String[] args) {
        String hex = ByteUtility.bytesToHex(ByteUtility.longToBytes(83103L));
        for (int i = 0; i < hex.length(); i += 2) {
            System.out.print(hex.substring(i, i + 2) + ":");
        }

    }


}
