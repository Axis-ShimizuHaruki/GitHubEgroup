package jp.co.ecample.nishikigi_emon.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.repository.SafetyRepository;

@Service
public class SafetyService {
	private final SafetyRepository repository;

	public SafetyService(SafetyRepository repository) {
		this.repository = repository;
	}

	// 安全点検を保存
	public void saveSafety(
			Integer scaffolding,
			Integer protectingOpenings,
			Integer safetyHarness,
			Integer equipmentInspection,
			Integer fireExtinguisher,
			Integer organization,
			Integer electricalInsulation,
			Integer siteId
			) {
		Safety safety = new Safety();
		safety.setScaffolding(scaffolding);
		safety.setProtectingOpenings(protectingOpenings);
		safety.setSafetyHarness(safetyHarness);
		safety.setEquipmentInspection(equipmentInspection);
		safety.setFireExtinguisher(fireExtinguisher);
		safety.setOrganization(organization);
		safety.setElectricalInsulation(electricalInsulation);
		safety.setsStatusFlag(0);
		safety.setsCreatedAt(LocalDateTime.now());
		safety.setsUpdatedAt(LocalDateTime.now());
		
		Site site = new Site();
		site.setSiteId(siteId);
		safety.setSite(site);
		
		// データベースに保存
		repository.save(safety);
	}

}
