package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.form.TroubleForm;
import jp.co.ecample.nishikigi_emon.service.TroubleService;

@Controller
public class TroubleController {

	private final TroubleService service;

	public TroubleController(TroubleService service) {
		this.service = service;
	}

	// トラブル登録画面表示
	@GetMapping("/trouble/new")
	public String input(@ModelAttribute TroubleForm form) {
		return "nishikigi/trouble";
	}

	// トラブル登録確認画面
	@PostMapping("/trouble/new/confirm")
	public String confirm(@ModelAttribute TroubleForm form, Model model) {
		model.addAttribute("form", form);
		return "nishikigi/troublecheck";
	}

	// トラブル登録画面のフォーム送信
	@PostMapping("/troubles")
	public String register(@ModelAttribute TroubleForm form, HttpSession session) {

		Integer siteId = (Integer) session.getAttribute("siteId");

		System.out.println("siteId = " + siteId);

		Trouble trouble = new Trouble();

		trouble.setPriority(form.getPriority());
		trouble.setTroubleType(form.getTroubleType());
		trouble.setOverview(form.getOverview());
		trouble.setDetail(form.getDetail());

		Site site = new Site();
		site.setSiteId(siteId);

		trouble.setSite(site);

		service.register(trouble);

		return "redirect:/complete";
	}

	// トラブル一覧表示
	@GetMapping("/trouble/list")
	public String list(Model model) {
		List<Trouble> troubleList = service.selectAll();
		model.addAttribute("troubleList", troubleList);
		return "nishikigi/troublelist";
	}
	
	

}
