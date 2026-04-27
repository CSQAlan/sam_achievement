package com.ruoyi.competition.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 竞赛导入日志对象 sam_competition_import_log
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public class SamCompetitionImportLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 文件名 */
    private String filename;

    /** 标签编码 */
    private String tagCode;

    /** 操作人 */
    private String operator;

    /** 导入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;

    /** 修改的赛事ID列表（逗号分隔） */
    private String modifiedIds;

    /** 状态（0正常 1已撤销） */
    private String status;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public Date getImportTime() {
        return importTime;
    }

    public void setModifiedIds(String modifiedIds) {
        this.modifiedIds = modifiedIds;
    }

    public String getModifiedIds() {
        return modifiedIds;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("filename", getFilename())
            .append("tagCode", getTagCode())
            .append("operator", getOperator())
            .append("importTime", getImportTime())
            .append("modifiedIds", getModifiedIds())
            .append("status", getStatus())
            .toString();
    }
}
