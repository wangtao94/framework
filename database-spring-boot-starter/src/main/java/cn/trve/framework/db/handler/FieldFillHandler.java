package cn.trve.framework.db.handler;

import cn.trve.framework.db.model.Entity;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <pre>
 * <b>填充字段</b>
 * <b>Description:</b> 填充{@link Entity}的基础字段
 * <b>Copyright:</b> Copyright 2022 360humi. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/3/6 17:17    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @since 2023/3/6
 */
@Component
public class FieldFillHandler implements MetaObjectHandler {

    /**
     * 插入时填充字段
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        String operatorName = getOperatorName();

        LocalDateTime now = LocalDateTime.now();

        Entity<?> entity = (Entity<?>) metaObject.getOriginalObject();

        if (entity.getCreateBy() == null) {
            entity.setCreateBy(operatorName);
        }
        if (entity.getCreateDate() == null) {
            entity.setCreateDate(now);
        }
        if (entity.getLastModifiedBy() == null) {
            entity.setLastModifiedBy(operatorName);
        }
        if (entity.getLastModifiedDate() == null) {
            entity.setLastModifiedDate(now);
        }
        if (entity.getDeleted() == null) {
            entity.setDeleted(0);
        }

    }

    /**
     * 更新时填充字段
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        String username = getOperatorName();


        Entity<?> entity = (Entity<?>) metaObject.getOriginalObject();

        if (entity.getLastModifiedBy() == null) {
            entity.setLastModifiedBy(username);
        }
        if (entity.getLastModifiedDate() == null) {
            entity.setLastModifiedDate(LocalDateTime.now());
        }
    }

    private String getOperatorName() {
        // TODO: 2023/4/12 接入用户安全上下文
        return "sys";
    }


}

