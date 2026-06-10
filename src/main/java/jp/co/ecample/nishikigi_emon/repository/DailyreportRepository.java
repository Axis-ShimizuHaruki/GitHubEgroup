package jp.co.ecample.nishikigi_emon.repository;

import java.time.LocalDate; // 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ★追加
import org.springframework.data.repository.query.Param; // ★追加
import org.springframework.stereotype.Repository;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;

/**
 * 日報情報リポジトリ
 * dailyreport テーブルへのCRUD操作および、日付や現場、ステータスを用いた各種検索処理を担当します。
 */
@Repository
public interface DailyreportRepository extends JpaRepository<Dailyreport, Integer> {
    
	/**
	 * 日報の重複登録チェック
	 * 「同じ現場」かつ「同じ日付」の日報が既にデータベースに登録されているかを判定します。
	 * 現場での同一日における日報の二重登録を防ぐバリデーション等で使用します。
	 *
	 * @param siteId 現場ID
	 * @param targetDate 対象日付
	 * @return すでに存在する場合は true、存在しない場合は false
	 */
	boolean existsBySiteSiteIdAndTargetDate(Integer siteId, java.time.LocalDate targetDate);
	
	
	/**
	 * 全現場用日報一覧取得（日付降順）
	 * 登録されているすべての現場の日報を、対象日付の新しい順（最新の日報が先頭）で全件取得します。
	 * 主に本社管理者向けの全現場日報一覧画面などで使用します。
	 *
	 * @return 日付降順に並び替えられた日報エンティティのリスト
	 */
	List<Dailyreport> findAllByOrderByTargetDateDesc();

	/**
	 * 特定現場用日報一覧取得（日付降順）
	 * 指定された現場IDに紐づく日報のみを、対象日付の新しい順（最新の日報が先頭）で取得します。
	 * 各現場の所長・作業員向けの現場ポータルや日報一覧画面などで使用します。
	 *
	 * @param siteId 絞り込み対象の現場ID
	 * @return 対象現場の日付降順に並び替えられた日報エンティティのリスト
	 */
	List<Dailyreport> findBySiteSiteIdOrderByTargetDateDesc(Integer siteId);

	/**
	 * 動的複合検索用クエリ（一覧画面の検索フォーム用）
	 * 日付、現場、本社確認ステータス、作業内容キーワードを組み合わせた高度な動的検索を行います。
	 * 各検索条件が未選択（NULL）または未入力（空文字）の場合は、その条件を自動的にスルー（無視）して全件対象とします。
	 *
	 * @param targetDate   検索対象の日付（未選択時はNULL）
	 * @param siteId       検索対象の現場ID（本社アカウントの絞り込み用 / 未選択時はNULL）
	 * @param dStatusFlag  検索対象の本社確認ステータス（0:提出済み, 1:確認完了 / 未選択時はNULL）
	 * @param workDetails  作業内容に含まれる検索キーワード（部分一致 / 未入力時はNULLまたは空文字）
	 * @return 検索条件に合致し、日付降順にソートされた日報エンティティのリスト
	 */
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