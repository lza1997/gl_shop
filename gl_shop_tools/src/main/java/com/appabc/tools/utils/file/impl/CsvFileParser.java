/**  
 * com.appabc.tools.utils.file.impl.CsvFileParser.java  
 *   
 * 2015年7月16日 下午4:19:07  
 * Copyright APPABC Information System Co.Ltd. All rights reserved.
 */
package com.appabc.tools.utils.file.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;

/**
 * @Description : 
 * @Copyright   : GL. All Rights Reserved
 * @Company     : 江苏国立网络技术有限公司
 * @author      : 黄建华
 * @version     : 1.0
 * @Create_Date  : 2015年7月16日 下午4:19:07
 */

public class CsvFileParser extends AbstractFileParser {

	public CsvFileParser(){}
	
	public CsvFileParser(String fileName){
		this.fileName = fileName.trim();
	}
	
	/* (non-Javadoc)  
	 * @see com.appabc.tools.utils.file.IFileParser#readFileAsString(java.io.InputStream)  
	 */
	@Override
	public String readFileAsString(InputStream in) {
		if(in == null){
			return StringUtils.EMPTY;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charsets.UTF_8));
		StringBuilder str = new StringBuilder();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
				str.append(COMMA);
				// CSV格式文件为逗号分隔符文件，这里根据逗号切分
			}
			if(str.length()>0 && str.lastIndexOf(COMMA) != -1){
				str.deleteCharAt(str.length() - 1);
			}
		} catch (IOException e) {
			log.debug(e.getMessage(),e);
		} finally {
			try {
				in.close();
				reader.close();
			} catch (IOException e) {
				log.debug(e.getMessage(),e);
			}
		}
		return str.toString();
	}

}
