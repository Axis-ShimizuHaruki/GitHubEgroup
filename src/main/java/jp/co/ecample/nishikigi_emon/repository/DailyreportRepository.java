package jp.co.ecample.nishikigi_emon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;

@Repository
public interface DailyreportRepository extends JpaRepository<Dailyreport, Integer> {
    
    // 全現場用：対象日付の新しい順で全取得
    List<Dailyreport> findAllByOrderByTargetDateDesc();

    // ★ 現場絞り込み用：Siteオブジェクトの中のsiteIdを基準に降順で取得
    List<Dailyreport> findBySiteSiteIdOrderByTargetDateDesc(Integer siteId);
}
