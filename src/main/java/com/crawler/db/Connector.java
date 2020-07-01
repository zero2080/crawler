package com.crawler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crawler.config.Config;
import com.crawler.model.CrawllingTarget;
import com.crawler.model.Product;

public class Connector {
	private static final Logger log = LogManager.getLogger(Connector.class);
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public Connector(String driver,String url, String id, String pw) {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,id,pw);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Connector(String target){
		try {
			
			if(target.equals("target")) {
				//크롤링 대상 정보가 있는 DB connection생성
				//쇼핑몰 정보 입력할때도 target을 사용
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
			
			conn.setAutoCommit(false);
			rs = pstmt.executeQuery();
			
			//veryfing
			if(rs.next()) {
				log.debug(String.format("DB Connect - %s / URL = %s",rs.getString(1),target.equals("target")?Config.TARGETURL:Config.SERVICEURL));
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
		String sql = "SELECT * FROM target_info ORDER BY seq LIMIT ?,?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			int startRow = (round+Config.THREADROUND)*Config.THREADUNIT;
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, Config.THREADUNIT);
			
			rs = pstmt.executeQuery();
			
			log.debug(String.format("query = SELECT * FROM target_info ORDER BY seq LIMIT %d,%d",startRow,Config.THREADUNIT));
			
			while(rs.next()) {
				int i = 1;
				CrawllingTarget target = new CrawllingTarget(
												rs.getInt(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getInt(i++),
												rs.getInt(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getString(i++),
												rs.getInt(i++),
												rs.getInt(i++)
											);
				targetList.add(target);
			}
			Thread.sleep(10);
		} catch(Exception e) {
			log.error(String.format("!!!! query error !!!! - %s", e.getMessage()));
			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return targetList;
	}
	
//	public void insertCrawllingResult(List<?> list) {
	public synchronized boolean insertCrawllingResult(List<Product> list) {
		boolean result = false;
		if(list==null || list.size()==0) {
			log.info(String.format("Crawlling target is empty"));
			return true;
		}
		
		Runnable2 runnable = new Runnable2(list);
		
		runnable.run();
		
		while(true) {
			if(runnable.getResult()) {
				result = runnable.getResult();
				break;
			}
		}
		return result;
	}
	
	/*
	 * API
	 */
	public boolean insertShopInfo(CrawllingTarget ct) {
		boolean result=false;
		String sql = "INSERT INTO target_info (shop_url," + 
											  "shop_name," +
											  "shop_description," +
											  "target," +
											  "category1," +
											  "category2," + 
											  "product_name, " + 
											  "product_price, " +
											  "product_discount_price, " +
											  "product_image, " + 
											  "product_url, " + 
											  "page_selector, " +
											  "page_size_selector, " +
											  "page_size, " +
											  "scroll_type)" + 
								 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int i = 1;
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(i++, ct.getShop_url()); 
			pstmt.setString(i++, ct.getShop_name());
			pstmt.setString(i++, ct.getShop_description());
			pstmt.setString(i++, ct.getTarget());
			pstmt.setInt(i++, ct.getCategory1());
			pstmt.setInt(i++, ct.getCategory2());
			pstmt.setString(i++, ct.getProduct_name());
			pstmt.setString(i++, ct.getProduct_price());
			pstmt.setString(i++, ct.getProduct_discount_price());
			pstmt.setString(i++, ct.getProduct_image());
			pstmt.setString(i++, ct.getProduct_url());
			pstmt.setString(i++, ct.getPage_selector());
			pstmt.setString(i++, ct.getPage_size_selector());
			pstmt.setInt(i++, ct.getPage_size());
			pstmt.setInt(i++, ct.getScroll_type());
			
			result = pstmt.executeUpdate()==1;
			
			conn.commit();
		}catch(Exception e) {
			rollback();
			e.printStackTrace();
		}finally {
			close();
		}
		
		return result;
	}
	
	public void createTmpTable() throws Exception{
		//임시테이블 생성
		String sql = "CREATE TABLE `tmp_" + Config.SERVICETABLE + "` (" + 
										"  `num` int(11) NOT NULL AUTO_INCREMENT," + 
										"  `category1` int(5) NOT NULL DEFAULT 0 COMMENT '상의(0)   하의(1)   한벌옷(2)   아우터(3)   잡화(4)   악세(5)   홈웨어(6)   출산(7)   장난감(8)   체험(9)'," + 
										"  `category2` int(5) NOT NULL DEFAULT 0 COMMENT '(0)상의 - 0.니트/스웨터 1.반팔 2.긴팔 3.민소매 4.셔츠/블라우스 5.후드티셔츠  //  (1)하의 - 0.데님 팬츠 1.코튼 팬츠 2.스포츠/기능성 팬츠 3.스커트/치마 4.반바지/숏팬츠 5.레깅스/타이즈  //  (2)한벌옷 - 0.원피스 1.점프수트 2.기타  //  (3)아우터 - 0.후드 집업 1.재킷 2.가디건 3.패딩 4.코트  //  (4)패션잡화 - 0.백팩 1.크로스백 2.에코백 3.안경 4.캡모자/야구모자 5.비니 6.양말/기타  //  (5)악세서리 - 0.시계/주얼리 1.브로치/머리핀/헤어악세서리 2.스니커즈 3.샌들/장화 4.슬리퍼/기타  //  (6)홈웨어 - 0.여아 이너웨어 1.남아 이너웨어 2.잠옷 3.내복  //  (7)출산&신생아 - 0.샤워용품 1.침구/가구 2.임부 속옷/임부복 3.유모차/카시트 4.젖병/목욕/기저귀  //  (8)장난감&완구 - 0.장난감 1.완구 2.미니자동차 등 탈 것  //  (9)만들기 체험 - 0. 체험'," + 
										"  `item_price` int(10) NOT NULL DEFAULT 0 COMMENT '가격'," +
										"  `discount_price` int(10) NOT NULL DEFAULT 0 COMMENT '할인가격'," +
										"  `shop_name` varchar(50) NOT NULL COMMENT '판매자 ID'," + 
										"  `sales_target` varchar(15) NOT NULL COMMENT '신생아(0) 유아(1) 키즈(2) 주니어(3) // 중복가능 스페이스바로 구분'," + 
										"  `item_name` varchar(100) NOT NULL," + 
										"  `thumbnail_img` mediumtext NOT NULL COMMENT '썸네일 이미지'," + 
										"  `detail_url` mediumtext NOT NULL COMMENT '상세화면 URL'," + 
										"  `create_date` datetime NOT NULL," + 
										"  PRIMARY KEY (`num`)" + 
										")";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			conn.commit();
			log.info("create tmp_table success");
			
			Date date = new Date();
			
			System.out.println(String.format("cteate table now : %s",date));
			
		}catch(Exception e) {
			rollback();
			throw new Exception("Temp table Already exists");
		}finally {
			close();
		}
	}

	public void dropTmpTable() {
		String sql = "DROP TABLE tmp_"+Config.SERVICETABLE;
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			log.debug(e.getMessage());
		}finally {
			close();
		}
	}
	
	public void deleteTmpTable() {
		String sql = "DELETE FROM tmp_"+Config.SERVICETABLE;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("ALTER TABLE tmp_"+Config.SERVICETABLE+" AUTO_INCREMENT = 1");
			pstmt.executeUpdate();
			
			conn.commit();
			
			log.debug("delete tmp_table success");
			
		}catch(Exception e) {
			log.error(String.format("delete temp table fail"));
			rollback();
			e.printStackTrace();
			System.exit(0);
		}finally {
			close();
		}
	}
	
	public int cleanUp() {
		int result = 0;
		
		String sql1 = "SELECT EXISTS (" + 
					  "  SELECT 1 FROM Information_schema.tables" + 
					  "  WHERE table_schema = '" + Config.SERVICEID + 
					  "'  AND table_name = '" + Config.SERVICETABLE + "_original'"+
					  ") AS flag";
		String sql2 = "DROP TABLE " + Config.SERVICETABLE + "_original";
		String sql3 = "ALTER TABLE "+Config.SERVICETABLE+" RENAME TO "+Config.SERVICETABLE+"_original";
		String sql4 = "ALTER TABLE tmp_"+Config.SERVICETABLE+" RENAME TO "+Config.SERVICETABLE;
		try {
			//1. original 테이블 존재여부 확인
			pstmt = conn.prepareStatement(sql1);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int tmp = rs.getInt(1);
				if(tmp!=1) {
					log.info(String.format("%s_original Table is not exists / %d", Config.SERVICETABLE,tmp));
				}else {
					log.info(String.format("%s_original table is exists, this table drop ", Config.SERVICETABLE));
					pstmt = conn.prepareStatement(sql2);
					tmp = pstmt.executeUpdate();
					if(tmp!=0) {
						throw new Exception(String.format("%s_original table drop fail", Config.SERVICETABLE));
					}
				}
			}
			conn.commit();
						
			pstmt = conn.prepareStatement(sql3);
			pstmt.executeUpdate(); 
			pstmt = conn.prepareStatement(sql4);
			result = pstmt.executeUpdate();
			
			conn.commit();
			close();
			
			log.info("table cleanup");
			
		}catch(Exception e) {
			log.info(String.format("System shutdown!! error catch : %s", e.getMessage()));
			log.error(e.getMessage());
			e.printStackTrace();
			rollback();
			close();
			connClose();
			System.exit(0);
		}
		
		return result;
	}
	
	public int getTargetCnt() {
		int result = 0;
		try {
			String sql = "SELECT COUNT(*) FROM target_info";
			pstmt=conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
		}catch(Exception e) {
			log.error(e.getMessage());
		}finally {
			close();
		}
		
		return result;
	}
	
	private void rollback() {
		try {
			conn.rollback();
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void close() {
		try {
			if(rs!=null) {
				rs.close();
			}
		}catch(Exception e) {
			log.error(String.format("!!!! ResultSet closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
		try {
			if(pstmt!=null) {
				pstmt.close();
			}
		}catch(Exception e) {
			log.error(String.format("!!!! PreparedStatement closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public void connClose() {
		try {
			if(conn!=null) {
				conn.close();
			}
		}catch(Exception e) {
			log.error(String.format("!!!! connector closer error !!!! - ", e.getMessage()));
			e.printStackTrace();
		}
	}
	
	
	private class Runnable2 implements Runnable{
		
		private boolean result = false;
		private List<Product> list = null;
		
		public Runnable2(List<Product> list) {
			this.list = list;
		}
		
		public boolean getResult() {
			return result;
		}
		
		@Override
		public synchronized void run() {
			// TODO Auto-generated method stub
			try {
				for(Product pro:list) {
					int i = 1;
					pstmt=conn.prepareStatement("INSERT INTO tmp_"+Config.SERVICETABLE + 
															" (	" + 
																"	category1,		" + 
																"	category2,		" + 
																"	item_price,		" +
																"	discount_price,	" +
																"	shop_name,		" +
																"	sales_target,	" + 
																"	item_name,		" +
																"	thumbnail_img,	" + 
																"	detail_url,		" + 
																"	create_date )	" +
														"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
					pstmt.setInt(i++,pro.getCategory1());
					pstmt.setInt(i++,pro.getCategory2());
					pstmt.setInt(i++,pro.getProduct_price());
					pstmt.setInt(i++,pro.getProduct_discount_price());
					pstmt.setString(i++,pro.getCompany());
					pstmt.setString(i++,pro.getTarget());
					pstmt.setString(i++,pro.getProduct_name());
					pstmt.setString(i++,pro.getProduct_img_url());
					pstmt.setString(i++,pro.getProduct_url());
					
					pstmt.executeUpdate();
					
				}
				log.info(String.format("insert company : %s / cnt : %d", list.get(0).getCompany(),list.size()));
				conn.commit();
				result = true;
				
				Config.TARGETCOUNT-=Config.THREADUNIT;
				if(Config.TARGETCOUNT<0) {
					Config.TARGETCOUNT=0;
				}
				
			}catch(Exception e) {
				log.error(String.format("error MSG : %s - %s",Thread.currentThread().getName(), e.getMessage()));
				rollback();
				e.printStackTrace();
			}finally{
				close();
			}
		}
	}
}


