package com.ruoyi.system.domain.vo;

/**
 * 部门导入选项
 */
public class DeptImportOptionsVo
{
    /** 仅新增 */
    public static final String IMPORT_MODE_ADD_ONLY = "ADD_ONLY";

    /** 存在则更新，不存在则新增 */
    public static final String IMPORT_MODE_UPSERT = "UPSERT";

    /** 存在则跳过，不存在则新增 */
    public static final String IMPORT_MODE_SKIP_EXISTING = "SKIP_EXISTING";

    /** 仅更新已存在数据 */
    public static final String IMPORT_MODE_UPDATE_ONLY = "UPDATE_ONLY";

    /** 允许部分成功 */
    public static final String COMMIT_MODE_PARTIAL = "PARTIAL";

    /** 任一失败则整体回滚 */
    public static final String COMMIT_MODE_ALL_OR_NOTHING = "ALL_OR_NOTHING";

    /** 导入模式 */
    private String importMode = IMPORT_MODE_ADD_ONLY;

    /** 提交模式 */
    private String commitMode = COMMIT_MODE_PARTIAL;

    /** 更新时空白字段是否不覆盖原值 */
    private Boolean skipBlankFields = Boolean.TRUE;

    public String getImportMode()
    {
        return importMode;
    }

    public void setImportMode(String importMode)
    {
        this.importMode = importMode;
    }

    public String getCommitMode()
    {
        return commitMode;
    }

    public void setCommitMode(String commitMode)
    {
        this.commitMode = commitMode;
    }

    public Boolean getSkipBlankFields()
    {
        return skipBlankFields;
    }

    public void setSkipBlankFields(Boolean skipBlankFields)
    {
        this.skipBlankFields = skipBlankFields;
    }
}
