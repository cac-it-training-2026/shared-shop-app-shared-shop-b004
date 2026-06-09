package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
			@Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者,商品詳細機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

	/**
	 * 追加
	 * 削除されていない商品情報を新着順で取得
	 *
	 * 登録日付の降順で商品情報を取得する。
	 * トップ画面で売れ筋商品が存在しない場合の
	 * 新着順表示に利用する。
	 *
	 * @param deleteFlag 削除フラグ
	 * @return 新着順の商品一覧
	 */

	List<Item> findByDeleteFlagOrderByInsertDateDesc(int deleteFlag);

	/**
	 * 削除フラグとカテゴリIDを条件に商品情報を新着順で取得
	 *
	 * @param deleteFlag 削除フラグ
	 * @param categoryId カテゴリID
	 * @return 商品エンティティのリスト
	 */
	List<Item> findByDeleteFlagAndCategoryIdOrderByInsertDateDesc(
			int deleteFlag,
			Integer categoryId);

	/**
	 * 商品情報を売れ筋順で取得
	 *
	 * 注文個数の合計が多い順に商品情報を取得する。
	 * 注文がない商品も一覧に表示する。
	 *
	 * @param deleteFlag 削除フラグ
	 * @return 売れ筋順の商品一覧
	 */
	@Query("SELECT i FROM Item i "
			+ "LEFT JOIN i.orderItemList oi "
			+ "WHERE i.deleteFlag = :deleteFlag "
			+ "GROUP BY i "
			+ "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC, i.insertDate DESC, i.id DESC")
	List<Item> findByDeleteFlagOrderByBestSeller(
			@Param("deleteFlag") int deleteFlag);

	/**
	 * カテゴリIDと削除フラグを条件に商品情報を売れ筋順で取得
	 *
	 * 注文個数の合計が多い順に商品情報を取得する。
	 * 注文がない商品も一覧に表示する。
	 *
	 * @param deleteFlag 削除フラグ
	 * @param categoryId カテゴリID
	 * @return カテゴリ別・売れ筋順の商品一覧
	 */
	@Query("SELECT i FROM Item i "
			+ "LEFT JOIN i.orderItemList oi "
			+ "WHERE i.deleteFlag = :deleteFlag "
			+ "AND i.category.id = :categoryId "
			+ "GROUP BY i "
			+ "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC, i.insertDate DESC, i.id DESC")
	List<Item> findByDeleteFlagAndCategoryIdOrderByBestSeller(
			@Param("deleteFlag") int deleteFlag,
			@Param("categoryId") Integer categoryId);
}