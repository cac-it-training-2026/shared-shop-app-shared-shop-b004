package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	/**
	 * 追加
	 * 売れ筋順の商品一覧を取得
	 *
	 * ・削除されていない商品のみ対象
	 * ・注文商品情報を商品ごとに集計
	 * ・注文個数の合計が多い順に並び替え
	 *
	 * @return 売れ筋順の商品一覧
	 */

	@Query("SELECT oi.item FROM OrderItem oi "
			+ "WHERE oi.item.deleteFlag = 0 "
			+ "GROUP BY oi.item "
			+ "ORDER BY SUM(oi.quantity) DESC")

	List<Item> findBestSellerItems();

	/**
	 * カテゴリIDを条件に売れ筋順の商品一覧を取得
	 *
	 * ・削除されていない商品のみ対象
	 * ・指定カテゴリの商品を対象
	 * ・注文個数の合計が多い順に並び替え
	 *
	 * @param categoryId カテゴリID
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT oi.item FROM OrderItem oi "
			+ "WHERE oi.item.deleteFlag = 0 "
			+ "AND oi.item.category.id = :categoryId "
			+ "GROUP BY oi.item "
			+ "ORDER BY SUM(oi.quantity) DESC")
	List<Item> findBestSellerItemsByCategoryId(
			@Param("categoryId") Integer categoryId);

}
