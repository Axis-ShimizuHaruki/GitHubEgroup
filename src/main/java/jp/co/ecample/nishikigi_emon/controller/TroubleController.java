package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.TroubleForm;
import jp.co.ecample.nishikigi_emon.form.TroubleSearchForm;
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
	public String list(@ModelAttribute TroubleSearchForm form, Model model) {
		List<Trouble> troubleList = service.search(
				form.getOccurredDate(),
				form.getSiteName(),
				form.getPriority(),
				form.getTroubleType(),
				form.getStatusFlag());
		model.addAttribute("troubleList", troubleList);
		model.addAttribute("troubleSearchForm", form);
		return "nishikigi/troublelist";
	}

	// トラブル詳細表示
	@GetMapping("/trouble/{id}")
	public String detail(@PathVariable Integer id, Model model, HttpSession session) {
		Trouble trouble = service.findById(id);

		if (trouble == null) {
			return "redirect:/trouble/list";
		}

		User loginUser = (User) session.getAttribute("loginUser");

		model.addAttribute("trouble", trouble);
		model.addAttribute("role", loginUser.getRoll());

		return "nishikigi/troubledetail";
	}

	// トラブル対応状況を一段進める
	@PostMapping("/trouble/status/{id}")
	public String updateStatus(@PathVariable Integer id) {

		service.updateStatus(id);

		return "redirect:/trouble/" + id;
	}

	// トラブル対応状況を一段戻す
	//	@PostMapping("/trouble/status/back/{id}")
	//	public String backStatus(@PathVariable Integer id) {
	//
	//		service.backStatus(id);
	//
	//		return "redirect:/trouble/" + id;
	//	}

	// トラブル編集表示
	@GetMapping("/trouble/{id}/edit")
	public String edit(@PathVariable Integer id, Model model, HttpSession session) {

		Trouble trouble = service.findById(id);

		if (trouble == null) {
			return "redirect:/trouble/list";
		}

		User loginUser = (User) session.getAttribute("loginUser");
		model.addAttribute("trouble", trouble);
		model.addAttribute("role", loginUser.getRoll());

		return "nishikigi/troubleedit";
	}

	// トラブル編集確認表示
	@PostMapping("/trouble/{id}/edit/confirm")
	public String confirm(@PathVariable Integer id, @ModelAttribute TroubleForm form, Model model) {

		model.addAttribute("form", form);

		return "nishikigi/troubleeditcheck";
	}

	// トラブル編集画面のフォーム送信
	@PostMapping("/trouble/{id}/edit/complete")
	public String complete(
			@ModelAttribute TroubleForm form) {

		Trouble trouble = new Trouble();

		trouble.setTroubleId(form.getTroubleId());
		trouble.setPriority(form.getPriority());
		trouble.setTroubleType(form.getTroubleType());
		trouble.setOverview(form.getOverview());
		trouble.setDetail(form.getDetail());
		trouble.setSiteMemo(form.getSiteMemo());
		trouble.setHqMemo(form.getHqMemo());

		service.update(trouble);

		return "redirect:/complete";
	}

}
