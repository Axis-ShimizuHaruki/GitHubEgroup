package jp.co.ecample.nishikigi_emon.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import jp.co.ecample.nishikigi_emon.dto.ChatMessage;
import jp.co.ecample.nishikigi_emon.entity.Chat;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.repository.ChatRepository;
import jp.co.ecample.nishikigi_emon.repository.SiteRepository;


@Controller
public class ChatController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private SiteRepository siteRepository;

    @MessageMapping("/send")
    public void send(ChatMessage message) {

        Site roomSite =
            siteRepository.findById(message.getSiteId())
                          .orElseThrow();

        Site senderSite =
            siteRepository.findById(message.getSenderSiteId())
                          .orElseThrow();

        Chat chat = new Chat();

        chat.setMessage(message.getMessage());
        chat.setDateTime(LocalDateTime.now());

        chat.setSite(roomSite);
        chat.setSenderSite(senderSite);

        chatRepository.save(chat);

        messagingTemplate.convertAndSend(
            "/topic/chat/" + roomSite.getSiteId(),
            message
        );
    }
    

    
}
