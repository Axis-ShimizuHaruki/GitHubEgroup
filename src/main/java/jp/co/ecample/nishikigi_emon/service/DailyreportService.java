package jp.co.ecample.nishikigi_emon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.repository.DailyreportRepository;

@Service
@Transactional(rollbackFor = Exception.class)
public class DailyreportService {

    @Autowired
    private DailyreportRepository dailyreportRepository;

    /**
     * 動作：全ての日報データを対象日付の新しい順で取得する（本社管理者用）
     */
    public List<Dailyreport> getAllReports() {
        return dailyreportRepository.findAllByOrderByTargetDateDesc();
    }
    
    /**
     * 動作：日付・現場名・判定フラグを条件に日報を検索・絞り込みする
     */
    public List<Dailyreport> searchReports(java.time.LocalDate targetDate, Integer siteId, Integer dStatusFlag) {
        return dailyreportRepository.searchReports(targetDate, siteId, dStatusFlag);
    }

    /**
     * 動作：特定の現場IDに絞り込んだ日報データを対象日付の新しい順で取得する（現場所長・一般用）
     */
    public List<Dailyreport> getReportsBySite(Integer siteId) {
        return dailyreportRepository.findBySiteSiteIdOrderByTargetDateDesc(siteId);
    }

    /**
     * 動作：指定された日報IDからデータを1件取得する
     */
    public Dailyreport getReportById(Integer reportId) {
        return dailyreportRepository.findById(reportId).orElse(null);
    }

    
    /**
     * 動作：新しい日報データをデータベースに新規登録（保存）する
     */
    public Dailyreport createReport(Dailyreport report) {
    	if (dailyreportRepository.existsBySiteSiteIdAndTargetDate(report.getSite().getSiteId(), report.getTargetDate())) {
    	    throw new IllegalStateException("該当日の日報は既に登録されています。");
    	}
        report.setDStatusFlag(0); // 業務ルール：提出時は自動的に「0:提出済み(未確認)」
        return dailyreportRepository.save(report);
    }

    /**
     * 動作：編集確認画面から送られた内容で、既存の日報データを上書き更新する
     */
    public Dailyreport updateReport(Dailyreport updatedData) {
        Dailyreport existingReport = dailyreportRepository.findById(updatedData.getReportId()).orElse(null);
        if (existingReport == null) {
            return null;
        }
        
        // 各入力項目を既存レコードへ確実に同期
        existingReport.setSite(updatedData.getSite());
        existingReport.setProjectNumber(updatedData.getProjectNumber());
        existingReport.setWeather(updatedData.getWeather());
        existingReport.setTemperature(updatedData.getTemperature());
        existingReport.setWorkDetails(updatedData.getWorkDetails());
        existingReport.setWorkerDetails(updatedData.getWorkerDetails());
        existingReport.setProgressPercent(updatedData.getProgressPercent());
        existingReport.setPhotoBefore(updatedData.getPhotoBefore());
        existingReport.setPhotoDuring(updatedData.getPhotoDuring());
        existingReport.setPhotoAfter(updatedData.getPhotoAfter());
        existingReport.setPhotoInspection(updatedData.getPhotoInspection());
        existingReport.setPhotoSafety(updatedData.getPhotoSafety());
        existingReport.setSafetyKy(updatedData.getSafetyKy());
        existingReport.setSafetyMeasure(updatedData.getSafetyMeasure());
        existingReport.setSafetyEducation(updatedData.getSafetyEducation());
        existingReport.setNearMiss(updatedData.getNearMiss());
        existingReport.setMaterials(updatedData.getMaterials());
        existingReport.setEquipments(updatedData.getEquipments());
        existingReport.setDesignChange(updatedData.getDesignChange());
        existingReport.setCustomerRequest(updatedData.getCustomerRequest());
        existingReport.setPartnerCoordination(updatedData.getPartnerCoordination());
        existingReport.setNextDaySchedule(updatedData.getNextDaySchedule());
        existingReport.setNotes(updatedData.getNotes());
        
        // 業務ルール：日報が修正されたため、ステータスを「0:提出済み(未確認)」に強制引き戻し
        //existingReport.setDStatusFlag(0);

        return dailyreportRepository.save(existingReport);
    }

    /**
     * 動作：本社管理者の確認アクションを受け、該当現場の日報を「1:確認完了」へ更新する
     */
    public boolean confirmReport(Integer reportId) {
        Dailyreport report = dailyreportRepository.findById(reportId).orElse(null);
        if (report != null) {
            report.setDStatusFlag(1); // 業務ルール：確認完了「1」をセット
            dailyreportRepository.save(report);
            return true;
        }
        return false;
    }
}