package jp.co.ecample.nishikigi_emon.service;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.Safety;
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
			Integer electricalInsulation
			) {
		Safety safety = new Safety();
		safety.setScaffolding(scaffolding);
		safety.setProtectingOpenings(protectingOpenings);
		safety.setSafetyHarness(safetyHarness);
		safety.setEquipmentInspection(equipmentInspection);
		safety.setFireExtinguisher(fireExtinguisher);
		safety.setOrganization(organization);
		safety.setElectricalInsulation(electricalInsulation);

		// データベースに保存
		repository.save(safety);
	}

}
