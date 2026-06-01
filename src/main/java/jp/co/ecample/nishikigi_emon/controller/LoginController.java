package jp.co.ecample.nishikigi_emon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	

	// ログイン画面の表示
		@GetMapping("/login")
		public String loginForm() {
			return "nishikigi/login";
			
		}
}
