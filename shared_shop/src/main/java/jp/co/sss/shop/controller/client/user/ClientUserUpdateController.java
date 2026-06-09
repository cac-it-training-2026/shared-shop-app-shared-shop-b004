package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserUpdateController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * 変更ボタン・戻るボタン押下時処理
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String inputUpdatePost() {
		// セッションから作業中のフォーム情報があるか確認
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");

		// セッションにデータがない場合DBから取得
		if (userForm == null) {

			// ログイン中のユーザーIDをセッションから取得
			UserBean loginUser = (UserBean) session.getAttribute("user");

			// 取得したIDを使ってDBから現在の会員情報を取得
			User user = userRepository.getReferenceById(loginUser.getId());

			//DBのデータをセット
			userForm = new UserForm();
			userForm.setId(user.getId());
			userForm.setEmail(user.getEmail());
			userForm.setPassword(user.getPassword());
			userForm.setName(user.getName());
			userForm.setPostalCode(user.getPostalCode());
			userForm.setAddress(user.getAddress());
			userForm.setPhoneNumber(user.getPhoneNumber());

			//フォーム情報をセッションに保存
			session.setAttribute("updateUserForm", userForm);
		}

		//GETメソッドへリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 変更入力画面の表示処理
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String inputUpdateGet(Model model) {
		//セッションに保存されているフォーム情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");

		//リクエストスコープに保存
		model.addAttribute("userForm", userForm);

		// 入力画面のHTMLを表示
		return "client/user/update_input";
	}

	/**
	 * 入力チェック処理
	 * @param userForm
	 * @param result
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String checkUpdatePost(@Valid @ModelAttribute UserForm userForm, BindingResult result) {
		//入力エラーがあった場合の処理
		if (result.hasErrors()) {
			return "client/user/update_input";
		}

		//画面から送られてきた最新の入力値でセッションの情報を上書き保存
		session.setAttribute("updateUserForm", userForm);

		//GETメソッドへリダイレクト
		return "redirect:/client/user/update/check";
	}

}
