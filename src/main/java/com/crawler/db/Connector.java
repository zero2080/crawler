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
	private String thisClass = null;
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
				
				ResultSet tmp = pstmt.executeQuery();
				tmp.next();
				log.info(tmp.getString(1));
				tmp=null;
				thisClass = "target";
			}else if(target.equals("service")) {
				//크롤링 결과를 저장할 DB connection생성
				
				Class.forName(Config.SERVICEDRIVER);
				
				
				
				conn = DriverManager.getConnection(	Config.SERVICEURL,
													Config.SERVICEID,
													Config.SERVICEPW);
				
				
				pstmt = conn.prepareStatement(Config.SERVICEVERIFYQUERY);
				
				
				ResultSet tmp = pstmt.executeQuery();
				tmp.next();
				log.info(tmp.getString(1));
				tmp=null;
				
				thisClass = "service";
			}
			
			conn.setAutoCommit(false);
			rs = pstmt.executeQuery();
			
			//veryfing
			if(rs.next()) {
				log.debug(String.format("DB Connect - %s / URL = %s",rs.getString(1),target.equals("target")?Config.TARGETURL:Config.SERVICEURL));
			}else {
				log.error("create connection error!!");
			}
		}catch(Exception e) {
			log.error(String.format("!!!! create connection error !!!! - %s", e.getMessage()));
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
	public List<CrawllingTarget> getTestRow(){
		if(conn==null) {
			log.info(String.format("not create connector. create connector first"));
			return null;
		}
		List<CrawllingTarget> targetList = new ArrayList<CrawllingTarget>();
		String sql = "SELECT * FROM target_info ORDER BY seq desc LIMIT 1";
		
		try {
			rs = pstmt.executeQuery(sql);
			
			log.debug(String.format("query = %s",sql));
			
			if(rs.next()) {
				CrawllingTarget target = new CrawllingTarget(
												rs.getInt("seq"),
												rs.getString("shop_url"),
												rs.getString("shop_name"),
												rs.getString("shop_description"),
												rs.getString("target"),
												rs.getInt("category1"),
												rs.getInt("category2"),
												rs.getString("product"),
												rs.getString("product_name"),
												rs.getString("product_price"),
												rs.getString("product_discount_price"),
												rs.getString("product_image"),
												rs.getString("product_url"),
												rs.getString("option_type"),
												rs.getString("option_selector_1"),
												rs.getString("option_selector_2"),
												rs.getString("option_selector_3"),
												rs.getInt("select_type"),
												null,
												rs.getString("soldout_checker"),
												rs.getString("page_selector"),
												rs.getString("page_size_selector"),
												rs.getInt("page_size"),
												rs.getInt("scroll_type")
											);
				targetList.add(target);
				log.debug(target.toString());
			}
			Thread.sleep(10);
		} catch(Exception e) {
			log.error(String.format("!!!! query error !!!! - %s", e.getMessage()));
//			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return targetList;
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
				CrawllingTarget target = new CrawllingTarget(
												rs.getInt("seq"),
												rs.getString("shop_url"),
												rs.getString("shop_name"),
												rs.getString("shop_description"),
												rs.getString("target"),
												rs.getInt("category1"),
												rs.getInt("category2"),
												rs.getString("product"),
												rs.getString("product_name"),
												rs.getString("product_price"),
												rs.getString("product_discount_price"),
												rs.getString("product_image"),
												rs.getString("product_url"),
												rs.getString("option_type"),
												rs.getString("option_selector_1"),
												rs.getString("option_selector_2"),
												rs.getString("option_selector_3"),
												rs.getInt("select_type"),
												null,
												rs.getString("soldout_checker"),
												rs.getString("page_selector"),
												rs.getString("page_size_selector"),
												rs.getInt("page_size"),
												rs.getInt("scroll_type")
											);
				targetList.add(target);
			}
			Thread.sleep(10);
		} catch(Exception e) {
			log.error(String.format("!!!! query error !!!! - %s", e.getMessage()));
//			rollback();
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
				result = true;
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
											  "option_type, " +
											  "option_selector_1, " + 
											  "option_selector_2, " + 
											  "option_selector_3, " + 
											  "page_selector, " +
											  "page_size_selector, " +
											  "page_size, " +
											  "scroll_type)" + 
								 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
			pstmt.setString(i++, ct.getOption_type());
			pstmt.setString(i++, ct.getOption_selector_1());
			pstmt.setString(i++, ct.getOption_selector_2());
			pstmt.setString(i++, ct.getOption_selector_3());
			pstmt.setString(i++, ct.getPage_selector());
			pstmt.setString(i++, ct.getPage_size_selector());
			pstmt.setInt(i++, ct.getPage_size());
			pstmt.setInt(i++, ct.getScroll_type());
			
			result = pstmt.executeUpdate()==1;
			
			conn.commit();
		}catch(Exception e) {
//			rollback();
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
										"  `options` mediumtext DEFAULT NULL COMMENT '옵션 - json타입으로 저장(target_info.option_selector_1~3)'," + 
										"  `item_state` int NOT NULL DEFAULT 0 COMMENT '0: 정상 / 1: 판매중단'," +
										"  `option_state` int not null default 0 comment '옵션상태 : 0: 정상 / 1: 판매중단'," +
										"  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," + 
										"  PRIMARY KEY (`num`)" + 
										") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			conn.commit();
			log.info("create tmp_table success");
			
			Date date = new Date();
			
			log.info(String.format("cteate table now : %s",date));
			
		}catch(Exception e) {
//			rollback();
			log.debug(e.getMessage());
			throw new Exception("Temp table Already exists");
		}finally {
			close();
		}
	}

	public void dropTmpTable() {
		if(thisClass.equals("service")) {
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
//			rollback();
			e.printStackTrace();
			System.exit(0);
		}finally {
			close();
		}
	}
	
	public int cleanUp() {
		int result = 0;
		
		//중복 상품 제거
//		String sql0 = "DELETE FROM tmp_" + Config.SERVICETABLE + " WHERE num IN (SELECT num FROM (SELECT *, COUNT(detail_url) AS cnt FROM tmp_" + Config.SERVICETABLE + " GROUP BY detail_url) a WHERE a.cnt>1)";
//		String sql0 = "DELETE FROM tmp_" + Config.SERVICETABLE + " WHERE num IN (SELECT num FROM (SELECT num,item_name,shop_name,detail_url,COUNT(*) AS cnt FROM tmp_" + Config.SERVICETABLE + " GROUP BY shop_name,item_name ORDER BY shop_name,item_name) AS a WHERE a.cnt>1)";
		String sql0 = "DELETE FROM tmp_" + Config.SERVICETABLE + " WHERE num IN (SELECT * FROM (SELECT a.num from tmp_" + Config.SERVICETABLE + " AS a ,(SELECT num,shop_name,item_name,cnt FROM (SELECT num,item_name,shop_name,detail_url,COUNT(*) AS cnt FROM tmp_" + Config.SERVICETABLE + " GROUP BY shop_name,item_name ORDER BY shop_name,item_name) AS b WHERE b.cnt>1) AS c WHERE a.num!=c.num AND a.shop_name=c.shop_name AND a.item_name=c.item_name) AS d)";
		
		
		//item_sales_original존재여부 확인
		String sql1 = "SELECT EXISTS (" + 
					  "  SELECT 1 FROM Information_schema.tables" + 
					  "  WHERE table_schema = '" + Config.SERVICEDB + 
					  "' AND table_name = '" + Config.SERVICETABLE + "_original'"+
					  ") AS flag";
		
		//item_sales_original 테이블 삭제
		String sql2 = " DROP TABLE " 		+ Config.SERVICETABLE + "_original";
		
		//정리 테이블 생성
		String sql3 = "CREATE TABLE " + Config.SERVICETABLE + "_newborn (" + 
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
					  "  `options` mediumtext DEFAULT NULL COMMENT '옵션 - json타입으로 저장(target_info.option_selector_1~3)'," +
					  "  `item_state` int NOT NULL DEFAULT 0 COMMENT '0: 정상 / 1: 판매중단'," +
					  "  `option_state` int not null default 0 comment '옵션상태 : 0: 정상 / 1: 판매중단'," +
					  "  `create_date` datetime NOT NULL," + 
					  "  PRIMARY KEY (`num`)" + 
					  ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상품 테이블'";
		
		//기존 상품 num 값 유지 및 정보 업데이트
		String sql4 = "INSERT INTO " + Config.SERVICETABLE + "_newborn (" +
					  "	 SELECT	a.num, " + 
					  "			b.category1," + 
					  "			b.category2," + 
					  "			b.item_price," + 
					  "			b.discount_price," + 
					  "			b.shop_name," + 
					  "			b.sales_target," + 
					  "			b.item_name," + 
					  "			b.thumbnail_img," + 
					  "			b.detail_url," +
					  "			b.options," + 
					  "			b.item_state," +
					  "			a.option_state," + 
					  "			a.create_date" + 
					  "		FROM " + Config.SERVICETABLE + " a , tmp_" + Config.SERVICETABLE + " b " + 
					  "		WHERE a.shop_name = b.shop_name AND a.item_name = b.item_name" + 
					  ")";
		
		//삭제상품 상태 변경
		String sql5 = "INSERT INTO " + Config.SERVICETABLE + "_newborn (" +
					  "	 SELECT	num, " + 
					  "			category1," +
					  "			category2," + 
					  "			item_price," + 
					  "			discount_price," + 
					  "			shop_name," + 
					  "			sales_target," + 
					  "			item_name," + 
					  "			thumbnail_img," + 
					  "			detail_url," +
					  "			options," + 
					  "			1," + 
					  "			1," +
					  "			create_date" + 
					  "		FROM " + Config.SERVICETABLE + " WHERE CONCAT(shop_name,item_name) NOT IN (" + 
					  "			SELECT CONCAT(shop_name,item_name) FROM tmp_" + Config.SERVICETABLE + ")" + 
					  ")";
		
//		String sql5 = "UPDATE " + Config.SERVICETABLE + "_newborn " + 
//					  "		SET item_state = 1	" + 
//					  "		WHERE detail_url IN(		" + 
//					  " 		SELECT detail_url FROM tmp_" + Config.SERVICETABLE +
//					  "				WHERE detail_url NOT IN (" + 
//					  "					SELECT detail_url FROM " + Config.SERVICETABLE + " )" + 
//					  "			)";
		
		//추가상품 업데이트
		String sql6 = "INSERT INTO " + Config.SERVICETABLE +"_newborn (	" + 
						"		category1, category2,		" + 
						"		item_price, discount_price,	" + 
						"		shop_name, sales_target,	" + 
						"		item_name, thumbnail_img,	" + 
						"		detail_url, options, item_state, " + 
						"		option_state, " + 
						"		create_date	" + 
						"	) SELECT 	category1, category2,		" + 
						"				item_price, discount_price,	" + 
						"				shop_name, sales_target,	" + 
						"				item_name, thumbnail_img,	" + 
						"				detail_url, options, item_state, " + 
						"				option_state, " + 
						"				create_date	" + 
						"			 FROM tmp_" + Config.SERVICETABLE +
						"			 WHERE CONCAT(shop_name,item_name) " +
						"		NOT IN(SELECT CONCAT(shop_name,item_name) FROM " + Config.SERVICETABLE + ")";

		String sql7 = "ALTER TABLE " + Config.SERVICETABLE +
						"	RENAME TO " + Config.SERVICETABLE + "_original";
		
//		String sql8 = "UPDATE item_sales_newborn SET detail_url=REPLACE(detail_url,'http:','https:') WHERE shop_name NOT IN('헤네스','나투라비타')";
		
		String sql9 = "ALTER TABLE " + Config.SERVICETABLE + "_newborn" +
						"	RENAME TO " + Config.SERVICETABLE;
		
		String sql10 = "DROP TABLE tmp_"	+ Config.SERVICETABLE;
		
		try {
			
			//0. 중복상품제거
			pstmt = conn.prepareStatement(sql0);
			pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("중복상품 제거 - 완료"));
			
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
			conn.commit();
			log.info(String.format("%s_newborn 테이블 생성 - 완료",Config.SERVICETABLE));
			
			pstmt = conn.prepareStatement(sql4);
			pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("기존 상품 정보 업데이트 - 완료"));
			
			pstmt = conn.prepareStatement(sql5);
			pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("삭제상품 상태값 변경 - 완료"));
			
			pstmt = conn.prepareStatement(sql6);
			pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("추가상품 업데이트 - 완료"));
			
			pstmt = conn.prepareStatement(sql7);
			pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("기존테이블 백업 - 완료"));
			
//			pstmt = conn.prepareStatement(sql8);
//			pstmt.executeUpdate();
//			conn.commit();
//			log.info(String.format("http > https"));
			
			pstmt = conn.prepareStatement(sql9);
			pstmt.executeUpdate();
			conn.commit();
			
			log.info(String.format("%s_newborn -> %s - 완료(테이블명 변경)",Config.SERVICETABLE,Config.SERVICETABLE));
			
			pstmt = conn.prepareStatement(sql10);
			result = pstmt.executeUpdate();
			conn.commit();
			log.info(String.format("tmp_%s 테이블 삭제 - 완료",Config.SERVICETABLE));
			
			close();
			
			log.info("table cleanup");
			
		}catch(Exception e) {
			log.info(String.format("System shutdown!! error catch : %s", e.getMessage()));
			e.printStackTrace();
//			rollback();
			close();
			connClose();
			System.exit(0);
		}finally {
			close();
			connClose();
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
	
//	private void rollback() {
//		try {
//			conn.rollback();
//		}catch(Exception e) {
//			log.error(e.getMessage());
//		}
//	}
	
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
					String sql = "INSERT INTO tmp_"+Config.SERVICETABLE + 
													" (	category1,		" + 
													"	category2,		" + 
													"	item_price,		" +
													"	discount_price,	" +
													"	shop_name,		" +
													"	sales_target,	" + 
													"	item_name,		" +
													"	thumbnail_img,	" + 
													"	detail_url,		" +
													"	options,		" +
													"	item_state,		" +
													"	option_state	" +  
													" 	)" +
												"SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS("+
													"SELECT 'A' FROM tmp_" + Config.SERVICETABLE + " WHERE detail_url = ? )";
					pstmt=conn.prepareStatement(sql);
					pstmt.setInt(i++,pro.getCategory1());
					pstmt.setInt(i++,pro.getCategory2());
					pstmt.setInt(i++,pro.getProduct_price()==0?pro.getProduct_discount_price():pro.getProduct_price());
					pstmt.setInt(i++,pro.getProduct_discount_price());
					pstmt.setString(i++,pro.getCompany());
					pstmt.setString(i++,pro.getTarget());
					pstmt.setString(i++,pro.getProduct_name());
					pstmt.setString(i++,pro.getProduct_img_url());
					pstmt.setString(i++,pro.getProduct_url());
					pstmt.setString(i++,pro.getOptions());
					pstmt.setInt(i++, pro.getItem_state());
					pstmt.setInt(i++, pro.getOption_state());
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
//				rollback();
				e.printStackTrace();
			}finally{
				close();
			}
		}
	}


	public CrawllingTarget getTestList(String product_list_url) {
		// TODO Auto-generated method stub
		if(conn==null) {
			log.info(String.format("not create connector. create connector first"));
			return null;
		}
		CrawllingTarget target=null;
		String sql = "SELECT * FROM target_info WHERE shop_url='"+product_list_url+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			log.debug(String.format("query = %s",sql));
			
			if(rs.next()) {
				target = new CrawllingTarget(
											rs.getInt("seq"),
											rs.getString("shop_url"),
											rs.getString("shop_name"),
											rs.getString("shop_description"),
											rs.getString("target"),
											rs.getInt("category1"),
											rs.getInt("category2"),
											rs.getString("product"),
											rs.getString("product_name"),
											rs.getString("product_price"),
											rs.getString("product_discount_price"),
											rs.getString("product_image"),
											rs.getString("product_url"),
											rs.getString("option_type"),
											rs.getString("option_selector_1"),
											rs.getString("option_selector_2"),
											rs.getString("option_selector_3"),
											rs.getInt("select_type"),
											rs.getString("price_selector"),
											rs.getString("soldout_checker"),
											rs.getString("page_selector"),
											rs.getString("page_size_selector"),
											rs.getInt("page_size"),
											rs.getInt("scroll_type")
										);
				log.debug(target.toString());
			}
		} catch(Exception e) {
			log.error(String.format("!!!! query error !!!! - %s", e.getMessage()));
//			rollback();
			e.printStackTrace();
		} finally {
			close();
		}
		return target;
	}
	
	
}


