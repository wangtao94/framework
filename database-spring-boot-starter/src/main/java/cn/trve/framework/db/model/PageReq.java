package cn.trve.framework.db.model;

import cn.trve.framework.web.util.AssertUtils;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * <b>分页请求对象</b>
 * <b>Description:扩展了入参对象</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 16:17    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/4/12
 * @since 2023/4/12
 */
public class PageReq<E, T> extends Page<T> {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageReq.class);
    private E entity;

    public E getEntity() {
        return entity;
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    public Optional<E> getEntityOpt() {
        return Optional.ofNullable(entity);
    }

    /**
     * 防止用户注入sql
     */
    @Override
    public List<OrderItem> orders() {
        // 获取sql注入的字段
        Set<OrderItem> injectItems = super.orders().stream()
                // 过滤空值
                .filter(orderItem -> null != orderItem && null != orderItem.getColumn())
                .filter(orderItem -> SqlInjectionUtils.check(orderItem.getColumn()))
                .collect(Collectors.toSet());

        // 如果有注入的字段，抛出异常
        AssertUtils.isFalse(CollectionUtils.isEmpty(injectItems), "排序字段: {} 非法",
                injectItems.stream().map(OrderItem::toString).collect(Collectors.joining("、")));

        return orders;
    }

}