package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ecample.nishikigi_emon.entity.Manager;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.repository.ManagerRepository;
import jp.co.ecample.nishikigi_emon.service.SiteService;
import jp.co.ecample.nishikigi_emon.service.TroubleService;
import jp.co.ecample.nishikigi_emon.service.UserService;


@Controller
public class LoginController {
	private final UserService Uservice;
	private final SiteService Sservice;
	private final TroubleService Tservice;
	private final ManagerRepository managerRepository;

	public LoginController(UserService Uservice,
			SiteService Sservice,
			TroubleService Tservice,
			ManagerRepository managerRepository) {
		this.Uservice = Uservice;
		this.Sservice = Sservice;
		this.Tservice = Tservice;
		this.managerRepository = managerRepository;
	}

	// ログイン画面の表示
		@GetMapping("/login")
		public String loginForm() {
			return "nishikigi/login";
		}
		
		
	// IDとパスワードを取得、DBに存在すればsessionに情報を保存しリストへ、存在しなければloginへredirect
		@PostMapping("/login")
		public String logintoForm(@RequestParam int userid, @RequestParam String password, Model model, HttpSession session) {
			Optional<User> result = Uservice.login(userid, password);

			if (result.isPresent()) {
				User user = result.get(); // 値を取り出す
				session.setAttribute("loginUser", user);
				
				// Manager取得
				Optional<Manager> managerOpt =
						managerRepository.findByUser_Userid(user.getUserid());

				// 現場IDをsession保存
				if(managerOpt.isPresent()) {
					Manager manager = managerOpt.get();

					Integer siteId = manager.getSite().getSiteId();

					session.setAttribute("siteId", siteId);
				}
				
				// 権限により遷移先ページを変更
				if(user.getRoll() == 0) {
					return "redirect:/homeoffice";
				}else {
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
		public String homeoffice(Model model) {
			
			// 全件取得
			List<Site> site = Sservice.selectAll();
			model.addAttribute("site", site);
			
			List<Trouble> trouble = Tservice.selectAll();
			model.addAttribute("trouble", trouble);
			
			return "nishikigi/list";
		}
		
		
		// 現場ホーム画面の表示
//		@GetMapping("/homesite")
//		public String homesite() {
//			return "nishikigi/home";
//		}
		
}
