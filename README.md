# SpringLoginZK
***
##說明
***
ZK是一個Ajax的框架，通常會與SpringMVC做配合，讓Spring管理Model並且傳給ZK顯示，
嚴格來說ZK就是一個支援Spring的前端框架，但是它並不適合用來做一般網頁，而是用來寫back office(後台)，
ZK可以單獨使用，不過這裡要說明如何將Spring與ZK合併使用。
***
##重點
***
根據文件，ZK有自有的注入方式，但是Spring也有自有的注入方式，但是既然使用ZK框架，
建議還是以ZK為主，這裡也是以Spring為主。

###配置ZK到Spring當中
<ol>
<li>利用pom.xml或者加入jar載入ZK框架所需的檔案</li>
<li>在Web.xml中加入需要的listener與Zk所需的servlet</li>
</ol>
Web.xml是用來載入主要的Web初始化，所以將ZK與Spring載入，詳情可以參照官網或者文件
***
###在所需頁面配置controller與控制初始
***
<ol>
<li>在所需的Composer加入controller連結以便控制，Composer就是ZK定義的物件，類似html的標籤、div</li>
<li>在需要頁面的正上方加入<?variable-resolver>屬性，表示可以讓ZK讀取controller權限，也可以在controller內添加，方法不限但必要</li>
</ol>
這裡有點像是需要讓ZK與controller建立起一個溝通管道，讓彼此可以互相交流(ZK與Controller)
***
###讓controller以ZK的方式獲取Spring的Bean
***
<ol>
<li>繼承Composer相關類別，或者實作Initiator介面，</li>
<li>使用@wirevariable取代Spring的@Autowire</li>
</ol>
前面有提及controller會與ZK的Composer做綁定，所以controller也要宣告綁定，
但如果是Controller或者ViewModel以外的物件，就必須實作Initiator介面才能注入。

***
##方法
***
###載入Controller：

ZK會有類似<Window>的標籤，這時就可以加入一個屬性apply變成`<Window apply="ControllerPackage">`，
這一行的目的是告訴當頁的zul中的Window這個Composer要讓哪個Controller去執行，
如果交給Spring管理，位置通常是`apply="@{Controller}"`，如果是交給ZK管理，則可以使用路徑`apply="com.zk.controller"`

###與Controller建立溝通：

除了把Controller載入外還需要加入一行程式碼到你需要的zul頁面上方，

    <?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?>

這一行可以在controller加入或者需要頁面上，但如果你有很多zk頁面則可以統一管理。

###讓Controller可以載入Spring的Bean：

ZK本身支援MVC或者MVVM，但是在注入上只限制Composer或者ViewModel才能注入Bean。
所以必須在Controller後面繼承Composer，像是`extends SelectorComposer<Component>`，
如此一來才能夠使用ZK既有的注入方式注入Bean。

當然如果是在限制以外，只要繼承Initiator介面，這樣就必須實作doIt方法，這時加入：

        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));

並且在所需的頁面載入`<?init>`在上方如

        <?init class="package"?>

這個意思是去掃描這一頁有`@WireVariables`的標籤並在需要的頁面注入，但這邊會有個問題，

**獲取的Bean只存在於這個方法內，也就是Bean正常要交給Controller或Model處理，但是這裡只有在進入某ZK頁面時獲取**，

這也表示如果要使用bean的方法都必須要掃描一次，這樣並不方便。

###利用@WireVariable取代Spring的@AutoWire：

`@AutoWire`是Spring本身常見的Annotation，但是在ZK有自己的方式，也就是`@WireVariable`注入，
這樣就不需要Setter方式注入bean，直接獲取即可，可是必須將上面的方式配置好才能注入，
用法基本上是跟Spring差不多的。

***
##範例-Login Example
***
本範例為一個簡易的登入，主要注入jdbc相關物件運用(jndi)，資料庫使用mysql
這邊會使用上述的所有方式，可以參照範例對照。

資料庫名稱:Logindb

資料庫語法：


    CREATE TABLE ‘userdata’ (
      ‘id’ int(11) NOT NULL,
      ‘account’ varchar(20) DEFAULT NULL,
      ‘password’ varchar(32) DEFAULT NULL) 
        
    
新增一筆資料：    

    INSERT INTO ‘userdata’ (‘id’, ‘account’, ‘password’) VALUES
    (1, 'Chaox', 'e1adc3949ba59abbe56e057f2f883e');
    
程式內容有使用到MD5+HEX加密，所以密碼會編譯過，如要登入密碼為123456。

###context.xml：這裡載入jdbc，使用的是jndi，載入jdbc相關設定

    <?xml version="1.0" encoding="UTF-8"?>
 
    <!-- JDBC -->
    <Context path="/SpringLoginZK" docBase="SpringLoginZK" debug="5" reloadable="true" crossContext="true">
      <Resource name="jdbc/mysqldb" username="root" password=""
      url="jdbc:mysql://localhost:3306/Logindb"
      auth="Container" validationQuery="SELECT * FROM userdata WHERE 1"
      driverClassName="com.mysql.jdbc.Driver" maxActive="20"
      timeBetweenEvictionRunsMillis="60000"
      type="javax.sql.DataSource"/>

    </Context>


###applicationContext.xml：這邊要載入bean，並且讓Spring可以使用Annotation的方式注入

雖然是使用ZK，但是注入的部分還是使用Spring的方式

  	<context:component-scan base-package="com.idv.madphiloshopy" />
    
之後載入需要的jdbcBean

	<bean id="dataSource" class="org.springframework.jndi.JndiObjectFactoryBean">
		<property name="jndiName">
			<value>java:comp/env/jdbc/mysqldb</value>
		</property>
	</bean>

	<bean id="templates" class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="dataSource" />
	</bean>
  
###index.zul：這裡是View的部分，負責畫面顯示

上方的Window就是ZK的Composer，apply負責載入Controller

    <window id="loginWin" title="Login with you name"
            apply="com.idv.madphiloshopy.controller.MainController" border="normal" hflex="min" >
            
最頂部的則是告訴這個頁面可以讓Spring的Controller控制(所以為Spring類別)

  <?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?>
  
###MainController：這裡負責控制ZK的Request

繼承Composer相關的類別是為了能注入Spring的Bean並且讓zk能知道，與前面的apply配合

    public class MainController extends SelectorComposer<Component> 

這麼一來才可以順利將Service

    @WireVariable
    private LoginService loginService;
    
    
###LoginServicImpl.java：Model，所以無法直接繼承Composer使用，只能實作Initiator

繼承介面並實作

    public class LoginServicImpl implements LoginService,Initiator{    
    
然後實作方法doit

    public void doInit(Page page, Map<String, Object> args) throws Exception {
      // TODO Auto-generated method stub

    }
    
並且在需要Bean時加入獲取Bean的方式，這邊需要從前面傳遞一個Page參數才能實作

     Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));    

否則如果再doinit中加入的話，就必須在zul加入類別，讓進來時可以讀取Bean，但是僅存在這個方法中

    <?init class="com.idv.madphiloshopy.service.impl.LoginServicImpl"?>

***
##總結
***
ZK的使用上有些不靈活，譬如像是載入Bean也可以使用GetBean方法，當然建議如果可以，
盡量把注入這件事情在Controller再傳入Service(Model)，或者直接使用ViewModel，
當然如果使用Spring也是可行，這裡主要是Spring與ZK合併注入的基礎示範。
