package jp.co.ecample.nishikigi_emon.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
	        dto.setsStatusFlag(safety.getsStatusFlag());

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
			Integer siteId,
			String judgement) {
	    List<Safety> safetyList = repository.search(sCreatedAt, siteId, judgement);
	    List<SafetyList> safetyListDto = new ArrayList<>();
	    
	    for (Safety safety : safetyList) {

	        SafetyList dto = new SafetyList();

	        dto.setSafetyId(safety.getSafetyId());
	        dto.setsCreatedAt(safety.getsCreatedAt());
	        dto.setSite(safety.getSite());
	        dto.setsStatusFlag(safety.getsStatusFlag());


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
	
	// 写真を保存
	public String savePhoto(
			byte[] imageBytes,
	        String fileName) {
		String newFileName =
		        UUID.randomUUID() + "_" + fileName;
		
        Path uploadPath =
                Paths.get("src/main/resources/static/images/safety");
        
        try {
			Files.createDirectories(uploadPath);
			
	        Files.write(
	                uploadPath.resolve(newFileName),
	                imageBytes);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        
        return "/images/safety/" + newFileName;
	}

	// 安全点検情報を更新
	public void updateSafety(
			Integer safetyId,
			Integer scaffolding,
			Integer protectingOpenings,
			Integer safetyHarness,
			Integer equipmentInspection,
			Integer fireExtinguisher,
			Integer organization,
			Integer electricalInsulation,
			String photo
			) {
		Safety safety = repository.findById(safetyId)
				.orElseThrow(() -> new RuntimeException("Not found"));
		
	    // 既存画像パスを保存
	    String oldPhoto = safety.getPhoto();

		safety.setScaffolding(scaffolding);
		safety.setProtectingOpenings(protectingOpenings);
		safety.setSafetyHarness(safetyHarness);
		safety.setEquipmentInspection(equipmentInspection);
		safety.setFireExtinguisher(fireExtinguisher);
		safety.setOrganization(organization);
		safety.setElectricalInsulation(electricalInsulation);
		safety.setPhoto(photo);
		safety.setsUpdatedAt(LocalDateTime.now());
		
	    // 画像が変更された場合のみ更新＆削除
	    if (photo != null && !photo.isEmpty() && oldPhoto != null && !oldPhoto.equals(photo)) {
	        // 古い画像削除
	            try {
	                Path oldPath = Paths.get("src/main/resources/static", oldPhoto);

	                Files.deleteIfExists(oldPath);

	            } catch (Exception e) {
	                System.out.println("旧画像削除失敗: " + e.getMessage());
	            }
	    }

		repository.save(safety);
	}

	// 確認完了処理
	public void confirmSafety(Integer safetyId) {
		Safety safety = repository.findById(safetyId)
				.orElseThrow(() -> new RuntimeException("Not found"));
		
		safety.setsStatusFlag(1);
		repository.save(safety);
	}
	
	// 確認解除処理
	public void confirmCancelSafety(Integer safetyId) {
		Safety safety = repository.findById(safetyId)
				.orElseThrow(() -> new RuntimeException("Not found"));
		
		safety.setsStatusFlag(0);
		repository.save(safety);
	}
}
