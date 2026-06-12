package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientUserDeleteController {

	@Autowired
	UserRepository repository;

	@Autowired
	HttpSession session;

	// 退会確認画面表示
	/**
	 * @return
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String userCheck() {

		//セッションからログイン中の会員情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		//DBから未削除の会員情報を取得
		User user = repository.findByIdAndDeleteFlag(
				loginUser.getId(),
				Constant.NOT_DELETED);

		//画面表示用Beanへコピー
		UserBean userForm = new UserBean();
		BeanUtils.copyProperties(user, userForm);

		//セッションスコープへ登録
		session.setAttribute("userForm", userForm);

		//GETメソッドへリダイレクト
		return "redirect:/client/user/delete/check";
	}

	// 退会確認画面
	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String userDeletecheck(Model model) {

		//セッションから会員情報を取得
		UserBean userForm = (UserBean) session.getAttribute("userForm");

		//リクエストスコープへ登録
		model.addAttribute("userForm", userForm);

		//退会確認画面を表示
		return "client/user/delete_check";
	}

	//会員退会処理
	/**
	 * @return
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String userComplete() {

		//セッションから会員情報を取得
		UserBean userForm = (UserBean) session.getAttribute("userForm");

		//DBから対象会員情報を取得
		User user = repository.findById(userForm.getId()).get();

		//削除フラグ削除済みに変更
		user.setDeleteFlag(Constant.DELETED);

		//会員情報を更新
		repository.save(user);

		//買い物かご情報を削除
		//session.removeAttribute("basketBeans");

		//セッション内の会員情報を削除
		//session.removeAttribute("userForm");

		//セッションを破棄し未ログイン状態にする
		session.invalidate();

		//退会完了画面へリダイレクト
		return "redirect:/client/user/delete/complete";
	}

	// 退会完了画面
	/**
	 * @return
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String userDeleteComplete() {

		return "client/user/delete_complete";
	}
}