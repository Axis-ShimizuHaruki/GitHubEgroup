package jp.co.ecample.nishikigi_emon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * 日報情報エンティティ
 * 現場から提出される日報データ（作業実績、出面、写真、安全管理、資材機材など）を管理します。
 */
@Entity
@Table(name = "dailyreport")
public class Dailyreport {

	/** 日報ID（主キー / 自動採番） */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Integer reportId;

	/** 
	 * 現場情報（多対一のリレーション）
	 * site_id カラムを外部キーとして site テーブルと結合します。
	 */
	@ManyToOne
	@JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
	private Site site;

	/** 工事番号（現場ごとに割り振られる最大10桁の固有識別番号） */
	@Column(name = "project_number", length = 10, nullable = false)
	private String projectNumber;

	/** 日報の対象日付 */
	@Column(name = "target_date", nullable = false)
	private LocalDate targetDate;

	/** 天候コード（1:晴れ、2:曇り、3:雨、4:雪） */
	@Column(name = "weather", nullable = false)
	private Integer weather;

	/** 気温（℃） */
	@Column(name = "temperature", nullable = false)
	private Integer temperature;

	/** 当日作業内容（工種別の作業実績テキスト） */
	@Column(name = "work_details", length = 500, nullable = false)
	private String workDetails;

	/** 
	 * 出面（でづら）情報（JSON形式文字列）
	 * 協力会社名、職人数、作業員氏名のリストをJSON配列構造で保持します。
	 * 例: [{"company":"錦木組", "count":2, "names":"山田、佐藤"}]
	 */
	@Column(name = "worker_details", columnDefinition = "JSON", nullable = false)
	private String workerDetails;

	/** 工程進捗率（0〜100のパーセンテージ数値） */
	@Column(name = "progress_percent", nullable = false)
	private Integer progressPercent;

	/** 写真：着工前（バイナリデータ） */
	@Lob
	@Column(name = "photo_before", columnDefinition="LONGBLOB")
	private byte[] photoBefore;

	/** 写真：施工中（バイナリデータ） */
	@Lob
	@Column(name = "photo_during", columnDefinition="LONGBLOB")
	private byte[] photoDuring;

	/** 写真：完了（バイナリデータ） */
	@Lob
	@Column(name = "photo_after", columnDefinition="LONGBLOB")
	private byte[] photoAfter;

	/** 配筋検査写真（バイナリデータ） */
	@Lob
	@Column(name = "photo_inspection", columnDefinition="LONGBLOB")
	private byte[] photoInspection; 

	/** 安全帯使用状況写真（必須項目 / バイナリデータ） */
	@Lob
	@Column(name = "photo_safety", columnDefinition="LONGBLOB", nullable = false)
	private byte[] photoSafety;

	/** 安全管理：KY（危険予知）項目 */
	@Column(name = "safety_ky", length = 500, nullable = false)
	private String safetyKy;

	/** 安全管理：KYに対する実施対策 */
	@Column(name = "safety_measure", length = 500, nullable = false)
	private String safetyMeasure;

	/** 安全管理：新規入場者教育の内容 */
	@Column(name = "safety_education", length = 500, nullable = false)
	private String safetyEducation;

	/** 安全管理：ヒヤリハット事例 */
	@Column(name = "near_miss", length = 500, nullable = false)
	private String nearMiss;

	/** 資材・機材：搬入資材、数量など */
	@Column(name = "materials", length = 500, nullable = false)
	private String materials;

	/** 資材・機材：レンタル機材などの稼働状況 */
	@Column(name = "equipments", length = 500, nullable = false)
	private String equipments;

	/** 問題・調整事項：設計変更指示の内容 */
	@Column(name = "design_change", length = 500, nullable = false)
	private String designChange;

	/** 問題・調整事項：客先からの追加要望 */
	@Column(name = "customer_request", length = 500, nullable = false)
	private String customerRequest;

	/** 問題・調整事項：協力会社との調整事項 */
	@Column(name = "partner_coordination", length = 500, nullable = false)
	private String partnerCoordination;

	/** 翌日の作業予定スケジュール */
	@Column(name = "next_day_schedule", length = 500, nullable = false)
	private String nextDaySchedule;

	/** 一日の総括（任意コメント） */
	@Column(name = "notes", length = 500)
	private String notes;

	/** 本社確認ステータスフラグ（0:提出済み[未確認], 1:本社確認完了） */
	@Column(name = "d_status_flag", nullable = false)
	private Integer dStatusFlag = 0;

	/** レコード作成日時（システムが自動設定、更新不可） */
	@Column(name = "d_created_at", nullable = false, updatable = false)
	private LocalDateTime dCreatedAt;

	/** レコード最終更新日時 */
	@Column(name = "d_updated_at", nullable = false)
	private LocalDateTime dUpdatedAt;

	/** 
	 * 永続化（インサート）前のエンティティリスナー
	 * レコード作成時、自動的に現在日時を登録・更新日時にセットします。
	 */
	@PrePersist
	protected void onCreate() {
		this.dCreatedAt = LocalDateTime.now();
		this.dUpdatedAt = LocalDateTime.now();
	}

	/** 
	 * 更新（アップデート）前のエンティティリスナー
	 * レコード更新時、自動的に最終更新日時を現在日時に書き換えます。
	 */
	@PreUpdate
	protected void onUpdate() {
		this.dUpdatedAt = LocalDateTime.now();
	}

	// --- Getters and Setters ---
	public Integer getReportId() { 
		return reportId; 
	}
	
	public void setReportId(Integer reportId) { 
		this.reportId = reportId; 
	}
	
	public Site getSite() { 
		return site; 
	}
	
	public void setSite(Site site) { 
		this.site = site; 
	}
	
	public String getProjectNumber() { 
		return projectNumber; 
	}
	
	public void setProjectNumber(String projectNumber) { 
		this.projectNumber = projectNumber; 
	}
	
	public LocalDate getTargetDate() { 
		return targetDate; 
	}
	
	public void setTargetDate(LocalDate targetDate) { 
		this.targetDate = targetDate; 
	}
	
	public Integer getWeather() { 
		return weather; 
	}
	
	public void setWeather(Integer weather) { 
		this.weather = weather; 
	}
	
	public Integer getTemperature() { 
		return temperature; 
	}
	
	public void setTemperature(Integer temperature) { 
		this.temperature = temperature; 
	}
	
	public String getWorkDetails() { 
		return workDetails; 
	}
	
	public void setWorkDetails(String workDetails) { 
		this.workDetails = workDetails; 
	}
	
	public String getWorkerDetails() { 
		return workerDetails; 
	}
	public void setWorkerDetails(String workerDetails) { 
		this.workerDetails = workerDetails; 
	}
	
	public Integer getProgressPercent() { 
		return progressPercent; 
	}
	
	public void setProgressPercent(Integer progressPercent) { 
		this.progressPercent = progressPercent; 
	}
	
	public byte[] getPhotoBefore() { 
		return photoBefore; 
	}
	
	public void setPhotoBefore(byte[] photoBefore) { 
		this.photoBefore = photoBefore; 
	}
	
	public byte[] getPhotoDuring() { 
		return photoDuring; 
	}
	
	public void setPhotoDuring(byte[] photoDuring) { 
		this.photoDuring = photoDuring; 
	}
	
	public byte[] getPhotoAfter() { 
		return photoAfter; 
	}
	
	public void setPhotoAfter(byte[] photoAfter) { 
		this.photoAfter = photoAfter; 
	}
	
	public byte[] getPhotoInspection() { 
		return photoInspection; 
	}
	
	public void setPhotoInspection(byte[] photoInspection) { 
		this.photoInspection = photoInspection; 
	}
	
	public byte[] getPhotoSafety() { 
		return photoSafety; 
	}
	
	public void setPhotoSafety(byte[] photoSafety) { 
		this.photoSafety = photoSafety; 
	}
	
	public String getSafetyKy() { 
		return safetyKy; 
	}
	
	public void setSafetyKy(String safetyKy) { 
		this.safetyKy = safetyKy; 
	}
	
	public String getSafetyMeasure() { 
		return safetyMeasure; 
	}
	
	public void setSafetyMeasure(String safetyMeasure) { 
		this.safetyMeasure = safetyMeasure; 
	}
	
	public String getSafetyEducation() { 
		return safetyEducation; 
	}
	
	public void setSafetyEducation(String safetyEducation) { 
		this.safetyEducation = safetyEducation; 
	}
	
	public String getNearMiss() { 
		return nearMiss; 
	}
	
	public void setNearMiss(String nearMiss) { 
		this.nearMiss = nearMiss; 
	}
	
	public String getMaterials() { 
		return materials; 
	}
	
	public void setMaterials(String materials) { 
		this.materials = materials; 
	}
	
	public String getEquipments() { 
		return equipments; 
	}
	
	public void setEquipments(String equipments) { 
		this.equipments = equipments; 
	}
	
	public String getDesignChange() { 
		return designChange; 
	}
	
	public void setDesignChange(String designChange) { 
		this.designChange = designChange; 
	}
	
	public String getCustomerRequest() { 
		return customerRequest; 
	}
	
	public void setCustomerRequest(String customerRequest) { 
		this.customerRequest = customerRequest; 
	}
	
	public String getPartnerCoordination() { 
		return partnerCoordination; 
	}
	
	public void setPartnerCoordination(String partnerCoordination) { 
		this.partnerCoordination = partnerCoordination; 
	}
	
	public String getNextDaySchedule() { 
		return nextDaySchedule; 
	}
	
	public void setNextDaySchedule(String nextDaySchedule) { 
		this.nextDaySchedule = nextDaySchedule; 
	}
	
	public String getNotes() { 
		return notes; 
	}
	
	public void setNotes(String notes) { 
		this.notes = notes; 
	}
	
	public Integer getDStatusFlag() { 
		return dStatusFlag; 
	}
	
	public void setDStatusFlag(Integer dStatusFlag) { 
		this.dStatusFlag = dStatusFlag; 
	}
	
	public LocalDateTime getDCreatedAt() { 
		return dCreatedAt; 
	}
	
	public LocalDateTime getDUpdatedAt() { 
		return dUpdatedAt; 
	}
}