access, refresh token 을 획득하기 위한 요청 시 auth code 를 어디로 보낼지에 따라서 코드가 많이 달라진다.

백엔드 위주로 하다보니, 당연히 스프링부트 서버로 보내야한다고 생각하고 열심히 구글링해서 따라했는데 그놈의 CORS 문제에 게속 부딪혔다.

# 백엔드로 Redirect

`REDIRECT_URI` 를 백엔드 서버, 8081 지정하고, token 을 header, body 에 담아서 보내면

CORS 에러가 발생한다.

![img_4.png](img_4.png)

처음 이 에러를 봤을 때는 잘 이해가 되지 않았다. (이미지 출처는 [이곳](https://ttl-blog.tistory.com/1434#%E2%9C%A8%20KakaoToken-1))

1. 프론트(`localhost:3000`)에서 로그인 요청을 받는다.
2. 백엔드(`localhost:8080`)에서 auth code 를 요청한다. (oauth2 가 해줌)
3. auth code 를 받고 다시 백엔드(`localhost:8080`)에서 access, refresh 토큰을 요청한다. (oauth2 가 해줌)
4. 발급받은 토큰을 cookie 에 담아서 프론트(`localhost:3000`) 으로 보낸다.
5. 프론트(`localhost:3000`) 은 LocalStorage 에 쿠키를 저장하고 자원 요청 시 사용한다.

여기서 백엔드 CORS 설정으로

```java

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000")
				.allowedHeaders("*")
				.allowedMethods("*")
				.allowCredentials(true)
				.maxAge(1800);
	}


}

```

프론트 - 백엔드 로그인 요청 시에만 CORS 이슈가 걸린다고 생각했다. 나머지 작업은 모두 백엔드 - 외부 인가서버 사이에서 이루어지는걸 생각했을 때 CORS 문제가 터지는게 이해가 되지 않았다.

가능한 원인을 고민해봤다. 

1. 외부 인가서버, 스프링서버에 등록된 redirect_uri 값이 다르다.
2. 





`[Redirect 할 프론트서버 URL]?accessToken=(로그인 후 발급한 access token, refres token)`

이렇게 Query String 으로 넘겨주어야 한다.

또한 Redirect 를 할수 없는 안드로이드 모바일 환경인 경우 애초에 사용하면 안된다.

따라서 `REDIRECT_URI` 를 백엔드 8081 이 아니라 프론트 3000 으로 지정해야한다.

별 생각없이 따라하던 대부분의 프로젝트에서 cookie 를 사용한다.

cookie 를 만들어서 토큰 담는거야 뭐 간단하다.

cookie 를 만들어서 어떻게 전달하는가?

```java
public String createAnyCookie(HttpServletResponse httpServletResponse){
		Cookie cookie=new Cookie("memberCode",null);
		cookie.setMaxAge(180);
		httpServletResponse.addCookie(cookie);
		return"redirect:/";
		}
```

HttpServletResponse 에 담아서 보낸다. 그럼 HttpServletResponse 는 엔드 유저에게 어떻게 전송되는가?

[공식 문서](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html#:~:text=Extends%20the%20ServletResponse,doGet%2C%20doPost%2C%20etc)
에 잘 나와있다.

간략하게 HTTP 요청에 응답하는 인터페이스로서 헤더, 쿠키, 상태 등을 담아서 전달한다.

```shell
Request URL: http://localhost:8080/servlet
Request Method: GET
Status Code: 200 
Remote Address: [::1]:8080
Referrer Policy: strict-origin-when-cross-origin
Connection: keep-alive
Content-Length: 0
Content-Type: text/plain
Date: Thu, 09 Sep 2021 08:33:42 GMT
Keep-Alive: timeout=60
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Accept-Encoding: gzip, deflate, br
Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
Connection: keep-alive
Host: localhost:8080
sec-ch-ua: "Google Chrome";v="93", " Not;A Brand";v="99", "Chromium";v="93"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Sec-Fetch-Dest: document
Sec-Fetch-Mode: navigate
Sec-Fetch-Site: none
Sec-Fetch-User: ?1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)
```

Servlet 을 통해서 여러가지 정보가 전달되는데, 세션과 쿠키 모두 웹 브라우저에 저장된다. LocalStorage 에 저장되도록 지정할 수 있고, 따로 지정하지 않는다면 Cookies 디렉토리에 저장된다.

![img_3.png](img_3.png)

# 프론트로 Redirect

백엔드로 Redirect 시킬 때와 전체 흐름은 비슷하다. 한가지 다른점은 인가서버에서 인가코드를 발급받고 백엔드가 아니라 프론트엔드 서버로 인가코드를 전송한다는 점이다.
프론트가 인가서버와 먼저 통신 후 인가코드를 받고, 그리고 백엔드로 요청을 보낸다.

프론트 <-> 백엔드 과정을 한단계 줄이고, 제3자의 인가서버에게 1단계를 위임하는 것이다. 외부 인가서버에 redirect uri 만 제대로 설정해주면 CORS 문제도 걱정없다.

그럼 백엔드 서버는 뭐하러 있는가?

인가코드는 엑세스, 리프레시 토큰을 요청하기 이전단계이니, 인가코드를 가진채로 실질적인 자원을 요청할 때 백엔드 서버를 거친다. 이 때, 백엔드 서버에서 스프링6 에서 새로나온 HTTP Interface 를 통해서
간단하게 외부 인가서버와 통신하며, 사용자 정보를 획득한다.
유효한 인가코드여야만 엑세스, 리프레시 토큰을 발급받으니 발급이 성공하면 회원 DB 에 데이터를 쌓고, 리프레시 토큰을 저장하는 등의 로직을 구현할 수 있다.

세션이 아니라 JWT 를 사용한다면, JWT 라이브러리를 이용해서 암호화, 복호화를 다 할 수 있다.

은인이나 다른없는 어떤 블로그에서도 확장성을 위해서 스프링 시큐리티를 아예 걷어내고, 오로지 소셜 로그인만 구현했다. JWT 암호화, 복호화 과정을 직접 구현한다면 스프링 시큐리티를 도입하지 않아도 상관없다.

물론 시큐리티를 도입하면 필터기반으로 돌아가는 특성 상, 컨트롤러 접근 이전에 모든 요청을 검증할 수 있다. Role 베이스로 세부적인 설정이 가능하며, 메서드 단위로 보안을 적용할 수도 있다.
로그인 성공, 실패 등등 여러가지 세부적인 보안 설정을 위한 인터페이스를 제공한다.

다만, 100%는 아니지만 필터와 인터셉터를 통해서 동일한 기능을 하도록 구현할 수 있다. (당연히 스프링 시큐리티가 스프링 프레임워크 내에서 만들어진 것이니 그래야만 한다)




