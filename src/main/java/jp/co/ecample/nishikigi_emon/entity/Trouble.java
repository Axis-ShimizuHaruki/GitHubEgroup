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
@Table(name = "trouble")
public class Trouble {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trouble_id")
	private Integer troubleId;

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	@Column(name = "priority", nullable = false)
	private Integer priority;

	@Column(name = "trouble_type", nullable = false)
	private Integer troubleType;

	@Column(name = "overview", nullable = false, length = 100)
	private String overview;

	@Column(name = "detail", nullable = false, length = 500)
	private String detail;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	@Column(name = "t_status_flag", nullable = false)
	private Integer tStatusFlag;

	@Column(name = "t_created_at", nullable = false)
	private LocalDateTime tCreatedAt;

	@Column(name = "t_updated_at", nullable = false)
	private LocalDateTime tUpdatedAt;

	@Column(name = "site_memo")
	private String siteMemo;

	@Column(name = "hq_memo")
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

	public LocalDateTime getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(LocalDateTime occurredAt) {
		this.occurredAt = occurredAt;
	}

	public Integer gettStatusFlag() {
		return tStatusFlag;
	}

	public void settStatusFlag(Integer tStatusFlag) {
		this.tStatusFlag = tStatusFlag;
	}

	public LocalDateTime gettCreatedAt() {
		return tCreatedAt;
	}

	public void settCreatedAt(LocalDateTime tCreatedAt) {
		this.tCreatedAt = tCreatedAt;
	}

	public LocalDateTime gettUpdatedAt() {
		return tUpdatedAt;
	}

	public void settUpdatedAt(LocalDateTime tUpdatedAt) {
		this.tUpdatedAt = tUpdatedAt;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
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
