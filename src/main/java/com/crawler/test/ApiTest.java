package com.crawler.test;

import com.crawler.api.ShopInfo;
import com.crawler.model.CrawllingTarget;

public class ApiTest {
	public static void main(String[] args) {

		// api 객체 생성
		ShopInfo si = new ShopInfo("org.mariadb.jdbc.Driver",
				"jdbc:mysql://localhost:3306/crawl?characterEncoding=UTF-8", "crawler", "crawler");

		String result = si.insertShopInfo("http://cooingkids.com/product/list.html?cate_no=92", "cooingkids", "kids", 0, 0,
				"#contents > div > div > ul > li > div > div > div > p > a",
				"#contents > div > div > ul > li > div > div > div > ul > li.product_price",
				"#contents > div > div > ul > li > div > div > a > img",
				"#contents > div > div > ul > li > div > div > a.link", "&page=",null,0, 0);

		System.out.println("String Parameter : " + result);

		// 객체로 넘겨도됨
		CrawllingTarget ct = new CrawllingTarget("http://cooingkids.com/product/list.html?cate_no=92", "cooingkids",
				"kids", 0, 0, "#contents > div > div > ul > li > div > div > div > p > a",
				"#contents > div > div > ul > li > div > div > div > ul > li.product_price",
				"#contents > div > div > ul > li > div > div > a > img",
				"#contents > div > div > ul > li > div > div > a.link", "&page=",null,0, 0);

		System.out.println("Obaject Parameter : " + si.insertShopInfo(ct));

	}
}
