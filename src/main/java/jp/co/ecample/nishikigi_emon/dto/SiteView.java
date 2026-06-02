package jp.co.ecample.nishikigi_emon.dto;

import jp.co.ecample.nishikigi_emon.entity.Site;

public class SiteView {

    private Site site;

    private Integer maxPriority;
    
    private boolean dailyChecked;

    private String dailyStatus;
    
    private String safetyStatus;
    
    public SiteView(Site site, Integer maxPriority) {
        this.site = site;
        this.maxPriority = maxPriority;
    }
    
    public SiteView(Site site, int maxPriority, boolean dailyChecked) {
        this.site = site;
        this.maxPriority = maxPriority;
        this.dailyChecked = dailyChecked;
    }
    
    public SiteView(
            Site site,
            int maxPriority,
            String dailyStatus,
            String safetyStatus) {

        this.site = site;
        this.maxPriority = maxPriority;
        this.dailyStatus = dailyStatus;
        this.safetyStatus = safetyStatus;
    }
    
    
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Integer getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(Integer maxPriority) {
        this.maxPriority = maxPriority;
    }

	public boolean isDailyChecked() {
		return dailyChecked;
	}

	public void setDailyChecked(boolean dailyChecked) {
		this.dailyChecked = dailyChecked;
	}
    
	public String getDailyStatus() {
	    return dailyStatus;
	}

	public void setDailyStatus(String dailyStatus) {
	    this.dailyStatus = dailyStatus;
	}
	
	public String getSafetyStatus() {
	    return safetyStatus;
	}

	public void setSafetyStatus(String safetyStatus) {
	    this.safetyStatus = safetyStatus;
	}
}