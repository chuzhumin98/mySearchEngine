package server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import net.sf.json.JSONObject;
import server.IndexTable;
import translate.TranslateUtil;



public class LuceneServer extends HttpServlet{
	public static final int PAGE_RESULT=10;
	String predQuery = "";
	public LuceneServer(){
		super();
		System.out.println("hello for visit me!");
	}
	
	/*
	 * 展示当前页面的网页
	 */
	public ScoreDoc[] showList(ScoreDoc[] allResult,int page) {
		if(allResult == null || allResult.length < (page-1) * PAGE_RESULT){
			System.err.println("no result in this page");
			return null;
		}
		int start = Math.max((page-1) * PAGE_RESULT, 0);
		int docnum = Math.min(allResult.length - start, PAGE_RESULT);
		ScoreDoc[] pageResult = new ScoreDoc[docnum];
		for(int i = 0; i < docnum; i++) {
			pageResult[i] = allResult[start + i];
		}
		return pageResult;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String pageString = request.getParameter("page"); //页编号信息
		String methodString = request.getParameter("method");
		int searchMethod = 2; //通过前端过来的数据分析出方法
		if (methodString != null) {
			searchMethod = Integer.parseInt(methodString);
		}
		System.out.println("searchMethod:"+searchMethod);
		String[] fields = IndexTable.getFields(searchMethod);
		
		int page = 1;
		if(pageString != null){
			page = Integer.parseInt(pageString);
		}
		if (searchMethod != 7) { //不为多域检索的处理方式
			System.out.println("Hello for visit simple query");
			String queryString = request.getParameter("query"); //query 信息
			if(queryString == null){
				System.err.println("the query is null");
				//request.getRequestDispatcher("/Image.jsp").forward(request, response);
			} else {
				String myQuery = queryString;
				if (searchMethod == 8) {
					//System.out.println("your query:"+query);
					try {
						myQuery = TranslateUtil.translate(queryString, TranslateUtil.AUTO, TranslateUtil.CHINA);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("translate:"+myQuery);
				}
				if (searchMethod == 9) {
					//System.out.println("your query:"+query);
					try {
						myQuery = TranslateUtil.translate(queryString, TranslateUtil.AUTO, TranslateUtil.ENGLISH);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("translate:"+myQuery);
				}
				JSONObject jsonTotal = new JSONObject();
				System.out.println("your query:" + queryString);
				IndexTable table = IndexTable.getInstance();
				ScoreDoc[] allResult = table.getSearchResult(searchMethod, myQuery);
				if (allResult != null) {
					ScoreDoc[] hits = showList(allResult, page);
					if (hits != null) {
						int docNum = Math.min(PAGE_RESULT, hits.length);
						for (int k = 0; k < fields.length; k++) {
							String[] fieldResult = new String [docNum];
							for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
								Document doc = IndexTable.myEngine[searchMethod].getDoc(hits[i].doc);
								//输出高亮之后的文本
								fieldResult[i] = IndexTable.myEngine
										[searchMethod].hightLightString(myQuery, doc.get(fields[k]));
							}
							jsonTotal.put(fields[k], fieldResult);
						}	
					} else {
						System.out.println("page null");
					}
				}else{
					System.out.println("result null");
				}
				System.out.println("json:"+jsonTotal.toString());
				
				request.setAttribute("resultNum", allResult.length);
				request.setAttribute("currentQuery",queryString);
				request.setAttribute("currentPage", page);
				request.setAttribute("searchMethod", searchMethod);
				request.setAttribute("results", jsonTotal);
				request.getRequestDispatcher("/resultShow.jsp").forward(request,
						response);
			}
		} else { //为多域检索的处理方式
			System.out.println("hello for visit multi field query!");
			String[] querys = new String [4];
			boolean isNull = true;
			for (int i = 0; i < 4; i++) {
				querys[i] = request.getParameter("query"+(i+1));
				if (querys[i] != null && !querys[i].equals("")) {
					isNull = false;
				} else {
					querys[i] = "";
				}
			}
			if(isNull){
				System.err.println("the query is null");
				//request.getRequestDispatcher("/Image.jsp").forward(request, response);
			} else {
				JSONObject jsonTotal = new JSONObject();
				System.out.println("your query:" + querys[0] + "...");
				IndexTable table = IndexTable.getInstance();
				ScoreDoc[] allResult = table.getSearchResult(searchMethod, querys);
				if (allResult != null) {
					ScoreDoc[] hits = showList(allResult, page);
					if (hits != null) {
						int docNum = Math.min(PAGE_RESULT, hits.length);
						for (int k = 0; k < fields.length; k++) {
							String[] fieldResult = new String [docNum];
							for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
								Document doc = IndexTable.myEngine[searchMethod].getDoc(hits[i].doc);
								//输出高亮之后的文本
								fieldResult[i] = doc.get(fields[k]);
								//fieldResult[i] = IndexTable.myEngine
										//[searchMethod].hightLightString(querys, doc.get(fields[k]));
							}
							jsonTotal.put(fields[k], fieldResult);
						}	
					} else {
						System.out.println("page null");
					}
				}else{
					System.out.println("result null");
				}
				System.out.println("json:"+jsonTotal.toString());
				
				request.setAttribute("resultNum", allResult.length);
				request.setAttribute("currentQuery",querys);
				request.setAttribute("currentPage", page);
				request.setAttribute("searchMethod", searchMethod);
				request.setAttribute("results", jsonTotal);
				request.getRequestDispatcher("/multiResultShow.jsp").forward(request,
						response);
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
