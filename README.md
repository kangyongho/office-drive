# office-drive (Spring Security)
Office Drive 프로젝트는 Spring Security로 Spring Applcation에 인증을 제공합니다.  
이전에 운영환경변수의 중앙집중제어 프로젝트에 인증을 추가했습니다.  
Rest API Server, Web, Android Applcation 세 가지 프로젝트로 구성되어 있습니다.  

## Spring Security
Spring Cloud Config를 이용하면 환경변수를 중앙서버에서 집중제어할 수 있지만 보안에 노출되어 있다.  
Spring Security로 인증을 적용하고 Git 접속방식도 SSH 방식으로 변경해보았다.  

#### Bitbucket Git Repository
config server가 바라보는 Git을 GitHub에서 Bitbucket으로 변경하면 private 계정을 사용할 수 있다.  
bitbucket에 data를 요청하려면 인증을 거쳐야 한다.  
basic auth로 id, password도 가능하지만 ssh 방식을 추천한다.  
전제조건으로 config server 운영 서버에 ssh 설정이 되어 있어야 한다.  
`bootstarp.yml`에 s`pring.cloud.config.server.git.uri`를 `git@bitbucket.org:account/repository`로 교체하면 ssh 방식으로 git에 접속한다.

#### WebSecurityConfigurerAdapter [link][0]
spring security 출발은 `WebSecurityConfigurerAdapter` interface의 구현으로 시작한다.  
url에 따른 접근제한을 설정할 수 있으며 우선순위가 높은 것부터 설정하면 된다.  
`spring filter chain` 각 설정에 맞는 Filter가 연결되어 인증과 접근제한을 처리한다.  

    public class WebConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/**")
                .authorizeRequests()
                    .antMatchers("/", "/css/**", "/js/**").permitAll() //모든 user가 접근가능
                    .antMatchers("/find/admin/**").hasRole("ADMIN")    //ADMIN user만 접근가능
                    .anyRequest().authenticated()
                    .and()
                .httpBasic();
        }
    }

#### 인증방식
httpBasic, Form Login, Jdbc Login, OAuth2, LDAP 등의 인증방식을 지원한다.  
`office-drive-web-application` 프로젝트는 위의 3가지 방식을 지원한다.

#### Encryption [link][0]
패스워드는 반드시 암호화해서 DB로 관리해야한다.  
spring security는 BCryptPasswordEncoder로 `BCrypt` 방식의 암호화를 지원한다.  
BCryptPasswordEncoder를 빈으로 등록해두면 패스워드 입력시 암호화를 적용한다.  
만약 DB에 암호화되지 않는 형식의 패스워드가 입력되어 있으면 인증 에러가 발생한다.  

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

#### Filter Chain 커스터마이징 [link][0]
spring security의 가장 중요한 것은 `filter chain`이다.  
인증 흐름은 `AuthenticationFilter - AuthenticationManager - AuthenticationProvider - UserDetailsService` 순서로 진행된다. 여기서 커스터마이징한 UserDetailsService와 BCryptPasswordEncoder빈을 `AuthenticationProvider`에 주입해주면 JPA User Entity와 Encryption 패스워드를 적용할 수 있게 된다.

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserRepositoryUserDetailsService uds) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(uds);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

## Rest API Server
Rest API는 json 타입의 데이터 요청에 자주 사용된다.  
여기서는 DB의 inbox 테이블 페이지 조회와 RabbitMQ 메시지 전송 End Point를 제공한다.  

#### Config Data
Rest 서버에서 사용할 환경변수는 config server에 요청하여 컨테이너 초기화에 사용한다.  
위에서 처럼 bootstarp.yml에 uri에 config server 도메인을 입력해주면 된다.  
자세한 내용은 [`운영환경별 환경변수 설정 연구`][1] 를 참고하자.

#### 인증 우선순위
현재 Rest 서버는 웹 애플리케이션과 안드로이드 앱 요청을 모두 처리한다.  
spring security는 `CSRF` 공격에 대비해서 `csrf token`을 이용하고 있다.  
상태를 저장하지 않는 stateless한 모바일 앱에서 csrf를 적용하면 개발 복잡도가 증가하는 문제가 있다.  
그래서 모바일 요청 url을 분리하여 csrf 적용을 피할 수 있게 설정할 수 있다.  

#### @Order [link][0]
`@Order` 어노테이션을 적용하면 `/android`로 시작되는 요청부터 분리할 수 있다.

    @Configuration
    @Order(1)
    public static class AndroidConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/android/** ")
                    .csrf().disable()
                    .httpBasic();
        }
    }

    @Configuration
    @Order(2)
    public static class WebConfiguration extends WebSecurityConfigurerAdapter {
        ...
    }

#### 페이징 End Point [link][2]
    @Autowired InboxService inboxService;

    @RequestMapping(value = "/dashboard/{username}", method = RequestMethod.GET)
    public Map<String, Object> dashboard(@PathVariable("username") String username, Model model) {
        Map<String, Object> inboxList = inboxService.getReceivedMessageRestAPI(1, username);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("inboxList", inboxList.get("list"));
        dashboard.put("startPage", 1);

        return dashboard;
    }

#### RabbitMQ End Point [link][3]
    @PostMapping(value = "/android/rabbitmq/send")
    public void sendMessageWithRabbitMQ(@RequestBody Inbox inbox) throws IOException, TimeoutException {

        //RabbitMQ AMQP로 안드로이드에게 PUSH 메시지 전송
        RabbitMQHelper rabbitMQHelper = new RabbitMQHelper(
                propertyConfig.getRabbitmq().get("host"),
                propertyConfig.getRabbitmq().get("username"),
                propertyConfig.getRabbitmq().get("password"),
                "pushService");  //helper object 생성

        rabbitMQHelper.getChannel("inbox", BuiltinExchangeType.DIRECT);

        String message = inbox.getMessage();
        String bindingKey = "broadcast";
        rabbitMQHelper.basicPublish(message, bindingKey);

        rabbitMQHelper.closeConnection();
    }

#### RabbitMQHelper [link][4]
RabbitMQ는 메시지 브로커다. 예전 `Pure Talk` Json Messenger Android 프로젝트에서 `GCM`으로 Push Service를 제공한것과 달리 RabbitMQ로 Push Service를 구현할 수 있다.  
아래 메서드를 통해 RabbitMQ를 사용했다. 아직 살펴보진 않았지만 `Spring AMQP` 프로젝트가 개발에 도움이 될 수 있을 것으로 생각된다.  

    public class RabbitMQHelper {
      ...
      public Channel getChannel() throws IOException, TimeoutException {
          if (this.connection == null) {
              getConnection();
              channel = connection.createChannel();
              return channel;
          }
          else {
              return channel;
          }
      }
      ...
      public void basicPublish(String message, String bindingKey) throws IOException {
          if (connection.isOpen()) {
              channel.basicPublish(exchangeName, bindingKey, null, message.getBytes("UTF-8"));
          }
          else {
              throw new IOException();
          }
      }
    }

## Web Application
현재 `mirlang2.ddns.net/office-drive/` 로 접속하면 테스트 가능하다.  
`접속정보` `ID` `PASSWARD` `daniel` `1234`  

AWS 서버 한대에서 모든 프로젝트를 가동하려면 애플리케이션 상호 호출 문제로 인해 `Docker`로 가상서버를 가동할 필요가 있다. 그러나 현재 테스트는 Web Application이 Rest API Server 역할까지 하고 있다.

#### RestTemplateBuilder [link][5]
spring은 RestTemplate로 편리하게 api를 호출할 수 있다. 그리고 `RestTemplateBuilder`를 이용하면 basic 인증헤더를 포함한 RestTemplate를 빈으로 등록해서 사용할 수 있다.

    private final RestTemplate restTemplate;

    @Autowired
    public AuthRestTemplate(RestTemplateBuilder builder, PropertyConfig propertyConfig) {
        this.restTemplate = builder.basicAuthorization(propertyConfig.getRest().get("username"), propertyConfig.getRest().get("password")).build();
    }

    public Map getDashboard(String username) {
        return this.restTemplate.getForObject("http://localhost:8010/dashboard/{username}", Map.class, username);
    }

    public Map getDashboard(String username, Integer setpage) {
        return this.restTemplate.getForObject("http://localhost:8010/dashboard/{username}/{setpage}", Map.class, username, setpage);
    }

#### 페이징 API 호출 [link][6]
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        //Rest API authentication call
        Map map = authRestTemplate.getDashboard(name);
        ...
        return "dashboard";
    }

## Android Application
Android는 config server를 사용하지 않는다. WAS와 달리 컨테이너 초기화 과정이 없기 때문이다.  
대신 application.properties 파일을 만들어두고 환경변수를 사용할 수 있다.  

#### application.properties [link][7]
아래처럼 등록한 환경변수를 사용할 수 있다.  

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this.getClass().getName(), "MessageService 실행");

        //set RabbitMQ properties
        RABBITMQ_USER = PropertyConfig.getConfigValue(this, "rabbitmq.user");
        RABBITMQ_PASSWORD = PropertyConfig.getConfigValue(this, "rabbitmq.password");
        RABBITMQ_HOST = PropertyConfig.getConfigValue(this, "rabbitmq.host");
        RABBITMQ_VIRTUAL_HOST = PropertyConfig.getConfigValue(this, "rabbitmq.virtualhost");
        RABBITMQ_EXCHANGE_NAME = PropertyConfig.getConfigValue(this, "rabbitmq.exchange");
        RABBITMQ_BINDING_KEY = PropertyConfig.getConfigValue(this, "rabbitmq.bindingkey");

        thread = new Thread(this);
        thread.start();
    }

#### Push Service 등록 [link][7]
GCM으로 Push Service를 구현하면 손실률이 있고 체계적인 관리가 어렵기 때문에 RabbitMQ 메시지 브로커를 사용했다. 아래 코드를 안드로이드의 서비스로 등록하면 백그라운드에서 동작하면서 메시지를 수신한다.  
수신한 메시지는 getNotification() 메서드로 알림을 표시한다.  

    public void receiveRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUsername(RABBITMQ_USER);
            factory.setPassword(RABBITMQ_PASSWORD);
            factory.setHost(RABBITMQ_HOST);
            factory.setVirtualHost(RABBITMQ_VIRTUAL_HOST);
            factory.setPort(5672);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(RABBITMQ_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, RABBITMQ_EXCHANGE_NAME, RABBITMQ_BINDING_KEY);

            Log.i(this.getClass().getName(), "RabbitMQ 연결됨");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    Log.i(this.getClass().getName(), "Received : " + message + " " + envelope.getRoutingKey());
                    getNotification(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

#### Push Message 발송 [link][8]
안드로이드에서 Rest API를 호출하여 메시지를 발송할 수 있다.
spring android 프로젝트를 이용하면 안드로이드에서도 RestTemplate을 사용해 HTTP 통신을 쉽게 할 수 있다.
다만 RestTemplateBuilder를 제공하지 않으므로 `HttpBasicAuthentication`로 인증헤더를 생성해서 사용하면 된다.

    public void sendRabbitMQ() {
        //메시지 객체 생성
        Inbox ...

        //인증 헤더 생성
        HttpHeaders httpHeaders = AuthHeaders.getHttpRequest(this);

        //spring android RestTemplate Http 통신
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            restTemplate.exchange(RABBITMQ_REST_URI, HttpMethod.POST, new HttpEntity<Inbox>(inbox, httpHeaders), Inbox.class);
        } ...
    }

#### HttpBasicAuthentication [link][9]
    public static HttpHeaders getHttpRequest(Context context) {
        String restUser = PropertyConfig.getConfigValue(context, "rest.user");
        String restPassword = PropertyConfig.getConfigValue(context, "rest.password");

        HttpAuthentication authHeader = new HttpBasicAuthentication(restUser, restPassword);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return requestHeaders;
    }

## 정리
지금까지 하드코딩된 property 정보를 사용하지 않았다. Spring Config Server에서 가져온 환경변수를 사용하여 profiles 별로 DataSource, Authentication 등의 Spring 빈을 자동등록하고 사용했다.  
Web Application에 Spring Cloud Bus를 추가하면 서버 재시작 없이 properties를 새로 적용하여 서비스할 수도 있다.

[0]: https://github.com/kangyongho/office-drive/blob/master/office-drive-rest-server/src/main/java/net/ddns/office/drive/config/WebSecurityConfig.java
[1]: https://github.com/kangyongho/spring-cloud-config
[2]: https://github.com/kangyongho/office-drive/blob/master/office-drive-rest-server/src/main/java/net/ddns/office/drive/controller/MainController.java
[3]: https://github.com/kangyongho/office-drive/blob/master/office-drive-rest-server/src/main/java/net/ddns/office/drive/controller/RabbitMQManagerController.java
[4]: https://github.com/kangyongho/office-drive/blob/master/office-drive-rest-server/src/main/java/net/ddns/office/drive/helper/RabbitMQHelper.java
[5]: https://github.com/kangyongho/office-drive/blob/master/office-drive-web-client/src/main/java/office/drive/web/clinet/config/AuthRestTemplate.java
[6]: https://github.com/kangyongho/office-drive/blob/master/office-drive-web-client/src/main/java/office/drive/web/clinet/controller/MainController.java
[7]: https://github.com/kangyongho/office-drive/blob/master/Office-drive-android-client/app/src/main/java/office/drive/android/MessageService.java
[8]: https://github.com/kangyongho/office-drive/blob/master/Office-drive-android-client/app/src/main/java/office/drive/android/SendMessageActivity.java
[9]: https://github.com/kangyongho/office-drive/blob/master/Office-drive-android-client/app/src/main/java/office/drive/android/config/AuthHeaders.java
