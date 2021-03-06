package com.stv.supervod.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Description: 验证信息有效性
 * Copyright (c) 永新视博
 * All Rights Reserved.
 * @version 1.0  2011-12-6 下午01:15:27 mustang created
 */
public class ValidateUtil {
	/**
	 * Description: 验证字符串是否为空、空格、null
	 * @Version1.0 2011-12-9 下午04:14:14 mustang created
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		if(str!=null&&!str.trim().equals("")&&!str.trim().equals(" ")){
			return false;
		}
		return true;
	}
	
	/**
	 * Description: 验证邮箱格式是否正确
	 * @Version1.0 2011-12-9 下午04:13:56 mustang created
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	/**
	 * Description: 验证是否为数字格式
	 * @Version1.0 2011-12-9 下午05:52:24 mustang created
	 * @param num
	 * @return
	 */
	public static boolean isNumber(String num){
		Pattern pattern = Pattern.compile("^[0-9]*$",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(num);
		return matcher.matches();
	}
	
}
