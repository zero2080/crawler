package com.crawler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crawler.config.Config;
import com.crawler.config.CrawllingTarget;
import com.crawler.model.Product;

public class Connector {
	private static final Logger log = LogManager.getLogger(Connector.class);
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public Connector(String target){
		try {
			
			if(target.equals("target")) {
				//크롤링 대상 정보가 있는 DB connection생성
				Class.forName(Config.TARGETDRIVER);
				conn = DriverManager.getConnection(	Config.TARGETURL,
													Config.TARGETID,
													Config.TARGETPW);
				pstmt = conn.prepareStatement(Config.TARGETVERIFYQUERY);
			}else if(target.equals("service")) {
				//크롤링 결과를 저장할 DB connection생성
				Class.forName(Config.SERVICEDRIVER);
				conn = DriverManager.getConnection(	Config.SERVICEURL,
													Config.SERVICEID,
													Config.SERVICEPW);
				pstmt = conn.prepareStatement(Config.SERVICEVERIFYQUERY);
			}
			
			rs = pstmt.executeQuery();
			
			//veryfing
			if(rs.next()) {
				log.debug(String.format("DB Connect - %s",rs.getString(1)));
				log.debug(String.format("DB URL = %s",target.equals("target")?Config.TARGETURL:Config.SERVICEURL));
			}else {
				System.out.println("create connection error!!");
			}
		}catch(Exception e) {
			log.error(String.format("!!!! create connection error !!!! - ", e.getMessage()));
		}finally {
			close();
		}
	}
	
	public Connection getConnection() {
		if(conn==null) {
			log.error("connection closed");
			return null;
		}
		return conn;
	}
	
	public List<CrawllingTarget> getTargetList(int round){
		if(conn==null) {
			log.info(String.format("not create connector. create connector first"));
			return null;
		}
		List<CrawllingTarget> targetList = new ArrayList<CrawllingTarget>();
		
		String sql = "SELECT * FROM target_info LIMIT ?,?";
		
		log.debug(String.format("query = SELECT * FROM target_info LIMIT %d,%d", round*Config.THREADUNIT,Config.THREADUNIT));
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, (round*Config.THREADUNIT));
			pstmt.setInt(2, Config.THREADUNIT);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int i = 1;
				CrawllingTarget target = new CrawllingTarget(
												rs.getInt(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getInt(i++)
											);
				targetList.add(target);
			}
			
			
		}catch(Exception e) {
			log.error(String.format("!!!! query error !!!! - %s", e.getMessage()));
			e.printStackTrace();
		}finally {
			close();
		}
		return targetList;
	}
	
//	public void insertCrawllingResult(List<?> list) {
	public void insertCrawllingResult(List<Product> list) {
		if(list==null || list.size()==0) {
			log.info(String.format("Crawlling target is empty"));
			return;
		}
		String sql = "INSERT INTO item_sales (	category1,	" + 
											"	category2,	" + 
											"	item_price,	" + 
											"	shop_name,	" + 
											"	item_name,	" + 
											"	thumbnail_img," + 
											"	detail_url,	" + 
											"	create_date )" +
									"VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					for(Product pro:list) {
						int i = 1;
						pstmt=conn.prepareStatement(sql);
						pstmt.setInt(i++,0);
						pstmt.setInt(i++,0);
						pstmt.setInt(i++,pro.getProduct_price());
						pstmt.setString(i++,pro.getCompany());
						pstmt.setString(i++,pro.getProduct_name());
						pstmt.setString(i++,pro.getProduct_img_url());
						pstmt.setString(i++,pro.getProduct_url());
						
						pstmt.executeUpdate();
					}
					log.info(String.format("insert company : %s / cnt : %d", list.get(0).getCompany(),list.size()));
				}catch(Exception e) {
					log.error(String.format("error MSG : %s", e.getMessage()));
					e.printStackTrace();
				}
			}
		};
		run.run();
	}
	
	private void close() {
		try {
			rs.close();
		}catch(Exception e) {
			log.error(String.format("!!!! ResultSet closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
		try {
			pstmt.close();
		}catch(Exception e) {
			log.error(String.format("!!!! PreparedStatement closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public void connClose() {
		try {
			conn.close();
		}catch(Exception e) {
			log.error(String.format("!!!! connector closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
	}
}
