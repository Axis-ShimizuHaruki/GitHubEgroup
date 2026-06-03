package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.service.DailyreportService;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
@RequestMapping("/dailyreport")
public class DailyreportController {

    /** 権限定数の定義 */
    private static final int ROLE_HONSHA = 0;          // 本社管理者
    private static final int ROLE_GENBA_MANAGER = 1;   // 現場管理者
    private static final int ROLE_GENBA_VIEWER = 2;    // 現場閲覧者

    @Autowired
    private DailyreportService dailyreportService;

    @Autowired
    private SiteService siteService;

    // =========================================================
    // 🌟 Empmanagerをリスペクトした共通権限チェックメソッド
    // =========================================================

    /** 1. ログインチェック（全員共通） */
    private String checkLogin(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }
        return null;
    }

    /** 2. 本社ユーザー(0)専用チェック（確認完了処理用） */
    private String checkHonshaOnly(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getRoll() != ROLE_HONSHA) {
            return "redirect:/dailyreport/list"; // 本社以外なら一覧へ突っ返す
        }
        return null;
    }

    /** 3. 現場管理者(1)専用チェック（新規登録・編集用） */
    private String checkManagerOnly(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getRoll() != ROLE_GENBA_MANAGER) {
            return "redirect:/dailyreport/list"; // 現場管理者(1)以外なら一覧へ突っ返す
        }
        return null;
    }

    /** 4. 現場ユーザー(1,2)の「自現場限定」アクセスチェック（URL直打ち覗き見・不正編集対策） */
    private String checkSiteAccess(HttpSession session, Integer reportSiteId) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 本社(0)なら、どの現場の日報でもアクセスOK
        if (loginUser.getRoll() == ROLE_HONSHA) {
            return null;
        }

        // 現場ユーザー(1,2)の場合、日報の現場IDが、自分の担当現場ID(siteId)と違っていれば一覧へ強制送還
        Integer userSiteId = (Integer) session.getAttribute("siteId");
        if (userSiteId == null || !userSiteId.equals(reportSiteId)) {
            return "redirect:/dailyreport/list";
        }
        return null;
    }

    // =========================================================
    // 各画面・処理のマッピング
    // =========================================================

    /**
     * 動作：日報一覧画面を表示する（検索・絞り込み対応版）
     */
    @GetMapping("/list") 
    public String showList(
            @RequestParam(name = "targetDate", required = false) String targetDateStr,
            @RequestParam(name = "siteId", required = false) Integer siteId,
            @RequestParam(name = "dStatusFlag", required = false) Integer dStatusFlag,
            @RequestParam(name = "workDetails", required = false) String workDetails, // 🌟 追記：画面の入力欄からキーワードを受け取る
            HttpSession session, Model model) {
        
        // 🛡️ ログインチェック
        String redirect = checkLogin(session);
        if (redirect != null) return redirect;
        
        User loginUser = (User) session.getAttribute("loginUser");
        Integer userSiteId = (Integer) session.getAttribute("siteId");

        model.addAttribute("siteList", siteService.selectAll());

        java.time.LocalDate targetDate = null;
        if (targetDateStr != null && !targetDateStr.isEmpty()) {
            targetDate = java.time.LocalDate.parse(targetDateStr);
        }

        // 🛡️ 現場ユーザー(1,2)の場合は、画面からの検索を無視して「自現場」で強制固定
        if (loginUser.getRoll() != ROLE_HONSHA) {
            siteId = userSiteId;
        }

        // 🌟 追記：入力されたキーワードをModelに乗せて画面に戻す（入力状態をキープするため）
        model.addAttribute("workDetails", workDetails);

        // 🌟 修正：引数の末尾に「workDetails」を追加してサービスへ投げる
        List<Dailyreport> reportList = dailyreportService.searchReports(targetDate, siteId, dStatusFlag, workDetails);
        model.addAttribute("reportList", reportList);
        return "nishikigi/dailylist";
    }
    
    /**
     * 動作：日報新規登録画面を表示する（現場管理者1のみ）
     */
    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;

        Integer userSiteId = (Integer) session.getAttribute("siteId");
        Dailyreport newReport = new Dailyreport();
        newReport.setTargetDate(java.time.LocalDate.now());
        
        if (userSiteId != null) {
            jp.co.ecample.nishikigi_emon.entity.Site currentSite = new jp.co.ecample.nishikigi_emon.entity.Site();
            currentSite.setSiteId(userSiteId);
            newReport.setSite(currentSite);
        }
        
        model.addAttribute("dailyreport", newReport);
        model.addAttribute("siteList", siteService.selectAll());
        return "nishikigi/dailyreport";
    }
    
    /**
     * 動作：日報登録の確認画面（現場管理者1のみ）
     */
    @PostMapping("/new/confirm")
    public String confirmCreate(@ModelAttribute("dailyreport") Dailyreport report, HttpSession session, Model model) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;
        
        if (report.getSite() != null && report.getSite().getSiteId() != null) {
            for (Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(report.getSite().getSiteId())) {
                    report.setSite(s); 
                    break;
                }
            }
        }
        
        model.addAttribute("dailyreport", report);
        return "nishikigi/dailyreportcheck";
    }

    /**
     * 動作：日報登録の実行（現場管理者1のみ）
     */
    @PostMapping("/create")
    public String registerReport(@ModelAttribute("dailyreport") Dailyreport report, HttpSession session) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;

        dailyreportService.createReport(report);
        return "redirect:/complete";
    }
    
    /**
     * 動作：日報詳細画面を表示する（全権限OK、ただし1,2は自現場のみ）
     */
    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") Integer reportId,
                            @RequestParam(name = "from", required = false, defaultValue = "list") String from,
                            HttpSession session, Model model) {
        
        // 🛡️ ログインチェック
        String redirect = checkLogin(session);
        if (redirect != null) return redirect;

        Dailyreport report = dailyreportService.getReportById(reportId);
        if (report == null) {
            return "error/404";
        }

        // 🛡️ 自現場チェック（現場ユーザーによるURL直打ち覗き見をガード）
        String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
        if (accessRedirect != null) return accessRedirect;

        model.addAttribute("report", report);
        model.addAttribute("from", from);
        return "nishikigi/dailyreportdetail";
    }

    /**
     * 動作：日報編集画面を表示する（現場管理者1のみ ＆ 自現場のみ）
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer reportId, HttpSession session, Model model) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;

        Dailyreport report = dailyreportService.getReportById(reportId);
        if (report == null) {
            return "error/404";
        }

        // 🛡️ 自現場チェック（他人の現場の日報をURL直打ちで編集しようとしたら弾く）
        String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
        if (accessRedirect != null) return accessRedirect;

        model.addAttribute("dailyreport", report);
        model.addAttribute("siteList", siteService.selectAll());
        return "nishikigi/dailyedit";
    }

    /**
     * 動作：日報編集の確認画面（現場管理者1のみ ＆ 自現場のみ）
     */
    @PostMapping("/{id}/edit/confirm")
    public String confirmUpdate(@ModelAttribute("dailyreport") Dailyreport report, HttpSession session, Model model) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;
        
        // 🛡️ フォームから送信された現場IDに対する不正アクセスチェック
        if (report.getSite() != null && report.getSite().getSiteId() != null) {
            String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
            if (accessRedirect != null) return accessRedirect;
            
            for (Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(report.getSite().getSiteId())) {
                    report.setSite(s);
                    break;
                }
            }
        }
        
        model.addAttribute("dailyreport", report);
        return "nishikigi/dailyeditcheck";
    }

    /**
     * 動作：日報編集の実行（現場管理者1のみ ＆ 自現場のみ）
     */
    @PostMapping("/{id}/update")
    public String updateReport(@ModelAttribute("dailyreport") Dailyreport report, HttpSession session) {
        
        // 🛡️ 現場管理者(1)チェック
        String redirect = checkManagerOnly(session);
        if (redirect != null) return redirect;

        // 🛡️ 保存を実行する前に、その現場IDへの書き込み権限があるか最終チェック
        if (report.getSite() != null && report.getSite().getSiteId() != null) {
            String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
            if (accessRedirect != null) return accessRedirect;
        }

        Dailyreport result = dailyreportService.updateReport(report);
        if (result == null) {
            return "error/404";
        }
        return "redirect:/complete";
    }

    /**
     * 動作：本社管理者が日報を「確認完了」にする（本社0のみ）
     */
    @PostMapping("/{id}/confirm")
    public String confirmReport(@PathVariable("id") Integer reportId,
                                @RequestParam(name = "from", required = false, defaultValue = "list") String from,
                                HttpSession session) {
        
        // 🛡️ 本社ユーザー(0)チェック
        String redirect = checkHonshaOnly(session);
        if (redirect != null) return redirect;

        boolean isSuccess = dailyreportService.confirmReport(reportId);
        if (!isSuccess) {
            return "error/404";
        }
        
        if ("home".equals(from)) {
            return "redirect:/homeoffice";
        } else {
            return "redirect:/dailyreport/list";
        }
    }
}