package com.koolbao.utils;

import java.security.MessageDigest;

public class MD5Utils {
	public static String create16Md5(String word) {
		return create32Md5(word).substring(8, 24);
	}

	public static String create32Md5(String word) {
		StringBuilder hexString = null;
		byte[] defaultBytes = word.getBytes();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			hexString = new StringBuilder();
			for (int i = 0; i < messageDigest.length; i++) {
				if (Integer.toHexString(0xFF & messageDigest[i]).length() == 1) {
					hexString.append(0);
				}
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			messageDigest.toString();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" md5处理异常  word:" + word);
		}
		return hexString.toString().toUpperCase();
	}

	public static String byte2hex(String data) {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			bytes = md.digest(data.getBytes("UTF-8"));
		} catch (Exception gse) {
			gse.printStackTrace();
			System.out.println(" md5处理异常  word:" + data);
		}

		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}
	
	/**
	 * 返回字符串的指纹数字
	 * 最大长度10位
	 * @param str
	 * @return
	 */
	public static long StringID(String str) {
		return Long.parseLong(MD5Utils.create16Md5(str).substring(4, 12), 16);
	}
}
