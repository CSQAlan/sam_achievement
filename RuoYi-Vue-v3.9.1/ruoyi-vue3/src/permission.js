import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken, setToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
const CAS_LOGIN_URL = 'https://csxrz.cqnu.edu.cn/cas/login'
const CAS_SERVICE = import.meta.env.VITE_CAS_SERVICE // 后端回调地址
NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/reset', '/cas']


const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const isProfilePage = (path) => path.startsWith('/user/profile')

const shouldBypassInit = (path) => isWhiteList(path) || isProfilePage(path)
//重定向到CAS登录
function redirectToCas(to) {
  // 记录用户原本想去的页面，等CAS回来后可以跳回
  sessionStorage.setItem('CAS_REDIRECT', to.fullPath)
  const service = encodeURIComponent(CAS_SERVICE)
  window.location.href = `${CAS_LOGIN_URL}?service=${service}`
}
// 检查是否完善了邮箱和手机号
const checkProfileInitialized = (to, next) => {
  const userStore = useUserStore()
  // 仅当明确为 0 时才拦截，避免字段未返回时误判
  if (userStore.profileInitialized === 0 && !shouldBypassInit(to.path)) {
    ElMessage.warning('请先完善邮箱和手机号再访问其他功能')
    next({ path: '/user/profile', query: { redirect: to.fullPath } })
    NProgress.done()
    return false
  }
  return true
}

router.beforeEach((to, from, next) => {
  NProgress.start()

  // 1. 拦截从后端重定向回来时 URL 带有的 token 参数
  const urlToken = to.query.token
  if (urlToken) {
    // 将 Token 存入前端 Cookie
    setToken(urlToken)
    
    // 清除 URL 中的 token 敏感参数，防止留在历史记录中或刷新时重复处理
    const query = { ...to.query }
    delete query.token
    
    // 使用 replace 模式跳转，清除浏览器历史记录中的 token
    next({ path: to.path, query, replace: true })
    NProgress.done()
    return
  }

  // 2. 正常校验流程
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      // 检查用户是否已登录且有角色信息
      if (useUserStore().roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        useUserStore().getInfo().then(() => {
          isRelogin.show = false
          if (!checkProfileInitialized(to, next)) return
          usePermissionStore().generateRoutes().then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            accessRoutes.forEach(route => {
              if (!isHttp(route.path)) {
                router.addRoute(route) // 动态添加可访问路由表
              }
            })
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
          useUserStore().logOut().then(() => {
            ElMessage.error(err)
            next({ path: '/' })
          })
        })
      } else {
        if (!checkProfileInitialized(to, next)) return
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      redirectToCas(to)   // 让 /login 也跳 CAS
      // NProgress.done()

      // 在免登录白名单，直接进入
      next()
    } else {
      // 重定向到CAS登录页
      redirectToCas(to)
      // next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      // NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
