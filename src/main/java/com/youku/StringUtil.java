package com.youku;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Author: ChengGong Date: 2005-5-24 Time: 0:07:53
 */
public class StringUtil {
    /**
     * �������н�ȡ������max������ժҪ
     *
     * @param text
     * @param max
     * @return summary
     */
    public static String summary(String text, int max) {
        return summary(text, max, "left");
    }

    public static String left(String text, int max) {
        if (text == null)
            text = "";
        String result = text;
        if (text.length() > max)
            result = text.substring(0, max - 3) + "...";
        return result;
    }

    public static String convertStreamToString(InputStream is) {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static String right(String text, int max) {
        if (text == null)
            text = "";
        String result = text;
        if (text.length() > max)
            result = "..." + text.substring(text.length() - max);
        return result;
    }

    public static String summary(String text, int max, String method) {
        if (method != null && method.toLowerCase().equals("right")) {
            return right(text, max);
        } else {
            return left(text, max);
        }
    }

    /**
     * �����ļ�size\��ʽ���ļ�size,��formatFileSize( 125060006, 100000, 1000 )
     *
     * @param fileSize �ļ���ʵ�ʴ�С
     * @param divied   �ֽ���,���ڶ��ٵĿ�ʼ����
     * @param unit     ����ĵ�λ�Ƕ���
     * @return ���ظ�ʽ�������ֵ
     */
    public static String formatFileSize(double fileSize, double divied, int unit) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        if (fileSize > divied) {
            double calcSize = fileSize / unit;
            return numberFormat.format(calcSize) + "M";
        } else {
            return numberFormat.format(fileSize) + "K";
        }
    }

    /**
     * ���ݿ���searchpointlistΪ,1,2,����ʽ������ȡ������ʱ�����1,2
     *
     * @return String
     */
    public static String convertSearchpointlistFromDb(String searchpointlist) {
        if (searchpointlist == null) {
            return searchpointlist;
        } else {
            String tmp = searchpointlist.substring(1,
                    searchpointlist.length() - 1);
            return tmp;
        }
    }

    /**
     * searchpointlistΪ1,2����ʽ�������,1,2,
     *
     * @param searchpointlist
     * @return String
     */
    public static String convertSearchpointlistToDb(String searchpointlist) {
        if (searchpointlist != null && searchpointlist.trim().length() > 0) {
            String tmp = "," + searchpointlist + ",";
            return tmp;
        } else {
            return searchpointlist;
        }
    }

    public static String filterSql(String sql) {
        if (sql != null) {
            int index = -1;
            while ((index = sql.indexOf("'")) > 0) {
                sql = sql.substring(0, index) + "''" + sql.substring(index + 1);
            }
        }
        return sql;
    }

    /**
     * �ö��ŷָ��ַ���
     *
     * @param source ��Ҫ�ָ���ַ���
     * @return String[]
     */
    public static String[] splitByComma(String source) {
        if (source == null)
            return null;

        List<String> buffer = new ArrayList<String>();
        int index = -1;
        while ((index = source.indexOf(",")) >= 0) {
            buffer.add(source.substring(0, index));
            source = source.substring(index + 1);
        }

        if (!source.equals("")) {
            buffer.add(source);
        }

        String[] result = new String[buffer.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) buffer.get(i);
        }

        return result;
    }

    /**
     * �ö�������
     *
     * @param source
     * @return String
     */
    public static String unionByComma(long[] source) {
        if (source == null || source.length == 0) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = source.length; i < length; i++) {
            buffer.append(",").append(source[i]);
        }
        return buffer.delete(0, 1).toString();
    }

    public static String union(String[] source, String separator) {
        if (source == null || source.length == 0) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = source.length; i < length; i++) {
            buffer.append(separator).append(source[i]);
        }
        return buffer.delete(0, 1).toString();
    }

    /**
     * ����һ��ָ����ָ��ַ���
     *
     * @param source     ��Ҫ���ָ���ַ���
     * @param separators �ָ���
     * @return String[]
     */
    public static String[] split(String source, String[] separators) {
        List<String> result = splitToList(source, separators);
        if (result != null && result.size() > 0) {
            String[] array = null;
            array = (String[]) result.toArray(new String[]{});
            return array;
        }
        return null;
    }

    /**
     * ����ַ���
     *
     * @param source     ��Ҫ���ָ���ַ���
     * @param separators �ָ���
     * @return List �ַ����б�
     */
    public static List<String> splitToList(String source, String[] separators) {
        if (source != null && separators != null) {
            List<String> result = new ArrayList<String>();
            Map<String, String> container = new HashMap<String, String>();
            for (int i = 0; i < separators.length; i++) {
                container.put(separators[i], "");
            }

            char[] chars = source.toCharArray();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < chars.length; i++) {
                if (container.containsKey(new String(new char[]{chars[i]}))) {
                    if (buffer.length() > 0) {
                        result.add(buffer.toString());
                        buffer = new StringBuffer();
                    }
                } else {
                    buffer.append(chars[i]);
                }
            }
            if (buffer.length() != 0) {
                result.add(buffer.toString());
            }

            return result;
        }

        return null;
    }

    public static String[] trimStringArr(String[] source) {
        if (source == null || source.length == 0) {
            return source;
        }
        List<String> templist = new ArrayList<String>();
        String temp = "";
        for (int i = 0; i < source.length; i++) {
            temp = source[i];
            if (!"".equals(temp.trim())) {
                templist.add(temp);
            }
        }
        String[] result = new String[templist.size()];
        for (int i = 0; i < templist.size(); i++) {
            result[i] = (String) templist.get(i);
        }
        return result;
    }
    /*public static String replace(String source, String regex, String replacement) {
		int index = -1;
		StringBuffer buffer = new StringBuffer();
		while ((index = source.indexOf(regex)) >= 0) {
			buffer.append(source.substring(0, index));
			buffer.append(replacement);
			source = source.substring(index + regex.length());
		}
		buffer.append(source);
		return buffer.toString();
	}*/

    public static String formatBlog(String blog) {
        blog = blog.replaceAll("<[^>]+>", "");
        blog = blog.replaceAll("<", "");
        blog = blog.replaceAll(">", "");
        return blog;
    }

    public static void main(String[] args) {
        System.out
                .print(formatBlog("<form name=\"frmSearch\" action=\"/epg/search/index.jsp\" method=\"post\">"
                        + "<TD align=left>"
                        + "<input name=\"searchContents\" id=\"searchContents\" type=\"text\" maxlength=\"50\" size=\"40\" class=\"myinput\" value=\"\">&nbsp;"
                        + "<input type=\"hidden\" name=\"c\" value=\"1\">"
                        + "<input type=\"button\" class=\"mysubmit\" onclick=\"onEncode(frmSearch.searchContents.value)\" value=\"�� ��\">&nbsp;&nbsp;"
                        + "</TD>" + "</form>" + "</TR>" + "</TABLE"));
    }

    public static String replace(String strSource, String strFrom, String strTo) {
        if (strSource == null) {
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuffer buf = new StringBuffer(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }

    //javaȥ���ַ����еĿո񡢻س������з����Ʊ��
    public  static String replaceBlank(String str) {
        String dest = "";
        if (str != null && str.trim().length() >0) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    //���ַ����л�ȡ����
    public  static String getNum(String str) {
        String dest = "";
        if (str != null) {
            dest = str.replaceAll("[^0-9]","");

        }
        return dest;
    }

    //���ַ����й�������
    public static String removeNum(String str) {
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
    //�滻��ģʽƥ��������ַ��������ֵ��ַ�����""�滻��
        return m.replaceAll("").trim();
    }
    
    /**
     * ��ȡǰһ��
     * @param date
     * @return
     */
    public static Date yesterDay(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	//calendar.add(Calendar.YEAR,-1);
    	calendar.add(Calendar.DATE, -1);
    	return calendar.getTime();
    }
    
	public static String md5(String str,String encode) throws UnsupportedEncodingException {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes(encode));
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	   public static String urlEncode(String obj) {
	        try {
	            return URLEncoder.encode(obj, "GBK");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        return obj;
	    }
	   
	   public static String urlDecode(String obj) {
	        try {
	            return URLDecoder.decode(obj, "GBK");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        return obj;
	    }
	   
	   public static String genKey(String signature, String content) {
	        if (content != null) {
	            content = content.replaceAll("http", "");
	            content = signature + content;
	            return md5(content);
	        }
	        return null;
	    }
	   
	   public static String md5(String str) {
	        StringBuffer buf = new StringBuffer("");
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            md.update(str.getBytes());
	            byte b[] = md.digest();
	            int i;
	            for (int offset = 0; offset < b.length; offset++) {
	                i = b[offset];
	                if (i < 0)
	                    i += 256;
	                if (i < 16)
	                    buf.append("0");
	                buf.append(Integer.toHexString(i));
	            }
	            return buf.toString();
	        } catch (NoSuchAlgorithmException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return buf.toString();
	    }
	   
	   public static String base64Decode(String content) {
//	        BASE64Decoder decoder = new BASE64Decoder();
	        String result = null;
	        try {
//	            byte[] b = decoder.decodeBuffer(content);
	            result = EncryptUtil.decrypt(content);
	        } catch (Exception e) {
	            return result;
	        }
	        return result;
	    }
	   
		public static String rebuildNickname(String nickname) {

			try {
				if (nickname != null) {
					int index = nickname.indexOf("_", 1);
					if (index > -1) {
						if (nickname.length() - index + 1 >= 8) {
							nickname = nickname.substring(0, index);
						}
					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return nickname;
		}
}
