package jp.co.ecample.nishikigi_emon.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ecample.nishikigi_emon.form.SafetyForm;
import jp.co.ecample.nishikigi_emon.service.SafetyService;

@Controller
public class SafetyController {
	private final SafetyService service;

	public SafetyController(SafetyService service, LoginController loginController) {
		this.service = service;
	}

	// 安全点検一覧画面表示
	@GetMapping("/safetyinspection/list")
	public String showSafetyList() {
		return "nishikigi/safetylist";
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
		System.out.println("test");
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
}
