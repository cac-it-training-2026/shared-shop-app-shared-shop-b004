//package jp.co.sss.shop.controller.client.user;
//
//import java.sql.Date;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import jp.co.sss.shop.entity.User;
//import jp.co.sss.shop.form.UserForm;
//import jp.co.sss.shop.repository.UserRepository;
//
//@Controller
////@RequestMapping("/client/user/regist")
//public class ClientUserRegistController {
//
//	private final UserRepository userRepository;
//
//	public ClientUserRegistController(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}
//
//	/**
//	 * 入力画面遷移→inputへ
//	 * URL：/client/user/regist/input/init
//	 */
//	@RequestMapping("/client/user/regist/input/init")
//	public String showUserInputInit(Model model) {
//
//		// input.htmlを表示
//		return "redirect:/client/user/regist/input";
//	}
//
//	/**
//	 * 入力画面表示(GET/POST)
//	 * URL： /input
//	 */
//	@RequestMapping(path = "/client/user/regist/input", method = { RequestMethod.GET, RequestMethod.POST })
//	public String showUserInput(Model model) {
//
//		// 空のFormを画面に渡す（フォーム入力用）
//		model.addAttribute("userForm", new UserForm());
//
//		// input.htmlを表示
//		return "client/user/regist_input";
//	}
//
//	/**
//	 * 確認画面(POST)
//	 * URL： /check
//	 */
//	@PostMapping("/client/user/regist/check")
//	public String confirmUser(@Valid @ModelAttribute UserForm form, BindingResult result, Model model,
//			HttpSession session) {
//
//		//入力チェック
//		if (result.hasErrors()) {
//			return "client/user/regist_input";
//		}
//		// セッション保存
//		session.setAttribute("userForm", form);
//
//		// 受け取った入力値をそのまま確認画面に渡す
//		model.addAttribute("userForm", form);
//
//		// 確認画面を表示
//		return "client/user/regist_check";
//
//	}
//
//	/**
//	 * 登録処理（POST）
//	 * URL： /complete
//	 */
//	@PostMapping("/client/user/regist/complete")
//	public String completeUserRegistration(HttpSession session) {
//
//		// セッションから取得
//		UserForm form = (UserForm) session.getAttribute("userForm");
//
//		//直接アクセス対策
//		if (form == null) {
//			return "redirect:/client/user/regist/input";
//		}
//
//		User user = new User();
//
//		// 入力された値をセット 
//		user.setEmail(form.getEmail());
//		user.setPassword(form.getPassword());
//		user.setName(form.getName());
//		user.setPostalCode(form.getPostalCode());
//		user.setAddress(form.getAddress());
//		user.setPhoneNumber(form.getPhoneNumber());
//
//		user.setAuthority(2); // 権限（一般ユーザーは2）
//		user.setDeleteFlag(0); // 削除フラグ（0 = 未削除）
//		user.setInsertDate(new Date(System.currentTimeMillis())); // 登録日時
//
//		// DBに保存
//		userRepository.save(user);
//
//		// セッション削除
//		session.removeAttribute("userForm");
//
//		// 完了画面へ
//		return "redirect:/client/user/regist/complete";
//	}
//
//	/**
//	 * 完了画面表示（GET）
//	 * URL：/client/user/regist/complete
//	 */
//
//	@GetMapping("/client/user/regist/complete")
//	public String showCompletePage() {
//		return "client/user/regist_complete";
//	}
//
//}
//package jp.co.sss.shop.controller.client.user;
//
//import java.sql.Date;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import jp.co.sss.shop.entity.User;
//import jp.co.sss.shop.form.UserForm;
//import jp.co.sss.shop.repository.UserRepository;
//
//@Controller
//public class ClientUserRegistController {
//
//	private final UserRepository userRepository;
//
//	public ClientUserRegistController(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}
//
//	/**
//	 * 処理1
//	 * 新規登録リンク押下
//	 */
//	@GetMapping("/client/user/regist/input/init")
//	public String showUserInputInit(HttpSession session) {
//
//		UserForm form = new UserForm();
//
//		session.setAttribute("userForm", form);
//
//		return "redirect:/client/user/regist/input";
//	}
//
//	@PostMapping("/client/user/regist/input/init")
//	public String returnFromCheck(HttpSession session) {
//
//		if (session.getAttribute("userForm") == null) {
//			session.setAttribute("userForm", new UserForm());
//		}
//
//		return "redirect:/client/user/regist/input";
//	}
//
//	/**
//	 * 処理2
//	 * 戻るボタン押下
//	 */
//	//	@PostMapping("/client/user/regist/input")
//	//	public String returnUserInput(
//	//			@ModelAttribute UserForm form,
//	//			HttpSession session) {
//	//
//	//		UserForm sessionForm = (UserForm) session.getAttribute("userForm");
//	//
//	//		if (sessionForm == null) {
//	//			sessionForm = new UserForm();
//	//		}
//	//
//	//		sessionForm.setEmail(form.getEmail());
//	//		sessionForm.setPassword(form.getPassword());
//	//		sessionForm.setName(form.getName());
//	//		sessionForm.setPostalCode(form.getPostalCode());
//	//		sessionForm.setAddress(form.getAddress());
//	//		sessionForm.setPhoneNumber(form.getPhoneNumber());
//	//
//	//		session.setAttribute("userForm", sessionForm);
//	//
//	//		return "redirect:/client/user/regist/input";
//	//	}
//	@PostMapping("/client/user/regist/input")
//	public String returnUserInput(HttpSession session) {
//		return "redirect:/client/user/regist/input";
//	}
//
package jp.co.sss.shop.controller.client.user;

import java.sql.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {

	private final UserRepository userRepository;

	public ClientUserRegistController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 初期表示
	 */
	@GetMapping("/client/user/regist/input/init")
	public String showUserInputInit(HttpSession session) {

		// 新規登録用のフォームをセッションに初期化して保持する
		session.setAttribute("userForm", new UserForm());

		// 入力画面へリダイレクト
		return "redirect:/client/user/regist/input";
	}

	/**
	 * init POST
	 */
	@PostMapping("/client/user/regist/input/init")
	public String returnFromCheck(HttpSession session) {

		// 既存の入力情報は保持したまま入力画面へ戻す（再初期化はしない）
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面表示
	 */
	@GetMapping("/client/user/regist/input")
	public String showUserInput(Model model, HttpSession session) {

		// セッションから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// 初回アクセス時はフォームを新規作成してセッションに保存
		if (form == null) {
			form = new UserForm();
			session.setAttribute("userForm", form);
		}

		// 画面表示用にモデルへ設定
		model.addAttribute("userForm", form);

		// バリデーションエラー情報がある場合は画面へ引き継ぐ
		Object errors = session.getAttribute("errors");

		// セッションに保存されていた入力チェックエラー情報を取得し、モデルに設定する
		if (errors != null) {
			model.addAttribute(
					"org.springframework.validation.BindingResult.userForm",
					errors);
			//エラーをセッションから削除
			session.removeAttribute("errors");
		}

		// デバッグ用（セッション内パスワード確認）
		//		System.out.println("password = " + ((UserForm) session.getAttribute("userForm")).getPassword());

		return "client/user/regist_input";
	}

	/**
	 * 確認処理
	 */
	@PostMapping("/client/user/regist/check")
	public String confirmUser(
			@Valid @ModelAttribute("userForm") UserForm form,
			BindingResult result,
			HttpSession session) {

		// セッションから既存の入力情報を取得
		UserForm sessionForm = (UserForm) session.getAttribute("userForm");

		// セッション未作成の場合は新規作成
		if (sessionForm == null) {
			sessionForm = new UserForm();
		}

		// 画面入力値をセッション用フォームへ反映
		sessionForm.setEmail(form.getEmail());
		sessionForm.setPassword(form.getPassword());
		sessionForm.setName(form.getName());
		sessionForm.setPostalCode(form.getPostalCode());
		sessionForm.setAddress(form.getAddress());
		sessionForm.setPhoneNumber(form.getPhoneNumber());

		// 更新したフォーム情報をセッションへ保存
		session.setAttribute("userForm", sessionForm);

		// 入力エラーがある場合は入力画面へ戻す
		if (result.hasErrors()) {
			session.setAttribute("errors", result);
			return "redirect:/client/user/regist/input";
		}

		// エラーがない場合は確認画面へ遷移
		return "redirect:/client/user/regist/check";
	}

	/**
	 * 確認画面
	 */
	@GetMapping("/client/user/regist/check")
	public String showCheckPage(Model model, HttpSession session) {

		// セッションから入力情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// セッションが存在しない場合は入力画面へ戻す
		if (form == null) {
			return "redirect:/client/user/regist/input";
		}

		// 確認画面表示用にモデルへ設定
		model.addAttribute("userForm", form);

		return "client/user/regist_check";
	}

	/**
	 * 登録処理
	 */
	@PostMapping("/client/user/regist/complete")
	public String completeUserRegistration(HttpSession session) {

		// セッションから入力情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// セッションが存在しない場合は入力画面へ戻す
		if (form == null) {
			return "redirect:/client/user/regist/input";
		}

		// エンティティ作成
		User user = new User();

		// フォーム情報をエンティティへ設定
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());

		// 権限・削除フラグ・登録日時を設定
		user.setAuthority(2);
		user.setDeleteFlag(0);
		user.setInsertDate(new Date(System.currentTimeMillis()));

		// DBへ保存
		userRepository.save(user);

		// セッションの入力情報を削除
		session.removeAttribute("userForm");

		// ログイン状態としてユーザ情報をセッションへ保持
		session.setAttribute("user", user);

		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 完了画面
	 */
	@GetMapping("/client/user/regist/complete")
	public String showCompletePage() {

		// 完了画面を表示
		return "client/user/regist_complete";
	}

}