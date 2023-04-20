package cn.trve.framework.db.model;

import cn.trve.framework.web.constant.dict.DatePattern;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <pre>
 * <b>ORM映射对象基类</b>
 * <b>Description:</b>
 * 所有ORM映射对象都要继承此类, 以便于统一管理.
 * 该类定义了基础字段
 *
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 14:32    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/4/12
 * @since 2023/4/12
 */
public abstract class Entity<T extends Model<?>> extends Model<T> implements Serializable {

    public static final String DEFAULT_DATE_FORMAT = DatePattern.ISO8601_PATTERN;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;

    /**
     * 创建时间, 仅新增时设置, 以后永久不变, 默认:sysdate.
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DEFAULT_DATE_FORMAT)
    protected LocalDateTime createDate;

    /**
     * 创建人:新增时插入,默认sys.
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    protected String createBy;

    /**
     * 修改时间, 每次修改记录时变更, 默认:sysdate.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = DEFAULT_DATE_FORMAT)
    @JsonIgnore
    protected LocalDateTime lastModifiedDate;

    /**
     * 修改人:新增和修改时插入，默认sys.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    protected String lastModifiedBy;

    /**
     * 数据版本, 默认0.
     */
    @Version
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    protected Long version;

    /**
     * 删除标示, 0:否(默认); 1:是.
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    protected Integer deleted;

    public Entity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * id相等则认为对象相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity<?> entity = (Entity<?>) o;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
