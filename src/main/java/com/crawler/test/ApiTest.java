package com.crawler.test;

import com.crawler.api.ShopInfo;
import com.crawler.model.CrawllingTarget;

public class ApiTest {
	public static void main(String[] args) {

		// api 객체 생성
		ShopInfo si = new ShopInfo("org.mariadb.jdbc.Driver",
				"jdbc:mysql://localhost:3306/crawl?characterEncoding=UTF-8", "crawler", "crawler");

//		String result = si.insertShopInfo(
//				"http://cooingkids.com/product/list.html?cate_no=92", "cooingkids",
//				"kids","target", 0, 0,
//				"#contents > div > div > ul > li > div > div > div > p > a",
//				"#contents > div > div > ul > li > div > div > div > ul > li.product_price",
//				"#contents > div > div > ul > li > div > div > div > ul > li.product_price",
//				"#contents > div > div > ul > li > div > div > a > img",
//				"#contents > div > div > ul > li > div > div > a.link",
//				0,
//				"#contents > div > div > ul > li > div > div > div > ul > li:nth-child(1)",
//				"#contents > div > div > ul > li > div > div > div > ul > li:nth-child(2)",
//				"#contents > div > div > ul > li > div > div > div > ul > li:nth-child(3)",
//				"&page=",null,0, 0);
//
//		System.out.println("String Parameter : " + result);

		
		// 객체로 넘겨도됨
		CrawllingTarget ct = new CrawllingTarget(
				"http://cooingkids.com/product/list.html?cate_no=92", "cooingkids",
				"kids","target", 0, 0,
				"#contents > div > div > ul > li",
				"div > div > div > p > a",
				"div > div > div > ul > li.product_price",
				"div > div > div > ul > li.product_price",
				"div > div > a > img",
				"div > div > a.link",
				0,
				"#product_option_id1 > option",
				"#product_option_id2 > option",
				null,
				"&page=",null,0, 0);
//		CrawllingTarget ct = new CrawllingTarget(
//				"http://woimam.com/shop/shopbrand.html?type=O&xcode=014", "워아이맘",
//				"키즈,주니어옷 스타일맨토,셔츠,타이,자켓,점퍼,팬츠,슈즈","0 1 2 3", 1, 1,
//				"#prdBrand > div > dl > dd > ul > li > div.name",
//				"#prdBrand > div > dl > dd > ul > li > div > span:nth-child(2)",
//				"#prdBrand > div > dl > dd > ul > li > div > span:nth-child(1) > strike",
//				"#prdBrand > div > dl > dt > a > img",
//				"#prdBrand > div > dl > dt > a",
//				0,
//				"#form1 > div > div.table-opt > table > tbody > tr:nth-child(4) > td > div > dl:nth-child(1) > dd > select > option",
//				"#form1 > div > div.table-opt > table > tbody > tr:nth-child(4) > td > div > dl:nth-child(2) > dd > select > option",
//				null,
//				"&page=",null,0, 0);
		System.out.println("Obaject Parameter : " + si.insertShopInfo(ct));

	}
}
