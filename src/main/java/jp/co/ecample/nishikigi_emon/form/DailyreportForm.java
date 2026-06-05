package jp.co.ecample.nishikigi_emon.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * 日報登録・更新フォーム
 */
public class DailyreportForm {

    /** 日報ID（更新時のみ使用） */
    private Integer reportId;

    /** 現場ID */
    @NotNull(message = "現場を選択してください")
    private Integer siteId;

    /** 工事番号 */
    @NotBlank(message = "工事番号を入力してください")
    @Size(max = 10, message = "工事番号は10文字以内で入力してください")
    private String projectNumber;

    /** 対象日付 */
    @NotNull(message = "日付を入力してください")
    @PastOrPresent(message = "未来の日付は登録できません")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    /** 天候（1:晴れ、2:曇り、3:雨、4:雪） */
    @NotNull(message = "天候を選択してください")
    private Integer weather;

    /** 気温 */
    @NotNull(message = "気温を入力してください")
    private Integer temperature;

    /** 当日作業内容 */
    @NotBlank(message = "作業内容を入力してください")
    @Size(max = 500, message = "作業内容は500文字以内で入力してください")
    private String workDetails;

    /** 出面情報（JSON文字列） */
    //@NotBlank(message = "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください")
    private String workerDetails;

    /** 工程進捗（%） */
    @NotNull(message = "進捗率を入力してください")
    @Min(value = 0, message = "進捗率は0%以上で入力してください")
    @Max(value = 100, message = "進捗率は100%以下で入力してください")
    private Integer progressPercent;

//    /** 写真関係（任意・上限100文字） */
//    @Size(max = 100, message = "ファイル名は100文字以内で入力してください")
//    private String photoBefore;
//
//    @Size(max = 100, message = "ファイル名は100文字以内で入力してください")
//    private String photoDuring;
//
//    @Size(max = 100, message = "ファイル名は100文字以内で入力してください")
//    private String photoAfter;
//
//    /** 写真関係（必須・上限100文字） */
////    @NotBlank(message = "配筋検査写真のファイル名を入力してください")
////    @Size(max = 100, message = "ファイル名は100文字以内で入力してください")
////    private String photoInspection;
//
//    @NotBlank(message = "安全帯使用状況写真のファイル名を入力してください")
//    @Size(max = 100, message = "ファイル名は100文字以内で入力してください")
//    private String photoSafety;
    
    
    /** 写真関係（ファイルアップロード用 ＆ 確認画面データ引き回し用） */
    private MultipartFile uploadPhotoBefore;
    private String photoBeforeBase64;

    private MultipartFile uploadPhotoDuring;
    private String photoDuringBase64;

    private MultipartFile uploadPhotoAfter;
    private String photoAfterBase64;

    private MultipartFile uploadPhotoInspection;
    private String photoInspectionBase64;

    private MultipartFile uploadPhotoSafety;
    private String photoSafetyBase64;

    /** 安全管理関係 */
    @NotBlank(message = "KY項目を入力してください")
    @Size(max = 500, message = "KY項目は500文字以内で入力してください")
    private String safetyKy;

    @NotBlank(message = "安全対策を入力してください")
    @Size(max = 500, message = "安全対策は500文字以内で入力してください")
    private String safetyMeasure;

    @NotBlank(message = "新規入場者教育を入力してください")
    @Size(max = 500, message = "新規入場者教育は500文字以内で入力してください")
    private String safetyEducation;

    @NotBlank(message = "ヒヤリハットを入力してください")
    @Size(max = 500, message = "ヒヤリハットは500文字以内で入力してください")
    private String nearMiss;

    /** 資材・機材 */
    @NotBlank(message = "搬入物を入力してください")
    @Size(max = 500, message = "搬入物は500文字以内で入力してください")
    private String materials;

    @NotBlank(message = "レンタル機材稼働を入力してください")
    @Size(max = 500, message = "レンタル機材は500文字以内で入力してください")
    private String equipments;

    /** 問題・調整事項 */
    @NotBlank(message = "設計変更指示を入力してください")
    @Size(max = 500, message = "設計変更指示は500文字以内で入力してください")
    private String designChange;

    @NotBlank(message = "客先追加要望を入力してください")
    @Size(max = 500, message = "客先追加要望は500文字以内で入力してください")
    private String customerRequest;

    @NotBlank(message = "協力会社調整を入力してください")
    @Size(max = 500, message = "協力会社調整は500文字以内で入力してください")
    private String partnerCoordination;

    @NotBlank(message = "翌日予定を入力してください")
    @Size(max = 500, message = "翌日予定は500文字以内で入力してください")
    private String nextDaySchedule;

    /** コメント（任意） */
    @Size(max = 500, message = "コメントは500文字以内で入力してください")
    private String notes;

    /** 判定フラグ */
    private Integer dStatusFlag = 0;

    // --- Getter / Setter 一覧 ---
    public Integer getReportId() { 
    	return reportId; 
    }
    
    public void setReportId(Integer reportId) { 
    	this.reportId = reportId; 
    }

    public Integer getSiteId() { 
    	return siteId; 
    }
    
    public void setSiteId(Integer siteId) { 
    	this.siteId = siteId; 
    }

    public String getProjectNumber() { 
    	return projectNumber; 
    }
    
    public void setProjectNumber(String projectNumber) { 
    	this.projectNumber = projectNumber; 
    }

    public LocalDate getTargetDate() { 
    	return targetDate; 
    }
    
    public void setTargetDate(LocalDate targetDate) { 
    	this.targetDate = targetDate; 
    }

    public Integer getWeather() { 
    	return weather; 
    }
    
    public void setWeather(Integer weather) { 
    	this.weather = weather; 
    }

    public Integer getTemperature() { 
    	return temperature; 
    }
    
    public void setTemperature(Integer temperature) { 
    	this.temperature = temperature; 
    }

    public String getWorkDetails() { 
    	return workDetails; 
    }
    
    public void setWorkDetails(String workDetails) { 
    	this.workDetails = workDetails; 
    }

    public String getWorkerDetails() { 
    	return workerDetails; 
    }
    
    public void setWorkerDetails(String workerDetails) { 
    	this.workerDetails = workerDetails; 
    }

    public Integer getProgressPercent() { 
    	return progressPercent; 
    }
    
    public void setProgressPercent(Integer progressPercent) { 
    	this.progressPercent = progressPercent; 
    }

//    public String getPhotoBefore() { 
//    	return photoBefore; 
//    }
//    
//    public void setPhotoBefore(String photoBefore) { 
//    	this.photoBefore = photoBefore; 
//    }
//
//    public String getPhotoDuring() { 
//    	return photoDuring; 
//    }
//    
//    public void setPhotoDuring(String photoDuring) { 
//    	this.photoDuring = photoDuring; 
//    }
//
//    public String getPhotoAfter() { 
//    	return photoAfter; 
//    }
//    
//    public void setPhotoAfter(String photoAfter) { 
//    	this.photoAfter = photoAfter; 
//    }

//    public String getPhotoInspection() { 
//    	return photoInspection; 
//    }
//    
//    public void setPhotoInspection(String photoInspection) { 
//    	this.photoInspection = photoInspection; 
//    }

//    public String getPhotoSafety() { 
//    	return photoSafety; 
//    }
//    
//    public void setPhotoSafety(String photoSafety) { 
//    	this.photoSafety = photoSafety; 
//    }
    public MultipartFile getUploadPhotoBefore() { 
    	return uploadPhotoBefore; 
    }
    
    public void setUploadPhotoBefore(MultipartFile uploadPhotoBefore) { 
    	this.uploadPhotoBefore = uploadPhotoBefore; 
    }
    
    public String getPhotoBeforeBase64() { 
    	return photoBeforeBase64; 
    }
    
    public void setPhotoBeforeBase64(String photoBeforeBase64) { 
    	this.photoBeforeBase64 = photoBeforeBase64; 
    }

    public MultipartFile getUploadPhotoDuring() { 
    	return uploadPhotoDuring; 
    }
    
    public void setUploadPhotoDuring(MultipartFile uploadPhotoDuring) { 
    	this.uploadPhotoDuring = uploadPhotoDuring; 
    }
    
    public String getPhotoDuringBase64() { 
    	return photoDuringBase64; 
    }
    
    public void setPhotoDuringBase64(String photoDuringBase64) { 
    	this.photoDuringBase64 = photoDuringBase64; 
    }

    public MultipartFile getUploadPhotoAfter() { 
    	return uploadPhotoAfter; 
    }
    
    public void setUploadPhotoAfter(MultipartFile uploadPhotoAfter) { 
    	this.uploadPhotoAfter = uploadPhotoAfter; 
    }
    
    public String getPhotoAfterBase64() { 
    	return photoAfterBase64; 
    }
    
    public void setPhotoAfterBase64(String photoAfterBase64) { 
    	this.photoAfterBase64 = photoAfterBase64; 
    }

    public MultipartFile getUploadPhotoInspection() { 
    	return uploadPhotoInspection; 
    }
    
    public void setUploadPhotoInspection(MultipartFile uploadPhotoInspection) { 
    	this.uploadPhotoInspection = uploadPhotoInspection; 
    }
    
    public String getPhotoInspectionBase64() { 
    	return photoInspectionBase64; 
    }
    
    public void setPhotoInspectionBase64(String photoInspectionBase64) { 
    	this.photoInspectionBase64 = photoInspectionBase64; 
    }

    public MultipartFile getUploadPhotoSafety() { 
    	return uploadPhotoSafety; 
    }
    
    public void setUploadPhotoSafety(MultipartFile uploadPhotoSafety) { 
    	this.uploadPhotoSafety = uploadPhotoSafety; 
    }
    
    public String getPhotoSafetyBase64() { 
    	return photoSafetyBase64; 
    }
    
    public void setPhotoSafetyBase64(String photoSafetyBase64) { 
    	this.photoSafetyBase64 = photoSafetyBase64; 
    }
    
    public String getSafetyKy() { 
    	return safetyKy; 
    }
    
    public void setSafetyKy(String safetyKy) { 
    	this.safetyKy = safetyKy; 
    }

    public String getSafetyMeasure() { 
    	return safetyMeasure; 
    }
    
    public void setSafetyMeasure(String safetyMeasure) { 
    	this.safetyMeasure = safetyMeasure; 
    }

    public String getSafetyEducation() { 
    	return safetyEducation; 
    }
    
    public void setSafetyEducation(String safetyEducation) { 
    	this.safetyEducation = safetyEducation; 
    }

    public String getNearMiss() { 
    	return nearMiss; 
    }
    
    public void setNearMiss(String nearMiss) { 
    	this.nearMiss = nearMiss; 
    }

    public String getMaterials() { 
    	return materials; 
    }
    
    public void setMaterials(String materials) { 
    	this.materials = materials; 
    }

    public String getEquipments() { 
    	return equipments; 
    }
    
    public void setEquipments(String equipments) { 
    	this.equipments = equipments; 
    }

    public String getDesignChange() { 
    	return designChange; 
    }
    
    public void setDesignChange(String designChange) { 
    	this.designChange = designChange; 
    }

    public String getCustomerRequest() { 
    	return customerRequest; 
    }
    
    public void setCustomerRequest(String customerRequest) { 
    	this.customerRequest = customerRequest; 
    }

    public String getPartnerCoordination() { 
    	return partnerCoordination; 
    }
    
    public void setPartnerCoordination(String partnerCoordination) { 
    	this.partnerCoordination = partnerCoordination; 
    }

    public String getNextDaySchedule() { 
    	return nextDaySchedule; 
    }
    
    public void setNextDaySchedule(String nextDaySchedule) { 
    	this.nextDaySchedule = nextDaySchedule; 
    }

    public String getNotes() { 
    	return notes; 
    }
    
    public void setNotes(String notes) { 
    	this.notes = notes; 
    }

    public Integer getDStatusFlag() { 
    	return dStatusFlag; 
    }
    
    public void setDStatusFlag(Integer dStatusFlag) { 
    	this.dStatusFlag = dStatusFlag; 
    }
}
