package com.ruoyi.achievement.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 鏂囦欢UUID鏄犲皠瀵硅薄 sys_file_uuid
 * 
 * @author ruoyi
 */
public class FileUuid extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** UUID */
    private String fileUuid;

    /** 鐪熷疄璺緞 */
    private String realPath;

    /** 鍘熷鏂囦欢鍚?*/
    private String originName;

    /** 鏄惁涓轰复鏃舵枃浠?(0姝ｅ紡 1涓存椂) */
    private Integer isTemp;

    public void setFileUuid(String fileUuid) 
    {
        this.fileUuid = fileUuid;
    }

    public String getFileUuid() 
    {
        return fileUuid;
    }
    public void setRealPath(String realPath) 
    {
        this.realPath = realPath;
    }

    public String getRealPath() 
    {
        return realPath;
    }
    public void setOriginName(String originName) 
    {
        this.originName = originName;
    }

    public String getOriginName() 
    {
        return originName;
    }
    public void setIsTemp(Integer isTemp) 
    {
        this.isTemp = isTemp;
    }

    public Integer getIsTemp() 
    {
        return isTemp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("fileUuid", getFileUuid())
            .append("realPath", getRealPath())
            .append("originName", getOriginName())
            .append("isTemp", getIsTemp())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
