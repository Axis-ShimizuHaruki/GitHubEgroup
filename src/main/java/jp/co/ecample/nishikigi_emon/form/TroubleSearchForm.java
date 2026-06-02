package jp.co.ecample.nishikigi_emon.form;

import java.time.LocalDate;

public class TroubleSearchForm {

	private LocalDate occurredDate;

	private String siteName;

	private Integer priority;

	private Integer troubleType;

	private Integer statusFlag;

	public LocalDate getOccurredDate() {
		return occurredDate;
	}

	public void setOccurredDate(LocalDate occurredDate) {
		this.occurredDate = occurredDate;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getTroubleType() {
		return troubleType;
	}

	public void setTroubleType(Integer troubleType) {
		this.troubleType = troubleType;
	}

	public Integer getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}

}