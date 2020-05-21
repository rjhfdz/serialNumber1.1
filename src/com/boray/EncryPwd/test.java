package com.boray.EncryPwd;

public class test {
	public static void main(String[] args) {
//		long[] src = new long[16];
//		String s = "BD01011113030C10171D0079";
//		for (int i = 0; i < s.length(); i=i+2) {
//			src[i/2] = Integer.valueOf(s.substring(i,i+2),16).intValue();
//		}
//		int[] key ={0x424F5344,0x4F4E4C6F,0x636B4C6F,0x6E767838};
//		int a = new TEA_Encrypt().TEA_Encrypt(src, 16, key);
//		System.out.println("");
//		for (int i = 0; i < src.length; i++) {
//			System.out.print(Long.toHexString((src[i]))+"//");
//			//3//ffffffe7//ffffffbd//2f//44//14//ffffffd0//ffffff95//17//1d//0//79//0//0//0//0//
//		}

		Integer value = Math.toIntExact(Long.parseLong("1F".toUpperCase(), 16));
		System.out.println(value);
	}
}
