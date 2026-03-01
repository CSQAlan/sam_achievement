**Student Achievements Management System**

赛事表



|实体名称|Competition|中文名称|赛事|模块名称|sam:competition:赛事|组件|搜索条件|||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|||||||||
|1|name|String(20)|赛事名称|非空|名称|文本|是|||||||||
|2|category|CodeTable<类别>:政府类，学会类|类别||类别（政府类或学会类）|字典单选|是|||||||||
|3|organizations|String(512)|盖章单位|非空|盖章单位,多个单位用分号分开|文本|是|||||||||
|4|tags|CodeTable<标签>|标签||标签（素质提升表/白名单）,多个标签用分号分隔|字典多选|是|||||||||
|5|memo|String(1000)|赛事说明||说明|多行文本|否|||||||||
|6|scope\_type|Char(1)|适用范围||0全校 1指定学院(见CompetitionDeptRel)|字典单选|是|||||||||
|8|status|Char(1)|状态||0停用 1启用|字典单选|是|||||||||
|9|create\_by|String(64)|创建人|||文本|否|||||||||
|10|create\_time|DateTime|创建时间|||日期时间|否|||||||||
|11|update\_by|String(64)|更新人|||文本|否|||||||||
|12|update\_time|DateTime|更新时间|||日期时间|否|||||||||
|13|remark|String(500)|备注|||多行文本|否|||||||||
|14|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|||||||||

赛事-部门关系表

|实体名称|CompetitionDeptRel|中文名称|赛事-部门关系|模块名称|sam:competition:赛事|组件|搜索条件||||||||||
| - | - | - | - | - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件||||||||||
|1|id|Long|主键|PK||文本|否||||||||||
|2|Session\_id|Long|赛事ID|非空|Competition.id|文本|是||||||||||
|3|dept\_id|Long|学院dept\_id|非空|sys\_dept.dept\_id（存学院dept）|部门选择|是||||||||||
|4|sort|Int|排序|||数字|否||||||||||
|5|create\_by|String(64)|创建人|||文本|否||||||||||
|6|create\_time|DateTime|创建时间|||日期时间|否||||||||||
|7|update\_by|String(64)|更新人|||文本|否||||||||||
|8|update\_time|DateTime|更新时间|||日期时间|否||||||||||
|9|remark|String(500)|备注|||多行文本|否||||||||||
|10|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否||||||||||

赛事申请表

|实体名称|CompetitionApply|中文名称|赛事申请|模块名称|sam:competition:赛事申请|组件|搜索条件||||||||||
| - | - | - | - | - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件||||||||||
|1|id|Long|主键|PK||文本|否||||||||||
|2|apply\_no|String(32)|申请编号|非空||文本|是||||||||||
|3|applicant\_user\_id|Long|申请人用户ID|非空|sys\_user.user\_id|用户选择|是||||||||||
|4|applicant\_dep\_id|Long|申请人学院|非空|sys\_dept.dept\_id（存学院dept）|部门选择|是||||||||||
|5|name|String(100)|赛事名称|非空||文本|是||||||||||
|6|category|CodeTable<类别>|类别||政府类/学会类|字典单选|是||||||||||
|7|organizations|String(512)|盖章单位||多个用分号分隔|文本|否||||||||||
|8|tags|CodeTable<标签>|标签||多个用分号分隔|字典多选|否||||||||||
|9|level|CodeTable<级别>|比赛级别||I级/II级等|字典单选|是||||||||||
|10|memo|String(1000)|赛事说明|||多行文本|否||||||||||
|11|scope\_type|Char(1)|适用范围|非空|0全校 1指定学院（通过后写入CompetitionDeptRel）|字典单选|是||||||||||
|12|status|Char(1)|审核状态|非空|0待审 1通过 2驳回 3撤回|字典单选|是||||||||||
|13|audit\_by|String(64)|审核人||sys\_user.user\_name/或user\_id|文本|否||||||||||
|14|audit\_time|DateTime|审核时间|||日期时间|否||||||||||
|15|audit\_remark|String(500)|审核意见|||多行文本|否||||||||||
|16|competition\_id|Long|生成赛事ID||审核通过后写入Competition.id|文本|否||||||||||
|17|create\_by|String(64)|创建人|||文本|否||||||||||
|18|create\_time|DateTime|创建时间|||日期时间|否||||||||||
|19|update\_by|String(64)|更新人|||文本|否||||||||||
|20|update\_time|DateTime|更新时间|||日期时间|否||||||||||
|21|remark|String(500)|备注|||多行文本|否||||||||||
|22|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否||||||||||

赛事申请附件表

|实体名称|CompetitionApplyAttachment|中文名称|赛事申请附件|模块名称|sam:competition:赛事申请||组件||||||||||
| - | - | - | - | - | - | - | - | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系||备注|组件||||||||||
|1|id|Long|主键|PK|||文本||||||||||
|2|apply\_id|Long|赛事申请|非空|CompetitionApply.id||文本||||||||||
|3|type|CodeTable<附件类别>|附件类型|非空||如赛事通知/证明材料/其他|字典单选||||||||||
|4|file\_type|CodeTable<文件类型>|文件类型|非空||PDF/等|字典单选||||||||||
|5|origin\_name|String(255)|原始文件名||||文本||||||||||
|6|store\_name|String(64)|存储文件名|||建议UUID(不含路径)|文本||||||||||
|7|file\_path|String(1000)|相对路径|||仅存相对路径(不含域名)|文本||||||||||
|8|file\_url|String(1000)|访问URL|||可由file\_path+配置拼接/ |文本||||||||||
|10|file\_size|Long|文件大小|||单位Byte|数字||||||||||
|12|upload\_by|Long|上传人||sys\_user.user\_id||文本||||||||||
|13|upload\_time|DateTime|上传时间||||日期时间||||||||||
|14|remark|String(500)|备注||||多行文本||||||||||
|15|del\_flag|Char(1)|删除标记|||0存在 2删除|字典单选||||||||||


届次表

|实体名称|Session|中文名称|届次|模块名称|sam:competition:赛事|组件|搜索条件|||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|||||||||
|1|competition\_id|Long|赛事|非空|Competition.id|文本|是|||||||||
|2|session|String(50)|届次|非空|例如2025、十九届|文本|是|||||||||
|3|category|CodeTable<类别>:政府类，学会类|类别||类别|字典单选|是|||||||||
|4|organizations|String(512)|盖章单位|非空|盖章单位,多个单位用分号分开|文本|是|||||||||
|5|level|CodeTable<级别>:Ⅰ级，Ⅱ级|级别||级别|字典单选|是|||||||||
|7|status|Char(1)|状态||0停用 1启用|字典单选|是|||||||||
|8|create\_by|String(64)|创建人|||文本|否|||||||||
|9|create\_time|DateTime|创建时间|||日期时间|否|||||||||
|10|update\_by|String(64)|更新人|||文本|否|||||||||
|11|update\_time|DateTime|更新时间|||日期时间|否|||||||||
|12|remark|String(500)|备注|||多行文本|否|||||||||
|13|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|||||||||

这个可以用字典实现，创建一个名为“标签”字典
标签表


|实体名称|Tag|中文名称|标签|模块名称|sam:competition:赛事|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|competition\_session\_id|Long|届次|非空|Session.id|文本|是|
|2|tag\_name|String(50)|标签名称|非空|字典/白名单|文本|是|
|3|dep\_id|Long|适用学院||sys\_dept.dept\_id；为空表示全校共享|部门选择|否|
|4|create\_by|String(64)|创建人|||文本|否|
|5|create\_time|DateTime|创建时间|||日期时间|否|
|6|update\_by|String(64)|更新人|||文本|否|
|7|update\_time|DateTime|更新时间|||日期时间|否|
|8|remark|String(500)|备注|||多行文本|否|
|9|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|

成果表



|实体名称|Achievement|中文名称|成果表|模块名称|sam:competition:成果|组件|搜索条件|||||||||||||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|||||||||||||||||||
|1|session\_id|Long|届次|非空|Session.id|文本|是|||||||||||||||||||
|2|category|CodeTable<类别>:政府类，学会类|类别||类别|字典单选|是|||||||||||||||||||
|3|name|String(200)|团队/作品名称||团队名称、作品名称|文本|是|||||||||||||||||||
|4|level|CodeTable<级别>:Ⅰ级，Ⅱ级|级别|非空|级别|字典单选|是|||||||||||||||||||
|5|grade|CodeTable<获奖等级>:一等奖，二等奖，三等奖|获奖等级|非空|获奖等级|字典单选|是|||||||||||||||||||
|6|track|String(200)|赛道/竞赛项目||赛道|文本|否|||||||||||||||||||
|7|certificate\_no|String(100)|证书编号|唯一,非空|证书编号|文本|是|||||||||||||||||||
|8|group|CodeTable<组别>:本科生，研究生|组别||组别|字典单选|否|||||||||||||||||||
|9|award\_time|Date|获奖时间|非空|获奖时间|日期选择|是|||||||||||||||||||
|10|fee|Double|报名费金额||报名金额|数字|否|||||||||||||||||||
||Year|int|||成果所属年份|数字|是|||||||||||||||||||
||is\_reimburse|tinyint(1)|是否报销||是否报销|文本|是|||||||||||||||||||
||is\_supplement|tinyint(1)|是否补录||是否补录|文本|是|||||||||||||||||||
|11|reimbursement\_fee|Double|报销金额||报销金额|数字|否|||||||||||||||||||
|12|reimbursement\_ratio|Integer|报销百分比||报销比例|数字|是|||||||||||||||||||
|13|reimbursement\_item\_id|Long|报销项目||ReimbursementItems.id|文本|否|||||||||||||||||||
|14|reimbursement\_date|Date|报销发放时间||是否发放/发放时间|日期选择|否|||||||||||||||||||
|15|owner\_dep\_id|Long|归属学院|非空|sys\_dept.dept\_id（顶层学院）；用于多学院数据隔离|部门选择|是|||||||||||||||||||
|16|submit\_time|DateTime|提交时间||为空表示未提交|日期时间|是|||||||||||||||||||
|17|submit\_by|Long|提交人||sys\_user.user\_id|文本|否|||||||||||||||||||
|18|audit\_time|DateTime|审核时间||为空表示未审核|日期时间|是|||||||||||||||||||
||dept\_audit\_status|tinyint|学院审核状态||0：待院级审核1：院级驳回2：院级通过|字典单选|否|||||||||||||||||||
||sdept\_audit\_status|tinyint|学校审核状态||0：校级驳回1：校级通过2：待校级审核|字典单选|否|||||||||||||||||||
|19|audit\_by|Long|审核人||sys\_user.user\_id|文本|否|||||||||||||||||||
|21|audit\_reason|String(500)|审核不通过原因|||多行文本|否|||||||||||||||||||
|22|create\_by|String(64)|创建人|||文本|否|||||||||||||||||||
|23|create\_time|DateTime|创建时间|||日期时间|否|||||||||||||||||||
|24|update\_by|String(64)|更新人|||文本|否|||||||||||||||||||
|25|update\_time|DateTime|更新时间|||日期时间|否|||||||||||||||||||
|26|remark|String(500)|备注|||多行文本|否|||||||||||||||||||
|27|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|||||||||||||||||||


报销比例表

|实体名称|ReimbursementRatio|中文名称|报销比例|模块名称|sam:competition:成果|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|grade|CodeTable<获奖等级>:一等奖，二等奖，三等奖|获奖等级|非空|获奖等级|字典单选|是|
|2|category|CodeTable<类别>:政府类，学会类|类别||类别|字典单选|是|
|3|ratio|Integer|报销百分比||报销百分比|数字|是|
|4|owner\_dep\_id|Long|归属学院||sys\_dept.dept\_id；为空表示全校规则|部门选择|是|
|5|status|Char(1)|状态||0停用 1启用|字典单选|是|
|6|create\_by|String(64)|创建人|||文本|否|
|7|create\_time|DateTime|创建时间|||日期时间|否|
|8|update\_by|String(64)|更新人|||文本|否|
|9|update\_time|DateTime|更新时间|||日期时间|否|
|10|remark|String(500)|备注|||多行文本|否|
|11|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|



成果附件

|实体名称|Attachment|中文名称|成果附件|模块名称|sam:competition:成果|erp:sam:competitionachievement:成果|组件||||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系||备注|组件||||||||||
|1|achievement\_id|Long|成果|非空|Achievement.id||文本||||||||||
|2|type|CodeTable<附件类别>:奖章,作品图片,比赛通知,发票,支付记录,微信收款码|附件类型|非空|||字典单选||||||||||
|3|file\_type|CodeTable<文件类型>:PDF,JPEG,PNG|文件类型|非空|||字典单选||||||||||
|4|origin\_name|String(255)|原始文件名|||原始上传文件名（用于展示）|文本||||||||||
|5|store\_name|String(64)|存储文件名|||建议UUID(不含路径)，避免猜测与覆盖|文本||||||||||
|6|file\_path|String(1000)|相对路径|||仅存相对路径(不含域名/盘符)，由配置拼接访问URL|文本||||||||||
|7|file\_url|String(1000)|访问URL|||可为空：由file\_path+配置拼接；或OSS直链|文本||||||||||
|8|oss\_id|Long|OSS文件ID|||（若使用若依文件/OSS模块）|文本||||||||||
|9|file\_size|Long|文件大小|||单位Byte|数字||||||||||
|10|sha256|String(64)|文件摘要|||用于防篡改/去重(可选)|文本||||||||||
|11|upload\_by|Long|上传人||sys\_user.user\_id||文本||||||||||
|12|upload\_time|DateTime|上传时间||||日期时间||||||||||
|13|remark|String(500)|备注||||多行文本||||||||||
|14|del\_flag|Char(1)|删除标记|||0存在 2删除|字典单选||||||||||

参赛选手表

|实体名称|Participant|中文名称|参赛选手|模块名称|sam:competition:成果|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|achievement\_id|Long|成果|非空|Achievement.id|文本|是|
|2|student\_id|Long|学生|非空|Student.id/学号映射|文本|是|
|3|order\_no|Integer|参赛次序||1,2,3...|数字|是|
|4|manager|Integer|是否负责人||0否 1是；默认第1为负责人|字典单选|是|
|5|create\_by|String(64)|创建人|||文本|否|
|6|create\_time|DateTime|创建时间|||日期时间|否|
|7|update\_by|String(64)|更新人|||文本|否|
|8|update\_time|DateTime|更新时间|||日期时间|否|
|9|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|

指导老师表


|实体名称|Advisor|中文名称|指导老师|模块名称|sam:competition:成果|组件|搜索条件||||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件||||||||||
|1|achievement\_id|Long|成果|非空|Achievement.id|文本|是||||||||||
|2|teacher\_id|Long|指导教师|非空|Teacher.id/工号映射|文本|是||||||||||
|3|order\_no|Integer|次序||1,2,3...|数字|是||||||||||
|4|manager|Integer|是否负责人||0否 1是；默认第1为负责人|字典单选|是||||||||||
|5|create\_by|String(64)|创建人|||文本|否||||||||||
|6|create\_time|DateTime|创建时间|||日期时间|否||||||||||
|7|update\_by|String(64)|更新人|||文本|否||||||||||
|8|update\_time|DateTime|更新时间|||日期时间|否||||||||||
|9|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否||||||||||



报销项目

|实体名称|ReimbursementItems|中文名称|报销项目|模块名称|sam:competition:报销|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|name|String(100)|报销项目名称|非空|报销项目名称|文本|是|
|2|reimbursement\_time|Date|报销时间|非空|报销时间|日期选择|是|
|3|total\_fee|Double|总金额|非空|总金额|数字|是|
|4|paid\_fee|Double|已发放金额|||数字|否|
|4|paid\_fee|Double|已发放金额|||数字|否|
|5|amount|Integer|数量||报销项目数量|数字|否|
|6|owner\_dep\_id|Long|归属学院|非空|sys\_dept.dept\_id（顶层学院）|部门选择|是|
|7|status|Char(1)|状态||0进行中 1已完成|字典单选|是|
|8|create\_by|String(64)|创建人|||文本|否|
|9|create\_time|DateTime|创建时间|||日期时间|否|
|10|update\_by|String(64)|更新人|||文本|否|
|11|update\_time|DateTime|更新时间|||日期时间|否|
|12|remark|String(500)|备注|||多行文本|否|
|13|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|

素质提升表

|实体名称|QualityDevelopmentForm|中文名称|素质提升表|模块名称|sam:competition:报销|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|year|Integer|年份|非空|年份|文本|是|
|2|amount|Integer|统计成果数|非空|统计成功数|数字|是|
|3|owner\_dep\_id|Long|归属学院||sys\_dept.dept\_id（顶层学院）|部门选择|是|
|4|create\_time|DateTime|生成时间|||日期时间|否|



这个表不要

框架里有user表
用户表


|实体名称|User|中文名称|用户|模块名称|samuser:用户|组件|搜索条件|
| :- | - | :- | - | :- | - | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件|
|1|user\_id|Long|用户ID|唯一,非空|sys\_user.user\_id|文本|否|
|2|user\_name|String(30)|登录账号|非空，唯一|sys\_user.user\_name|文本|是|
|3|nick\_name|String(30)|姓名|非空|sys\_user.nick\_name|文本|是|
|4|password|String(100)|密码|非空||文本|否|
|5|dep\_id|Long|部门/专业|非空|对应sys\_user.dept\_id（sys\_dept.dept\_id）|部门选择|是|
|6|phonenumber|String(11)|手机号|||文本|是|
|7|email|String(50)|邮箱|||文本|是|
|8|sex|Char(1)|性别||0男 1女 2未知|字典单选|否|
|9|status|Char(1)|状态||0停用 1正常|字典单选|是|
|10|login\_ip|String(128)|最后登录IP|||文本|否|
|11|login\_date|DateTime|最后登录时间|||日期时间|否|
|12|create\_by|String(64)|创建人|||文本|否|
|13|create\_time|DateTime|创建时间|||日期时间|否|
|14|update\_by|String(64)|更新人|||文本|否|
|15|update\_time|DateTime|更新时间|||日期时间|否|
|16|remark|String(500)|备注|||多行文本|否|
|17|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否|







学生表


|实体名称|Student|中文名称|学生信息|模块名称|sam:competition:学生|组件|搜索条件||||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件||||||||||
|1|student\_id|Long|学生ID|唯一,非空|内部主键|文本|否||||||||||
|2|name|String(20)|学生姓名|非空|名称|文本|是||||||||||
|3|no|String(32)|学号|唯一,非空|学号|文本|是||||||||||
|4|dep\_id|Long|部门/专业|非空|对应sys\_dept.dept\_id（建议指向专业/班级节点）|部门选择|是||||||||||
|5|class\_name|String(50)|班级|||文本|是||||||||||
|6|class\_year|Integer|年级|||数字|是||||||||||
|9|status|Char(1)|状态||0停用 1正常|字典单选|是||||||||||
|10|create\_by|String(64)|创建人|||文本|否||||||||||
|11|create\_time|DateTime|创建时间|||日期时间|否||||||||||
|12|update\_by|String(64)|更新人|||文本|否||||||||||
|13|update\_time|DateTime|更新时间|||日期时间|否||||||||||
|14|remark|String(500)|备注|||多行文本|否||||||||||
|15|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否||||||||||

教师表

|实体名称|Teacher|中文名称|教师信息|模块名称|sam:competition:教师|组件|搜索条件||||||||||
| :- | - | :- | - | :- | - | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- | :- |
|字段编号|字段名称|字段类型|中文名称|约束关系|备注|组件|搜索条件||||||||||
|1|teacher\_id|Long|教师ID|唯一,非空|内部主键|文本|否||||||||||
|2|name|String(20)|教师姓名|非空|名称|文本|是||||||||||
|3|no|String(32)|工号|唯一,非空|工号|文本|是||||||||||
|4|dep\_id|Long|部门/学院|非空|对应sys\_dept.dept\_id|部门选择|是||||||||||
|7|status|Char(1)|状态||0停用 1正常|字典单选|是||||||||||
|8|create\_by|String(64)|创建人|||文本|否||||||||||
|9|create\_time|DateTime|创建时间|||日期时间|否||||||||||
|10|update\_by|String(64)|更新人|||文本|否||||||||||
|11|update\_time|DateTime|更新时间|||日期时间|否||||||||||
|12|remark|String(500)|备注|||多行文本|否||||||||||
|13|del\_flag|Char(1)|删除标记||0存在 2删除|字典单选|否||||||||||

