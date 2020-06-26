package com.crawler.api;

import com.crawler.db.Connector;
import com.crawler.model.CrawllingTarget;

public class ShopInfo {
	Connector conn = null;
	String sql = null;

	/*
	 * @param driver ex) org.mariadb.jdbc.Driver
	 * @param url	 ex) jdbc:mysql://localhost:3306/crawl?characterEncoding=UTF-8
	 * @param id	 ex) crawler
	 * @param pw	 ex) crawler
	 */
	public ShopInfo(String driver,String url, String id, String pw) {
		conn = new Connector(driver,url,id,pw);
	}
	
	public String insertShopInfo(CrawllingTarget ct) {
		String result = "";
		
		if(conn.insertShopInfo(ct)) {
			result = "insert success";
		}else {
			result = "insert fail";
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param shop_url	shopping mall URL address / ex) http://cooingkids.com/product/list.html?cate_no=92
	 * @param shop_name	shopping mall name / ex) 쿠잉키즈
	 * @param shop_description	ex) 유럽감성 어쩌구저쩌구
	 * @param product_name	css selector / ex) #container>div>div>ul>li>p
	 * @param product_price	css selector / ex) #container>div>div>ul>li>span
	 * @param product_image	css selector / ex) #container>div>div>ul>li>img
	 * @param product_url	css selector / ex) #container>div>div>ul>li>a
	 * @param page_selector	css selector / ex) &page=
	 * @param scroll_type	0== page tyle / 1=infinit scroll
	 * @return
	 */
	
	public String insertShopInfo(String shop_url,String shop_name, String shop_description, String product_name, String product_price, String product_image,String product_url, String page_selector,int scroll_type) {
		CrawllingTarget ct = new CrawllingTarget(shop_url, shop_name, shop_description, product_name, product_price, product_image, product_url, page_selector, scroll_type);
		return insertShopInfo(ct);
	}
}
