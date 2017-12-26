package translate;

import java.io.BufferedReader;
/**
 * Copyright (c) blackbear, Inc All Rights Reserved.
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * TranslateUtil
 * 
 * <pre>翻?工具
 * PS: 透?google translate
 * </pre>
 * 
 * @author catty
 * @version 1.0, Created on 2011/9/2
 */
public class TranslateUtil {

 protected static final String URL_TEMPLATE = "https://translate.google.cn/?hl=cn#{0}/{1}/{2}";
 protected static final String ID_RESULTBOX = "result_box";
 protected static final String ENCODING = "UTF-8";

 public static final String AUTO = "auto"; // google自?优??碓凑Z系
 public static final String TAIWAN = "zh-TW"; // 繁中
 public static final String CHINA = "zh-CN"; // ?中
 public static final String ENGLISH = "en"; // 英
 public static final String JAPAN = "ja"; // 日

 /**
  * <pre>Google翻?
  * PS: 交由google自?优??碓凑Z系
  * </pre>
  * 
  * @param text
  * @param target_lang 目?苏Z系
  * @return
  * @throws Exception
  */
 public static String translate(final String text, final String target_lang) throws Exception {
  return translate(text, AUTO, target_lang);
 }

 /**
  * <pre>Google翻?</pre>
  * 
  * @param text
  * @param src_lang ?碓凑Z系
  * @param target_lang 目?苏Z系
  * @return
  * @throws Exception
  */
 public static String translate(final String text, final String src_lang, final String target_lang)
   throws Exception {
  InputStream is = null;
  Document doc = null;
  Element ele = null;
  try {
   // create URL string
   String url = MessageFormat.format(URL_TEMPLATE,
     URLEncoder.encode(src_lang, ENCODING),
     URLEncoder.encode(target_lang, ENCODING),
     URLEncoder.encode(text, ENCODING));

   System.out.println(url);
   // connect & download html
   /*URL urls = new URL(url);
   HttpURLConnection con = (HttpURLConnection) urls.openConnection(); 
   con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
   BufferedReader breader = new BufferedReader(new InputStreamReader(con.getInputStream()));
   System.out.println(breader);
   while (true) {
	   String tmp = breader.readLine();
	   if (tmp == null) break;
	   System.out.println(tmp);
   } */
  
   
   
   Runtime rt = Runtime.getRuntime();  
   Process p = rt.exec("phantomjs.exe D:/code.js "+url);//这里我的codes.js是保存在c盘下面的phantomjs目录
   is = p.getInputStream();
   //System.out.println(is);
   // parse html by Jsoup
   doc = Jsoup.parse(is, ENCODING, "");
   ele = doc.getElementById(ID_RESULTBOX);
   System.out.println(ele);
   String result = ele.text();
   return result;

  } finally {
   IOUtils.closeQuietly(is);
   is = null;
   doc = null;
   ele = null;
  }
 }

 /**
  * <pre>Google翻?: ?中-->繁中</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String cn2tw(final String text) throws Exception {
  return translate(text, CHINA, TAIWAN);
 }

 /**
  * <pre>Google翻?: 繁中-->?中</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String tw2cn(final String text) throws Exception {
  return translate(text, TAIWAN, CHINA);
 }

 /**
  * <pre>Google翻?: 英文-->繁中</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String en2tw(final String text) throws Exception {
  return translate(text, ENGLISH, TAIWAN);
 }

 /**
  * <pre>Google翻?: 繁中-->英文</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String tw2en(final String text) throws Exception {
  return translate(text, TAIWAN, ENGLISH);
 }

 /**
  * <pre>Google翻?: 日文-->繁中</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String jp2tw(final String text) throws Exception {
  return translate(text, JAPAN, TAIWAN);
 }

 /**
  * <pre>Google翻?: 繁中-->日</pre>
  * 
  * @param text
  * @return
  * @throws Exception
  */
 public static String tw2jp(final String text) throws Exception {
  return translate(text, TAIWAN, JAPAN);
 }
 
 public static void main(String[] args) throws Exception {
	 String query = "Поиск действительно интересный";
	 System.out.println("your query:"+query);
	 String text = TranslateUtil.translate(query, TranslateUtil.AUTO, TranslateUtil.CHINA);
	 System.out.println("translate:"+text);
 }
}

  