package jp.co.ecample.nishikigi_emon.form;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import jp.co.ecample.nishikigi_emon.entity.Site;

public class SafetyForm {
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
	@Column(name = "photo")
	private String photo;
	
	// siteテーブル参照
	@ManyToOne
	@JoinColumn(name="site_id")
	private Site site;

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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	
}
