package jp.co.ecample.nishikigi_emon.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.repository.TroubleRepository;

@Service
public class TroubleService {

	private final TroubleRepository troubleRepository;

	public TroubleService(TroubleRepository troubleRepository) {
		this.troubleRepository = troubleRepository;
	}

	// トラブル登録処理
	public void register(Trouble trouble) {

		trouble.setOccurredAt(LocalDateTime.now());
		trouble.settStatusFlag(0);
		trouble.settCreatedAt(LocalDateTime.now());
		trouble.settUpdatedAt(LocalDateTime.now());

		troubleRepository.save(trouble);
	}

}
