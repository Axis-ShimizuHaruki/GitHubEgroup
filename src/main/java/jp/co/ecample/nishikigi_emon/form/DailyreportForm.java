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
 * 画面からの入力値を受け取り、相関チェックやバリデーションを行うためのデータ転送オブジェクト（DTO）です。
 */
public class DailyreportForm {

    /** 日報ID（新規登録時はnull、既存日報の編集・更新時のみ使用） */
    private Integer reportId;

    /** 現場ID（どの現場の日報かを取り込むためのID / 選択必須） */
    @NotNull(message = "現場を選択してください")
    private Integer siteId;

    /** 工事番号（現場ごとに割り振られる最大10桁の固有識別番号 / 入力必須） */
    @NotBlank(message = "工事番号を入力してください")
    @Size(max = 10, message = "工事番号は10文字以内で入力してください")
    private String projectNumber;

    /** 対象日付（日報の対象となる稼働日 / 入力必須・未来日付の登録不可） */
    @NotNull(message = "日付を入力してください")
    @PastOrPresent(message = "未来の日付は登録できません")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    /** 天候コード（1:晴れ、2:曇り、3:雨、4:雪 / 選択必須） */
    @NotNull(message = "天候を選択してください")
    private Integer weather;

    /** 気温（当日の気温 / 入力必須、-99℃〜999℃の範囲内） */
    @NotNull(message = "気温を入力してください")
    @Min(value = -99, message = "気温は-99以上の数値を入力してください")
    @Max(value = 999, message = "気温は999以下の数値を入力してください")
    private Integer temperature;

    /** 当日作業内容（工種別の作業実績テキスト / 入力必須・最大500文字） */
    @NotBlank(message = "作業内容を入力してください")
    @Size(max = 500, message = "作業内容は500文字以内で入力してください")
    private String workDetails;

    /** * 出面（くずら）情報（JavaScript側で生成されたJSON文字列形式）
     * 協力会社名、職人数、作業員氏名の動的リストを格納します。
     * コントローラー側でカスタムバリデーション（0やマイナスのチェック等）を実施します。
     */
    //@NotBlank(message = "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください")
    private String workerDetails;

    /** 全体工程進捗率（0%〜100%の数値 / 入力必須） */
    @NotNull(message = "進捗率を入力してください")
    @Min(value = 0, message = "進捗率は0%以上で入力してください")
    @Max(value = 100, message = "進捗率は100%以下で入力してください")
    private Integer progressPercent;
    
    
    /** 写真関係（ファイルアップロード用 ＆ 確認画面データ引き回し用） */
    
    /** 着工前写真：ブラウザからアップロードされたファイルデータ */
    private MultipartFile uploadPhotoBefore;
    /** 着工前写真：確認画面への引き継ぎやプレビュー用のBase64エンコード済み文字列 */
    private String photoBeforeBase64;

    /** 施工中写真：ブラウザからアップロードされたファイルデータ */
    private MultipartFile uploadPhotoDuring;
    /** 施工中写真：確認画面への引き継ぎやプレビュー用のBase64エンコード済み文字列 */
    private String photoDuringBase64;

    /** 完了写真：ブラウザからアップロードされたファイルデータ */
    private MultipartFile uploadPhotoAfter;
    /** 完了写真：確認画面への引き継ぎやプレビュー用のBase64エンコード済み文字列 */
    private String photoAfterBase64;

    /** 配筋検査写真：ブラウザからアップロードされたファイルデータ */
    private MultipartFile uploadPhotoInspection;
    /** 配筋検査写真：確認画面への引き継ぎやプレビュー用のBase64エンコード済み文字列 */
    private String photoInspectionBase64;

    /** 安全帯使用状況写真：ブラウザからアップロードされたファイルデータ（必須項目） */
    private MultipartFile uploadPhotoSafety;
    /** 安全帯使用状況写真：確認画面への引き継ぎやプレビュー用のBase64エンコード済み文字列 */
    private String photoSafetyBase64;

    /** 安全管理：KY（危険予知）項目（入力必須・最大500文字） */
    @NotBlank(message = "KY項目を入力してください")
    @Size(max = 500, message = "KY項目は500文字以内で入力してください")
    private String safetyKy;

    /** 安全管理：KY項目に対する実施安全対策（入力必須・最大500文字） */
    @NotBlank(message = "安全対策を入力してください")
    @Size(max = 500, message = "安全対策は500文字以内で入力してください")
    private String safetyMeasure;

    /** 安全管理：新規入場者教育の実施内容（入力必須・最大500文字） */
    @NotBlank(message = "新規入場者教育を入力してください")
    @Size(max = 500, message = "新規入場者教育は500文字以内で入力してください")
    private String safetyEducation;

    /** 安全管理：当日のヒヤリハット事例（入力必須・最大500文字） */
    @NotBlank(message = "ヒヤリハットを入力してください")
    @Size(max = 500, message = "ヒヤリハットは500文字以内で入力してください")
    private String nearMiss;

    /** 資材・機材：当日の搬入資材、数量など（入力必須・最大500文字） */
    @NotBlank(message = "搬入物を入力してください")
    @Size(max = 500, message = "搬入物は500文字以内で入力してください")
    private String materials;

    /** 資材・機材：レンタル機材などの稼働状況（入力必須・最大500文字） */
    @NotBlank(message = "レンタル機材稼働を入力してください")
    @Size(max = 500, message = "レンタル機材は500文字以内で入力してください")
    private String equipments;

    /** 問題・調整事項：設計変更指示の内容（入力必須・最大500文字） */
    @NotBlank(message = "設計変更指示を入力してください")
    @Size(max = 500, message = "設計変更指示は500文字以内で入力してください")
    private String designChange;

    /** 問題・調整事項：客先からの追加要望（入力必須・最大500文字） */
    @NotBlank(message = "客先追加要望を入力してください")
    @Size(max = 500, message = "客先追加要望は500文字以内で入力してください")
    private String customerRequest;

    /** 問題・調整事項：協力会社との調整事項（入力必須・最大500文字） */
    @NotBlank(message = "協力会社調整を入力してください")
    @Size(max = 500, message = "協力会社調整は500文字以内で入力してください")
    private String partnerCoordination;

    /** 翌日の作業予定スケジュール（入力必須・最大500文字） */
    @NotBlank(message = "翌日予定を入力してください")
    @Size(max = 500, message = "翌日予定は500文字以内で入力してください")
    private String nextDaySchedule;

    /** 本社への総括・連絡等（任意項目・最大500文字） */
    @Size(max = 500, message = "コメントは500文字以内で入力してください")
    private String notes;

    /** 本社確認ステータスフラグ（デフォルト0:提出済み[未確認]、1:確認完了） */
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