package jp.co.ecample.nishikigi_emon.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.ecample.nishikigi_emon.entity.Chat;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.repository.ChatRepository;
import jp.co.ecample.nishikigi_emon.service.SiteService;

@Controller
public class SiteController {
	private final SiteService service;
	public SiteController(SiteService service) {
		this.service = service;
	}
	
	@Autowired
	private ChatRepository chatRepository;

	@GetMapping("/homesite")
	public String showNewHome(
	        HttpSession session,
	        Model model) {

	    Integer siteId =
	        (Integer) session.getAttribute("siteId");

	    Site site =
	        service.findById(siteId);

	    List<Chat> chatList =
	        chatRepository
	            .findBySiteSiteIdOrderByDateTimeAsc(
	                siteId);

	    model.addAttribute("site", site);
	    model.addAttribute("chatList", chatList);
	    model.addAttribute("loginSiteId", siteId);

	    return "nishikigi/home";
	}
	
//	@GetMapping("/dailyreport/new/confirm")
//	public String showNewDailyreport() {
//		return "nishikigi/dailyreport";
//	}
	
//	@GetMapping("/dailyreport/list")
//	public String showNewDailyList() {
//		return "nishikigi/dailylist";
//	}
//	
//	@GetMapping("/safetycheck")
//	public String showNewSafetyCheck() {
//		return "nishikigi/safetycheck";
//	}
//	
//	@GetMapping("/safetylist")
//	public String showNewSafetyList() {
//		return "nishikigi/safetylist";
//	}
//	@GetMapping("/trouble")
//	public String showNewTrouble() {
//		return "nishikigi/trouble";
//	}
//	
//	@GetMapping("/troublelist")
//	public String showNewTroubleList() {
//		return "nishikigi/troublelist";
//	}
//	
}
