## User Authority Customizing
<hr>
- Spring Security에서 기본적으로 제공 <br>
- UserDetails, UserDetailsService를 이해하여 Custom <br>
<hr>

- User Domain Class
```java
import org.springramework.security.core.userdetails.UserDetails;

public class User implements UserDetails {
	@Override
	....................
}
```
> UserDetails
- Spring Security에서 사용자의 정보를 담는 인터페이스이다.
- Spring Security에서 기본적으로 제공하는 UserDetails Object로는 Application에 필요한 User 정보를 모두 담을 수 없으므로 Custom해서 사용
<hr>

- User Repo Interface
```java
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
```
<hr>

- UserRepositoryUserDetailsService
```java
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserRepositoryUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if(user != null) {
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + username + "' not found");
    }
}
```
> UserDetailsService
- Spring Security에서 유저의 정보를 가져오는 인터페이스이다.
- Override loadUserByUsername()

## Security 후 정리 필요
XSS
SQL-Injection
CSRF
Spring Security_CSRF Token의 개념과 사용 방법

출처 : <br>
https://sj602.github.io/2018/07/10/what-is-xss/
https://sj602.github.io/2018/07/13/what-is-SQL-injection/
https://sj602.github.io/2018/07/14/what-is-CSRF/
https://codevang.tistory.com/282