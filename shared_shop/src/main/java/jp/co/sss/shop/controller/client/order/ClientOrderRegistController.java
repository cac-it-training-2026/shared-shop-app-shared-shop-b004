package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    //入力された届け先住所の形式チェックを行い、成功時はセッションに保存して支払い方法入力画面を表示。
    @PostMapping("/client/order/payment/input")
    public String showPaymentForm(
            @RequestParam(name = "postalCode") String postalCode,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "phoneNumber") String phoneNumber,
            HttpSession session, Model model) {

        // ログインチェック
        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 注文フォームを新規生成
        OrderBean orderForm = new OrderBean();
        orderForm.setPostalCode(postalCode);
        orderForm.setAddress(address);
        orderForm.setName(name);
        orderForm.setPhoneNumber(phoneNumber);

        // 住所が未入力、または空白のみの場合
        if (address == null || address.trim().isEmpty()) {
            model.addAttribute("orderForm", orderForm);
            model.addAttribute("categoryList", categoryRepository.findAll());
            return "client/order/address_input"; 
        }

        // 正常系ルート
        if (user != null) {
            orderForm.setUserName(user.getName());
        }
        session.setAttribute("orderForm", orderForm);
        model.addAttribute("payMethod", orderForm.getPayMethod());
        model.addAttribute("categoryList", categoryRepository.findAll());

        return "client/order/payment_input";
    }

}
