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

@Controller
public class ClientUserShowController {

	@Autowired
	UserRepository repository;

	// 会員詳細画面表示
	/**
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String userDetail(HttpSession session, Model model) {
		//セッションからログイン中の会員情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// セッション切れなどにより会員情報が取得できない場合はログイン画面へ遷移
		if (loginUser == null) {
			return "redirect:/login";
		}

		//DBから最新の会員情報を取得
		User user = repository.getReferenceById(loginUser.getId());
		//画面表示用Beanへコピー
		UserBean userBean = new UserBean();

		BeanUtils.copyProperties(user, userBean);
		//リクエストスコープへ登録
		model.addAttribute("userBean", userBean);
		//会員詳細画面へ遷移
		return "client/user/detail";
	}
}