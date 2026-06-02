package jp.co.ecample.nishikigi_emon.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ecample.nishikigi_emon.dto.SiteView;
import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Manager;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.repository.ManagerRepository;
import jp.co.ecample.nishikigi_emon.repository.SiteRepository;
import jp.co.ecample.nishikigi_emon.service.SiteService;
import jp.co.ecample.nishikigi_emon.service.TroubleService;
import jp.co.ecample.nishikigi_emon.service.UserService;

@Controller
public class LoginController {
	private final UserService Uservice;
	private final ManagerRepository managerRepository;
	private final SiteRepository siteRepository;

	public LoginController(UserService Uservice,
			SiteService Sservice,
			TroubleService Tservice,
			ManagerRepository managerRepository,
			SiteRepository siteRepository) {
		this.Uservice = Uservice;
		this.managerRepository = managerRepository;
		this.siteRepository = siteRepository;
	}

	// ログイン画面の表示
	@GetMapping("/login")
	public String loginForm() {
		return "nishikigi/login";
	}

	// IDとパスワードを取得、DBに存在すればsessionに情報を保存しリストへ、存在しなければloginへredirect
	@PostMapping("/login")
	public String logintoForm(@RequestParam int userid, @RequestParam String password, Model model,
			HttpSession session) {

		Optional<User> result = Uservice.login(userid, password);

		if (result.isPresent()) {
			User user = result.get(); // 値を取り出す
			session.setAttribute("loginUser", user);

			// Manager取得
			Optional<Manager> managerOpt = managerRepository.findByUser_Userid(user.getUserid());

			// 現場IDをsession保存
			if (managerOpt.isPresent()) {
				Manager manager = managerOpt.get();

				Integer siteId = manager.getSite().getSiteId();

				session.setAttribute("siteId", siteId);
			}

			// 権限により遷移先ページを変更
			if (user.getRoll() == 0) {
				return "redirect:/homeoffice";
			} else {
				return "redirect:/homesite";
			}

		} else {
			// 該当ユーザーが存在しなかった場合の処理
			model.addAttribute("error", "ユーザーが見つかりません");
			return "redirect:login";
		}

	}

	// ログアウト、sessionを空にしlogin画面へ
	@PostMapping("/logout")
	public String logoutForm(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	// 本社ホーム画面の表示
	@GetMapping("/homeoffice")
	public String homeoffice(Model model, HttpSession session) {

		// ログインチャック
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login";
		}
		
		User loginUser = (User) session.getAttribute("loginUser");
		
		List<Site> siteList = siteRepository.findAll();

		List<SiteView> siteViews = new ArrayList<>();

		LocalDate today = LocalDate.now();

		for (Site site : siteList) {

			// 本社除外
			if (site.getOfficecheck()) {
				continue;
			}

			int maxPriority = 0;

			for (Trouble trouble : site.getTroubleList()) {

				if (trouble.getPriority() > maxPriority) {
					maxPriority = trouble.getPriority();
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

			        if ("1".equals(safety.getsStatusFlag())) {

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
			        dailyStatus,
			        safetyStatus,
			        mySite);

			view.setTodayReport(todayReport);
			view.setTodaySafety(todaySafety);

			siteViews.add(view);
			
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
		}
		

		model.addAttribute("siteViews", siteViews);

		return "nishikigi/list";
	}

	//完了画面の表示
	@GetMapping("/complete")
	public String homesite(HttpSession session) {

		// ログインチャック
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/login";
		}
		return "nishikigi/complete";
	}

}
