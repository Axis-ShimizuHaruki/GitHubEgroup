package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // ★追加

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // ★追加
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.DailyreportForm; // ★追加
import jp.co.ecample.nishikigi_emon.service.DailyreportService;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
@RequestMapping("/dailyreport")
public class DailyreportController {

    private static final int ROLE_HONSHA = 0;
    private static final int ROLE_GENBA_MANAGER = 1;
    private static final int ROLE_GENBA_VIEWER = 2;

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
     * 動作：日報登録の確認画面（バリデーション追加）
     */
    @PostMapping("/new/confirm")
    public String confirmCreate(
            @Valid @ModelAttribute("dailyreport") DailyreportForm form, // 🌟 @Valid を追加
            BindingResult bindingResult,                                // 🌟 追加
            HttpSession session, Model model) {
        
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;
        
     // 出面情報（JSON文字列）の高度な手動バリデーション
        String wd = form.getWorkerDetails();
        if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
            // 何も入力がない、またはすべて消された場合
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        } else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
            // どれか1つでも空欄（""）が含まれている不備がある場合
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        }
        
        // 🌟 入力エラーがあれば登録画面に戻す
        if (bindingResult.hasErrors()) {
            model.addAttribute("siteList", siteService.selectAll());
            return "nishikigi/dailyreport";
        }
        
        // 画面に選択した現場名を表示するためのドッキング処理
        if (form.getSiteId() != null) {
            for (Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(form.getSiteId())) {
                    model.addAttribute("chosenSiteName", s.getSiteName()); // 現場名をModelに乗せる
                    break;
                }
            }
        }
        
        model.addAttribute("dailyreport", form);
        return "nishikigi/dailyreportcheck";
    }

    /**
     * 動作：日報登録の実行（FormからEntityへの詰め替え処理）
     */
    @PostMapping("/create")
    public String registerReport(@ModelAttribute("dailyreport") DailyreportForm form, HttpSession session) {
        String redirect = checkManagerOnly(session); if (redirect != null) return redirect;

        // 🌟 FormクラスのデータをEntityへ詰め替える
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
        form.setPhotoBefore(report.getPhotoBefore());
        form.setPhotoDuring(report.getPhotoDuring());
        form.setPhotoAfter(report.getPhotoAfter());
        form.setPhotoInspection(report.getPhotoInspection());
        form.setPhotoSafety(report.getPhotoSafety());
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
        
     // 編集時も全く同じ手動バリデーションをかけます
        String wd = form.getWorkerDetails();
        if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
        } else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
            bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
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
        report.setPhotoBefore(form.getPhotoBefore());
        report.setPhotoDuring(form.getPhotoDuring());
        report.setPhotoAfter(form.getPhotoAfter());
        report.setPhotoInspection(form.getPhotoInspection());
        report.setPhotoSafety(form.getPhotoSafety());
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
}