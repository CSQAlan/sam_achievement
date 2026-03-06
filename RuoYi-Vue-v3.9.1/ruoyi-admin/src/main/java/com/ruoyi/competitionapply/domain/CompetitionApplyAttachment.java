package com.ruoyi.competitionapply.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事申请附件对象 competition_apply_attachment
 *
 * @author ruoyi
 * @date 2026-02-01
 */
public class CompetitionApplyAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 赛事申请ID（关联competition_apply.id） */
    @Excel(name = "赛事申请ID", readConverterExp = "关=联competition_apply.id")
    private Long competitionApplyId; // 字段名修改为competitionApplyId，对应数据库competition_apply_id

    /** 文件UUID */
    @Excel(name = "文件UUID")
    private String uuid; // 新增字段，对应数据库uuid

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String path; // 新增字段，对应数据库path

    /** 文档名称 */
    @Excel(name = "文档名称")
    private String documentName; // 新增字段，对应数据库document_name

    /** 附件类型 */
    @Excel(name = "附件类型")
    private Integer attachmentType; // 新增字段，对应数据库attachment_type (tinyint)

    private String delFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setCompetitionApplyId(Long competitionApplyId)
    {
        this.competitionApplyId = competitionApplyId;
    }

    public Long getCompetitionApplyId()
    {
        return competitionApplyId;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public void setDocumentName(String documentName)
    {
        this.documentName = documentName;
    }

    public String getDocumentName()
    {
        return documentName;
    }

    public void setAttachmentType(Integer attachmentType)
    {
        this.attachmentType = attachmentType;
    }

    public Integer getAttachmentType()
    {
        return attachmentType;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("competitionApplyId", getCompetitionApplyId())
                .append("uuid", getUuid())
                .append("path", getPath())
                .append("documentName", getDocumentName())
                .append("attachmentType", getAttachmentType())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}