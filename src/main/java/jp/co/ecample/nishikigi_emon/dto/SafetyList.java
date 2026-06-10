package jp.co.ecample.nishikigi_emon.dto;

import java.time.LocalDateTime;

import jp.co.ecample.nishikigi_emon.entity.Site;

public class SafetyList {
	private Integer safetyId;
	
	private LocalDateTime sCreatedAt;
	
	private String siteName;
	
	private int goodCount;
	
	private int badCount;
	
	private String judgement;
	
	private Integer sStatusFlag;
	
	private Site site;
	
	public Integer getSafetyId() {
		return safetyId;
	}

	public void setSafetyId(Integer safetyId) {
		this.safetyId = safetyId;
	}

	public LocalDateTime getsCreatedAt() {
		return sCreatedAt;
	}

	public void setsCreatedAt(LocalDateTime sCreatedAt) {
		this.sCreatedAt = sCreatedAt;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getGoodCount() {
		return goodCount;
	}

	public void setGoodCount(int goodCount) {
		this.goodCount = goodCount;
	}

	public int getBadCount() {
		return badCount;
	}

	public void setBadCount(int badCount) {
		this.badCount = badCount;
	}

	public String getJudgement() {
		return judgement;
	}

	public void setJudgement(String judgement) {
		this.judgement = judgement;
	}

	public Integer getsStatusFlag() {
		return sStatusFlag;
	}

	public void setsStatusFlag(Integer sStatusFlag) {
		this.sStatusFlag = sStatusFlag;
	}

	
}
