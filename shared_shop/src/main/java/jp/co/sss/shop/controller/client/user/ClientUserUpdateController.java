package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
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

	/**
	 * 変更ボタン・確認画面-戻るボタン押下時処理
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String inputUpdatePost(HttpSession session) {
		//セッションスコープに入力フォーム情報があるか確認
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");

		//セッションにデータがない場合DBから取得
		if (userForm == null) {

			//ログイン中のユーザーIDをセッションスコープから取得
			UserBean loginUser = (UserBean) session.getAttribute("user");

			//一般会員変更はセッションに保存されたIDを使用し、変更対象のデータをDBから取得
			User user = userRepository.getReferenceById(loginUser.getId());

			//取得データを元に入力画面初期表示用の入力フォーム情報を新規生成
			userForm = new UserForm();
			userForm.setId(user.getId());
			userForm.setEmail(user.getEmail());
			userForm.setPassword(user.getPassword());
			userForm.setName(user.getName());
			userForm.setPostalCode(user.getPostalCode());
			userForm.setAddress(user.getAddress());
			userForm.setPhoneNumber(user.getPhoneNumber());

			//入力フォーム情報をセッションスコープに保存
			session.setAttribute("updateUserForm", userForm);
		}

		// 変更入力画面表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 変更入力画面表示処理
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String inputUpdateGet(Model model, HttpSession session) {
		//セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");

		//入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		//変更入力画面表示
		return "client/user/update_input";
	}

	/**
	 * 確認ボタン押下時処理
	 * @param userForm
	 * @param result
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String checkUpdatePost(@Valid @ModelAttribute UserForm userForm, BindingResult result, HttpSession session) {
		//入力エラーがある場合の処理
		if (result.hasErrors()) {
			return "client/user/update_input";
		}

		//画面から入力された入力フォームをセッションスコープに入力フォーム情報として保存
		session.setAttribute("updateUserForm", userForm);

		//変更確認画面表示処理にリダイレクト
		return "redirect:/client/user/update/check";
	}

	/**
	 * 変更確認画面表示処理
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String checkUpdateGet(Model model, HttpSession session) {
		//セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");

		// セッションが切れていた場合は入力画面へ戻す
		if (userForm == null) {
			return "redirect:/client/user/update/input";
		}

		//入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		//変更確認画面表示
		return "client/user/update_check";
	}

	/**
	 * 登録ボタン押下時処理
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String completeUpdatePost(HttpSession session) {
		//セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");
		if (userForm == null) {
			return "redirect:/client/user/update/input";
		}

		//入力フォーム情報を元にDB登録用エンティティオブジェクトを生成（取得して上書き）
		User user = userRepository.getReferenceById(userForm.getId());
		user.getName();
		user.setEmail(userForm.getEmail());
		user.setPassword(userForm.getPassword());
		user.setName(userForm.getName());
		user.setPostalCode(userForm.getPostalCode());
		user.setAddress(userForm.getAddress());
		user.setPhoneNumber(userForm.getPhoneNumber());

		//DB更新実施
		userRepository.save(user);

		//セッションスコープの入力フォーム情報削除
		session.removeAttribute("updateUserForm");

		//セッションスコープの会員情報を更新
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser != null) {
			//最新の情報をコピー
			BeanUtils.copyProperties(user, loginUser);
			// セッションスコープを最新情報で上書き
			session.setAttribute("user", loginUser);
		}

		//変更完了画面表示処理にリダイレクト
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面の表示処理
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String completeUpdateGet() {
		// ・登録完了画面表示
		return "client/user/update_complete";
	}

}
