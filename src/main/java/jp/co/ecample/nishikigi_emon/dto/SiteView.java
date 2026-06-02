package jp.co.ecample.nishikigi_emon.dto;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;

public class SiteView {

    private Site site;

    private Integer maxPriority;
    
    private boolean dailyChecked;

    private String dailyStatus;
    
    private String safetyStatus;
    
    private Dailyreport todayReport;
    
    private Safety todaySafety;
    
    private boolean mySite;
    
    public SiteView(
            Site site,
            int maxPriority,
            String dailyStatus,
            String safetyStatus,
            boolean mySite) {

        this.site = site;
        this.maxPriority = maxPriority;
        this.dailyStatus = dailyStatus;
        this.safetyStatus = safetyStatus;
        this.mySite = mySite;
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
	public Dailyreport getTodayReport() {
	    return todayReport;
	}

	public void setTodayReport(Dailyreport todayReport) {
	    this.todayReport = todayReport;
	}
	public Safety getTodaySafety() {
	    return todaySafety;
	}

	public void setTodaySafety(Safety todaySafety) {
	    this.todaySafety = todaySafety;
	}
	
	public boolean isMySite() {
	    return mySite;
	}

	public void setMySite(boolean mySite) {
	    this.mySite = mySite;
	}
}