package jp.co.ecample.nishikigi_emon.repository;

import java.time.LocalDate; // 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ★追加
import org.springframework.data.repository.query.Param; // ★追加
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;

@Repository
public interface DailyreportRepository extends JpaRepository<Dailyreport, Integer> {
    
	// 同じ日付の日報は登録されない
	boolean existsBySiteSiteIdAndTargetDate(Integer siteId, java.time.LocalDate targetDate);
	
	
    // 全現場用：対象日付の新しい順で全取得
    List<Dailyreport> findAllByOrderByTargetDateDesc();

    // 現場絞り込み用
    List<Dailyreport> findBySiteSiteIdOrderByTargetDateDesc(Integer siteId);

    // 動的検索用のクエリ（選択されていない項目(NULL)は自動的にスルー）
    @Query("SELECT d FROM Dailyreport d WHERE " +
            "(:targetDate IS NULL OR d.targetDate = :targetDate) AND " +
            "(:siteId IS NULL OR d.site.siteId = :siteId) AND " +
            "(:dStatusFlag IS NULL OR d.dStatusFlag = :dStatusFlag) AND " +
            // 作業内容のキーワードあいまい検索（未入力・空文字の時は自動スルー）
            "(:workDetails IS NULL OR :workDetails = '' OR d.workDetails LIKE CONCAT('%', :workDetails, '%')) " +
            "ORDER BY d.targetDate DESC")
     List<Dailyreport> searchReports(
         @Param("targetDate") LocalDate targetDate,
         @Param("siteId") Integer siteId,
         @Param("dStatusFlag") Integer dStatusFlag,
         @Param("workDetails") String workDetails // 
     );
}
