package com.crawler.config;

public class CrawllingTarget {
	//shop info	
	private int seq;
	private String shop_url;
	private String shop_name;
	private String shop_description;
	
	//css selector
	private String product_name;
	private String product_price;
	private String product_image;
	private String product_url;
	
	// URL page qyery / ex) &page=
	private String page_selector;
	
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
	
	public String getShop_description() {
		return shop_description;
	}
	
	public String getProduct_name() {
		return product_name;
	}

	public String getProduct_price() {
		return product_price;
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

	public int getScroll_type() {
		return scroll_type;
	}

	public CrawllingTarget(int seq, String shop_url, String shop_name, String shop_description, String product_name, String product_price, String product_image,
			String product_url, String page_selector, int scroll_type) {
		this.seq=seq;
		this.shop_url = shop_url;
		this.shop_name = shop_name;
		this.shop_description=shop_description;
		this.product_name = product_name;
		this.product_price = product_price;
		this.product_image = product_image;
		this.product_url = product_url;
		this.page_selector = page_selector;
		this.scroll_type = scroll_type;
	}

	@Override
	public String toString() {
		return "CrawllingTarget [seq=" + seq + ", shop_url=" + shop_url + ", shop_name=" + shop_name + ", product_name=" + product_name
				+ ", product_price=" + product_price + ", product_image=" + product_image + ", product_url=" + product_url + ", page_selector="
				+ page_selector + ", scroll_type=" + scroll_type + "]";
	}
}
