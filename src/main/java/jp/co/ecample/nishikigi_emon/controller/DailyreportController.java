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
import jp.co.ecample.nishikigi_emon.service.SiteService; // ★エラー解消のためにインポートを追加

@Controller
@RequestMapping("/dailyreport")
public class DailyreportController {

    @Autowired
    private DailyreportService dailyreportService;

    @Autowired
    private SiteService siteService; // ★エラー解消のためにSiteServiceを定義（追加）

    /**
     * 動作：日報一覧画面を表示する（検索・絞り込み対応版）
     * URL：GET /dailyreport/list
     */
    @GetMapping("/list") 
    public String showList(
            @RequestParam(name = "targetDate", required = false) String targetDateStr,
            @RequestParam(name = "siteId", required = false) Integer siteId,
            @RequestParam(name = "dStatusFlag", required = false) Integer dStatusFlag,
            HttpSession session, Model model) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        
        Integer userSiteId = (Integer) session.getAttribute("siteId");

        // 検索プルダウン用に、現場マスタ一覧（siteList）をModelに乗せる
        model.addAttribute("siteList", siteService.selectAll());

        // 画面から送られてきた日付文字列(String)を LocalDate 型に変換する
        java.time.LocalDate targetDate = null;
        if (targetDateStr != null && !targetDateStr.isEmpty()) {
            targetDate = java.time.LocalDate.parse(targetDateStr);
        }

        // 権限に応じた現場IDのコントロール
        if (loginUser.getRoll() != 0) {
            // 現場所長・一般ユーザーの場合は、画面からの現場検索を無視して「自分の現場ID」で強制固定
            siteId = userSiteId;
        }
        
        

        // 検索処理の実行
        List<Dailyreport> reportList = dailyreportService.searchReports(targetDate, siteId, dStatusFlag);

        model.addAttribute("reportList", reportList);
        return "nishikigi/dailylist";
    }
    
    /**
     * 動作：日報新規登録画面を表示する
     * URL：GET /dailyreport/new
     */
    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        Integer userSiteId = (Integer) session.getAttribute("siteId");

        if (loginUser == null) return "redirect:/login";

        Dailyreport newReport = new Dailyreport();
        
        // 日付の初期値に「今日(LocalDate.now())」をセット
        newReport.setTargetDate(java.time.LocalDate.now());
        
        //  現場ユーザー(roll != 0)なら、初期現場に「自分の現場ID」をセット
        if (loginUser.getRoll() != 0 && userSiteId != null) {
            jp.co.ecample.nishikigi_emon.entity.Site currentSite = new jp.co.ecample.nishikigi_emon.entity.Site();
            currentSite.setSiteId(userSiteId);
            newReport.setSite(currentSite);
        }
        
        model.addAttribute("dailyreport", newReport);

        // プルダウン表示用に現場マスタ一覧を送る
        List<Site> siteList = siteService.selectAll(); 
        model.addAttribute("siteList", siteList);
        
        return "nishikigi/dailyreport";
    }
    
    
    /**
     * 動作：日報登録画面からの入力を受け取り、登録確認画面を表示する
     * URL：POST /dailyreport/new/confirm
     */
    @PostMapping("/new/confirm")
    public String confirmCreate(@ModelAttribute("dailyreport") Dailyreport report, Model model) {
        
        // 登録画面から届いた現場IDを元に、完全な現場情報（現場名含む）をドッキングする
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
     * 動作：日報登録確認画面から確定され、データをDBに保存後、完了画面へリダイレクトする
     * URL：POST /nishikigi/dailyreport/create
     * 遷移：redirect:/nishikigi/complete
     */
    @PostMapping("/create")
    public String registerReport(@ModelAttribute("dailyreport") Dailyreport report) {
        dailyreportService.createReport(report);
        return "redirect:/complete";
    }
    
    /**
     * 動作：日報詳細画面を表示する
     * URL：GET /nishikigi/dailyreport/{id}
     * 画面：nishikigi/dailyreportdetail.html
     */
    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") Integer reportId,
            // 目印を受け取る（届かなかった場合は安全のため list にする）
            @RequestParam(name = "from", required = false, defaultValue = "list") String from,
            HttpSession session, Model model) {
        Dailyreport report = dailyreportService.getReportById(reportId);
        if (report == null) {
            return "error/404";
        }
        model.addAttribute("report", report);
        model.addAttribute("from", from);
        return "nishikigi/dailyreportdetail";
    }


    /**
     * 動作：日報編集画面を表示する（既存データの読み込み）
     * URL：GET /dailyreport/{id}/edit
     * 画面：nishikigi/dailyedit.html
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer reportId, Model model) {
        Dailyreport report = dailyreportService.getReportById(reportId);
        if (report == null) {
            return "error/404";
        }
        model.addAttribute("dailyreport", report);
        
        // 編集画面のプルダウン復元用に、現場リストをModelに詰める
        model.addAttribute("siteList", siteService.selectAll());
        
        return "nishikigi/dailyedit";
    }

    /**
     * 動作：日報編集画面からの修正入力を受け取り、編集確認画面を表示する
     * URL：POST /nishikigi/dailyreport/{id}/edit/confirm
     * 画面：nishikigi/dailyeditcheck.html
     */
    @PostMapping("/{id}/edit/confirm")
    public String confirmUpdate(@ModelAttribute("dailyreport") Dailyreport report, Model model) {
        
        // 画面から届いた現場IDを元に、現場名を含んだ完全な現場情報をドッキングする
        if (report.getSite() != null && report.getSite().getSiteId() != null) {
            // すでに実績のある selectAll() から対象の現場をループで安全に特定します
            for (jp.co.ecample.nishikigi_emon.entity.Site s : siteService.selectAll()) {
                if (s.getSiteId().equals(report.getSite().getSiteId())) {
                    report.setSite(s); // 現場名が入った完全なオブジェクトをセット
                    break;
                }
            }
        }
        
        model.addAttribute("dailyreport", report);
        return "nishikigi/dailyeditcheck";
    }

    /**
     * 動作：日報編集確認画面から確定され、データをDBに更新後、完了画面へリダイレクトする
     * URL：POST /nishikigi/dailyreport/{id}/update
     * 遷移：redirect:/nishikigi/complete
     */
    @PostMapping("/{id}/update")
    public String updateReport(@ModelAttribute("dailyreport") Dailyreport report) {
        Dailyreport result = dailyreportService.updateReport(report);
        if (result == null) {
            return "error/404";
        }
        return "redirect:/complete";
    }

   
    /**
     * 動作：本社管理者が「確認ボタン」を押下した際、ステータスを更新して一覧画面へ戻る（リロード）
     * URL：POST /nishikigi/dailyreport/{id}/confirm
     * 遷移：redirect:/nishikigi/dailyreport/list
     */
    @PostMapping("/{id}/confirm")
    public String confirmReport(@PathVariable("id") Integer reportId,
    							@RequestParam(name = "from", required = false, defaultValue = "list") String from) {
        boolean isSuccess = dailyreportService.confirmReport(reportId);
        if (!isSuccess) {
            return "error/404";
        }
        // ★ 詳細に戻るのではなく、一覧画面（/dailyreport/list）に引き戻してあげる
        if ("home".equals(from)) {
            return "redirect:/homeoffice";
        } else {
        return "redirect:/dailyreport/list";
        }
    }
}


