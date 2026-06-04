package jp.co.ecample.nishikigi_emon.controller;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.DailyreportForm;
import jp.co.ecample.nishikigi_emon.service.DailyreportService;
import jp.co.ecample.nishikigi_emon.service.SiteService;




@Controller
@RequestMapping("/dailyreport")
public class DailyreportController {

    private static final int ROLE_HONSHA = 0;
    private static final int ROLE_GENBA_MANAGER = 1;
    private static final int ROLE_GENBA_VIEWER = 2;
    
    private static final java.util.Map<String, String> imageCache = new ConcurrentHashMap<>();

    @Autowired
    private DailyreportService dailyreportService;

    @Autowired
    private SiteService siteService;

    // --- 共通チェックメソッド群 (変更なしのため省略) ---
    private String checkLogin(HttpSession session) { return session.getAttribute("loginUser") == null ? "redirect:/login" : null; }
    private String checkHonshaOnly(HttpSession session) { User u = (User) session.getAttribute("loginUser"); return (u == null) ? "redirect:/login" : (u.getRoll() != ROLE_HONSHA ? "redirect:/dailyreport/list" : null); }
    private String checkManagerOnly(HttpSession session) { User u = (User) session.getAttribute("loginUser"); return (u == null) ? "redirect:/login" : (u.getRoll() != ROLE_GENBA_MANAGER ? "redirect:/dailyreport/list" : null); }
    private String checkSiteAccess(HttpSession session, Integer reportSiteId) { User u = (User) session.getAttribute("loginUser"); if (u == null) return "redirect:/login"; if (u.getRoll() == ROLE_HONSHA) return null; Integer sId = (Integer) session.getAttribute("siteId"); return (sId == null || !sId.equals(reportSiteId)) ? "redirect:/dailyreport/list" : null; }

    /**
     * 一覧画面表示（変更なし）
     */
    @GetMapping("/list") 
    public String showList(
            @RequestParam(name = "targetDate", required = false) String targetDateStr,
            @RequestParam(name = "siteId", required = false) Integer siteId,
            @RequestParam(name = "dStatusFlag", required = false) Integer dStatusFlag,
            @RequestParam(name = "workDetails", required = false) String workDetails,
            HttpSession session, Model model) {
        String redirect = checkLogin(session); if (redirect != null) return redirect;
        User loginUser = (User) session.getAttribute("loginUser");
        Integer userSiteId = (Integer) session.getAttribute("siteId");
        model.addAttribute("siteList", siteService.selectAll());
        java.time.LocalDate targetDate = null;
        if (targetDateStr != null && !targetDateStr.isEmpty()) { targetDate = java.time.LocalDate.parse(targetDateStr); }
        if (loginUser.getRoll() != ROLE_HONSHA) { siteId = userSiteId; }
        model.addAttribute("workDetails", workDetails);
        List<Dailyreport> reportList = dailyreportService.searchReports(targetDate, siteId, dStatusFlag, workDetails);
        model.addAttribute("reportList", reportList);
        return "nishikigi/dailylist";
    }
    
    /**
     * 動作：日報新規登録画面を表示する
     */
    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        Integer userSiteId = (Integer) session.getAttribute("siteId");

        // 🌟 修正：EntityではなくFormを作成して初期値をセットする
        DailyreportForm form = new DailyreportForm();
        form.setTargetDate(java.time.LocalDate.now());
        if (userSiteId != null) {
            form.setSiteId(userSiteId);
        }
        
        model.addAttribute("dailyreport", form); // HTML側の th:object="${dailyreport}" を維持するため、名前はそのまま
        model.addAttribute("siteList", siteService.selectAll());
        return "nishikigi/dailyreport";
    }
    
    /**
     * 動作：日報登録の確認画面
     */
    @PostMapping("/new/confirm")
    public String confirmCreate(
            @Valid @ModelAttribute("dailyreport") DailyreportForm form,
            BindingResult bindingResult,
            HttpSession session, Model model) {
        
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        
        String wd = form.getWorkerDetails();
        if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        } else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        }
        
        form.setPhotoBeforeBase64(convertToBase64(form.getUploadPhotoBefore(), form.getPhotoBeforeBase64()));
        form.setPhotoDuringBase64(convertToBase64(form.getUploadPhotoDuring(), form.getPhotoDuringBase64()));
        form.setPhotoAfterBase64(convertToBase64(form.getUploadPhotoAfter(), form.getPhotoAfterBase64()));
        form.setPhotoInspectionBase64(convertToBase64(form.getUploadPhotoInspection(), form.getPhotoInspectionBase64()));
        form.setPhotoSafetyBase64(convertToBase64(form.getUploadPhotoSafety(), form.getPhotoSafetyBase64()));

        if (form.getPhotoSafetyBase64() == null || form.getPhotoSafetyBase64().isEmpty()) {
            bindingResult.rejectValue("photoSafetyBase64", "", "安全帯使用状況写真は必須です（ファイルを選択してください）");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("siteList", siteService.selectAll());
            return "nishikigi/dailyreport";
        }
        
        if (form.getSiteId() != null) {
            for (Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(form.getSiteId())) {
                    model.addAttribute("chosenSiteName", s.getSiteName());
                    break;
                }
            }
        }
        
        // 🌟【最終対策】新規登録側でも引き換え券（UUID）を発行してMapに逃がす
        String keyBefore = UUID.randomUUID().toString();
        String keyDuring = UUID.randomUUID().toString();
        String keyAfter = UUID.randomUUID().toString();
        String keyInspection = UUID.randomUUID().toString();
        String keySafety = UUID.randomUUID().toString();

        if (form.getPhotoBeforeBase64() != null) imageCache.put(keyBefore, form.getPhotoBeforeBase64());
        if (form.getPhotoDuringBase64() != null) imageCache.put(keyDuring, form.getPhotoDuringBase64());
        if (form.getPhotoAfterBase64() != null) imageCache.put(keyAfter, form.getPhotoAfterBase64());
        if (form.getPhotoInspectionBase64() != null) imageCache.put(keyInspection, form.getPhotoInspectionBase64());
        if (form.getPhotoSafetyBase64() != null) imageCache.put(keySafety, form.getPhotoSafetyBase64());

        session.setAttribute("create_key_Before", keyBefore);
        session.setAttribute("create_key_During", keyDuring);
        session.setAttribute("create_key_After", keyAfter);
        session.setAttribute("create_key_Inspection", keyInspection);
        session.setAttribute("create_key_Safety", keySafety);
        
        model.addAttribute("dailyreport", form);
        return "nishikigi/dailyreportcheck";
    }

    /**
     * 動作：日報登録の実行
     */
    @PostMapping("/create")
    public String registerReport(@ModelAttribute("dailyreport") DailyreportForm form, HttpSession session) {
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;

        // 🌟【最終対策】Mapから画像データを安全に回収
        String keyBefore = (String) session.getAttribute("create_key_Before");
        String keyDuring = (String) session.getAttribute("create_key_During");
        String keyAfter = (String) session.getAttribute("create_key_After");
        String keyInspection = (String) session.getAttribute("create_key_Inspection");
        String keySafety = (String) session.getAttribute("create_key_Safety");

        if (keyBefore != null) form.setPhotoBeforeBase64(imageCache.remove(keyBefore));
        if (keyDuring != null) form.setPhotoDuringBase64(imageCache.remove(keyDuring));
        if (keyAfter != null) form.setPhotoAfterBase64(imageCache.remove(keyAfter));
        if (keyInspection != null) form.setPhotoInspectionBase64(imageCache.remove(keyInspection));
        if (keySafety != null) form.setPhotoSafetyBase64(imageCache.remove(keySafety));

        session.removeAttribute("create_key_Before");
        session.removeAttribute("create_key_During");
        session.removeAttribute("create_key_After");
        session.removeAttribute("create_key_Inspection");
        session.removeAttribute("create_key_Safety");

        Dailyreport report = new Dailyreport();
        copyFormToEntity(form, report);

        dailyreportService.createReport(report);
        return "redirect:/complete";
    }
    
    /**
     * 動作：日報詳細画面（変更なし）
     */
    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") Integer reportId, @RequestParam(name = "from", required = false, defaultValue = "list") String from, HttpSession session, Model model) {
        String redirect = checkLogin(session); if (redirect != null) return redirect;
        Dailyreport report = dailyreportService.getReportById(reportId); if (report == null) return "error/404";
        String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId()); if (accessRedirect != null) return accessRedirect;
        
     // DBから取得した画像（byte[]）をBase64文字列に変換してModelへ格納
        model.addAttribute("photoBeforeBase64", encodeBase64(report.getPhotoBefore()));
        model.addAttribute("photoDuringBase64", encodeBase64(report.getPhotoDuring()));
        model.addAttribute("photoAfterBase64", encodeBase64(report.getPhotoAfter()));
        model.addAttribute("photoInspectionBase64", encodeBase64(report.getPhotoInspection()));
        model.addAttribute("photoSafetyBase64", encodeBase64(report.getPhotoSafety()));
        
        model.addAttribute("report", report); model.addAttribute("from", from);
        return "nishikigi/dailyreportdetail";
    }

    /**
     * 動作：日報編集画面を表示する（EntityからFormへの逆詰め替え）
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer reportId, HttpSession session, Model model) {
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        Dailyreport report = dailyreportService.getReportById(reportId); if (report == null) return "error/404";
        String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId()); if (accessRedirect != null) return accessRedirect;

        // 🌟 EntityのデータをFormへ詰め替えて画面に渡す
        DailyreportForm form = new DailyreportForm();
        form.setReportId(report.getReportId());
        form.setSiteId(report.getSite().getSiteId());
        form.setProjectNumber(report.getProjectNumber());
        form.setTargetDate(report.getTargetDate());
        form.setWeather(report.getWeather());
        form.setTemperature(report.getTemperature());
        form.setWorkDetails(report.getWorkDetails());
        form.setWorkerDetails(report.getWorkerDetails());
        form.setProgressPercent(report.getProgressPercent());
//        form.setPhotoBefore(report.getPhotoBefore());
//        form.setPhotoDuring(report.getPhotoDuring());
//        form.setPhotoAfter(report.getPhotoAfter());
//        form.setPhotoInspection(report.getPhotoInspection());
//        form.setPhotoSafety(report.getPhotoSafety());
        
        form.setPhotoBeforeBase64(encodeBase64(report.getPhotoBefore()));
        form.setPhotoDuringBase64(encodeBase64(report.getPhotoDuring()));
        form.setPhotoAfterBase64(encodeBase64(report.getPhotoAfter()));
        form.setPhotoInspectionBase64(encodeBase64(report.getPhotoInspection()));
        form.setPhotoSafetyBase64(encodeBase64(report.getPhotoSafety()));
        
        form.setSafetyKy(report.getSafetyKy());
        form.setSafetyMeasure(report.getSafetyMeasure());
        form.setSafetyEducation(report.getSafetyEducation());
        form.setNearMiss(report.getNearMiss());
        form.setMaterials(report.getMaterials());
        form.setEquipments(report.getEquipments());
        form.setDesignChange(report.getDesignChange());
        form.setCustomerRequest(report.getCustomerRequest());
        form.setPartnerCoordination(report.getPartnerCoordination());
        form.setNextDaySchedule(report.getNextDaySchedule());
        form.setNotes(report.getNotes());
        form.setDStatusFlag(report.getDStatusFlag());

        model.addAttribute("dailyreport", form);
        model.addAttribute("siteList", siteService.selectAll());
        return "nishikigi/dailyedit";
    }

    /**
     * 動作：日報編集の確認画面
     */
    @PostMapping("/{id}/edit/confirm")
    public String confirmUpdate(
            @Valid @ModelAttribute("dailyreport") DailyreportForm form,
            BindingResult bindingResult,
            HttpSession session, Model model) {
        
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        
        String wd = form.getWorkerDetails();
        if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        } else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        }
        
        form.setPhotoBeforeBase64(convertToBase64(form.getUploadPhotoBefore(), form.getPhotoBeforeBase64()));
        form.setPhotoDuringBase64(convertToBase64(form.getUploadPhotoDuring(), form.getPhotoDuringBase64()));
        form.setPhotoAfterBase64(convertToBase64(form.getUploadPhotoAfter(), form.getPhotoAfterBase64()));
        form.setPhotoInspectionBase64(convertToBase64(form.getUploadPhotoInspection(), form.getPhotoInspectionBase64()));
        form.setPhotoSafetyBase64(convertToBase64(form.getUploadPhotoSafety(), form.getPhotoSafetyBase64()));

        if (form.getPhotoSafetyBase64() == null || form.getPhotoSafetyBase64().isEmpty()) {
            bindingResult.rejectValue("photoSafetyBase64", "", "安全帯使用状況写真は必須です（ファイルを選択してください）");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("siteList", siteService.selectAll());
            return "nishikigi/dailyedit";
        }
        
        if (form.getSiteId() != null) {
            String accessRedirect = checkSiteAccess(session, form.getSiteId()); if (accessRedirect != null) return accessRedirect;
            for (Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(form.getSiteId())) {
                    model.addAttribute("chosenSiteName", s.getSiteName());
                    break;
                }
            }
        }
        
        // 🌟【最終対策】巨大な画像は専用のMapに逃がし、セッションにはただの「引き換え券キー（UUID）」を保存する
        String keyBefore = UUID.randomUUID().toString();
        String keyDuring = UUID.randomUUID().toString();
        String keyAfter = UUID.randomUUID().toString();
        String keyInspection = UUID.randomUUID().toString();
        String keySafety = UUID.randomUUID().toString();

        if (form.getPhotoBeforeBase64() != null) imageCache.put(keyBefore, form.getPhotoBeforeBase64());
        if (form.getPhotoDuringBase64() != null) imageCache.put(keyDuring, form.getPhotoDuringBase64());
        if (form.getPhotoAfterBase64() != null) imageCache.put(keyAfter, form.getPhotoAfterBase64());
        if (form.getPhotoInspectionBase64() != null) imageCache.put(keyInspection, form.getPhotoInspectionBase64());
        if (form.getPhotoSafetyBase64() != null) imageCache.put(keySafety, form.getPhotoSafetyBase64());

        session.setAttribute("edit_key_Before", keyBefore);
        session.setAttribute("edit_key_During", keyDuring);
        session.setAttribute("edit_key_After", keyAfter);
        session.setAttribute("edit_key_Inspection", keyInspection);
        session.setAttribute("edit_key_Safety", keySafety);
        
        model.addAttribute("dailyreport", form);
        return "nishikigi/dailyeditcheck";
    }

    /**
     * 動作：日報編集の実行（DB更新）
     */
    @PostMapping("/{id}/update")
    public String updateReport(@ModelAttribute("dailyreport") DailyreportForm form, HttpSession session) {
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        if (form.getSiteId() != null) { String accessRedirect = checkSiteAccess(session, form.getSiteId()); if (accessRedirect != null) return accessRedirect; }

        Dailyreport report = dailyreportService.getReportById(form.getReportId());
        
        // 🌟【最終対策】セッションから引き換え券を取り出し、Mapから本物の画像データを安全に回収
        String keyBefore = (String) session.getAttribute("edit_key_Before");
        String keyDuring = (String) session.getAttribute("edit_key_During");
        String keyAfter = (String) session.getAttribute("edit_key_After");
        String keyInspection = (String) session.getAttribute("edit_key_Inspection");
        String keySafety = (String) session.getAttribute("edit_key_Safety");

        if (keyBefore != null) form.setPhotoBeforeBase64(imageCache.remove(keyBefore));
        if (keyDuring != null) form.setPhotoDuringBase64(imageCache.remove(keyDuring));
        if (keyAfter != null) form.setPhotoAfterBase64(imageCache.remove(keyAfter));
        if (keyInspection != null) form.setPhotoInspectionBase64(imageCache.remove(keyInspection));
        if (keySafety != null) form.setPhotoSafetyBase64(imageCache.remove(keySafety));

        // 引き換え券セッションもお掃除
        session.removeAttribute("edit_key_Before");
        session.removeAttribute("edit_key_During");
        session.removeAttribute("edit_key_After");
        session.removeAttribute("edit_key_Inspection");
        session.removeAttribute("edit_key_Safety");

        copyFormToEntity(form, report);

        Dailyreport result = dailyreportService.updateReport(report);
        if (result == null) return "error/404";
        return "redirect:/complete";
    }

    /** 本社確認ボタン（変更なし） */
    @PostMapping("/{id}/confirm")
    public String confirmReport(@PathVariable("id") Integer reportId, @RequestParam(name = "from", required = false, defaultValue = "list") String from, HttpSession session) { String redirect = checkHonshaOnly(session); if (redirect != null) return redirect; boolean isSuccess = dailyreportService.confirmReport(reportId); if (!isSuccess) return "error/404"; return "home".equals(from) ? "redirect:/homeoffice" : "redirect:/dailyreport/list"; }

    /**
     * 🌟 共通ヘルパーメソッド：FormからEntityへデータを安全に詰め替える
     */
    private void copyFormToEntity(DailyreportForm form, Dailyreport report) {
        Site site = new Site();
        site.setSiteId(form.getSiteId());
        report.setSite(site);
        
        report.setReportId(form.getReportId());
        report.setProjectNumber(form.getProjectNumber());
        report.setTargetDate(form.getTargetDate());
        report.setWeather(form.getWeather());
        report.setTemperature(form.getTemperature());
        report.setWorkDetails(form.getWorkDetails());
        report.setWorkerDetails(form.getWorkerDetails());
        report.setProgressPercent(form.getProgressPercent());
//        report.setPhotoBefore(form.getPhotoBefore());
//        report.setPhotoDuring(form.getPhotoDuring());
//        report.setPhotoAfter(form.getPhotoAfter());
//        report.setPhotoInspection(form.getPhotoInspection());
//        report.setPhotoSafety(form.getPhotoSafety());
        
        report.setPhotoBefore(decodeBase64(form.getPhotoBeforeBase64()));
        report.setPhotoDuring(decodeBase64(form.getPhotoDuringBase64()));
        report.setPhotoAfter(decodeBase64(form.getPhotoAfterBase64()));
        report.setPhotoInspection(decodeBase64(form.getPhotoInspectionBase64()));
        report.setPhotoSafety(decodeBase64(form.getPhotoSafetyBase64()));
        
        report.setSafetyKy(form.getSafetyKy());
        report.setSafetyMeasure(form.getSafetyMeasure());
        report.setSafetyEducation(form.getSafetyEducation());
        report.setNearMiss(form.getNearMiss());
        report.setMaterials(form.getMaterials());
        report.setEquipments(form.getEquipments());
        report.setDesignChange(form.getDesignChange());
        report.setCustomerRequest(form.getCustomerRequest());
        report.setPartnerCoordination(form.getPartnerCoordination());
        report.setNextDaySchedule(form.getNextDaySchedule());
        report.setNotes(form.getNotes());
        report.setDStatusFlag(form.getDStatusFlag());
    }
    
 // 🌟 画像データとBase64文字列を相互に変換する便利メソッド
    private String convertToBase64(MultipartFile file, String existingBase64) {
        if (file != null && !file.isEmpty()) {
            try { return Base64.getEncoder().encodeToString(file.getBytes()); } 
            catch (Exception e) { e.printStackTrace(); }
        }
        return existingBase64; // 新しいファイルが無ければ既存を維持
    }
    private byte[] decodeBase64(String base64) {
        if (base64 != null && !base64.isEmpty()) { return Base64.getDecoder().decode(base64); }
        return null;
    }
    private String encodeBase64(byte[] bytes) {
        if (bytes != null && bytes.length > 0) { return Base64.getEncoder().encodeToString(bytes); }
        return null;
    }
}