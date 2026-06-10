package jp.co.ecample.nishikigi_emon.dto;

import java.time.LocalDateTime;

public record DefectSafetyDto(String siteName, LocalDateTime createdAt, Integer siteId) {
	
}

