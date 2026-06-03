package jp.co.ecample.nishikigi_emon.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TroubleForm {

	private Integer troubleId;

	@NotNull(message = "緊急度を選択してください")
	private Integer priority;

	@NotNull(message = "トラブル種別を選択してください")
	private Integer troubleType;

	@NotBlank(message = "概要を入力してください")
	private String overview;

	@NotBlank(message = "詳細を入力してください")
	@Size(max = 500, message = "詳細は500文字以内で入力してください")
	private String detail;

	private String siteMemo;

	private String hqMemo;

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

	public String getSiteMemo() {
		return siteMemo;
	}

	public void setSiteMemo(String siteMemo) {
		this.siteMemo = siteMemo;
	}

	public String getHqMemo() {
		return hqMemo;
	}

	public void setHqMemo(String hqMemo) {
		this.hqMemo = hqMemo;
	}

}
