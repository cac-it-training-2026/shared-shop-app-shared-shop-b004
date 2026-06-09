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

	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	public String userDetail(HttpSession session, Model model) {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		User user = repository.getReferenceById(loginUser.getId());

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}
}