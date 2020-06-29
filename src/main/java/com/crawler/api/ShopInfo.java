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
	 * @param shop_url	shopping mall URL address / ex) http://cooingkids.com/product/list.html?cate_no=92
	 * @param shop_name	shopping mall name / ex) 쿠잉키즈
	 * @param shop_description	ex) 유럽감성 어쩌구저쩌구
	 * @param category1 상품 분류1 / 상의(0)   하의(1)   한벌옷(2)   아우터(3)   잡화(4)   악세(5)   홈웨어(6)   출산(7)   장난감(8)   체험(9)
	 * @param category2 상품 분류2 / 상의(0) - 0.니트/스웨터	1.반팔							2.긴팔 					3.민소매		4.셔츠/블라우스		5.후드티셔츠  
	 * 								 하의(1) - 0.데님 팬츠 		1.코튼 팬츠						2.스포츠/기능성 팬츠	3.스커트/치마	4.반바지/숏팬츠		5.레깅스/타이즈 
	 * 							   한벌옷(2) - 0.원피스 		1.점프수트						2.기타  
	 * 							   아우터(3) - 0.후드 집업		1.재킷							2.가디건				3.패딩			4.코트
	 * 							 패션잡화(4) - 0.백팩			1.크로스백						2.에코백				3.안경 			4.캡모자/야구모자	5.비니 6.양말/기타
	 * 							 악세서리(5) - 0.시계/주얼리	1.브로치/머리핀/헤어악세서리	2.스니커즈				3.샌들/장화		4.슬리퍼/기타
	 * 							   홈웨어(6) - 0.여아 이너웨어	1.남아 이너웨어					2.잠옷 3.내복
	 * 						  출산&신생아(7) - 0.샤워용품		1.침구/가구						2.임부 속옷/임부복		3.유모차/카시트	4.젖병/목욕/기저귀
	 * 						  장난감&완구(8) - 0.장난감			1.완구							2.미니자동차 등 탈 것
	 * 						  만들기 체험(9) - 0.체험
	 * @param product_name	css selector / ex) #container>div>div>ul>li>p
	 * @param product_price	css selector / ex) #container>div>div>ul>li>span
	 * @param product_image	css selector / ex) #container>div>div>ul>li>img
	 * @param product_url	css selector / ex) #container>div>div>ul>li>a
	 * @param page_selector	css selector / ex) &page=
	 * @param page_size_selector	css selector / ex) &pageSize=
	 * @param page_size		product count in a page / 한페이지 안에 들어가는 상품 수 / 0=사용안함
	 * @param scroll_type	0== page tyle / 1=infinit scroll
	 * @return
	 */
	
	public String insertShopInfo(String shop_url,String shop_name, String shop_description, int category1, int category2, String product_name, String product_price, String product_image,String product_url, String page_selector,String page_size_selector,int page_size,int scroll_type) {
		CrawllingTarget ct = new CrawllingTarget(shop_url, shop_name, shop_description, category1, category2, product_name, product_price, product_image, product_url, page_selector,page_size_selector,page_size, scroll_type);
		return insertShopInfo(ct);
	}
}
