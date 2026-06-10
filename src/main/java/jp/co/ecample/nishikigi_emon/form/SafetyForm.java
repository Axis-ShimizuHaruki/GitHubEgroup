package jp.co.ecample.nishikigi_emon.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import jp.co.ecample.nishikigi_emon.entity.Site;

public class SafetyForm {
	// ID
	private Integer safetyId;
	
	// 足場(手すり・幅木・滑り止め)
	@NotNull(message = "この項目は必須です")
	private Integer scaffolding;
	
	// 開口部養生
	@NotNull(message = "この項目は必須です")
	private Integer protectingOpenings;

	// 高所作業の安全帯使用
	@NotNull(message = "この項目は必須です")
	private Integer safetyHarness;
	
	// 重機点検記録
	@NotNull(message = "この項目は必須です")
	private Integer equipmentInspection;
	
	// 火気使用時の消火器配置
	@NotNull(message = "この項目は必須です")
	private Integer fireExtinguisher;
	
	// 整理整頓(つまずきリスク)
	@NotNull(message = "この項目は必須です")
	private Integer organization;

	// 仮設電気の絶縁
	@NotNull(message = "この項目は必須です")
	private Integer electricalInsulation;
	
	// 写真
	private String photo;
	private MultipartFile photoFile;
	
	// 作成日時
	private LocalDateTime sCreatedAt;
	
	// 0：提出済み（未確認）, 1：確認完了
	private Integer sStatusFlag;

	// 更新日時
	private LocalDateTime sUpdatedAt;
	
	// siteテーブル参照
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

	public MultipartFile getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(MultipartFile photoFile) {
		this.photoFile = photoFile;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public LocalDateTime getsCreatedAt() {
		return sCreatedAt;
	}

	public void setsCreatedAt(LocalDateTime sCreatedAt) {
		this.sCreatedAt = sCreatedAt;
	}

	public Integer getsStatusFlag() {
		return sStatusFlag;
	}

	public void setsStatusFlag(Integer sStatusFlag) {
		this.sStatusFlag = sStatusFlag;
	}

	public LocalDateTime getsUpdatedAt() {
		return sUpdatedAt;
	}

	public void setsUpdatedAt(LocalDateTime sUpdatedAt) {
		this.sUpdatedAt = sUpdatedAt;
	}

	
}
