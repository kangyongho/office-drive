package office.drive.web.clinet.service;

import office.drive.web.clinet.domain.Inbox;
import office.drive.web.clinet.domain.User;
import office.drive.web.clinet.repository.InboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-12.
 */
@Service
@Transactional
public class InboxService {

    @Autowired
    private InboxRepository inboxRepository;

    public List<Inbox> getListAll() {
        return inboxRepository.getListAll();
    }

    public Map<String, Object> getReceivedMessage(Integer setpage, User user) {
        return inboxRepository.getReceivedMessage(setpage, user);
    }

    public Map<String, Object> getSentMessage(Integer setpage, User user) {
        return inboxRepository.getSentMessage(setpage, user);
    }

    public void insert(Inbox inbox) {
        inboxRepository.insert(inbox);
    }
}
