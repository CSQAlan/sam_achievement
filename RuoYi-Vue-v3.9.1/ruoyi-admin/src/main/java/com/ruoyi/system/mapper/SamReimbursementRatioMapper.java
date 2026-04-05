package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SamReimbursementRatio;

/**
 * 报销比例Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public interface SamReimbursementRatioMapper 
{
    /**
     * 查询报销比例
     * 
     * @param id 报销比例主键
     * @return 报销比例
     */
    public SamReimbursementRatio selectSamReimbursementRatioById(Long id);

    /**
     * 查询报销比例列表
     * 
     * @param samReimbursementRatio 报销比例
     * @return 报销比例集合
     */
    public List<SamReimbursementRatio> selectSamReimbursementRatioList(SamReimbursementRatio samReimbursementRatio);

    /**
     * 新增报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    public int insertSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio);

    /**
     * 修改报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    public int updateSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio);

    /**
     * 删除报销比例
     * 
     * @param id 报销比例主键
     * @return 结果
     */
    public int deleteSamReimbursementRatioById(Long id);

    /**
     * 批量删除报销比例
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamReimbursementRatioByIds(Long[] ids);
    
    /**
     * 根据获奖等级、类别、归属学院获取报销比例
     */
    public Integer getReimbursementRatio(@Param("grade") String grade, 
                                          @Param("category") String category,
                                          @Param("ownerDepId") Long ownerDepId);
}
