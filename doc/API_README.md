# API 사용 설명서 ##
## API CLASS ##
 ### 1. 객체 생성 ###
 > ShopInfo shopinfo = new ShopInfo( [Driver], [URL], [ID], [PW] );<br>
 > EX)
 >
 > ```java
 > ShopInfo shopinfo = new ShopInfo("org.mariadb.jdbc.Driver",
 >            "jdbc:mysql://localhost:3306/crawl?characterEncoding=UTF-8", 
 >            "crawler", 
 >            "crawler");
 > ```



### 2. 크롤링 대상 정보 DB입력

> Insert Crawlling Target Information
>
> ```java
> // 방법 1. 파라미터 직접 입력
> shopinfo.insertShopInfo(String  shop_url,
>                      String shop_name,
>                      String shop_description,
>                      int category1,
>                      int category2,
>                      String product_name,
>                      String product_price,
>                      String product_image,
>                      String product_url,
>                      String page_selector,
>                      String page_size_selector,
>                      int page_size,
>                      int scroll_type);
> 
> //방법 2. 크롤링타겟 객체 생성 후 사용
> CrawllingTarget crawllingtarget = new CrawllingTarget(String  shop_url,
>                      String shop_name,
>                      String shop_description,
>                      int category1,
>                      int category2,
>                      String product_name,
>                      String product_price,
>                      String product_image,
>                      String product_url,
>                      String page_selector,
>                      String page_size_selector,
>                      int page_size,
>                      int scroll_type);
> 
> shopinfo.insertShopInfo(crawllingtarget);
> ```
>
> Parameter description
>
> > - shop_url : 쇼핑몰 URL 주소(크롤링 주소) / ex) http://cooingkids.com/product/list.html?cate_no=92
> > - shop_name : 쇼핑몰 이름  ex) 쿠잉키즈
> > - shop_description : 쇼핑몰 설명 or 페이지 설명 ex) 유럽감성 어쩌구저쩌구
> > - category1 상품 분류1 : 상의(0)   하의(1)   한벌옷(2)   아우터(3)   잡화(4)   악세(5)   홈웨어(6)   출산(7)   장난감(8)   체험(9)
> > - category2 상품 분류2 : category1의 하위선택자로 0~ 6의 숫자로 구분한다.
> >
> > | category1 | 0           | 1         | 2                  | 3           | 4             |5|6|
> > | :-------: | :---------: | :----------: | :-------------------: | :---------: | :-----------: |:-----------: |:-----------: |
> > | 상의(0)   | 니트/스웨터 | 반팔      | 긴팔               | 민소매      | 셔츠/블라우스 |||
> > | 하의(1)   | 데님 팬츠   | 코튼 팬츠 | 스포츠/기능성 팬츠 | 스커트/치마 | 반바지/숏팬츠 |레깅스/타이즈||
> > | 한벌옷(2) | 원피스 | 점프수트 | 기타 |             |               |||
> > | 아우터(3) | 후드 집업 | 재킷 | 가디건 | 패딩 | 코트 |||
> > | 패션잡화(4) | 백팩 | 크로스백 | 에코백 | 안경 | 캡모자/야구모자 |비니|양말/기타|
> > | 악세서리(5) | 시계/주얼리 | 스니커즈 | 브로치/머리핀/헤어악세서리 | 샌들/장화 | 슬리퍼/기타 |||
> > | 홈웨어(6) | 여아 이너웨어 | 남아 이너웨어 | 잠옷 | 내복 |               |||
> > | 출산&신생아(7) | 샤워용품 | 침구/가구 | 임부 속옷/임부복 | 유모차/카시트 | 젖병/목욕/기저귀 |||
> > | 장난감&완구(8) | 장난감 | 완구 | 미니자동차 등 탈 것 |             |               |||
> > | 만들기 체험(9) | 체험 | | | | | | |
> >
> > - product_name : 상품이름 css selector / ex) #container>div>div>ul>li>p
> > - product_price : 상품 가격 css selector / ex) #container>div>div>ul>li>span
> > - product_image : 상품 이미지 URL css selector / ex) #container>div>div>ul>li>img
> > - product_url : 상품 상세 보기 URL css selector / ex) #container>div>div>ul>li>a
> > - page_selector : 페이지 URL구분자  / ex) &page=
> > - page_size_selector : 한 페이지 당 상품 수 URL구분자 / ex) &pageSize=
> > - page_size : 한페이지 안에 들어가는 상품 수 / 0=사용안함
> > - scroll_type: 0=페이지 타입 / 1=무한스크롤

