package jp.co.ecample.nishikigi_emon.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
public class SiteController {
	private final SiteService service;
	public SiteController(SiteService service) {
		this.service = service;
	}

	@GetMapping("/homesite")
	public String showNewHome(HttpSession session, Model model) {
		Integer siteId = (Integer) session.getAttribute("siteId");

	    Site site = service.findById(siteId);

	    model.addAttribute("site", site);
		
		return "nishikigi/home";
	}
	
	@GetMapping("/dailyreport")
	public String showNewDailyreport() {
		return "nishikigi/dailyreport";
	}
	
	@GetMapping("/dailylist")
	public String showNewDailyList() {
		return "nishikigi/dailylist";
	}
	
	@GetMapping("/safetycheck")
	public String showNewSafetyCheck() {
		return "nishikigi/safetycheck";
	}
	
	@GetMapping("/safetylist")
	public String showNewSafetyList() {
		return "nishikigi/safetylist";
	}
	@GetMapping("/trouble")
	public String showNewTrouble() {
		return "nishikigi/trouble";
	}
	
	@GetMapping("/troublelist")
	public String showNewTroubleList() {
		return "nishikigi/troublelist";
	}
}
