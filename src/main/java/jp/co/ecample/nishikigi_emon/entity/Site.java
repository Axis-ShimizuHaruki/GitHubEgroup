package jp.co.ecample.nishikigi_emon.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id")
    private Integer siteId;

    /**
     * 本社(0) / 現場(1)
     */
    @Column(name = "officecheck", nullable = false)
    private Boolean officecheck;

    @Column(name = "site_name", nullable = false, length = 100)
    private String siteName;

    //本社ホーム表示用に
    @OneToMany(mappedBy = "site")
    private List<Trouble> troubleList;
    
    @OneToMany(mappedBy = "site")
    private List<Dailyreport> dailyreportList;
    
    @OneToMany(mappedBy = "site")
    private List<Safety> safetyList;
    
    @OneToMany(mappedBy = "site")
    private List<Manager> managerList;
    
    // コンストラクタ
    public Site() {
    }

    // getter setter
    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Boolean getOfficecheck() {
        return officecheck;
    }

    public void setOfficecheck(Boolean officecheck) {
        this.officecheck = officecheck;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

	public List<Trouble> getTroubleList() {
		return troubleList;
	}

	public void setTroubleList(List<Trouble> troubleList) {
		this.troubleList = troubleList;
	}
    
	public List<Dailyreport> getDailyreportList() {
	    return dailyreportList;
	}

	public void setDailyreportList(List<Dailyreport> dailyreportList) {
	    this.dailyreportList = dailyreportList;
	}
	
	public List<Safety> getSafetyList() {
	    return safetyList;
	}

	public void setSafetyList(List<Safety> safetyList) {
	    this.safetyList = safetyList;
	}
	
	public List<Manager> getManagerList() {
	    return managerList;
	}
}