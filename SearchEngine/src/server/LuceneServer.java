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



public class LuceneServer extends HttpServlet{
	public static final int PAGE_RESULT=10;
	int searchMethod = 0; //通过前端过来的数据分析出方法
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
		String queryString = request.getParameter("query"); //query 信息
		String pageString = request.getParameter("page"); //页编号信息
		
		if (!queryString.equals(predQuery)) {
			searchMethod = (searchMethod+1)%6; //改变查询时更换方法
			this.predQuery = queryString;
		}
		System.out.println("searchMethod:"+searchMethod);
		String[] fields = IndexTable.getFields(searchMethod);
		
		int page = 1;
		if(pageString!=null){
			page = Integer.parseInt(pageString);
		}
		if(queryString == null){
			System.err.println("the query is null");
			//request.getRequestDispatcher("/Image.jsp").forward(request, response);
		} else {
			JSONObject jsonTotal = new JSONObject();
			System.out.println("your query:" + queryString);
			IndexTable table = IndexTable.getInstance();
			ScoreDoc[] allResult = table.getSearchResult(searchMethod, queryString);
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
									[searchMethod].hightLightString(queryString, doc.get(fields[k]));
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
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
