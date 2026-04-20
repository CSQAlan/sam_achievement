package com.ruoyi.system.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * Achievement post-based permission helper.
 * Role stays teacher/student; admin scope is derived from post.
 */
@Service
public class AchievementPostPermissionService
{
    private static final Set<String> MY_PAGE_PERMS = new HashSet<>(Arrays.asList(
            "achievement:manage:list",
            "achievement:manage:query",
            "achievement:manage:participated:list",
            "achievement:manage:guided:list",
            "achievement:manage:responsible:list",
            "achievement:manage:list-participated",
            "achievement:manage:list-guided",
            "achievement:manage:list-responsible"));

    private static final Set<String> REVIEW_OPS = new HashSet<>(Arrays.asList(
            "list", "query", "add", "edit", "remove", "export", "review"));

    private static final String CN_COLLEGE_LEVEL = "\u9662\u7ea7";
    private static final String CN_COLLEGE = "\u5b66\u9662";
    private static final String CN_SCHOOL_LEVEL = "\u6821\u7ea7";
    private static final String CN_ADMIN = "\u7ba1\u7406\u5458";
    private static final String CN_SUPER_ADMIN = "\u603b\u7ba1\u7406\u5458";

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserMapper userMapper;

    public boolean canAccessCollegeReview(String userName)
    {
        AccessScope scope = resolveScope(userName);
        return scope.collegeReview;
    }

    public boolean canAccessSchoolReview(String userName)
    {
        AccessScope scope = resolveScope(userName);
        return scope.schoolReview;
    }

    public boolean hasAnyAdminPost(String userName)
    {
        AccessScope scope = resolveScope(userName);
        return scope.collegeReview || scope.schoolReview;
    }

    public Set<String> buildExtraPermissions(String userName)
    {
        AccessScope scope = resolveScope(userName);
        Set<String> perms = new HashSet<>();
        if (!scope.collegeReview && !scope.schoolReview)
        {
            return perms;
        }
        perms.addAll(MY_PAGE_PERMS);
        if (scope.collegeReview)
        {
            addReviewPerms(perms, "achievement:college_level_unreviewed");
            addReviewPerms(perms, "achievement:college_level_reviewed");
        }
        if (scope.schoolReview)
        {
            addReviewPerms(perms, "achievement:school_level_unreviewed");
            addReviewPerms(perms, "achievement:school_level_reviewed");
        }
        return perms;
    }

    private void addReviewPerms(Set<String> perms, String prefix)
    {
        REVIEW_OPS.forEach(op -> perms.add(prefix + ":" + op));
    }

    private AccessScope resolveScope(String userName)
    {
        AccessScope scope = new AccessScope();
        if (isSystemSuperAdmin(userName))
        {
            scope.collegeReview = true;
            scope.schoolReview = true;
            return scope;
        }

        Set<String> tags = loadPostTags(userName);
        boolean collegeSuper = tags.stream().anyMatch(this::isCollegeSuperAdminTag);
        boolean schoolSuper = tags.stream().anyMatch(this::isSchoolSuperAdminTag);
        boolean collegeAdmin = tags.stream().anyMatch(this::isCollegeAdminTag);
        boolean schoolAdmin = tags.stream().anyMatch(this::isSchoolAdminTag);

        // Super admin of either level can manage both review levels.
        scope.collegeReview = collegeAdmin || collegeSuper || schoolSuper;
        scope.schoolReview = schoolAdmin || schoolSuper || collegeSuper;
        return scope;
    }

    private boolean isSystemSuperAdmin(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            return false;
        }
        SysUser user = userMapper.selectUserByUserName(userName);
        return user != null && SysUser.isAdmin(user.getUserId());
    }

    private Set<String> loadPostTags(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            return new HashSet<>();
        }
        List<SysPost> posts = postMapper.selectPostsByUserName(userName);
        if (posts == null || posts.isEmpty())
        {
            return new HashSet<>();
        }
        return posts.stream()
                .flatMap(post -> Arrays.asList(post.getPostCode(), post.getPostName()).stream())
                .filter(StringUtils::isNotEmpty)
                .map(this::normalizeTag)
                .collect(Collectors.toSet());
    }

    private String normalizeTag(String value)
    {
        return value.trim().toLowerCase()
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
    }

    private boolean isCollegeAdminTag(String tag)
    {
        boolean explicit = containsAny(tag, "collegeadmin", "collegeleveladmin", "deptadmin", "departmentadmin");
        boolean chinese = containsAny(tag, CN_COLLEGE_LEVEL, CN_COLLEGE) && tag.contains(CN_ADMIN);
        return (explicit || chinese) && !isCollegeSuperAdminTag(tag);
    }

    private boolean isCollegeSuperAdminTag(String tag)
    {
        boolean explicit = containsAny(tag, "collegesuperadmin", "collegelevelsuperadmin", "deptsuperadmin", "departmentsuperadmin");
        boolean chinese = containsAny(tag, CN_COLLEGE_LEVEL, CN_COLLEGE) && tag.contains(CN_SUPER_ADMIN);
        return explicit || chinese;
    }

    private boolean isSchoolAdminTag(String tag)
    {
        boolean explicit = containsAny(tag, "schooladmin", "schoolleveladmin", "universityadmin");
        boolean chinese = tag.contains(CN_SCHOOL_LEVEL) && tag.contains(CN_ADMIN);
        return (explicit || chinese) && !isSchoolSuperAdminTag(tag);
    }

    private boolean isSchoolSuperAdminTag(String tag)
    {
        boolean explicit = containsAny(tag, "schoolsuperadmin", "schoollevelsuperadmin", "universitysuperadmin");
        boolean chinese = tag.contains(CN_SCHOOL_LEVEL) && tag.contains(CN_SUPER_ADMIN);
        return explicit || chinese;
    }

    private boolean containsAny(String text, String... tokens)
    {
        for (String token : tokens)
        {
            if (text.contains(token))
            {
                return true;
            }
        }
        return false;
    }

    private static class AccessScope
    {
        private boolean collegeReview;
        private boolean schoolReview;
    }
}
