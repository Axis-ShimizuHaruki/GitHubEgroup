package jp.co.ecample.nishikigi_emon.dto;

public class ChatMessage {

    private Integer siteId;
    private Integer senderSiteId;
    private String message;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSenderSiteId() {
        return senderSiteId;
    }

    public void setSenderSiteId(Integer senderSiteId) {
        this.senderSiteId = senderSiteId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
