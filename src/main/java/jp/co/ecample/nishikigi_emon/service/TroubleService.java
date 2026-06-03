package jp.co.ecample.nishikigi_emon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.repository.TroubleRepository;

@Service
public class TroubleService {

	private final TroubleRepository troubleRepository;

	public TroubleService(TroubleRepository troubleRepository) {
		this.troubleRepository = troubleRepository;
	}

	// DBから社員の情報を全件取得
	public List<Trouble> selectAll() {
		return troubleRepository.findAll();
	}

	//	// トラブル登録処理
	//	public void register(Trouble trouble) {
	//
	//		trouble.setOccurredAt(LocalDateTime.now());
	//		trouble.settStatusFlag(0);
	//		trouble.settCreatedAt(LocalDateTime.now());
	//		trouble.settUpdatedAt(LocalDateTime.now());
	//
	//		troubleRepository.save(trouble);
	//	}

	// トラブル登録処理
	public Trouble register(Trouble trouble) {

		trouble.setOccurredAt(LocalDateTime.now());
		trouble.settStatusFlag(0);
		trouble.settCreatedAt(LocalDateTime.now());
		trouble.settUpdatedAt(LocalDateTime.now());

		return troubleRepository.save(trouble);
	}

	// 通知表示用
	public List<Trouble> getActiveTroubles() {

		return troubleRepository.findByPriorityNot(0);

	}

	// トラブル検索処理
	public List<Trouble> search(
			LocalDate occurredDate,
			String siteName,
			Integer priority,
			Integer troubleType,
			Integer statusFlag) {

		return troubleRepository.search(
				occurredDate,
				siteName,
				priority,
				troubleType,
				statusFlag);
	}

	// 指定IDの情報をDBから取得
	public Trouble findById(Integer troubleId) {

		return troubleRepository.findById(troubleId)
				.orElse(null);
	}

	// トラブル状況を一段進める
	public void updateStatus(Integer id) {

		Trouble trouble = troubleRepository.findById(id).orElseThrow();

		if (trouble.gettStatusFlag() == 0) {

			// 提出済み → 対応中
			trouble.settStatusFlag(1);

		} else if (trouble.gettStatusFlag() == 1) {

			// 対応中 → 対応完了
			trouble.settStatusFlag(2);

			// 緊急度を0にする
			//trouble.setPriority(0);
		}

		troubleRepository.save(trouble);
	}

	// トラブル状況を一段戻す
	//	public void backStatus(Integer id) {
	//
	//		Trouble trouble = troubleRepository.findById(id).orElseThrow();
	//
	//		if (trouble.gettStatusFlag() == 2) {
	//
	//			// 対応完了 → 対応中
	//			trouble.settStatusFlag(1);
	//
	//		} else if (trouble.gettStatusFlag() == 1) {
	//
	//			// 対応中 → 提出済み
	//			trouble.settStatusFlag(0);
	//
	//		}
	//
	//		troubleRepository.save(trouble);
	//	}

	@Transactional
	public void update(Trouble input) {

		Trouble trouble = troubleRepository.findById(input.getTroubleId()).orElseThrow();

		trouble.setPriority(input.getPriority());
		trouble.setTroubleType(input.getTroubleType());
		trouble.setOverview(input.getOverview());
		trouble.setDetail(input.getDetail());

		trouble.setSiteMemo(input.getSiteMemo());
		trouble.setHqMemo(input.getHqMemo());

		trouble.settUpdatedAt(LocalDateTime.now());

		troubleRepository.save(trouble);
	}

}
