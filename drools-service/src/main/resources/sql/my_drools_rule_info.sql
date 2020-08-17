INSERT INTO `my-drools`.rule_info (id, created_date, created_user_oid, deleted, modified_date, modified_user_oid, oid, content, rule_desc, scene_id) VALUES (1, '2020-06-01 15:24:07', '1', false, '2020-06-01 15:24:14', '1', '1', '//图书优惠规则
package rules.scene_1.rule_1
import com.slk.drools.db.entity.Order
//规则一:所购图书总价在100元以下的没有优惠
rule "book_discount_1"
    when
        $order:Order(originalPrice<100)
    then
        $order.setRealPrice($order.getOriginalPrice());
        System.out.println("成功匹配到规则1:所购图书总价在100亓以下的没有优惠");
end
//规则二:所购图书总价在100到200元的优惠20元
rule "book_discount_2"
    when
        $order:Order(originalPrice<200 && originalPrice >=100)
    then
        $order.setRealPrice($order.getOriginalPrice()-20);
        System.out.println("成功匹配到规则2:所购图书总价在100亓以上200元以下，优惠20");
end', '场景1规则1', 1);
INSERT INTO `my-drools`.rule_info (id, created_date, created_user_oid, deleted, modified_date, modified_user_oid, oid, content, rule_desc, scene_id) VALUES (2, '2020-06-01 15:24:07', '1', false, '2020-06-01 15:24:14', '1', '2', '//图书优惠规则
package rules.scene_3.rule_2
import com.slk.drools.db.entity.Order
//规则一:所购图书总价在100元以下的没有优惠
rule "book_discount_1"
    when
        $order:Order(originalPrice<100)
    then
        $order.setRealPrice($order.getOriginalPrice());
        System.out.println("成功匹配到规则一:所购图书总价在100亓以下的没有优惠");
end
//规则二:所购图书总价在100到200元的优惠20元
rule "book_discount_2"
    when
        $order:Order(originalPrice<200 && originalPrice >=100)
    then
        $order.setRealPrice($order.getOriginalPrice()-20);
        System.out.println("成功匹配到规则二:所购图书总价在100亓以上200元以下，优惠20");
end', '场景2规则1', 3);