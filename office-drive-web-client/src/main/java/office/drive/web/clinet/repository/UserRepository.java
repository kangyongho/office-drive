package office.drive.web.clinet.repository;

import office.drive.web.clinet.domain.Authorities;
import office.drive.web.clinet.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-07.
 */
@Repository
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public User findOne(String name) {
        return em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Map<String, Object> findAllRelatedEntity(String name) {
        User user = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .getSingleResult();

//        String authority = user.getAuthoritiesList().get(0).getAuthority(); //TODO: 지연로딩 세션 오류 해결방법 찾기. 지연로딩 설정방법
//        String authority = "ROLE_USER";
        //TODO: User Entity를 찾지 못했을 때 예외처리

        Authorities authorities = em.find(Authorities.class, user.getId());
        String authority = authorities.getAuthority();
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("authority", authority);
        return map;
    }

    public User findByUserName(String name) {
        return em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
