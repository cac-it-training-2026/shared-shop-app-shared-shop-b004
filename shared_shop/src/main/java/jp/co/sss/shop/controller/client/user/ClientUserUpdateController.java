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
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserUpdateController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String inputUpdatePost() {
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");
		if (userForm == null) {
			Integer loginUserId = (Integer) session.getAttribute("loginUserId");
			User user = userRepository.findByIdAndDeleteFlag(loginUserId, 0);
			userForm = new UserForm();
			userForm.setId(user.getId());
			userForm.setEmail(user.getEmail());
			userForm.setPassword(user.getPassword());
			userForm.setName(user.getName());
			userForm.setPostalCode(user.getPostalCode());
			userForm.setAddress(user.getAddress());
			userForm.setPhoneNumber(user.getPhoneNumber());
			session.setAttribute("updateUserForm", userForm);
		}

		return "redirect:/client/user/update/input";
	}

	@RequestMapping(path = "/client/user/update/input")
	public String inputUpdateGet(Model model) {
		UserForm userForm = (UserForm) session.getAttribute("updateUserForm");
		if (userForm != null) {
			model.addAttribute("userForm", userForm);
		}
		return "client/user/update_input";
	}

	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String checkUpdatePost(@Valid @ModelAttribute UserForm userForm, BindingResult result) {
		if (result.hasErrors()) {
			return "client/user/update_input";
		}

		session.setAttribute("updateUserForm", userForm);
		return "redirect:/client/user/update/check";
	}

}
