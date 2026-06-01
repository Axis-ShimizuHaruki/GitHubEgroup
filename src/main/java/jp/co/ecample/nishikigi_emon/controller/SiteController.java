package jp.co.ecample.nishikigi_emon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

	@GetMapping("/home")
	public String showNewHome() {	
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
