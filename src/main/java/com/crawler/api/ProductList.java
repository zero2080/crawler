package com.crawler.api;

import java.util.ArrayList;

import com.crawler.model.Product;

public class ProductList<Product> extends ArrayList<Product>{
	
	@Override
	public boolean equals(Object list) {
		boolean result = false;
		@SuppressWarnings("unchecked")
		ArrayList<Product> temp = (ArrayList<Product>)list;
		
		for(int i =0;i<this.size();i++) {
			if(!((Product)this.get(i)).equals(temp.get(i))) {
				result = false;
				break;
			}
			result = true;
		}
		
		return result;
	}
}
