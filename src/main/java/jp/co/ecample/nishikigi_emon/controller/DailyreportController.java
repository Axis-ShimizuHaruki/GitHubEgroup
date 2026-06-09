package jp.co.ecample.nishikigi_emon.controller;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ecample.nishikigi_emon.entity.Dailyreport;
import jp.co.ecample.nishikigi_emon.entity.Site;
import jp.co.ecample.nishikigi_emon.entity.User;
import jp.co.ecample.nishikigi_emon.form.DailyreportForm;
import jp.co.ecample.nishikigi_emon.service.DailyreportService;
import jp.co.ecample.nishikigi_emon.service.SiteService;

/**
 * 日報管理システム コントローラークラス
 * 日報の一覧・検索・詳細表示・新規登録・編集・本社確認機能の制御を行う
 */
@Controller
@RequestMapping("/dailyreport")
public class DailyreportController {

	// ロール（権限）定数
	/** 権限コード：本社ユーザー（全現場の閲覧・確認権限を持つ） */
	private static final int ROLE_HONSHA = 0;
	/** 権限コード：現場管理者（自現場の日報作成・編集・再提出権限を持つ） */
	private static final int ROLE_GENBA_MANAGER = 1;
	/** 権限コード：現場閲覧者（自現場の情報閲覧のみ、作成や編集は不可） */
	private static final int ROLE_GENBA_VIEWER = 2;

	// 【画像アップロード対策】確認画面の往復でセッションが肥大化・パンクするのを防ぐための一時的な画像置き場
	// 各写真のBase64文字列をUUID（引き換えキー）と紐づけてスレッドセーフに管理します
	private static final java.util.Map<String, String> imageCache = new ConcurrentHashMap<>();

	@Autowired
	private DailyreportService dailyreportService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// ==========================================
	//        共通認証・アクセスチェックメソッド群
	// ==========================================

	/**
	 * 未ログインチェック
	 * セッションにログインユーザー情報がない場合、ログイン画面（/login）への強制遷移用パスを返します。
	 */
	private String checkLogin(HttpSession session) {
		return session.getAttribute("loginUser") == null ? "redirect:/login" : null;
	}

	/**
	 * 本社ユーザー限定チェック
	 * ログインユーザーが本社アカウント（roll=0）でない場合、ログイン画面または日報一覧画面へ追い出すパスを返します。
	 */
	private String checkHonshaOnly(HttpSession session) {
		User u = (User) session.getAttribute("loginUser");
		return (u == null) ? "redirect:/login" : (u.getRoll() != ROLE_HONSHA ? "redirect:/dailyreport/list" : null);
	}

	/**
	 * 現場管理者限定チェック
	 * 日報の新規作成や変更画面（/new, /edit）において、現場管理者（roll=1）以外がアクセスしてきた場合に一覧へ追い出します。
	 */
	private String checkManagerOnly(HttpSession session) {
		User u = (User) session.getAttribute("loginUser");
		return (u == null) ? "redirect:/login"
				: (u.getRoll() != ROLE_GENBA_MANAGER ? "redirect:/dailyreport/list" : null);
	}

	/**
	 * 現場所属セキュリティチェック（不正URL直打ち覗き見ガード）
	 * 本社ユーザーは全ての現場を閲覧可能とし、現場アカウントの場合は自現場以外のID（reportSiteId）のアクセスを拒否します。
	 */
	private String checkSiteAccess(HttpSession session, Integer reportSiteId) {
		User u = (User) session.getAttribute("loginUser");
		if (u == null)
			return "redirect:/login";
		if (u.getRoll() == ROLE_HONSHA)
			return null; // 本社はセキュリティスルー
		Integer sId = (Integer) session.getAttribute("siteId");
		return (sId == null || !sId.equals(reportSiteId)) ? "redirect:/dailyreport/list" : null;
	}

	// ==========================================
	//                日報：一覧・検索機能
	// ==========================================

	/**
	 * 日報一覧画面を表示する（検索機能・ポータル起点記憶機能つき）
	 */
	@SuppressWarnings("unused")
	@GetMapping("/list")
	public String showList(
			@RequestParam(name = "targetDate", required = false) String targetDateStr, // 検索条件：対象日付
			@RequestParam(name = "siteId", required = false) Integer siteId, // 検索条件：現場ID
			@RequestParam(name = "dStatusFlag", required = false) Integer dStatusFlag, // 検索条件：本社確認ステータス
			@RequestParam(name = "workDetails", required = false) String workDetails, // 検索条件：作業内容キーワード
			@RequestParam(name = "portalSiteId", required = false) Integer portalSiteId, // 起点現場ポータルID（戻り先管理用）
			@RequestParam(name = "clear", required = false) Boolean clear, // 検索リセット合図フラグ
			@RequestParam(name = "from", required = false) String from, // 遷移元判定フラグ（header/listなど）
			@RequestParam(name = "search", required = false) Boolean isSearch, // 検索ボタン押下フラグ
			HttpSession session, Model model) {

		// 1. ログインチェック
		String redirect = checkLogin(session);
		if (redirect != null)
			return redirect;
		User loginUser = (User) session.getAttribute("loginUser");

		// 2. 本社ユーザー限定：どの現場ポータルから出発したか（スタート地点）を管理するロジック
		if (loginUser.getRoll() == ROLE_HONSHA) {
			if (portalSiteId != null) {
				// パターンA: 現場ポータル等から初めて一覧に遷移してきた、またはヘッダーから遷移してきた場合
				session.setAttribute("fromPortalSiteId", portalSiteId); // 出発地点の現場IDをセッションにガッチリ記憶

				// ヘッダーの文字リンク（from=header）以外から来た場合（下部のボタン等）は、その現場で初期絞り込みを行う
				if (siteId == null && !"header".equals(from)) {
					siteId = portalSiteId;
				}
			} else if (Boolean.TRUE.equals(clear)) {
				// パターンB: 一覧画面で「リセット」ボタンが押された場合
				// 検索条件（siteIdなど）はクリアするが、出発地点（fromPortalSiteId）のセッションは消さずに維持する
			} else {
				// パターンC: 通常の検索ボタン押下、詳細画面からの戻り、またはメニューから直接遷移した場合
				// 検索ボタン押下(isSearch=true)、各条件の入力あり、詳細からの戻り(from=list)であれば画面遷移中とみなす
				boolean isFormSearch = Boolean.TRUE.equals(isSearch)
						|| (targetDateStr != null && !targetDateStr.isEmpty())
						|| siteId != null
						|| dStatusFlag != null
						|| (workDetails != null && !workDetails.isEmpty())
						|| "list".equals(from);
				if (!isFormSearch) {
					// 検索でも戻りでもない完全な初期状態（左メニューから直接一覧を開いた場合など）は、出発地点の記憶を消去する
					session.removeAttribute("fromPortalSiteId");
				}
			}
		}

		// 3. 現場所属ユーザーの場合は、検索条件を自分の現場IDに強制固定する
		Integer userSiteId = (Integer) session.getAttribute("siteId");
		model.addAttribute("siteList", siteService.selectAll()); // プルダウン用の全現場リスト

		java.time.LocalDate targetDate = null;
		if (targetDateStr != null && !targetDateStr.isEmpty()) {
			targetDate = java.time.LocalDate.parse(targetDateStr);
		}
		if (loginUser.getRoll() != ROLE_HONSHA) {
			siteId = userSiteId;
		}

		// 4. 条件を元にサービス層経由でデータベースから日報リストを検索取得
		model.addAttribute("workDetails", workDetails);
		List<Dailyreport> reportList = dailyreportService.searchReports(targetDate, siteId, dStatusFlag, workDetails);
		model.addAttribute("reportList", reportList);

		// 5. 【詳細画面への引き継ぎ用】現在の検索条件をModelに格納（配列バグ防止・Thymeleafの選択維持用）
		model.addAttribute("currentSiteId", siteId);
		model.addAttribute("currentTargetDate", targetDateStr);
		model.addAttribute("currentDStatusFlag", dStatusFlag);
		model.addAttribute("currentWorkDetails", workDetails);

		// ヘッダーでの「〇〇現場（絞り込み中）」というタイトル表示用
		model.addAttribute("headerSiteId", siteId);

		return "nishikigi/dailylist";
	}

	// ==========================================
	//                日報：新規登録機能
	// ==========================================

	/**
	 * 日報新規登録画面を表示する
	 */
	@GetMapping("/new")
	public String showCreateForm(HttpSession session, Model model) {
		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;
		Integer userSiteId = (Integer) session.getAttribute("siteId");

		// 画面用のFormオブジェクトを用意し、本日の日付と所属現場IDを初期セット
		DailyreportForm form = new DailyreportForm();
		form.setTargetDate(java.time.LocalDate.now());
		if (userSiteId != null) {
			form.setSiteId(userSiteId);
		}
		
		// 進捗率の初期値をスライダーの中央値（50%）にセット
		form.setProgressPercent(50);

		model.addAttribute("dailyreport", form);
		model.addAttribute("siteList", siteService.selectAll());
		return "nishikigi/dailyreport";
	}

	/**
	 * 日報登録の確認画面を表示する（バリデーションチェック）
	 */
	@PostMapping("/new/confirm")
	public String confirmCreate(
			@Valid @ModelAttribute("dailyreport") DailyreportForm form, // 入力値の相関チェック（単体アノテーション）
			BindingResult bindingResult, // エラー結果格納庫
			HttpSession session, Model model) {

		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;

		// 【個別チェック1】出面情報（JSON文字列）が正しく入力されているか動的検知
		String wd = form.getWorkerDetails();
		if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
			bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
		} else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
			bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
		} else if (wd.contains("\"count\":\"0\"") || wd.contains("\"count\":\"-") || wd.contains("\"count\":0")) {
			// 職人数が0、またはマイナス（-）が含まれるJSON文字列を検知してフロントのすり抜けをサーバーで完全にブロック
			bindingResult.rejectValue("workerDetails", "", "職人数には1以上の数値を入力してください");
		}

		// 【個別チェック2】同じ現場で同じ日付の日報がすでに登録されていないか、DBを二重登録チェック
		if (form.getSiteId() != null && form.getTargetDate() != null) {
			List<Dailyreport> existingReports = dailyreportService.searchReports(form.getTargetDate(), form.getSiteId(),
					null, null);
			if (!existingReports.isEmpty()) {
				bindingResult.rejectValue("targetDate", "", "選択された日付の日報は既に登録されています。");
			}
		}

		// アップロードされた画像ファイル（MultipartFile）をBase64文字列へと変換
		form.setPhotoBeforeBase64(convertToBase64(form.getUploadPhotoBefore(), form.getPhotoBeforeBase64()));
		form.setPhotoDuringBase64(convertToBase64(form.getUploadPhotoDuring(), form.getPhotoDuringBase64()));
		form.setPhotoAfterBase64(convertToBase64(form.getUploadPhotoAfter(), form.getPhotoAfterBase64()));
		form.setPhotoInspectionBase64(
				convertToBase64(form.getUploadPhotoInspection(), form.getPhotoInspectionBase64()));
		form.setPhotoSafetyBase64(convertToBase64(form.getUploadPhotoSafety(), form.getPhotoSafetyBase64()));

		// 【個別チェック3】必須画像（安全帯使用状況写真）の添付漏れがないかチェック
		if (form.getPhotoSafetyBase64() == null || form.getPhotoSafetyBase64().isEmpty()) {
			bindingResult.rejectValue("photoSafetyBase64", "", "安全帯使用状況写真は必須です（ファイルを選択してください）");
		}

		// どこか1箇所でもバリデーションエラーがあれば、入力画面へ差し戻す
		if (bindingResult.hasErrors()) {
			model.addAttribute("siteList", siteService.selectAll());
			return "nishikigi/dailyreport";
		}

		// 画面表示用に選択された現場名を取得
		if (form.getSiteId() != null) {
			for (Site s : siteService.selectAll()) {
				if (s.getSiteId().equals(form.getSiteId())) {
					model.addAttribute("chosenSiteName", s.getSiteName());
					break;
				}
			}
		}

		// 【画像一時預かりロジック】セッション肥大化（TomcatのCookie上限・メモリパンク）を回避するため、
		// ランダムなキー（引き換え券）を発行して静的Map（imageCache）に一時退避します
		String keyBefore = UUID.randomUUID().toString();
		String keyDuring = UUID.randomUUID().toString();
		String keyAfter = UUID.randomUUID().toString();
		String keyInspection = UUID.randomUUID().toString();
		String keySafety = UUID.randomUUID().toString();

		if (form.getPhotoBeforeBase64() != null)
			imageCache.put(keyBefore, form.getPhotoBeforeBase64());
		if (form.getPhotoDuringBase64() != null)
			imageCache.put(keyDuring, form.getPhotoDuringBase64());
		if (form.getPhotoAfterBase64() != null)
			imageCache.put(keyAfter, form.getPhotoAfterBase64());
		if (form.getPhotoInspectionBase64() != null)
			imageCache.put(keyInspection, form.getPhotoInspectionBase64());
		if (form.getPhotoSafetyBase64() != null)
			imageCache.put(keySafety, form.getPhotoSafetyBase64());

		// セッションには引き換え券の「キー（UUID文字列）」だけを入れてセッション重量を極限まで軽量化
		session.setAttribute("create_key_Before", keyBefore);
		session.setAttribute("create_key_During", keyDuring);
		session.setAttribute("create_key_After", keyAfter);
		session.setAttribute("create_key_Inspection", keyInspection);
		session.setAttribute("create_key_Safety", keySafety);

		model.addAttribute("dailyreport", form);
		model.addAttribute("siteList", siteService.selectAll());
		model.addAttribute("headerSiteId", form.getSiteId()); // ヘッダー用現場IDのセット

		return "nishikigi/dailyreportcheck";
	}

	/**
	 * 新規日報をデータベースに正式登録する
	 */
	@PostMapping("/create")
	public String registerReport(@ModelAttribute("dailyreport") DailyreportForm form, HttpSession session) {
		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;

		// 🌟【戻るボタン対策】必須写真の引き換え券（キー）がセッションにあるか確認
		// 1回目の保存で消去されているため、戻るボタンで再送信された場合は必ず null になる
		String keySafety = (String) session.getAttribute("create_key_Safety");
		if (keySafety == null) {
			// 既に処理済み（または不正アクセス）とみなし、日報一覧画面へ安全に逃がす
			return "redirect:/dailyreport/list";
		}

		// セッションから画像引き換え券（キー）を取り出す
		String keyBefore = (String) session.getAttribute("create_key_Before");
		String keyDuring = (String) session.getAttribute("create_key_During");
		String keyAfter = (String) session.getAttribute("create_key_After");
		String keyInspection = (String) session.getAttribute("create_key_Inspection");

		// 一時預かりMapから本物の画像データ（Base64文字列）を回収してFormに詰め直す（回収後はMapからメモリ解放のためにremoveお掃除）
		if (keyBefore != null)
			form.setPhotoBeforeBase64(imageCache.remove(keyBefore));
		if (keyDuring != null)
			form.setPhotoDuringBase64(imageCache.remove(keyDuring));
		if (keyAfter != null)
			form.setPhotoAfterBase64(imageCache.remove(keyAfter));
		if (keyInspection != null)
			form.setPhotoInspectionBase64(imageCache.remove(keyInspection));
		if (keySafety != null)
			form.setPhotoSafetyBase64(imageCache.remove(keySafety));

		// 用済みのセッション引き換え券情報も綺麗にお掃除
		session.removeAttribute("create_key_Before");
		session.removeAttribute("create_key_During");
		session.removeAttribute("create_key_After");
		session.removeAttribute("create_key_Inspection");
		session.removeAttribute("create_key_Safety");

		// Form用モデルからDB用モデル（Entity）へデータをコピーしてDBに永続化保存
		Dailyreport report = new Dailyreport();
		copyFormToEntity(form, report);
		dailyreportService.createReport(report);

		// WebSocket通信（STOMP）を用いて、接続中の本社管理者等のブラウザ画面へ「リアルタイムリロード通知」を送信
		messagingTemplate.convertAndSend(
				"/topic/notice",
				"{\"type\":\"reload\"}");

		return "redirect:/complete"; // PRGパターン：登録完了画面へリダイレクトし、ブラウザの「F5連打」による二重投稿を完全に防止
	}

	// ==========================================
	//                日報：詳細表示機能
	// ==========================================

	/**
	 * 日報詳細画面を表示する（検索パラメータを引数に保持して回収）
	 */
	@GetMapping("/{id}")
	public String showDetail(
			@PathVariable("id") Integer reportId,
			@RequestParam(name = "from", required = false, defaultValue = "list") String from,
			@RequestParam(name = "targetDate", required = false) String targetDate,
			@RequestParam(name = "siteId", required = false) Integer siteId,
			@RequestParam(name = "dStatusFlag", required = false) Integer dStatusFlag,
			@RequestParam(name = "workDetails", required = false) String workDetails,
			HttpSession session, Model model) {

		String redirect = checkLogin(session);
		if (redirect != null)
			return redirect;

		// データベースから該当する日報データを1件取得（存在しなければ404エラー画面へ）
		Dailyreport report = dailyreportService.getReportById(reportId);
		if (report == null)
			return "error/404";

		// 他の現場ユーザーが不正にURL直接書き換え（IDハック）で他現場を覗き見していないかセキュリティガード
		String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
		if (accessRedirect != null)
			return accessRedirect;

		// データベース内の画像（byte[]バイナリ）をHTMLにimgタグ表示（src="data:image/..."）させるためにBase64へエンコード変換
		model.addAttribute("photoBeforeBase64", encodeBase64(report.getPhotoBefore()));
		model.addAttribute("photoDuringBase64", encodeBase64(report.getPhotoDuring()));
		model.addAttribute("photoAfterBase64", encodeBase64(report.getPhotoAfter()));
		model.addAttribute("photoInspectionBase64", encodeBase64(report.getPhotoInspection()));
		model.addAttribute("photoSafetyBase64", encodeBase64(report.getPhotoSafety()));

		model.addAttribute("report", report);
		model.addAttribute("from", from);

		model.addAttribute("siteList", siteService.selectAll());

		// 現場ポータルの戻り先ID（headerSiteId）を決定するロジック
		// 一覧からの引き継ぎ（siteId）が空なら、表示する日報の現場IDを自動でセットしてパンくずリンクを救済
		Integer headerSiteId = siteId;
		if (headerSiteId == null && report.getSite() != null) {
			headerSiteId = report.getSite().getSiteId();
		}
		model.addAttribute("headerSiteId", headerSiteId);

		// 一覧から引き継いだ純粋な検索条件としての現場IDを別途保管（HTMLでの見出し表記分岐用）
		model.addAttribute("searchSiteId", siteId);

		// 一覧画面から引き継いできた現在の「検索絞り込み条件」を詳細画面のModelに保持（戻るボタンで条件を壊さないため）
		model.addAttribute("targetDate", targetDate);
		model.addAttribute("dStatusFlag", dStatusFlag);
		model.addAttribute("workDetails", workDetails);

		return "nishikigi/dailyreportdetail";
	}

	// ==========================================
	//                日報：編集・更新機能
	// ==========================================

	/**
	 * 日報編集画面を表示する
	 */
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable("id") Integer reportId, HttpSession session, Model model) {
		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;
		Dailyreport report = dailyreportService.getReportById(reportId);
		if (report == null)
			return "error/404";
		String accessRedirect = checkSiteAccess(session, report.getSite().getSiteId());
		if (accessRedirect != null)
			return accessRedirect;

		// DBから取り出したEntityのデータ（過去の登録内容）を、編集画面用Formクラスにまるごと詰め替え（コンバート）
		DailyreportForm form = new DailyreportForm();
		form.setReportId(report.getReportId());
		form.setSiteId(report.getSite().getSiteId());
		form.setProjectNumber(report.getProjectNumber()); // 工事番号のセット
		form.setTargetDate(report.getTargetDate());
		form.setWeather(report.getWeather());
		form.setTemperature(report.getTemperature());
		form.setWorkDetails(report.getWorkDetails());
		form.setWorkerDetails(report.getWorkerDetails());
		form.setProgressPercent(report.getProgressPercent());

		// 既存の画像をBase64文字列に変換してセット（これにより、新しい画像を選択しなくても元の画像データが画面に残る）
		form.setPhotoBeforeBase64(encodeBase64(report.getPhotoBefore()));
		form.setPhotoDuringBase64(encodeBase64(report.getPhotoDuring()));
		form.setPhotoAfterBase64(encodeBase64(report.getPhotoAfter()));
		form.setPhotoInspectionBase64(encodeBase64(report.getPhotoInspection()));
		form.setPhotoSafetyBase64(encodeBase64(report.getPhotoSafety()));

		form.setSafetyKy(report.getSafetyKy());
		form.setSafetyMeasure(report.getSafetyMeasure());
		form.setSafetyEducation(report.getSafetyEducation());
		form.setNearMiss(report.getNearMiss());
		form.setMaterials(report.getMaterials());
		form.setEquipments(report.getEquipments());
		form.setDesignChange(report.getDesignChange());
		form.setCustomerRequest(report.getCustomerRequest());
		form.setPartnerCoordination(report.getPartnerCoordination());
		form.setNextDaySchedule(report.getNextDaySchedule());
		form.setNotes(report.getNotes());
		form.setDStatusFlag(report.getDStatusFlag());

		model.addAttribute("dailyreport", form);
		model.addAttribute("siteList", siteService.selectAll());
		return "nishikigi/dailyedit";
	}

	/**
	 * 日報編集の確認画面を表示する（新規登録時と同様の画像退避処理と入力相関チェック）
	 */
	@PostMapping("/{id}/edit/confirm")
	public String confirmUpdate(
			@Valid @ModelAttribute("dailyreport") DailyreportForm form,
			BindingResult bindingResult,
			HttpSession session, Model model) {

		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;

		// 出面情報の入力漏れ相関バリデーション
		String wd = form.getWorkerDetails();
		if (wd == null || wd.isEmpty() || "[]".equals(wd)) {
			bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
		} else if (wd.contains("\"company\":\"\"") || wd.contains("\"count\":\"\"") || wd.contains("\"names\":\"\"")) {
			bindingResult.rejectValue("workerDetails", "", "出面情報は、協力会社・職人数・作業員氏名をすべて入力してください");
		}

		// 新しいファイルの選択があればBase64を生成して上書き、なければ画面から引き回された既存のBase64文字列を維持
		form.setPhotoBeforeBase64(convertToBase64(form.getUploadPhotoBefore(), form.getPhotoBeforeBase64()));
		form.setPhotoDuringBase64(convertToBase64(form.getUploadPhotoDuring(), form.getPhotoDuringBase64()));
		form.setPhotoAfterBase64(convertToBase64(form.getUploadPhotoAfter(), form.getPhotoAfterBase64()));
		form.setPhotoInspectionBase64(
				convertToBase64(form.getUploadPhotoInspection(), form.getPhotoInspectionBase64()));
		form.setPhotoSafetyBase64(convertToBase64(form.getUploadPhotoSafety(), form.getPhotoSafetyBase64()));

		if (form.getPhotoSafetyBase64() == null || form.getPhotoSafetyBase64().isEmpty()) {
			bindingResult.rejectValue("photoSafetyBase64", "", "安全帯使用状況写真は必須です（ファイルを選択してください）");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("siteList", siteService.selectAll());
			return "nishikigi/dailyedit";
		}

		if (form.getSiteId() != null) {
			String accessRedirect = checkSiteAccess(session, form.getSiteId());
			if (accessRedirect != null)
				return accessRedirect;
			for (Site s : siteService.selectAll()) {
				if (s.getSiteId().equals(form.getSiteId())) {
					model.addAttribute("chosenSiteName", s.getSiteName());
					break;
				}
			}
		}

		// 【編集用画像一時預かり】セッションパンク回避用のUUID引き換えキー発行と静的Mapへの退避
		String keyBefore = UUID.randomUUID().toString();
		String keyDuring = UUID.randomUUID().toString();
		String keyAfter = UUID.randomUUID().toString();
		String keyInspection = UUID.randomUUID().toString();
		String keySafety = UUID.randomUUID().toString();

		if (form.getPhotoBeforeBase64() != null)
			imageCache.put(keyBefore, form.getPhotoBeforeBase64());
		if (form.getPhotoDuringBase64() != null)
			imageCache.put(keyDuring, form.getPhotoDuringBase64());
		if (form.getPhotoAfterBase64() != null)
			imageCache.put(keyAfter, form.getPhotoAfterBase64());
		if (form.getPhotoInspectionBase64() != null)
			imageCache.put(keyInspection, form.getPhotoInspectionBase64());
		if (form.getPhotoSafetyBase64() != null)
			imageCache.put(keySafety, form.getPhotoSafetyBase64());

		// 編集確認画面用の引き換えキーをセッションに保管
		session.setAttribute("edit_key_Before", keyBefore);
		session.setAttribute("edit_key_During", keyDuring);
		session.setAttribute("edit_key_After", keyAfter);
		session.setAttribute("edit_key_Inspection", keyInspection);
		session.setAttribute("edit_key_Safety", keySafety);

		model.addAttribute("dailyreport", form);
		model.addAttribute("siteList", siteService.selectAll());
		model.addAttribute("headerSiteId", form.getSiteId());

		return "nishikigi/dailyeditcheck";
	}

	/**
	 * 変更された日報データをデータベースに正式に更新（UPDATE）する
	 */
	@PostMapping("/{id}/update")
	public String updateReport(@ModelAttribute("dailyreport") DailyreportForm form, HttpSession session) {
		String redirect = checkManagerOnly(session);
		if (redirect != null)
			return redirect;
		if (form.getSiteId() != null) {
			String accessRedirect = checkSiteAccess(session, form.getSiteId());
			if (accessRedirect != null)
				return accessRedirect;
		}

		// 元々登録されていた古いレコードデータを一度DBからロードして取得
		Dailyreport report = dailyreportService.getReportById(form.getReportId());

		// セッションから編集用の画像引き換え券を取り出し、Mapから本物の画像データ（Base64文字列）を回収
		String keyBefore = (String) session.getAttribute("edit_key_Before");
		String keyDuring = (String) session.getAttribute("edit_key_During");
		String keyAfter = (String) session.getAttribute("edit_key_After");
		String keyInspection = (String) session.getAttribute("edit_key_Inspection");
		String keySafety = (String) session.getAttribute("edit_key_Safety");

		if (keyBefore != null)
			form.setPhotoBeforeBase64(imageCache.remove(keyBefore));
		if (keyDuring != null)
			form.setPhotoDuringBase64(imageCache.remove(keyDuring));
		if (keyAfter != null)
			form.setPhotoAfterBase64(imageCache.remove(keyAfter));
		if (keyInspection != null)
			form.setPhotoInspectionBase64(imageCache.remove(keyInspection));
		if (keySafety != null)
			form.setPhotoSafetyBase64(imageCache.remove(keySafety));

		// セッションに残った不要な引き換え券情報をお掃除
		session.removeAttribute("edit_key_Before");
		session.removeAttribute("edit_key_During");
		session.removeAttribute("edit_key_After");
		session.removeAttribute("edit_key_Inspection");
		session.removeAttribute("edit_key_Safety");

		// 回収して揃った新しいデータをEntityへ詰め替えて上書きし、DBを更新（UPDATE）実行
		copyFormToEntity(form, report);
		Dailyreport result = dailyreportService.updateReport(report);
		if (result == null)
			return "error/404";

		return "redirect:/complete";
	}

	// ==========================================
	//                日報：本社確認機能
	// ==========================================

	/**
	 * 本社ユーザーが日報を「確認済み」ステータスに変更する
	 */
	@PostMapping("/{id}/confirm")
	public String confirmReport(@PathVariable("id") Integer reportId,
			@RequestParam(name = "from", required = false, defaultValue = "list") String from, HttpSession session) {
		String redirect = checkHonshaOnly(session);
		if (redirect != null)
			return redirect;

		// サービス層を呼び出し、DB内の確認完了ステータスフラグを 0(未確認) から 1(確認済) に上書き書き換え
		boolean isSuccess = dailyreportService.confirmReport(reportId);
		if (!isSuccess)
			return "error/404";

		// WebSocket通信にて、接続中の現場等の画面へリアルタイムリロード通知を配信
		messagingTemplate.convertAndSend(
				"/topic/notice",
				"{\"type\":\"reload\"}");

		// 本社ホーム(homeoffice)のダッシュボードから押されたならホームへ、一覧からなら日報一覧へ綺麗に戻す分岐
		return "home".equals(from) ? "redirect:/homeoffice" : "redirect:/dailyreport/list";
	}

	// ==========================================
	//            データ型変換・共通ヘルパー
	// ==========================================

	/**
	 * Formクラス(画面入力用モデル)からEntityクラス(DB保存用モデル)へデータを安全に詰め替えるマッピング処理
	 */
	private void copyFormToEntity(DailyreportForm form, Dailyreport report) {
		Site site = new Site();
		site.setSiteId(form.getSiteId());
		report.setSite(site); // 外部キーリレーションを接続

		report.setReportId(form.getReportId());
		report.setProjectNumber(form.getProjectNumber()); // 工事番号の詰め替え
		report.setTargetDate(form.getTargetDate());
		report.setWeather(form.getWeather());
		report.setTemperature(form.getTemperature());
		report.setWorkDetails(form.getWorkDetails());
		report.setWorkerDetails(form.getWorkerDetails());
		report.setProgressPercent(form.getProgressPercent());

		// 画面用のBase64文字列（String）を、DB保存用のバイナリ配列（byte[]）にデコード逆変換してエンティティに同期
		report.setPhotoBefore(decodeBase64(form.getPhotoBeforeBase64()));
		report.setPhotoDuring(decodeBase64(form.getPhotoDuringBase64()));
		report.setPhotoAfter(decodeBase64(form.getPhotoAfterBase64()));
		report.setPhotoInspection(decodeBase64(form.getPhotoInspectionBase64()));
		report.setPhotoSafety(decodeBase64(form.getPhotoSafetyBase64()));

		report.setSafetyKy(form.getSafetyKy());
		report.setSafetyMeasure(form.getSafetyMeasure());
		report.setSafetyEducation(form.getSafetyEducation());
		report.setNearMiss(form.getNearMiss());
		report.setMaterials(form.getMaterials());
		report.setEquipments(form.getEquipments());
		report.setDesignChange(form.getDesignChange());
		report.setCustomerRequest(form.getCustomerRequest());
		report.setPartnerCoordination(form.getPartnerCoordination());
		report.setNextDaySchedule(form.getNextDaySchedule());
		report.setNotes(form.getNotes());
		report.setDStatusFlag(form.getDStatusFlag());
	}

	/**
	 * アップロードされた画像ファイル（MultipartFile）をBase64文字列にエンコード変換する
	 */
	private String convertToBase64(MultipartFile file, String existingBase64) {
		if (file != null && !file.isEmpty()) {
			try {
				return Base64.getEncoder().encodeToString(file.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return existingBase64; // 新しいファイルのアップロードが空であれば、既存の画像をそのまま引き継いで維持
	}

	/**
	 * Base64文字列を解読してbyte[]バイナリに戻す（DB保存前のデコード処理）
	 */
	private byte[] decodeBase64(String base64) {
		if (base64 != null && !base64.isEmpty()) {
			return Base64.getDecoder().decode(base64);
		}
		return null;
	}

	/**
	 * byte[]バイナリをBase64文字列に変換する（HTML画面表示前のエンコード処理）
	 */
	private String encodeBase64(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			return Base64.getEncoder().encodeToString(bytes);
		}
		return null;
	}
}