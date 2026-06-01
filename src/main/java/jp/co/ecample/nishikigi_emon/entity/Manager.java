package jp.co.ecample.nishikigi_emon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manager")
public class Manager {
	@Id // 主キー
	@GeneratedValue /* 自動生成 */(strategy = GenerationType.IDENTITY)
	@Column(name = "manager_id")
	private Integer managerid;

	@Column(name = "site_id", nullable = false)
	private Integer siteid;
	
	@Column(name = "user_id", nullable = false)
	private Integer userid;
	
	
	// 外部キー(子側)
	@ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

	
	public Integer getManagerid() {
		return managerid;
	}

	public void setManagerid(Integer managerid) {
		this.managerid = managerid;
	}

	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
