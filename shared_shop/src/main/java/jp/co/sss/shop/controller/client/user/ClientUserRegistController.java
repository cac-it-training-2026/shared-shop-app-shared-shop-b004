package jp.co.sss.shop.controller.client.user;

import java.sql.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
//@RequestMapping("/client/user/regist")
public class ClientUserRegistController {

	private final UserRepository userRepository;

	public ClientUserRegistController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 入力画面遷移→inputへ
	 * URL：/client/user/regist/input/init
	 */
	@RequestMapping("/client/user/regist/input/init")
	public String showUserInputInit(Model model) {

		// input.htmlを表示
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面表示(GET/POST)
	 * URL： /input
	 */
	@RequestMapping(path = "/client/user/regist/input", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUserInput(Model model) {

		// 空のFormを画面に渡す（フォーム入力用）
		model.addAttribute("userForm", new UserForm());

		// input.htmlを表示
		return "client/user/regist_input";
	}

	/**
	 * 確認画面(POST)
	 * URL： /check
	 */
	@PostMapping("/client/user/regist/check")
	public String confirmUser(@Valid @ModelAttribute UserForm form, BindingResult result, Model model,
			HttpSession session) {

		//入力チェック
		if (result.hasErrors()) {
			return "client/user/regist_input";
		}
		// セッション保存
		session.setAttribute("userForm", form);

		// 受け取った入力値をそのまま確認画面に渡す
		model.addAttribute("userForm", form);

		// 確認画面を表示
		return "client/user/regist_check";

	}

	/**
	 * 登録処理（POST）
	 * URL： /complete
	 */
	@PostMapping("/client/user/regist/complete")
	public String completeUserRegistration(HttpSession session) {

		// セッションから取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		//直接アクセス対策
		if (form == null) {
			return "redirect:/client/user/regist/input";
		}

		User user = new User();

		// 入力された値をセット 
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());

		user.setAuthority(2); // 権限（一般ユーザーは2）
		user.setDeleteFlag(0); // 削除フラグ（0 = 未削除）
		user.setInsertDate(new Date(System.currentTimeMillis())); // 登録日時

		// DBに保存
		userRepository.save(user);

		// セッション削除
		session.removeAttribute("userForm");

		// 完了画面へ
		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 完了画面表示（GET）
	 * URL：/client/user/regist/complete
	 */

	@GetMapping("/client/user/regist/complete")
	public String showCompletePage() {
		return "client/user/regist_complete";
	}

}
