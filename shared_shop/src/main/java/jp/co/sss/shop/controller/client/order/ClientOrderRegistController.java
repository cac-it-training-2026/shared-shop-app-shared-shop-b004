package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
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
        if (!StringUtils.hasText(address)) {
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
    
    // 選択された支払い方法を保存。最新の在庫状況から小計・合計金額を算出し、注文確認画面を表示。
    @PostMapping("/client/order/check")
    public String showOrderConfirm(@RequestParam(name = "payMethod") int payMethod, HttpSession session, Model model) {

        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        OrderBean orderForm = (OrderBean) session.getAttribute("orderForm");
        if (orderForm != null) {
            orderForm.setPayMethod(payMethod);
        }

        List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
        
        List<OrderItemBean> orderItemBeans = new ArrayList<>();
        int subtotal = 0;

        if (basket != null && !basket.isEmpty()) {
            for (int i = 0; i < basket.size(); i++) {
                BasketBean currentBean = basket.get(i);
                
                Item dbItem = itemRepository.findByIdAndDeleteFlag(currentBean.getId(), 0);
                
                if (dbItem == null || dbItem.getStock() == 0) {
                    basket.remove(i);
                    i--;
                    model.addAttribute("message", currentBean.getName() + "ただ今売れ切りました。");
                } else {
                	
                    OrderItemBean orderItem = new OrderItemBean();
                    
                    orderItem.setId(dbItem.getId());           
                    orderItem.setName(dbItem.getName());       
                    orderItem.setPrice(dbItem.getPrice());     
                    orderItem.setImage(dbItem.getImage());     
                    orderItem.setOrderNum(currentBean.getOrderNum()); 
                    
                    int itemSubtotal = dbItem.getPrice() * currentBean.getOrderNum();
                    orderItem.setSubtotal(itemSubtotal);
                    
                    orderItemBeans.add(orderItem);
                    
                    subtotal += itemSubtotal;
                }
            }
            session.setAttribute("basketBeans", basket);
        }

        if (orderForm != null) {
            orderForm.setTotal(subtotal);
            session.setAttribute("orderForm", orderForm);
        }

        model.addAttribute("orderItemBeans", orderItemBeans); 
        model.addAttribute("total", subtotal);               
        model.addAttribute("subtotal", subtotal);            
        model.addAttribute("categoryList", categoryRepository.findAll());
        model.addAttribute("orderForm", orderForm);

        return "client/order/check";
    }
    
    // 注文情報と注文商品情報をデータベースに登録。商品の在庫数を減らし、買い物かごを空にする。
    @PostMapping("/client/order/complete")
    public String registerOrder(HttpSession session) {

        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        OrderBean orderForm = (OrderBean) session.getAttribute("orderForm");
        List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");

        if (orderForm != null && basket != null && !basket.isEmpty()) {
            
            for (BasketBean bean : basket) {
                Item dbItem = itemRepository.findByIdAndDeleteFlag(bean.getId(), 0);
                if (dbItem != null) {
                    dbItem.setStock(dbItem.getStock() - bean.getOrderNum());
                    itemRepository.save(dbItem); 
                }
            }

            Order newOrder = new Order();
            newOrder.setPostalCode(orderForm.getPostalCode());
            newOrder.setAddress(orderForm.getAddress());
            newOrder.setName(orderForm.getName());
            newOrder.setPhoneNumber(orderForm.getPhoneNumber());
            newOrder.setPayMethod(orderForm.getPayMethod());
            
            User dbUser = userRepository.findByIdAndDeleteFlag(user.getId(), 0);
            newOrder.setUser(dbUser);
            
            orderRepository.save(newOrder);

            session.removeAttribute("basketBeans");
            session.removeAttribute("orderForm");
        }

        return "redirect:/client/order/complete";
    }

    //　注文完成画面
    @GetMapping("/client/order/complete")
    public String showOrderCompletePage(HttpSession session, Model model) {
        
        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("categoryList", categoryRepository.findAll());

        return "client/order/complete";
    }
    
    @GetMapping("/client/order/address/input")
    public String showAddressFormByGet(HttpSession session, Model model) {
    	
        UserBean user = (UserBean) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        OrderBean orderForm = (OrderBean) session.getAttribute("orderForm");

        if (orderForm == null) {
            orderForm = new OrderBean();
            User dbUser = userRepository.findByIdAndDeleteFlag(user.getId(), 0); 
            if (dbUser != null) {
                orderForm.setPostalCode(dbUser.getPostalCode());
                orderForm.setAddress(dbUser.getAddress());
                orderForm.setName(dbUser.getName());
                orderForm.setPhoneNumber(dbUser.getPhoneNumber()); 
            }
        }

        model.addAttribute("orderForm", orderForm);
        model.addAttribute("categoryList", categoryRepository.findAll());

        return "client/order/address_input";
    }

    @PostMapping("/client/basket/list")
    public String handleBasketListBack() {
    	
        return "redirect:/client/basket/list";
    }

    @PostMapping("/client/order/payment/back")
    public String handlePaymentBackToAddressInput() {

        return "redirect:/client/order/address/input";
    }


}
