package com.crawler.test;

import java.util.List;

import com.crawler.config.Config;
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
	
	
//	public static void main(String[] args) throws Exception {
//		Config.setConfig("C:\\Develop\\source\\Crawler\\conf\\config.conf");
//		Crawler crawler = new Crawler();
//		
//		CrawllingTarget ct = new CrawllingTarget(1,"http://cooingkids.com/product/list.html?cate_no=92",
//				 "쿠잉키즈","",
//				 "#contents > div > div > ul > li > div > div > div > p > a",
//				 "#contents > div > div > ul > li > div > div > div > ul > li.product_price",
//				 "#contents > div > div > ul > li > div > div > a > img",
//				 "#contents > div > div > ul > li > div > div > a.link",
//				 "&page=",
//				 0);
//		
//		List<Product> pList = crawler.getProductList(ct);
//		
//		for(Product p:pList) {
//			System.out.println(p.toString());
//			
//		}
//		System.out.println(String.format("product cnt : %d", pList.size()));
//	}
	
//	public static void main(String[] args) {
//		Config.setConfig(args[0]);
//		System.out.println(new com.crawler.db.Connector("service").cleanUp());
//	}
}
	