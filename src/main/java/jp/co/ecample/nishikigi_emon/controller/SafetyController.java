package jp.co.ecample.nishikigi_emon.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.ecample.nishikigi_emon.dto.SafetyList;
import jp.co.ecample.nishikigi_emon.entity.Safety;
import jp.co.ecample.nishikigi_emon.entity.Site;
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

	// 安全点検一覧画面表示
	@GetMapping("/safetyinspection/list")
	public String showSafetyList(
			@RequestParam(required = false) Integer siteId,
			Model model
			) {
		List<SafetyList> safetyList;
		
	    List<Site> siteList = siteService.selectAll();
	    if (!siteList.isEmpty()) {
	        siteList.remove(0);
	    }
		
	    if (siteId == null) {
	        // ホーム画面から
	        safetyList = service.selectAll();
	    } else {
	        // 現場ポータルから
	        safetyList = service.search(null, siteId, null);
	    }
		
		model.addAttribute("safetyList", safetyList);
		model.addAttribute("siteList", siteList);
		
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

		List<SafetyList> safetyList = service.search(sCreatedAt, siteId, judgement);
		
		List<Site> siteList = siteService.selectAll();
		if (!siteList.isEmpty()) {
		    siteList.remove(0);
		}
		
		model.addAttribute("safetyList", safetyList);
	    model.addAttribute("siteList", siteList);
		
		return "nishikigi/safetylist";
	}

	// 詳細画面表示
	@GetMapping("/safetyinspection/{id}")
	public String showSafetyDetail(
			@PathVariable Integer id,
			Model model,
			HttpSession session
			) {
		Optional<Safety> result = service.findById(id);
		Safety safety = result.get();

		model.addAttribute("safety", safety);
		
		return "nishikigi/safetycheckdetail";
	}
	
	// 安全点検登録画面表示
	@GetMapping("/safetyinspection/new")
	public String showSafetyNewForm(Model model) {
		model.addAttribute("safety", new SafetyForm());
		
		return "nishikigi/safetycheck";
	}
	
	// 安全点検登録画面に戻る
	@PostMapping("/safetyinspection/new")
	public String backSafetyInput(
	        @ModelAttribute("safety") SafetyForm safety,
	        Model model) {

	    model.addAttribute("safety", safety);

	    return "nishikigi/safetycheck";
	}
	
	// 安全点検登録確認画面を表示
	@PostMapping("/safetyinspection/new/confirm")
	public String showSafetyConfirm(
			@Valid @ModelAttribute("safety") SafetyForm safety,
			BindingResult result,
			Model model
			) {
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
		service.saveSafety(
				safety.getScaffolding(),
				safety.getProtectingOpenings(),
				safety.getSafetyHarness(),
				safety.getEquipmentInspection(),
				safety.getFireExtinguisher(),
				safety.getOrganization(),
				safety.getElectricalInsulation(),
				(Integer)session.getAttribute("siteId")
				);
		
		return "redirect:/complete";
	}
	
	// 編集画面表示
	@PostMapping("/safetyinspection/{id}/edit")
	public String showSafetyEdit(
			@PathVariable Integer id,
			Model model,
			HttpSession session
			) {
		Optional<Safety> result = service.findById(id);
		Safety safety = result.get();
		
		model.addAttribute("safety", safety);
		
		return "nishikigi/safetyedit";
	}
	
	// 編集確認画面を表示
	@PostMapping("/safetyinspection/{id}/edit/confirm")
	public String showSafetyEditConfirm(
			@Valid @ModelAttribute("safety") SafetyForm safety,
			BindingResult result,
			Model model, 
			HttpSession session) {
		
	    if (result.hasErrors()) {
	        return "nishikigi/safetyedit";
	    }
	    
	    if (!safety.getPhotoFile().isEmpty()) {
	        String photoPath =
	                service.savePhoto(safety.getPhotoFile());

	        safety.setPhoto(photoPath);
	    }
	    System.out.println("photo = " + safety.getPhoto());
	    model.addAttribute("safety", safety);
		
		return "nishikigi/safetyeditcheck";
	}
	
	// 安全点検編集処理
	@PostMapping("/safetyinspection/{id}/edit/confirmed")
	public String updateSafety(
			@ModelAttribute("safety") Safety safety,
			HttpSession session) {
		service.updateSafety(
				safety.getSafetyId(),
				safety.getScaffolding(),
				safety.getProtectingOpenings(),
				safety.getSafetyHarness(),
				safety.getEquipmentInspection(),
				safety.getFireExtinguisher(),
				safety.getOrganization(),
				safety.getElectricalInsulation(),
				safety.getPhoto()
				);
		
		return "redirect:/complete";
	}

	// 安全点検確認完了
	@PostMapping("/safetyinspection/{id}/confirmed")
	public String confirmSafety(
			@PathVariable Integer id,
			RedirectAttributes redirectAttributes,
			HttpSession session
			) {
		service.confirmSafety(id);
		
		redirectAttributes.addAttribute("id", id);
		
		return "redirect:/safetyinspection/{id}";
	}
	
	// 安全点検確認解除
	@PostMapping("/safetyinspection/{id}/confirmed/cancel")
	public String confirmCancelSafety(
			@PathVariable Integer id,
			RedirectAttributes redirectAttributes,
			HttpSession session
			) {
		service.confirmCancelSafety(id);
		
		redirectAttributes.addAttribute("id", id);
		
		return "redirect:/safetyinspection/{id}";
	}
}
