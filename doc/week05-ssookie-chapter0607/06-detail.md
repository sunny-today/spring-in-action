# REST API (Representational safe transfer)

## REST 구성
* 자원(Resource) - URI
* 행위(Verb) - HTTP METHOD 
* 표현(Representations) - MESSAGE


* 예) '이름이 Terry인 사용자를 생성한다' 라는 호출
    * 사용자 - 생성되는 리소스
    * 생성한다 - 메서드
    * 메시지 - 이름이 Terry인 사용자
    ```
    // REST 형태로 표현해보면
    
    HTTP POST, http://myweb/users/
    {  
       "users":{  
          "name":"terry"
       }
    }
    ```

### REST 메서드
* HTTP 메서드를 그대로 사용 (POST, GET, PUT, DELETE ..)

### HTTP 리소스
* REST 는 리소스 지향 아키텍쳐 스타일 → 모든 것을 리소스 즉 명사로 표현함.
```
// 사용자 생성
HTTP Post, http://myweb/users/
{  
   "name":"terry",
   "address":"seoul"
}

// 사용자 조회
HTTP Get, http://myweb/users/terry

// 사용자 수정
HTTP PUT Post, http://myweb/users/
{  
   "name":"terry",
   "address":"seoul"
}

// 사용자 삭제
HTTP DELETE, http://myweb/users/terry
```
## Reference
* [REST API의 이해와 설계-#1 개념 소개](https://bcho.tistory.com/953)

# SPA(Single-Page Application) vs. MPA(Multi-Page Application)



## Reference
* SPA vs. MPA(https://velog.io/@thms200/SPA-vs.-MPA)