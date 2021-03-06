
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
		
		int searchMethod = 2; //通过前端过来的数据分析出方法
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
					for (int k = 0; k < fields.length; k++) {
						String string = "";
						for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
							Document doc = IndexTable.myEngine[searchMethod].getDoc(hits[i].doc);
							string = doc.get(fields[k]);
						}
						jsonTotal.put(fields[k], string);
					}	
				} else {
					System.out.println("page null");
				}
			}else{
				System.out.println("result null");
			}
			System.out.println("json:"+jsonTotal.toString());
			request.setAttribute("currentQuery",queryString);
			request.setAttribute("currentPage", page);
			for (int k = 0; k < fields.length; k++) {
				request.setAttribute(fields[k], jsonTotal.get(fields[k]));
			}
			request.getRequestDispatcher("/imageshow.jsp").forward(request,
					response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
