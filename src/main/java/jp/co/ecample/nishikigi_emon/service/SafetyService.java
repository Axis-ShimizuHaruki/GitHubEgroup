package jp.co.ecample.nishikigi_emon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.dto.SafetyList;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.repository.SafetyRepository;

@Service
public class SafetyService {
	private final SafetyRepository repository;

	public SafetyService(SafetyRepository repository) {
		this.repository = repository;
	}
	
	// 全件取得
	public List<SafetyList> selectAll() {
	    List<Safety> safetyList = repository.findAll();
	    List<SafetyList> safetyListDto = new ArrayList<>();

	    for (Safety safety : safetyList) {

	        SafetyList dto = new SafetyList();

	        dto.setSafetyId(safety.getSafetyId());
	        dto.setsCreatedAt(safety.getsCreatedAt());
	        dto.setSite(safety.getSite());

	        int goodCount = 0;
	        int badCount = 0;

	        if (safety.getScaffolding() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getProtectingOpenings() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        if (safety.getSafetyHarness() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getEquipmentInspection() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getFireExtinguisher() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getOrganization() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        if (safety.getElectricalInsulation() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        dto.setGoodCount(goodCount);
	        dto.setBadCount(badCount);

	        dto.setJudgement(
	            badCount > 0 ? "要対応" : "良好"
	        );

	        safetyListDto.add(dto);
	    }

	    return safetyListDto;
	}
	
	// 検索機能
	public List<SafetyList> search(
			LocalDate sCreatedAt,
			String siteName,
			String judgement) {
	    List<Safety> safetyList = repository.search(sCreatedAt, siteName, judgement);
	    List<SafetyList> safetyListDto = new ArrayList<>();
	    
	    for (Safety safety : safetyList) {

	        SafetyList dto = new SafetyList();

	        dto.setSafetyId(safety.getSafetyId());
	        dto.setsCreatedAt(safety.getsCreatedAt());
	        dto.setSite(safety.getSite());

	        int goodCount = 0;
	        int badCount = 0;

	        if (safety.getScaffolding() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getProtectingOpenings() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        if (safety.getSafetyHarness() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getEquipmentInspection() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getFireExtinguisher() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }

	        if (safety.getOrganization() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        if (safety.getElectricalInsulation() == 0) {
	            goodCount++;
	        } else {
	            badCount++;
	        }
	        
	        dto.setGoodCount(goodCount);
	        dto.setBadCount(badCount);

	        dto.setJudgement(
	            badCount > 0 ? "要対応" : "良好"
	        );

	        safetyListDto.add(dto);
	    }
	    
	    return safetyListDto;
	}
	
	// IDから取得
	public Optional<Safety> findById(Integer safetyId) {
		return repository.findById(safetyId);
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
