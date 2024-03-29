package com.crawler.model;

public class Product {
	private int category1;
	private int category2;
	private String company;
	private String product_name;
	private String description;
	private int product_price;
	private int product_discount_price;
	private String target;
	private String product_img_url;
	private String product_url;
	private int item_state;
	private int option_state;
	private String options;
	
	public Product( int category1,
					int category2,
					String company,
					String product_name,
					String description,
					String target,
					int product_price,
					int product_discount_price,
					String product_url,
					String product_img_url,
					int item_state,
					int option_state,
					String options) {
		this.category1=category1;
		this.category2=category2;
		this.company = company;
		this.product_name = product_name;
		this.description=description;
		this.target=target;
		this.product_price = product_price;
		this.product_discount_price = product_discount_price;
		this.product_url = product_url;
		this.product_img_url=product_img_url;
		this.item_state=item_state;
		this.option_state=option_state;
		this.options=options;
	}
	
	public String getTarget() {
		return target;
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
	public int getItem_state() {
		return item_state;
	}
	public void setItem_state(int item_state) {
		this.item_state=item_state;
	}
	public int getOption_state() {
		return option_state;
	}
	public void setItem_state_2(int option_state) {
		this.option_state=option_state;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options=options;
	}

	@Override
	public String toString() {
		return "Product [category1=" + category1 + ", category2=" + category2 + ", company=" + company + 
					  ", product_name=" + product_name + ", description=" + description + 
					  ", product_price=" + product_price + ", product_discount_price=" + product_discount_price + 
					  ", target=" + target + ", product_img_url=" + product_img_url + 
					  ", product_url=" + product_url + ", item_state=" + item_state + 
					  ", option_state=" + option_state + ", options=" + options + "]";
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		Product temp = (Product)obj;
		
		if(this.company.equals(temp.company) 
			&& this.product_name.equals(temp.product_name)
			&& this.product_price==temp.product_price
			&& this.description==temp.description
			&& this.target==temp.target
			&& this.product_discount_price==temp.product_discount_price
			&& this.product_img_url.equals(temp.product_img_url)
			&& this.product_url.equals(temp.product_url)
			&& this.item_state==temp.item_state
			&& this.option_state==temp.option_state
			&& this.options.equals(temp.options)) {
			result = true;
		}
		
		return result;
	}
}
