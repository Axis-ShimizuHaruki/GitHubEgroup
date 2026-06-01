package jp.co.ecample.nishikigi_emon.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    /**
     * ルーム(現場)
     */
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    /**
     * 送信元現場
     */
    @ManyToOne
    @JoinColumn(name = "sender_site_id", nullable = false)
    private Site senderSite;

    // コンストラクタ
    public Chat() {
    }

    // getter setter
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}