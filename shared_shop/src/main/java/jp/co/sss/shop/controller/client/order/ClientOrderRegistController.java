package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientOrderRegistController {
	
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository; 

    @Autowired
    private UserRepository userRepository; 
    
    @Autowired
    private CategoryRepository categoryRepository;


    //会員情報を取得し、届け先入力欄の初期値として設定して画面を表示。
    @PostMapping("/client/order/address/input")
    public String showAddressForm(HttpSession session, Model model) {

        //ログインチェック
    	UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
			return "redirect:/login";
		}

        //空の注文フォームを生成
        OrderBean orderForm = new OrderBean();

        //データベースから最新のユーザー情報を取得
        User dbUser = userRepository.findByIdAndDeleteFlag(user.getId(), 0); 
        //デフォルトの配送先情報をフォームに設定する
        if (dbUser != null) {
        	
            orderForm.setPostalCode(dbUser.getPostalCode());
            orderForm.setAddress(dbUser.getAddress());
            orderForm.setName(dbUser.getName());
            orderForm.setPhoneNumber(dbUser.getPhoneNumber()); 
        }

        model.addAttribute("orderForm", orderForm);
        model.addAttribute("categoryList", categoryRepository.findAll());

        return "client/order/address_input";
    }

}
