package jp.co.ecample.nishikigi_emon.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ecample.nishikigi_emon.entity.Manager;
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


	public LoginController(UserService Uservice,
			SiteService Sservice,
			TroubleService Tservice,
			ManagerRepository managerRepository,
			SiteRepository siteRepository) {
		this.Uservice = Uservice;
		this.managerRepository = managerRepository;
	}

	// ログイン画面の表示
	@GetMapping("/login")
	public String loginForm() {
		return "nishikigi/login";
	}

	// IDとパスワードを取得、DBに存在すればsessionに情報を保存しリストへ、存在しなければloginへredirect
	@PostMapping("/login")
	public String logintoForm(@RequestParam int userid, @RequestParam String password, Model model,
			HttpSession session,RedirectAttributes redirectAttributes) {

		Optional<User> result = Uservice.login(userid, password);

		if (result.isPresent()) {
			User user = result.get(); // 値を取り出す
			session.setAttribute("loginUser", user);

			if (userid == 1) {
				session.setAttribute("siteId", 1);
			} else {
				// Manager取得
				Optional<Manager> managerOpt = managerRepository.findByUser_Userid(user.getUserid());

				// 現場IDをsession保存
				if (managerOpt.isPresent()) {
					Manager manager = managerOpt.get();

					Integer siteId = manager.getSite().getSiteId();

					session.setAttribute("siteId", siteId);
				}
			}

			// 権限により遷移先ページを変更
			if (user.getRoll() == 0) {
				return "redirect:/homeoffice";
			} else {
				return "redirect:/homesite";
			}

		} else {
			// 該当ユーザーが存在しなかった場合の処理
			redirectAttributes.addFlashAttribute(
					"loginError",
					true);

			return "redirect:/login";
		}

	}

	// ログアウト、sessionを空にしlogin画面へ
	@PostMapping("/logout")
	public String logoutForm(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	

	//完了画面の表示
	@GetMapping("/complete")
	public String homesite(HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 管理者以外は拒否
		if (loginUser.getRoll() != 0) {
			return "redirect:/login";
		}
		
		return "nishikigi/complete";
	}

}
