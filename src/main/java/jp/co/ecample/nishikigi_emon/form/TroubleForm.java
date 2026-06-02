package jp.co.ecample.nishikigi_emon.form;

public class TroubleForm {

	private Integer troubleId;

	private Integer priority;

	private Integer troubleType;

	private String overview;

	private String detail;

	public Integer getTroubleId() {
		return troubleId;
	}

	public void setTroubleId(Integer troubleId) {
		this.troubleId = troubleId;
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

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
