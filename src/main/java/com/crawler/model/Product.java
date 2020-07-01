package com.crawler.model;

public class Product {
	private int category1;
	private int category2;
	private String company;
	private String product_name;
	private String description;
	private int product_price;
	private int product_discount_price;
	private String product_img_url;
	private String product_url;
	
	public Product( int category1,
					int category2,
					String company, 
					String product_name,
					String description,
					int product_price,
					int product_discount_price,
					String product_url,
					String product_img_url) {
		this.category1=category1;
		this.category2=category2;
		this.company = company;
		this.product_name = product_name;
		this.description=description;
		this.product_price = product_price;
		this.product_discount_price = product_discount_price;
		this.product_url = product_url;
		this.product_img_url=product_img_url;
	}
	
	public int getCategory1() {
		return category1;
	}

	public void setCategory1(int category1) {
		this.category1 = category1;
	}

	public int getCategory2() {
		return category2;
	}
	public void setCategory2(int category2) {
		this.category2 = category2;
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
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description=description;
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
		return "Product [category1=" + category1 + ", category2=" + category2 + ", company=" + company
				+ ", product_name=" + product_name + ", description=" + description + ", product_price=" + product_price
				+ ", product_discount_price=" + product_discount_price + ", product_img_url=" + product_img_url
				+ ", product_url=" + product_url + "]";
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		Product temp = (Product)obj;
		
		if(this.company.equals(temp.company) 
			&& this.product_name.equals(temp.product_name)
			&& this.product_price==temp.product_price
			&& this.description==temp.description
			&& this.product_discount_price==temp.product_discount_price
			&& this.product_img_url.equals(temp.product_img_url)
			&& this.product_url.equals(temp.product_url) ) {
			result = true;
		}
		
		return result;
	}
}
