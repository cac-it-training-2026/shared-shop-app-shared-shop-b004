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
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String userCheck() {

		UserBean loginUser = (UserBean) session.getAttribute("user");
		User user = repository.findByIdAndDeleteFlag(
				loginUser.getId(),
				Constant.NOT_DELETED);

		UserBean userForm = new UserBean();
		BeanUtils.copyProperties(user, userForm);

		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/delete/check";
	}

	// 退会確認画面
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String userCheck2(Model model) {

		UserBean userForm = (UserBean) session.getAttribute("userForm");

		model.addAttribute("userForm", userForm);

		return "client/user/delete_check";
	}

	// 退会処理
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String userComplete() {

		UserBean userForm = (UserBean) session.getAttribute("userForm");

		User user = repository.findById(userForm.getId()).get();

		user.setDeleteFlag(Constant.DELETED);

		repository.save(user);

		session.removeAttribute("userForm");
		session.invalidate();

		return "redirect:/client/user/delete/complete";
	}

	// 退会完了画面
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String userComplete2() {

		return "client/user/delete_complete";
	}
}