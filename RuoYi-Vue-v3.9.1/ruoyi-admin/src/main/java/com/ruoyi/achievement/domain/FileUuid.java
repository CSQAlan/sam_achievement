package com.ruoyi.achievement.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class FileUuid extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 文件唯一UUID */
    private String fileUuid;

    /** 文件真实路径 */
    private String realPath;

    /** 原始文件名 */
    private String originName;

    /** 是否临时文件 */
    private Integer isTemp;

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public Integer getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(Integer isTemp) {
        this.isTemp = isTemp;
    }
}
