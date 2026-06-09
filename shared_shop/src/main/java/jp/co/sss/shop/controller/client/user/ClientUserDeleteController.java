package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;

@Controller
public class ClientUserDeleteController {

	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String userCheck() {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		UserBean userForm = new UserBean();
		BeanUtils.copyProperties(loginUser, userForm);

		session.setAttribute("userForm", userForm);

		return "redirect:/client/user/delete/check";
	}

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String userCheck2(Model model) {

		UserBean userForm = (UserBean) session.getAttribute("userForm");

		model.addAttribute("userForm", userForm);

		return "client/user/delete_check";

	}
}
