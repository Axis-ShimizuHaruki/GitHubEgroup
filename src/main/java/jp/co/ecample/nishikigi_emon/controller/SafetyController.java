package jp.co.ecample.nishikigi_emon.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ecample.nishikigi_emon.dto.SafetyList;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.SafetyForm;
import jp.co.ecample.nishikigi_emon.service.SafetyService;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
public class SafetyController {
	private final SafetyService service;
	private final SiteService siteService;

	public SafetyController(SafetyService service, SiteService siteService) {
		this.service = service;
		this.siteService = siteService;
	}

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// 安全点検一覧画面表示
	@GetMapping("/safetyinspection/list")
	public String showSafetyList(
			@RequestParam(required = false) Integer siteId,
			Model model,
			HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		
		// ログインしていない
		if (user == null) {
			return "redirect:/login";
		}
		
		List<SafetyList> safetyList;

		List<Site> siteList = siteService.selectAll();
		siteList.removeIf(site -> site.getOfficecheck() == true);

		if (user.getRoll() == 0 && siteId == null) {
			// ホーム画面から
			safetyList = service.selectAll();
		} else if (user.getRoll() == 0 && siteId != null) {
			// 現場ポータルから
			safetyList = service.search(null, siteId, null);
		} else {
			siteId = (Integer) session.getAttribute("siteId");
			safetyList = service.search(null, siteId, null);
		}

		model.addAttribute("safetyList", safetyList);
		model.addAttribute("siteList", siteList);

		// 検索条件保持用
		model.addAttribute("siteId", siteId);

		return "nishikigi/safetylist";
	}

	// 検索処理制御
	@GetMapping("/safetyinspection/search")
	public String search(
			@RequestParam(required = false) LocalDate sCreatedAt,
			@RequestParam(required = false) Integer siteId,
			@RequestParam(required = false) String judgement,
			Model model,
			HttpSession session) {
		//		if(session.getAttribute("loginUser") == null) {
		//			return "redirect:/login";
		//		}
		User loginUser = (User) session.getAttribute("loginUser");
		
		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 管理者以外は強制的に自分の現場にする
		if (loginUser.getRoll() != 0) {
			siteId = (Integer) session.getAttribute("siteId");
		}

		List<SafetyList> safetyList = service.search(sCreatedAt, siteId, judgement);

		List<Site> siteList = siteService.selectAll();
		siteList.removeIf(site -> site.getOfficecheck() == true);

		model.addAttribute("safetyList", safetyList);
		model.addAttribute("siteList", siteList);

		// 検索条件保持用
		model.addAttribute("sCreatedAt", sCreatedAt);
		model.addAttribute("siteId", siteId);
		model.addAttribute("judgement", judgement);

		return "nishikigi/safetylist";
	}

	// 詳細画面表示
	@GetMapping("/safetyinspection/{id}")
	public String showSafetyDetail(
			@PathVariable Integer id,
			Model model,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}

		Optional<Safety> result = service.findById(id);
		Safety safety = result.get();

		model.addAttribute("safety", safety);

		return "nishikigi/safetycheckdetail";
	}

	// 安全点検登録画面表示
	@GetMapping("/safetyinspection/new")
	public String showSafetyNewForm(Model model, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		model.addAttribute("safety", new SafetyForm());

		return "nishikigi/safetycheck";
	}

	// 安全点検登録画面に戻る
	@PostMapping("/safetyinspection/new")
	public String backSafetyInput(
			@ModelAttribute("safety") SafetyForm safety,
			Model model,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		model.addAttribute("safety", safety);

		return "nishikigi/safetycheck";
	}

	// 安全点検登録確認画面を表示
	@PostMapping("/safetyinspection/new/confirm")
	public String showSafetyConfirm(
			@Valid @ModelAttribute("safety") SafetyForm safety,
			BindingResult result,
			Model model,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		// バリデーションNGなら登録画面に戻す
		if (result.hasErrors()) {
			return "nishikigi/safetycheck";
		}

		model.addAttribute("safety", safety);

		return "nishikigi/safetycheckcheck";
	}

	// 安全点検登録処理
	@PostMapping("/safetyinspection/new/comfirmed")
	public String createSafety(@ModelAttribute("safety") SafetyForm safety,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}
		
		service.saveSafety(
				safety.getScaffolding(),
				safety.getProtectingOpenings(),
				safety.getSafetyHarness(),
				safety.getEquipmentInspection(),
				safety.getFireExtinguisher(),
				safety.getOrganization(),
				safety.getElectricalInsulation(),
				(Integer) session.getAttribute("siteId"));

		// =========================
		// どれか1件でも不備なら通知
		// =========================
		boolean hasProblem = safety.getScaffolding() == 1 ||
				safety.getProtectingOpenings() == 1 ||
				safety.getSafetyHarness() == 1 ||
				safety.getEquipmentInspection() == 1 ||
				safety.getFireExtinguisher() == 1 ||
				safety.getOrganization() == 1 ||
				safety.getElectricalInsulation() == 1;

		// =========================
		// 通知送信
		// =========================
		if (hasProblem) {

			Integer siteId = (Integer) session.getAttribute("siteId");

			Site site = siteService.findById(siteId);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

			String createdAt = LocalDateTime.now().format(formatter);

			Map<String, Object> notice = new HashMap<>();

			notice.put("type", "safety");
			notice.put("siteId", siteId);
			notice.put("siteName", site.getSiteName());
			notice.put("createdAt", createdAt);

			messagingTemplate.convertAndSend(
					"/topic/notice",
					(Object) notice);
		}
		return "redirect:/complete";
	}

	// 編集画面表示
	@PostMapping("/safetyinspection/{id}/edit")
	public String showSafetyEdit(
			@PathVariable Integer id,
			Model model,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		Optional<Safety> result = service.findById(id);
		Safety safety = result.get();

		model.addAttribute("safety", safety);

		return "nishikigi/safetyedit";
	}

	// 編集確認画面を表示
	@PostMapping("/safetyinspection/{id}/edit/confirm")
	public String showSafetyEditConfirm(
			@Valid @ModelAttribute SafetyForm safety,
			BindingResult result,
			Model model,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		if (result.hasErrors()) {
			return "nishikigi/safetyedit";
		}

		if (!safety.getPhotoFile().isEmpty()) {
			MultipartFile photoFile = safety.getPhotoFile();
		    byte[] bytes;
		    String previewImageStr;
			try {
				bytes = safety.getPhotoFile().getBytes();
			    previewImageStr =
				        Base64.getEncoder().encodeToString(bytes);
			    model.addAttribute("previewImage", previewImageStr);
			    model.addAttribute("fileName", photoFile.getOriginalFilename());
			    model.addAttribute("contentType", photoFile.getContentType());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		model.addAttribute("safety", safety);

		return "nishikigi/safetyeditcheck";
	}

	// 安全点検編集処理
	@PostMapping("/safetyinspection/{id}/edit/confirmed")
	public String updateSafety(
			@ModelAttribute("safety") Safety safety,
			@RequestParam("previewImage") String previewImage,
			@RequestParam("fileName") String fileName,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 現場管理者でなければそれぞれのホームに戻す
		if (loginUser.getRoll() != 1) {
			if(loginUser.getRoll() == 0)
				return "redirect:/homeoffice";
			if(loginUser.getRoll() == 2)
				return "redirect:/homesite";
		}

		byte[] bytes = Base64.getDecoder().decode(previewImage);
		
		String photo = safety.getPhoto();

		if (previewImage != null && previewImage != "") {
			photo = service.savePhoto(bytes, fileName);
		}

		service.updateSafety(
				safety.getSafetyId(),
				safety.getScaffolding(),
				safety.getProtectingOpenings(),
				safety.getSafetyHarness(),
				safety.getEquipmentInspection(),
				safety.getFireExtinguisher(),
				safety.getOrganization(),
				safety.getElectricalInsulation(),
				photo);

		return "redirect:/complete";
	}

	// 安全点検確認完了
	@PostMapping("/safetyinspection/{id}/confirmed")
	public String confirmSafety(
			@PathVariable Integer id,
			RedirectAttributes redirectAttributes,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 本社ユーザーでなければホームに戻す
		if (loginUser.getRoll() != 0) {
			return "redirect:/homesite";
		}

		service.confirmSafety(id);

		redirectAttributes.addAttribute("id", id);

		return "redirect:/safetyinspection/{id}";
	}

	// 安全点検確認解除
	@PostMapping("/safetyinspection/{id}/confirmed/cancel")
	public String confirmCancelSafety(
			@PathVariable Integer id,
			RedirectAttributes redirectAttributes,
			HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていない
		if (loginUser == null) {
			return "redirect:/login";
		}
		// 本社ユーザーでなければホームに戻す
		if (loginUser.getRoll() != 0) {
			return "redirect:/homesite";
		}

		service.confirmCancelSafety(id);

		redirectAttributes.addAttribute("id", id);

		return "redirect:/safetyinspection/{id}";
	}
}
