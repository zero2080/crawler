package com.crawler.test;

import java.util.List;

import com.crawler.config.Config;
import com.crawler.db.Connector;
import com.crawler.main.Crawler;
import com.crawler.model.CrawllingTarget;
import com.crawler.model.Product;

public class Test {
	
//	public static void main(String[] args) throws Exception{
//		Config.getInstance(args[0]);
//		Connector conn = new Connector("target");
//		System.out.println("======== Test method ========");
//		System.out.println("Config Parsing");
//		System.out.println("OS : "				+ Config.OS);
//		System.out.println("RootPath : " 		+ Config.ROOTPATH);
//		System.out.println("CrawllingDriver : "	+ Config.BROWSERDRVIER);
//		System.out.println("=============================  DataBase  ===========================");
//		System.out.println("URL : "		+ Config.TARGETURL);
//		System.out.println("ID : "		+ Config.TARGETID);
//		System.out.println("PW : "		+ Config.TARGETPW);
//		System.out.println("Query : "	+ Config.TARGETVERIFYQUERY);
//		System.out.println("Driver : "	+ Config.TARGETDRIVER);
//		System.out.println("=============================  Thread  ===========================");
//		System.out.println("Size : " + Config.THREADCNT);
//		System.out.println("a thread in row : " + Config.THREADUNIT);
//
//		Map<Integer,List<CrawllingTarget>> result = new HashMap<Integer,List<CrawllingTarget>>();
//		
//		for(int i=0;i<Config.THREADCNT;i++) {
//			List<CrawllingTarget> list = conn.getTargetList(i);
//			if(list.size()>0) {
//				result.put(i,list);
//			}
//			if(list.size()<Config.THREADUNIT) {
//				break;
//			}
//		}
//		
//		System.out.println();
//		System.out.println("Crawlling target count : "+result.size());
//
//		conn.connClose();
//	}
	
	
	public static void main(String[] args) throws Exception {
		Config.setConfig("C:\\Develop\\source\\conf\\config.conf");
		Crawler crawler = new Crawler();
		
//		CrawllingTarget ct = new CrawllingTarget(1,"http://www.lumiground.com/product/list.html?cate_no=91",
//				 "루미그라운드","","1 2 3",0,1,
//				 "#contents > div > div > ul > li",
//				 ".xans-record- > div.description > strong > a > span:nth-child(2)",
//				 ".xans-record- > div.description > ul > li:nth-child(1) > span:nth-child(2)",
//				 ".xans-record- > div.description > ul > li:nth-child(2) > span",
//				 ".prdImg > a > img",".prdImg > a","{\"option1\":\"table.xans-element-> tbody:nth-of-type(2) >tr>td>ul>li:nth-of-type(%d)\",  \"option2\":\"table.xans-element-> tbody:nth-of-type(3) >tr>td>ul>li:nth-of-type(%d)\", \"option3\":\"null\" }",
//				 "#product_option_id1 > option","#product_option_id2 > option","#product_option_id3 > option",
//				 0,"#totalProducts > table > tbody.option_products > tr%s > td.right > span > span",null,
//				 "&page=",null,0,
//				 0);
//		CrawllingTarget ct = new CrawllingTarget(1,"https://www.naturelovemere.co.kr/goods/goods_list.php?cateCd=002",
//				"네러메몰(네이쳐러브메레)","","0 1",)

		String product_list_url = "http://www.jilmajae.com/category/%EA%B8%B0%ED%9A%8D%EC%84%B8%ED%8A%B8/48/";
		Connector conn = new Connector("target");
		CrawllingTarget ct = conn.getTestList(product_list_url);
		
		List<Product> pList = crawler.getProductList(ct);
		
		for(Product p:pList) {
			System.out.println(p.toString());
			
		}
		System.out.println(String.format("product cnt : %d", pList.size()));
	}
	
//	public static void main(String[] args) {
//		Config.setConfig(args[0]);
//		System.out.println(new com.crawler.db.Connector("service").cleanUp());
//	}
}
	