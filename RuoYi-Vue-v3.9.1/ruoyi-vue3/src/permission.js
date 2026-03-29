import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']
const profilePath = '/user/profile'

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const isProfileRoute = (path) => {
  return path === profilePath || path.startsWith(`${profilePath}/`)
}

const shouldForceProfile = (path) => {
  return Number(useUserStore().profileInitialized || 0) !== 1 && !isProfileRoute(path)
}

const getProfileRedirect = (targetPath) => {
  const query = {}
  if (targetPath && !isProfileRoute(targetPath)) {
    query.redirect = targetPath
  }
  return { path: profilePath, query }
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (useUserStore().roles.length === 0) {
        isRelogin.show = true
        useUserStore().getInfo().then(() => {
          isRelogin.show = false
          usePermissionStore().generateRoutes().then(accessRoutes => {
            accessRoutes.forEach(route => {
              if (!isHttp(route.path)) {
                router.addRoute(route)
              }
            })
            if (shouldForceProfile(to.path)) {
              next({ ...getProfileRedirect(to.fullPath), replace: true })
            } else {
              next({ ...to, replace: true })
            }
          })
        }).catch(err => {
          useUserStore().logOut().then(() => {
            ElMessage.error(err)
            next({ path: '/' })
          })
        })
      } else if (shouldForceProfile(to.path)) {
        next(getProfileRedirect(to.fullPath))
      } else {
        next()
      }
    }
  } else if (isWhiteList(to.path)) {
    next()
  } else {
    next(`/login?redirect=${to.fullPath}`)
    NProgress.done()
  }
})

router.afterEach(() => {
  NProgress.done()
})