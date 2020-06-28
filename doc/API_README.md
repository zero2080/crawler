# API 사용 설명서 ##
## API CLASS ##
 ### 1. 객체 생성 ###
 > ShopInfo si = new ShopInfo( [Driver], [URL], [ID], [PW] );<br>
 > EX)
 > ```java
 > ShopInfo si = new ShopInfo("org.mariadb.jdbc.Driver",
 >               "jdbc:mysql://localhost:3306/crawl?characterEncoding=UTF-8", 
 >               "crawler", 
 >               "crawler");
 >```

