package jp.co.ecample.nishikigi_emon.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.Trouble;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.TroubleForm;
import jp.co.ecample.nishikigi_emon.form.TroubleSearchForm;
import jp.co.ecample.nishikigi_emon.service.SiteService;
import jp.co.ecample.nishikigi_emon.service.TroubleService;

@Controller
public class TroubleController {

	private final TroubleService service;

	public TroubleController(TroubleService service) {
		this.service = service;
	}

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private SiteService siteService;

	// トラブル登録画面表示
	@GetMapping("/trouble/new")
	public String input(@ModelAttribute TroubleForm form) {
		return "nishikigi/trouble";
	}

	// トラブル登録確認画面
	@PostMapping("/trouble/new/confirm")
	public String confirm(@Valid @ModelAttribute TroubleForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "nishikigi/trouble";
		}
		model.addAttribute("form", form);
		return "nishikigi/troublecheck";
	}

	// トラブル登録画面のフォーム送信
	//	@PostMapping("/troubles")
	//	public String register(@ModelAttribute TroubleForm form, HttpSession session) {
	//
	//		Integer siteId = (Integer) session.getAttribute("siteId");
	//
	//		System.out.println("siteId = " + siteId);
	//
	//		Trouble trouble = new Trouble();
	//
	//		trouble.setPriority(form.getPriority());
	//		trouble.setTroubleType(form.getTroubleType());
	//		trouble.setOverview(form.getOverview());
	//		trouble.setDetail(form.getDetail());
	//
	//		Site site = new Site();
	//		site.setSiteId(siteId);
	//
	//		trouble.setSite(site);
	//
	//		service.register(trouble);
	//		return "redirect:/complete";
	//	}

	// トラブル登録画面のフォーム送信
	@PostMapping("/troubles")
	public String register(@ModelAttribute TroubleForm form,
			HttpSession session) {

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

		// DB登録
		Trouble savedTrouble = service.register(trouble);

		// 現場取得
		Site savedSite = siteService.findById(siteId);

		// =========================
		// 通知データ作成
		// =========================

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

		String createdAt = savedTrouble.gettCreatedAt()
				.format(formatter);

		Map<String, Object> notice = new HashMap<>();

		notice.put("type", "trouble");
		notice.put("siteName", savedSite.getSiteName());
		notice.put("overview", savedTrouble.getOverview());
		notice.put("priority", savedTrouble.getPriority());
		notice.put("troubleId", savedTrouble.getTroubleId());
		notice.put("createdAt", createdAt);

		// これだけでOK
		messagingTemplate.convertAndSend(
				"/topic/notice",
				(Object) notice);

		return "redirect:/complete";
	}

	// トラブル一覧表示
	@GetMapping("/trouble/list")
	public String list(@RequestParam(required = false) Integer siteId, @ModelAttribute TroubleSearchForm form,
			Model model, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// 現場ポータルから遷移時の初回表示
		if (siteId != null
				&& form.getSiteName() == null
				&& form.getOccurredDate() == null
				&& form.getPriority() == null
				&& form.getTroubleType() == null
				&& form.getStatusFlag() == null) {

			Site site = siteService.findById(siteId);

			form.setSiteName(site.getSiteName());
		}

		// 現場管理者の初回表示
		if ((loginUser.getRoll() == 1 || loginUser.getRoll() == 2)
				&& siteId == null
				&& form.getSiteName() == null
				&& form.getOccurredDate() == null
				&& form.getPriority() == null
				&& form.getTroubleType() == null
				&& form.getStatusFlag() == null) {

			Integer sessionSiteId = (Integer) session.getAttribute("siteId");

			Site site = siteService.findById(sessionSiteId);

			form.setSiteName(site.getSiteName());
		}
		List<Trouble> troubleList = service.search(
				form.getOccurredDate(),
				form.getSiteName(),
				form.getPriority(),
				form.getTroubleType(),
				form.getStatusFlag());

		List<Site> siteList = siteService.selectAll();

		model.addAttribute("siteList", siteList);
		model.addAttribute("troubleList", troubleList);
		model.addAttribute("troubleSearchForm", form);
		return "nishikigi/troublelist";
	}

	// 検索リセット表示
	@GetMapping("/trouble/list/reset")
	public String reset(Model model) {

		TroubleSearchForm form = new TroubleSearchForm();

		List<Trouble> troubleList = service.search(
				null,
				null,
				null,
				null,
				null);

		List<Site> siteList = siteService.selectAll();

		model.addAttribute("siteList", siteList);
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
		model.addAttribute("troubleForm", trouble);
		model.addAttribute("role", loginUser.getRoll());

		return "nishikigi/troubleedit";
	}

	// トラブル編集確認表示
	@PostMapping("/trouble/{id}/edit/confirm")
	public String confirm(
			@PathVariable Integer id,
			@Valid @ModelAttribute("troubleForm") TroubleForm form,
			BindingResult result,
			Model model,
			HttpSession session) {

		if (result.hasErrors()) {

			Trouble trouble = service.findById(id);

			User loginUser = (User) session.getAttribute("loginUser");

			model.addAttribute("trouble", trouble);
			model.addAttribute("troubleForm", form);
			model.addAttribute("role", loginUser.getRoll());

			return "nishikigi/troubleedit";
		}

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
