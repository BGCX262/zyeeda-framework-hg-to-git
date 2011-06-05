package com.zyeeda.framework.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class LdapEncryptUtils {
	
	public static String md5Encode(String standardMd5) throws UnsupportedEncodingException {
//		String standardMd5 = DigestUtils.md5Hex(password);
		byte[] ba = new byte[standardMd5.length() / 2];
		for (int i = 0; i < standardMd5.length(); i = i + 2) {
			ba[i == 0 ? 0 : i / 2] = (byte) (0xff & Integer.parseInt(
					standardMd5.substring(i, i + 2), 16));
		}
		Base64 base64 = new Base64();
		return new String(base64.encode(ba), "UTF-8").trim();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(md5Encode("e10adc3949ba59abbe56e057f20f883e"));
	}
}
