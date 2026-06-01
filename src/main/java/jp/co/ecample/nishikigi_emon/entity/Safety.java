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
@Table(name = "safety")
public class Safety {
	// 安全点検ID
	@Id
	@Column(name = "safety_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer safetyId;
	
	// 足場(手すり・幅木・滑り止め)
	@Column(name = "scaffolding", nullable = false)
	private Integer scaffolding;
	
	// 開口部養生
	@Column(name = "protecting_openings", nullable = false)
	private Integer protectingOpenings;

	// 高所作業の安全帯使用
	@Column(name = "safety_harness", nullable = false)
	private Integer safetyHarness;
	
	// 重機点検記録
	@Column(name = "equipment_inspection", nullable = false)
	private Integer equipmentInspection;
	
	// 火気使用時の消火器配置
	@Column(name = "fire_extinguisher", nullable = false)
	private Integer fireExtinguisher;
	
	// 整理整頓(つまずきリスク)
	@Column(name = "organization", nullable = false)
	private Integer organization;

	// 仮設電気の絶縁
	@Column(name = "electrical_insulation", nullable = false)
	private Integer electricalInsulation;
	
	// 写真
	@Column(name = "photo")
	private String photo;

	// 作成日時
	@Column(name = "s_created_at", nullable = false)
	private LocalDateTime sCreatedAt = LocalDateTime.now();

	// 0：提出済み（未確認）, 1：確認完了
	@Column(name = "s_status_flag", nullable = false)
	private String sStatusFlag;

	// 更新日時
	@Column(name = "s_updated_at")
	private LocalDateTime sUpdatedAt;
	
	// siteテーブル参照
	@ManyToOne
	@JoinColumn(name="site_id")
	private Site site;

	public Integer getSafetyId() {
		return safetyId;
	}

	public void setSafetyId(Integer safetyId) {
		this.safetyId = safetyId;
	}

	public Integer getScaffolding() {
		return scaffolding;
	}

	public void setScaffolding(Integer scaffolding) {
		this.scaffolding = scaffolding;
	}

	public Integer getProtectingOpenings() {
		return protectingOpenings;
	}

	public void setProtectingOpenings(Integer protectingOpenings) {
		this.protectingOpenings = protectingOpenings;
	}

	public Integer getSafetyHarness() {
		return safetyHarness;
	}

	public void setSafetyHarness(Integer safetyHarness) {
		this.safetyHarness = safetyHarness;
	}

	public Integer getEquipmentInspection() {
		return equipmentInspection;
	}

	public void setEquipmentInspection(Integer equipmentInspection) {
		this.equipmentInspection = equipmentInspection;
	}

	public Integer getFireExtinguisher() {
		return fireExtinguisher;
	}

	public void setFireExtinguisher(Integer fireExtinguisher) {
		this.fireExtinguisher = fireExtinguisher;
	}

	public Integer getOrganization() {
		return organization;
	}

	public void setOrganization(Integer organization) {
		this.organization = organization;
	}

	public Integer getElectricalInsulation() {
		return electricalInsulation;
	}

	public void setElectricalInsulation(Integer electricalInsulation) {
		this.electricalInsulation = electricalInsulation;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public LocalDateTime getsCreatedAt() {
		return sCreatedAt;
	}

	public void setsCreatedAt(LocalDateTime sCreatedAt) {
		this.sCreatedAt = sCreatedAt;
	}

	public String getsStatusFlag() {
		return sStatusFlag;
	}

	public void setsStatusFlag(String sStatusFlag) {
		this.sStatusFlag = sStatusFlag;
	}

	public LocalDateTime getsUpdatedAt() {
		return sUpdatedAt;
	}

	public void setsUpdatedAt(LocalDateTime sUpdatedAt) {
		this.sUpdatedAt = sUpdatedAt;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	
}
