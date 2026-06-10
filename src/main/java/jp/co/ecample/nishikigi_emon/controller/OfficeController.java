package jp.co.ecample.nishikigi_emon.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jp.co.ecample.nishikigi_emon.dto.SafetyList;
import jp.co.ecample.nishikigi_emon.dto.SiteView;
import jp.co.ecample.nishikigi_emon.entity.Chat;
import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Manager;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.repository.ChatRepository;
import jp.co.ecample.nishikigi_emon.repository.SiteRepository;
import jp.co.ecample.nishikigi_emon.service.SafetyService;
import jp.co.ecample.nishikigi_emon.service.TroubleService;

@Controller
public class OfficeController {
	private final SiteRepository siteRepository;
	private final TroubleService Tservice;
	private final SafetyService Sservice;

	public OfficeController(SiteRepository siteRepository, TroubleService Tservice, SafetyService Sservice) {
		this.siteRepository = siteRepository;
		this.Tservice = Tservice;
		this.Sservice = Sservice;
	}

	@Autowired
	private ChatRepository chatRepository;

	// 本社ホーム画面の表示
	@GetMapping("/homeoffice")
	public String homeoffice(Model model, HttpSession session) {
 
		User loginUser = (User) session.getAttribute("loginUser");
 
		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
 
		// 管理者以外は拒否
		if (loginUser.getRoll() != 0) {
			return "redirect:/login";
		}
		// fromThisSiteIdをリセット
		if((Integer)session.getAttribute("fromThisSiteId") != null) {
			session.removeAttribute("fromThisSiteId");
		}
 
		// 🌟【修正：現場データ0件の異常系ガード処理】
		// 現場データ取得を上部に移動。マスタが1件もない初期状態の場合は、
		// 後続の通知処理やマッピングでのクラッシュを完全に防ぐため、空のリストを詰めて安全に早期リターンさせます。
		List<Site> siteList = siteRepository.findAll();
		if (siteList == null || siteList.isEmpty()) {
			model.addAttribute("noticeList", new ArrayList<Trouble>());
			model.addAttribute("defectList", new ArrayList<Map<String, Object>>());
			model.addAttribute("siteViews", new ArrayList<SiteView>());
			return "nishikigi/list"; // 安全に空のホーム画面を表示
		}
 
		// 通知用データの取得
		List<Trouble> noticeList = Tservice.getActiveTroubles();
		List<SafetyList> allSafetyList = Sservice.selectAll();
		// 🌟【修正：データ不整合（孤立レコード）対策ガード】
		// .filter(dto -> dto.getSite() != null) を追加。
		// 親である現場（Site）が削除等で存在しない不整合データがあっても、ここで安全に除外してヌルポを絶対に回避します。
		List<Map<String, Object>> defectList = allSafetyList.stream()
			    .filter(dto -> "要対応".equals(dto.getJudgement()) && dto.getsStatusFlag() == 0) // 要対応のデータのみに絞り込む
			    .filter(dto -> dto.getSite() != null) // 親現場が存在するものだけに厳密に制限
			    .map(dto -> {
			        Map<String, Object> map = new HashMap<String, Object>();
			        map.put("siteName", dto.getSite().getSiteName()); // 現場名
			        map.put("createdAt", dto.getsCreatedAt());        // 登録日
			        map.put("siteId", dto.getSite().getSiteId());      // 現場ID
			        map.put("safetyId", dto.getSafetyId());
			        return map;
			    })
			    .toList();	
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("defectList", defectList);
 
		List<SiteView> siteViews = new ArrayList<>();
 
		LocalDate today = LocalDate.now();
 
		for (Site site : siteList) {
 
			// 本社除外
			if (site.getOfficecheck()) {
				continue;
			}
 
			int maxPriority = 0;
			int tStatusFlag = 2;
 
			for (Trouble trouble : site.getTroubleList()) {
 
				if (trouble.getPriority() > maxPriority && trouble.gettStatusFlag() != 2) {
					maxPriority = trouble.getPriority();
				}
				if (trouble.gettStatusFlag() < tStatusFlag) {
					tStatusFlag = trouble.gettStatusFlag();
				}
			}
 
			// ====================
			// 日報状態
			// ====================
 
			String dailyStatus = "未提出";
 
			Dailyreport todayReport = null;
 
			for (Dailyreport report : site.getDailyreportList()) {
 
				if (today.equals(report.getTargetDate())) {
 
					// 今日の日報を保存
					todayReport = report;
 
					dailyStatus = "未確認";
 
					if (report.getDStatusFlag() == 1) {
 
						dailyStatus = "確認済";
						break;
					}
				}
			}
 
			String safetyStatus = "未提出";
 
			Safety todaySafety = null;
 
			for (Safety safety : site.getSafetyList()) {
 
				// 今日の安全点検か
				if (today.equals(
						safety.getsCreatedAt().toLocalDate())) {
 
					// 保存
					todaySafety = safety;
 
					safetyStatus = "未確認";
 
					if (safety.getsStatusFlag() == 1) {
 
						safetyStatus = "確認済";
						break;
					}
				}
			}
			boolean mySite = false;
 
			for (Manager manager : site.getManagerList()) {
 
				if (manager.getUser().getUserid()
						.equals(loginUser.getUserid())) {
 
					mySite = true;
					break;
				}
			}
			SiteView view = new SiteView(
					site,
					maxPriority,
					tStatusFlag,
					dailyStatus,
					safetyStatus,
					mySite);
 
			view.setTodayReport(todayReport);
			view.setTodaySafety(todaySafety);
 
			siteViews.add(view);
		}
		// 🌟ソート処理はすべての現場ループが完了した後に1回だけ実行するようにブロック外へ配置（元のロジックの最適化）
		siteViews.sort((a, b) -> {
			// 自分の担当現場を先に
			if (a.isMySite() && !b.isMySite()) {
				return -1;
			}
			if (!a.isMySite() && b.isMySite()) {
				return 1;
			}
			return 0;
		});
		model.addAttribute("siteViews", siteViews);
 
		return "nishikigi/list";
	}
 
	// 現場ポータル
	@GetMapping("/portal/{id}")
	public String loginForm(
			@PathVariable("id") Integer siteId,
			HttpSession session,
			Model model) {
 
		User loginUser = (User) session.getAttribute("loginUser");
 
		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
 
		// 管理者以外は拒否
		if (loginUser.getRoll() != 0) {
			return "redirect:/login";
		}
		// siteIdを保存
		session.setAttribute("fromThisSiteId", siteId);
 
		Site site = siteRepository.findById(siteId)
				.orElseThrow(() -> new RuntimeException("現場が存在しません"));
 
		// 本社除外
		if (site.getOfficecheck()) {
			return "redirect:/home";
		}
 
		int maxPriority = 0;
		int tStatusFlag = 0;
 
		for (Trouble trouble : site.getTroubleList()) {
 
			if (trouble.getPriority() > maxPriority) {
				maxPriority = trouble.getPriority();
			}
		}
 
		LocalDate today = LocalDate.now();
 
		// ====================
		// 日報状態
		// ====================
 
		String dailyStatus = "未提出";
 
		Dailyreport todayReport = null;
 
		for (Dailyreport report : site.getDailyreportList()) {
 
			if (today.equals(report.getTargetDate())) {
 
				todayReport = report;
 
				dailyStatus = "未確認";
 
				if (report.getDStatusFlag() == 1) {
 
					dailyStatus = "確認済";
					break;
				}
			}
		}
 
		// ====================
		// 安全点検
		// ====================
 
		String safetyStatus = "未提出";
 
		Safety todaySafety = null;
 
		for (Safety safety : site.getSafetyList()) {
 
			if (today.equals(
					safety.getsCreatedAt().toLocalDate())) {
 
				todaySafety = safety;
 
				safetyStatus = "未確認";
 
				if (safety.getsStatusFlag() == 1) {
 
					safetyStatus = "確認済";
					break;
				}
			}
		}
 
		boolean mySite = false;
 
		for (Manager manager : site.getManagerList()) {
 
			if (manager.getUser() != null &&
					manager.getUser().getUserid()
							.equals(loginUser.getUserid())) {
 
				mySite = true;
				break;
			}
		}
 
		SiteView view = new SiteView(
				site,
				maxPriority,
				tStatusFlag,
				dailyStatus,
				safetyStatus,
				mySite);
 
		view.setTodayReport(todayReport);
		view.setTodaySafety(todaySafety);
 
		model.addAttribute("view", view);
 
		List<Chat> chatList = chatRepository
				.findBySiteSiteIdOrderByDateTimeAsc(
						view.getSite().getSiteId());
 
		model.addAttribute("chatList", chatList);
 
		model.addAttribute(
				"loginSiteId",
				view.getSite().getSiteId());
		model.addAttribute("siteId", siteId);
 
		return "nishikigi/portal";
	}
}