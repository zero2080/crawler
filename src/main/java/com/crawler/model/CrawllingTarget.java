package com.crawler.model;

public class CrawllingTarget {
	//shop info	
	private int seq;
	private String shop_url;
	private String shop_name;
	private String shop_description;
	private String target;
	private int category1;
	private int category2;
	
	//css selector
	private String product_name;
	private String product_price;
	private String product_discount_price;
	private String product_image;
	private String product_url;
	
	// URL page qyery / ex) &page=
	private String page_selector;
	private String page_size_selector;
	
	// product count in a page / 0 = not used  
	private int page_size;
	
	/*
	 * 0 = page number type
	 * 1 = infinit scroll type
	 */
	private int scroll_type;

	public int getSeq() {
		return this.seq;
	}
	public String getShop_url() {
		return shop_url;
	}
	
	public String getShop_name() {
		return shop_name;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getShop_description() {
		return shop_description;
	}
	
	public int getCategory1() {
		return category1;
	}
	
	public int getCategory2() {
		return category2;
	}
	
	public String getProduct_name() {
		return product_name;
	}

	public String getProduct_price() {
		return product_price;
	}

	public String getProduct_discount_price() {
		return product_discount_price;
	}
	
	public String getProduct_image() {
		return product_image;
	}

	public String getProduct_url() {
		return product_url;
	}

	public String getPage_selector() {
		return page_selector;
	}
	
	public String getPage_size_selector() {
		return page_size_selector;
	}
	
	public int getPage_size() {
		return page_size;
	}

	public int getScroll_type() {
		return scroll_type;
	}
	
	public CrawllingTarget(String shop_url, String shop_name, String shop_description, String target, int category1, int category2, String product_name, String product_price, String product_discount_price, String product_image,
			String product_url, String page_selector, String page_size_selector, int page_size, int scroll_type) {
		this.shop_url = shop_url;
		this.shop_name = shop_name;
		this.shop_description=shop_description;
		this.target=target;
		this.category1 = category1;
		this.category2 = category2;
		this.product_name = product_name;
		this.product_price = product_price;
		this.product_discount_price = product_discount_price;
		this.product_image = product_image;
		this.product_url = product_url;
		this.page_selector = page_selector;
		this.page_size_selector = page_size_selector;
		this.page_size = page_size;
		this.scroll_type = scroll_type;
	}

	public CrawllingTarget(int seq, String shop_url, String shop_name, String shop_description, String target, int category1, int category2, String product_name, String product_price, String product_discount_price, String product_image,
			String product_url, String page_selector,String page_size_selector, int page_size, int scroll_type) {
		this.seq=seq;
		this.shop_url = shop_url;
		this.shop_name = shop_name;
		this.shop_description=shop_description;
		this.target=target;
		this.category1 = category1;
		this.category2 = category2;
		this.product_name = product_name;
		this.product_price = product_price;
		this.product_discount_price = product_discount_price;
		this.product_image = product_image;
		this.product_url = product_url;
		this.page_selector = page_selector;
		this.page_size_selector = page_size_selector;
		this.page_size = page_size;
		this.scroll_type = scroll_type;
	}
	
	@Override
	public String toString() {
		return "CrawllingTarget [seq=" + seq + ", shop_url=" + shop_url + ", shop_name=" + shop_name
				+ ", shop_description=" + shop_description + ", category1=" + category1 + ", category2=" + category2
				+ ", product_name=" + product_name + ", product_price=" + product_price + ", product_discount_price="
				+ product_discount_price + ", product_image=" + product_image + ", product_url=" + product_url
				+ ", page_selector=" + page_selector + ", page_size_selector=" + page_size_selector + ", page_size="
				+ page_size + ", scroll_type=" + scroll_type + "]";
	}
}
