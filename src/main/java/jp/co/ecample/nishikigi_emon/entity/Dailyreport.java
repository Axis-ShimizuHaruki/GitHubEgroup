package jp.co.ecample.nishikigi_emon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "dailyreport")
public class Dailyreport {

	//日報ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    // ★ site_id カラムを外部キーとして Site エンティティと結合
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
    private Site site;

    @Column(name = "project_number", length = 10, nullable = false)
    private String projectNumber;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "weather", nullable = false)
    private Integer weather;

    @Column(name = "temperature", nullable = false)
    private Integer temperature;

    @Column(name = "work_details", length = 500, nullable = false)
    private String workDetails;

    @Column(name = "worker_details", columnDefinition = "JSON", nullable = false)
    private String workerDetails;

    @Column(name = "progress_percent", nullable = false)
    private Integer progressPercent;

    @Column(name = "photo_before", length = 100)
    private String photoBefore;

    @Column(name = "photo_during", length = 100)
    private String photoDuring;

    @Column(name = "photo_after", length = 100)
    private String photoAfter;

    @Column(name = "photo_inspection", length = 100, nullable = false)
    private String photoInspection;

    @Column(name = "photo_safety", length = 100, nullable = false)
    private String photoSafety;

    @Column(name = "safety_ky", length = 500, nullable = false)
    private String safetyKy;

    @Column(name = "safety_measure", length = 500, nullable = false)
    private String safetyMeasure;

    @Column(name = "safety_education", length = 500, nullable = false)
    private String safetyEducation;

    @Column(name = "near_miss", length = 500, nullable = false)
    private String nearMiss;

    @Column(name = "materials", length = 500, nullable = false)
    private String materials;

    @Column(name = "equipments", length = 500, nullable = false)
    private String equipments;

    @Column(name = "design_change", length = 500, nullable = false)
    private String designChange;

    @Column(name = "customer_request", length = 500, nullable = false)
    private String customerRequest;

    @Column(name = "partner_coordination", length = 500, nullable = false)
    private String partnerCoordination;

    @Column(name = "next_day_schedule", length = 500, nullable = false)
    private String nextDaySchedule;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "d_status_flag", nullable = false)
    private Integer dStatusFlag = 0; // 0:提出済み（未確認）, 1:確認完了

    @Column(name = "d_created_at", nullable = false, updatable = false)
    private LocalDateTime dCreatedAt;

    @Column(name = "d_updated_at", nullable = false)
    private LocalDateTime dUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.dCreatedAt = LocalDateTime.now();
        this.dUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dUpdatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public Integer getReportId() { 
    	return reportId; 
    }
    
    public void setReportId(Integer reportId) { 
    	this.reportId = reportId; 
    }
    
    public Site getSite() { 
    	return site; 
    }
    
    public void setSite(Site site) { 
    	this.site = site; 
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
    
    public String getPhotoBefore() { 
    	return photoBefore; 
    }
    
    public void setPhotoBefore(String photoBefore) { 
    	this.photoBefore = photoBefore; 
    }
    
    public String getPhotoDuring() { 
    	return photoDuring; 
    }
    
    public void setPhotoDuring(String photoDuring) { 
    	this.photoDuring = photoDuring; 
    }
    
    public String getPhotoAfter() { 
    	return photoAfter; 
    }
    
    public void setPhotoAfter(String photoAfter) { 
    	this.photoAfter = photoAfter; 
    }
    
    public String getPhotoInspection() { 
    	return photoInspection; 
    }
    
    public void setPhotoInspection(String photoInspection) { 
    	this.photoInspection = photoInspection; 
    }
    
    public String getPhotoSafety() { 
    	return photoSafety; 
    }
    
    public void setPhotoSafety(String photoSafety) { 
    	this.photoSafety = photoSafety; 
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
    
    public LocalDateTime getDCreatedAt() { 
    	return dCreatedAt; 
    }
    
    public LocalDateTime getDUpdatedAt() { 
    	return dUpdatedAt; 
    }
}
