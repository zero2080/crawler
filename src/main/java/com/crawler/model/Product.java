package com.crawler.model;

public class Product {
	private String company;
	private String product_name;
	private int product_price;
	private int product_discount_price;
	private String product_img_url;
	private String product_url;
	
	public Product(String company, String product_name, int product_price, int product_discount_price,
			String product_url,String product_img_url) {
		this.company = company;
		this.product_name = product_name;
		this.product_price = product_price;
		this.product_discount_price = product_discount_price;
		this.product_url = product_url;
		this.product_img_url=product_img_url;
	}
	
	public String getProduct_img_url() {
		return product_img_url;
	}
	public void setProduct_img_url(String product_img_url) {
		this.product_img_url=product_img_url;
	}
	public String getCompany() {
		return company;
	}
	public void setCompay(String company) {
		this.company = company;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public int getProduct_price() {
		return product_price;
	}
	public void setProduct_price(int product_price) {
		this.product_price = product_price;
	}
	public int getProduct_discount_price() {
		return product_discount_price;
	}
	public void setProduct_discount_price(int product_discount_price) {
		this.product_discount_price = product_discount_price;
	}
	public String getProduct_url() {
		return product_url;
	}
	public void setProduct_url(String product_url) {
		this.product_url = product_url;
	}
	
	@Override
	public String toString() {
		return "Model [company=" + company + ", product_name=" + product_name + ", product_price=" + product_price
				+ ", product_discount_price=" + product_discount_price + ", product_img_url=" + product_img_url 
				+ ", product_url=" + product_url + "]";
	}
}
