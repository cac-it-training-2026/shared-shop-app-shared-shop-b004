package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;

@Controller
public class ClientBasketController {
	
	@Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
	HttpSession session;

    //セッションから買い物かごを取得。在庫チェックを行い、最後に追加された順で画面に表示。空の場合はメッセージを設定。
    @GetMapping("/client/basket/list")
    public String showBasketList(HttpSession session, Model model) {
    	
    	//ログインチェック
    	UserBean user = (UserBean) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login"; 
		}

    	// セッションからカート情報を取得
    	List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
        
     // カートが空の場合、新規作成してセッションに保存
    	if (basket == null || basket.isEmpty()) {
            session.removeAttribute("basketBeans");
        } else {
            // 商品の在庫チェック
            List<String> itemNameListZero = new ArrayList<>();
            List<String> itemNameListLessThan = new ArrayList<>();

            for (BasketBean currentBean : basket) {
                // 最新の在庫情報をデータベースから取得
                Item dbItem = itemRepository.findById(currentBean.getId()).orElse(null);
                if (dbItem != null) {
                	
                    //最新在庫をBeanに保存
                	currentBean.setStock(dbItem.getStock());

                    // チェック：在庫数切れの場合
                    if (dbItem.getStock() == 0) {
                        itemNameListZero.add(currentBean.getName());
                    } 
                    //チェック：在庫数＜買い物かごの個数となる場合
                    else if (dbItem.getStock() < currentBean.getOrderNum()) {
                        itemNameListLessThan.add(currentBean.getName());
                    }
                }
            }

            // Listでエラーがある場合のみ${itemNameListLessThan != null} をtureにする
            if (!itemNameListZero.isEmpty()) {
                model.addAttribute("itemNameListZero", itemNameListZero);
            }
            if (!itemNameListLessThan.isEmpty()) {
                model.addAttribute("itemNameListLessThan", itemNameListLessThan);
            }
            
            // 更新されたカート情報をセッションに保存
            session.setAttribute("basketBeans", basket);
        }
     // 画面表示用のカテゴリリストを取得
        model.addAttribute("categoryList", categoryRepository.findAll());
        
        return "client/basket/list";
    }

    //商品IDを受け取り、セッション内の買い物かごに商品を追加。完了後、一覧にリダイレクト。
    @PostMapping("/client/basket/add")
    public String addBasket(@RequestParam(name = "id") int itemId, HttpSession session) {
    	
    	//ログインチェック　
    	UserBean user = (UserBean) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login"; 
		}
		
    	// カート情報を取得。なければ新規作成
        List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
        if (basket == null) {
            basket = new ArrayList<BasketBean>();
        }
        
     // 追加する商品の情報を取得
        Item item = itemRepository.findById(itemId).get();

     // 重複チェック：同じ商品がすでにカートにあるか
        boolean isExist = false;
        for (BasketBean bean : basket) {
            if (bean.getId() == itemId) {

            	// すでにある場合は数量を+1
            	bean.setOrderNum(bean.getOrderNum() + 1);
                isExist = true;
                break;
            }
        }

     // 新しい商品の場合、カートに追加
        if (!isExist) {
            BasketBean newBean = new BasketBean();
            newBean.setId(item.getId());
            newBean.setName(item.getName());
            newBean.setStock(item.getStock());
            newBean.setOrderNum(1); // 初期数量は1
            
            basket.add(newBean);
        }

     // セッションを更新し、カート画面へリダイレクト
        session.setAttribute("basketBeans", basket);
        return "redirect:/client/basket/list";
    }

 
    //商品IDを受け取り、該当商品の個数を1つ減らす。個数が1の場合は買い物かごから削除。
    @PostMapping("/client/basket/delete")
    public String deleteBasket(@RequestParam(name = "id") Integer itemId, HttpSession session) {
    	
    	// カート情報を取得
        List<BasketBean> basket = (List<BasketBean>) session.getAttribute("basketBeans");
        
        if (basket != null) {
            Iterator<BasketBean> iterator = basket.iterator();
            
            // 対象の商品をループで探す
            while (iterator.hasNext()) {
                BasketBean bean = iterator.next();
                if (bean.getId().equals(itemId)) {
                    // 数が2以上のとき：該当商品の数を減らす
                    if (bean.getOrderNum() >= 2) {
                        bean.setOrderNum(bean.getOrderNum() - 1);
                    } else {
                        iterator.remove(); // 数が1のとき:買い物かごから該当商品を削除
                    }
                    break;              
                }
            }
            // セッションを更新
            session.setAttribute("basketBeans", basket);
        }

     // カート画面へリダイレクト
        return "redirect:/client/basket/list";
    }


    //セッション内の買い物かごデータをすべて消去。
    @PostMapping("/client/basket/allDelete")
    public String allDeleteBasket(HttpSession session) {

    			session.removeAttribute("basketBeans");

    			return "redirect:/client/basket/list";
    }
}
