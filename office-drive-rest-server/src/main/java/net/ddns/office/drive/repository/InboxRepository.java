package net.ddns.office.drive.repository;

import net.ddns.office.drive.domain.Inbox;
import net.ddns.office.drive.domain.User;
import net.ddns.office.drive.helper.Pagination;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-12.
 */
@Repository
public class InboxRepository {

    @PersistenceContext
    EntityManager em;

    public void insert(Inbox inbox) {
        em.persist(inbox);
    }

    public List<Inbox> getListAll() {
        TypedQuery<Inbox> query = em.createQuery("SELECT i FROM Inbox i", Inbox.class);
        List<Inbox> resultList = query.getResultList();
        return resultList;
    }

    /**
     * 페이징 로직과 함께 사용
     * 총 게시물 수
     * @return
     */
    public int getTotalListCount() {
        int count = ((Number) em.createQuery("SELECT COUNT(*) FROM Inbox i").getSingleResult()).intValue();
        return count;
    }

    public Map<String, Object> getReceivedMessage(Integer setpage, User user) {
        //Pagination
        Pagination pagination = new Pagination(getTotalListCount(), 5, 5, setpage);

        //DB 조회
        List<Inbox> inboxList = em.createQuery("SELECT i FROM Inbox i WHERE i.receiver=:receiver ORDER BY id DESC", Inbox.class)
                .setParameter("receiver", user.getName())
                .setFirstResult((pagination.getPage() - 1) * pagination.getCountList())
                .setMaxResults(pagination.getCountList())
                .getResultList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", inboxList);
        map.put("startPage", pagination.getStartPage());
        return map;
    }

    public Map<String, Object> getSentMessage(Integer setpage, User user) {
        //Pagination
        Pagination pagination = new Pagination(getTotalListCount(), 5, 5, setpage);

        //DB 조회
        List<Inbox> inboxList = em.createQuery("SELECT i FROM Inbox i WHERE i.sender=:sender ORDER BY id DESC", Inbox.class)
                .setParameter("sender", user.getName())
                .setFirstResult((pagination.getPage() - 1) * pagination.getCountList())
                .setMaxResults(pagination.getCountList())
                .getResultList();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("list", inboxList);
        map.put("startPage", pagination.getStartPage());
        return map;
    }


}
