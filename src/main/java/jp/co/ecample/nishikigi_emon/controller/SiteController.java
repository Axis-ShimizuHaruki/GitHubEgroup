package jp.co.ecample.nishikigi_emon.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ecample.nishikigi_emon.entity.Chat;
import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.repository.ChatRepository;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
public class SiteController {
	private final SiteService service;

	public SiteController(SiteService service) {
		this.service = service;
	}

	@Autowired
	private ChatRepository chatRepository;

	@GetMapping("/homesite")
	public String showNewHome(
			HttpSession session,
			Model model) {

		User loginUser = (User) session.getAttribute("loginUser");

		// ログインチャック
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login";
		}
		if (loginUser.getRoll() == 0) {
			return "redirect:/login";
		}

		Integer siteId = (Integer) session.getAttribute("siteId");

		Site site = service.findById(siteId);

		List<Chat> chatList = chatRepository
				.findBySiteSiteIdOrderByDateTimeAsc(
						siteId);

		model.addAttribute("site", site);
		model.addAttribute("chatList", chatList);
		model.addAttribute("loginSiteId", siteId);

		// 左下ステータス表示用
		LocalDate today = LocalDate.now();

		// ====================
		// 日報
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

		model.addAttribute("dailyStatus", dailyStatus);

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

		model.addAttribute("safetyStatus", safetyStatus);

		return "nishikigi/home";
	}

}
