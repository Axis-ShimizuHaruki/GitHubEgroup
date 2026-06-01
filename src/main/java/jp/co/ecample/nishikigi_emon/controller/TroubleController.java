package jp.co.ecample.nishikigi_emon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ecample.nishikigi_emon.service.TroubleService;

@Controller
public class TroubleController {

	private final TroubleService service;

	public TroubleController(TroubleService service) {
		this.service = service;
	}

	// トラブル登録画面表示
	@GetMapping("/trouble/new")
	public String TroubleRegist() {
		return "nishikigi/trouble";
	}

	// トラブル登録画面のフォーム送信
	@PostMapping("/troubles")
	public String postMethodName() {
		return "redirect:";
	}

}
