package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	//	追加した部分
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		/*TODO 現在は全件表示を行っている
		 * 追加
		 * 
		 * これを売れ筋（注文回数が多い順）に改修する*/
		int sortType = 2;//売れ筋順に初期化

		// 注文情報DBから売れ筋順の商品一覧を取得
		List<Item> itemList = orderItemRepository.findBestSellerItems();

		// 売れ筋商品がなかった場合、新着順の商品一覧を取得
		if (itemList == null || itemList.isEmpty()) {

			// 新着順の商品一覧を取得
			itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);

			// 新着順に切り替え
			sortType = Constant.DEFAULT_SORT_TYPE;
		}
		// Entityを画面表示用Beanにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// リクエストスコープに設定
		model.addAttribute("sortType", sortType);
		model.addAttribute("items", itemBeanList);

		return "index";//・トップ画面表示

	}

	/**
	 * 商品一覧表示処理
	 *
	 * @param sortType 並び順
	 * @param categoryId カテゴリID
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItemList(
			@PathVariable int sortType,
			@RequestParam(required = false) Integer categoryId,
			Model model) {

		// 商品一覧格納用
		List<Item> itemList;

		// カテゴリ指定なしの場合
		if (categoryId == null) {

			// 新着順の場合
			if (sortType == Constant.DEFAULT_SORT_TYPE) {

				// 商品情報を新着順で取得
				itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(
						Constant.NOT_DELETED);

			} else {

				// 商品情報を売れ筋順で取得
				// 商品情報を売れ筋順で取得
				itemList = itemRepository.findByDeleteFlagOrderByBestSeller(
						Constant.NOT_DELETED);
			}

		} else {

			// カテゴリ指定あり・新着順の場合
			if (sortType == Constant.DEFAULT_SORT_TYPE) {

				// 指定カテゴリの商品情報を新着順で取得
				itemList = itemRepository
						.findByDeleteFlagAndCategoryIdOrderByInsertDateDesc(
								Constant.NOT_DELETED,
								categoryId);

			} else {

				// 指定カテゴリの商品情報を売れ筋順で取得
				itemList = itemRepository
						.findByDeleteFlagAndCategoryIdOrderByBestSeller(
								Constant.NOT_DELETED,
								categoryId);
			}
		}

		// Entityを画面表示用Beanにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		// 並び順をViewへ渡す
		model.addAttribute("sortType", sortType);

		// カテゴリIDをViewへ渡す
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * カテゴリ一覧をViewへ渡す
	 *
	 * @param model Viewとの値受渡し
	 */
	@ModelAttribute
	public void addCategoryList(Model model) {

		// 削除されていないカテゴリ一覧を取得
		List<Category> categoryList = categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(
				Constant.NOT_DELETED);

		// カテゴリ一覧をViewへ渡す
		model.addAttribute("categories", categoryList);
	}

	//	// 旧実装（全件表示）
	//	// 注文情報の商品情報を全件表示
	//List<Item> itemList = itemRepository.findAll();
	//	// エンティティ内の検索結果をJavaBeansにコピー
	//List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);
	//
	//	// 商品情報をViewへ渡す
	//model.addAttribute("items",itemBeanList);
	//
	//return"index";
	//	}

	/**
	 * 詳細表示処理
	 *
	 * @param id      表示対象ID
	 * @param model   Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得する
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		return "client/item/detail";
	}

}
