package com.boray.EncryPwd;

import io.netty.buffer.ByteBufUtil;

public class TeaUtil {

    private static final long UINT32_MAX = 0xFFFFFFFFL;
    private static final long BYTE_1 = 0xFFL;
    private static final long BYTE_2 = 0xFF00L;
    private static final long BYTE_3 = 0xFF0000L;
    private static final long BYTE_4 = 0xFF000000L;
    private static final long delta = 0x9E3779B9L;

    //通过TEA算法加密信息
    public static byte[] encryptByHexKey(String hexKey, String info){
        return encryptByCommon(ByteBufUtil.decodeHexDump(hexKey), info.getBytes());
    }
    //通过TEA算法加密信息
    public static byte[] encryptByHexKey(String hexKey, byte[] temp){
        return encryptByCommon(ByteBufUtil.decodeHexDump(hexKey), temp);
    }
    //通过TEA算法加密信息
    public static byte[] encryptByKey(String key, String info){
        return encryptByCommon(key.getBytes(), info.getBytes());
    }
    //通过TEA算法加密信息
    public static byte[] encryptByKey(String key, byte[] temp){
        return encryptByCommon(key.getBytes(), temp);
    }

    //通过TEA算法加密信息
    public static byte[] encryptByCommon(byte[] key, byte[] data){
        int data_len = data.length; // 数据的长度
        if (data_len == 0) {
            return new byte[] {};
        }

        long[] longKeys = generateKey(key);

        int group_len = 8;
        int residues = data_len % group_len; // 余数
        int dlen = data_len - residues;

        // 用于储存加密的密文，第一字节为余数的大小
        int result_len = data_len + 1;
        if (residues > 0) {
            result_len += group_len - residues;
        }
        byte[] result = new byte[result_len];
        result[0] = (byte)residues;

        byte[] plain = new byte[group_len];
        byte[] enc = new byte[group_len];

        for (int i = 0; i < dlen; i += group_len) {
            for (int j = 0; j < group_len; j++) {
                plain[j] = data[i + j];
            }
            enc = encrypt_group(longKeys, plain, 32);
            for (int k = 0; k < group_len; k++) {
                result[i + k + 1] = enc[k];
            }
        }
        if (residues > 0) {
            for (int j = 0; j < residues; j++) {
                plain[j] = data[dlen + j];
            }
            int padding = group_len - residues;
            for (int j = 0; j < padding; j++) {
                plain[residues + j] = (byte)0x00;
            }
            enc = encrypt_group(longKeys, plain, 32);
            for (int k = 0; k < group_len; k++) {
                result[dlen + k + 1] = enc[k];
            }
        }
        return result;
    }

    /**
     * 加密一组明文
     * @param v 需要加密的明文
     * @return 返回密文
     */
    private static byte[] encrypt_group(long[] k, byte[] v, int loops) {
        long k0 = k[0], k1 = k[1], k2 = k[2], k3 = k[3];
        long v0 = bytes_to_uint32(new byte[] {v[0], v[1], v[2], v[3]});
        long v1 = bytes_to_uint32(new byte[] {v[4], v[5], v[6], v[7]});
        long sum = 0L;
        long v0_xor_1 = 0L, v0_xor_2 = 0L, v0_xor_3 = 0L;
        long v1_xor_1 = 0L, v1_xor_2 = 0L, v1_xor_3 = 0L;
        for (int i = 0; i < loops; i++) {
            sum = toUInt32(sum + delta);
            v0_xor_1 = toUInt32(toUInt32(v1 << 4) + k0);
            v0_xor_2 = toUInt32(v1 + sum);
            v0_xor_3 = toUInt32((v1 >> 5) + k1);
            v0 = toUInt32(  v0 + toUInt32(v0_xor_1 ^ v0_xor_2 ^ v0_xor_3)  );
            v1_xor_1 = toUInt32(toUInt32(v0 << 4) + k2);
            v1_xor_2 = toUInt32(v0 + sum);
            v1_xor_3 = toUInt32((v0 >> 5) + k3);
            v1 = toUInt32(  v1 + toUInt32(v1_xor_1 ^ v1_xor_2 ^ v1_xor_3)  );
        }
        byte[] b0 = long_to_bytes(v0, 4);
        byte[] b1 = long_to_bytes(v1, 4);
        return new byte[] {b0[0], b0[1], b0[2], b0[3], b1[0], b1[1], b1[2], b1[3]};
    }


    //通过TEA算法解密信息
    public static byte[] decryptByHexKey(String hexKey, String info){
        return decryptCommon(ByteBufUtil.decodeHexDump(hexKey), info.getBytes());
    }
    //通过TEA算法解密信息
    public static byte[] decryptByHexKey(String hexKey, byte[] secretInfo){
        return decryptCommon(ByteBufUtil.decodeHexDump(hexKey), secretInfo);
    }
    //通过TEA算法解密信息
    public static byte[] decryptByKey(String key, String info){
        return decryptCommon(key.getBytes(), info.getBytes());
    }
    //通过TEA算法解密信息
    public static byte[] decryptByKey(String key, byte[] secretInfo){
        return decryptCommon(key.getBytes(), secretInfo);
    }

    public static byte[] decryptCommon(byte[] key, byte[] data) {
        int group_len = 8;
        if (data.length % group_len != 1) {
            return new byte[] {};
        }

        long[] longKeys = generateKey(key);

        int data_len = data.length - 1, dlen; // 数据的长度
        int residues = (int)(data[0]); // 余数
        if (residues > 0) {
            dlen = data_len - group_len;
        } else {
            dlen = data_len;
        }

        byte[] result = new byte[dlen + residues];

        byte[] dec = new byte[group_len];
        byte[] enc = new byte[group_len];
        for (int i = 0; i < dlen; i += group_len) {
            for (int j = 0; j < group_len; j++) {
                enc[j] = data[i + j + 1];
            }
            dec = decrypt_group(longKeys, enc, 32);
            for (int k = 0; k < group_len; k++) {
                result[i + k] = dec[k];
            }
        }
        if (residues > 0) {
            for (int j = 0; j < group_len; j++) {
                enc[j] = data[dlen + j + 1];
            }
            dec = decrypt_group(longKeys, enc, 32);
            for (int k = 0; k < residues; k++) {
                result[dlen + k] = dec[k];
            }
        }
        return result;
    }

    /**
     * 设置密钥
     * @param k 密钥
     * @return 密钥长度为16个byte时， 设置密钥并返回true，否则返回false
     */
    public static long[] generateKey(byte[] k) {
        long[] keys = new long[] {
                bytes_to_uint32(new byte[] {k[0], k[1], k[2], k[3]}),
                bytes_to_uint32(new byte[] {k[4], k[5], k[6], k[7]}),
                bytes_to_uint32(new byte[] {k[8], k[9], k[10], k[11]}),
                bytes_to_uint32(new byte[] {k[12], k[13], k[14], k[15]})
        };
        return keys;
    }

    /**
     * 解密一组密文
     * @param v 要解密的密文
     * @return 返回明文
     */
    private static byte[] decrypt_group(long[] k, byte[] v, int loops) {
        long k0 = k[0], k1 = k[1], k2 = k[2], k3 = k[3];
        long v0 = bytes_to_uint32(new byte[] {v[0], v[1], v[2], v[3]});
        long v1 = bytes_to_uint32(new byte[] {v[4], v[5], v[6], v[7]});
        long sum = 0xC6EF3720L, tmp = 0L;
        for (int i = 0; i < loops; i++) {
            tmp = toUInt32(toUInt32(v0 << 4) + k2);
            v1 = toUInt32(  v1 - toUInt32(tmp ^  toUInt32(v0 + sum) ^ toUInt32((v0 >> 5) + k3))  );
            tmp = toUInt32(toUInt32(v1 << 4) + k0);
            v0 = toUInt32(  v0 - toUInt32(tmp ^  toUInt32(v1 + sum) ^ toUInt32((v1 >> 5) + k1))  );
            sum = toUInt32(sum - delta);
        }
        byte[] b0 = long_to_bytes(v0, 4);
        byte[] b1 = long_to_bytes(v1, 4);
        return new byte[] {b0[0], b0[1], b0[2], b0[3], b1[0], b1[1], b1[2], b1[3]};
    }


    /**
     * 将 long 类型的 n 转为 byte 数组，如果 len 为 4，则只返回低32位的4个byte
     * @param n 需要转换的long
     * @param len 若为4，则只返回低32位的4个byte，否则返回8个byte
     * @return 转换后byte数组
     */
    private static byte[] long_to_bytes(long n, int len) {
        byte a = (byte)((n & BYTE_4) >> 24);
        byte b = (byte)((n & BYTE_3) >> 16);
        byte c = (byte)((n & BYTE_2) >> 8);
        byte d = (byte)(n & BYTE_1);
        if (len == 4) {
            return new byte[] {a, b, c, d};
        }
        byte ha = (byte)(n >> 56);
        byte hb = (byte)((n >> 48) & BYTE_1);
        byte hc = (byte)((n >> 40) & BYTE_1);
        byte hd = (byte)((n >> 32) & BYTE_1);
        return new byte[] {ha, hb, hc, hd, a, b, c, d};
    }

    /**
     * 将4个byte转为 Unsigned Integer 32，以 long 形式返回
     * @param bs 需要转换的字节
     * @return 返回 long，高32位为0，低32位视为Unsigned Integer
     */
    private static long bytes_to_uint32(byte[] bs) {
        return ((bs[0]<<24) & BYTE_4) +
                ((bs[1]<<16) & BYTE_3) +
                ((bs[2]<<8)  & BYTE_2) +
                (bs[3] & BYTE_1);
    }

    /**
     * 将long的高32位清除，只保留低32位，低32位视为Unsigned Integer
     * @param n 需要清除的long
     * @return 返回高32位全为0的long
     */
    private static long toUInt32(long n) {
        return n & UINT32_MAX;
    }

    public static void main(String[] args) {
        /*byte[] en = encryptByHexKey("04463931363733350101011004463931", ByteBufUtil.decodeHexDump("11223344556677"));
        System.out.println(ByteBufUtil.hexDump(en));
        System.out.println(ByteBufUtil.hexDump(decryptByHexKey("04463931363733350101011004463931", en)));*/
    	byte[] en = encryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", ByteBufUtil.decodeHexDump("BD1501011113030C10171D0079"));
        System.out.println(ByteBufUtil.hexDump(en));
        //0599ace8d5fba7c2b373e56b6004d47844
        //04e3be144123c5016842a32283f79047c1
    	//13031b0e352e03bd01011113031b0e352e00b5
    	//036dbaf443305d59f6175b0a8d9064f13bee7fe8c8e2f095f6
    	//0494C2A9EBFC04797994C2A9EBFC047979
    	//byte[] en = ByteBufUtil.decodeHexDump("0494C2A9EBFC04797994C2A9EBFC047979");
        //System.out.println(ByteBufUtil.hexDump(decryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", en)));
    	/*byte[] en = encryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", ByteBufUtil.decodeHexDump("030A21"));
        System.out.println(ByteBufUtil.hexDump(en));
        System.out.println(ByteBufUtil.hexDump(decryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", en)));*/
    }
    
}
