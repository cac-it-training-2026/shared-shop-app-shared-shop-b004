package jp.co.sss.shop.controller.client;

import java.sql.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
@RequestMapping("/client/user/regist")
public class ClientUserRegistController {

	private final UserRepository userRepository;

	public ClientUserRegistController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * 入力画面表示
	 * URL：GET /input/init
	 */
	@GetMapping("/input/init")
	public String showUserInput(Model model) {

		// 空のFormを画面に渡す（フォーム入力用）
		model.addAttribute("userForm", new UserForm());

		// input.htmlを表示
		return "client/user/regist_input";
	}

	/**
	 * 確認画面
	 * URL：POST /check
	 */
	@PostMapping("/check")
	public String confirmUser(@ModelAttribute UserForm form, Model model) {

		// 受け取った入力値をそのまま確認画面に渡す
		model.addAttribute("userForm", form);

		// confirm.htmlを表示
		return "client/user/regist_check";
	}

	/**
	 * 登録処理（完了画面）
	 * URL：POST /complete
	 */
	@PostMapping("/complete")
	public String completeUserRegistration(@ModelAttribute UserForm form) {

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

		// 完了画面へ
		return "client/user/regist_complete";
	}

}
