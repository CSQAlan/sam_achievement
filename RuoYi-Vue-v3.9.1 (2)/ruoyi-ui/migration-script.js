/**
 * Vue2 到 Vue3 自动迁移脚本
 * 
 * 此脚本会批量更新项目中的文件，将 Vue2 语法转换为 Vue3
 * 
 * 使用方法：
 * node migration-script.js
 */

const fs = require('fs')
const path = require('path')

// 需要处理的文件扩展名
const extensions = ['.vue', '.js']

// 需要排除的目录
const excludeDirs = ['node_modules', 'dist', '.git']

// 替换规则
const replacements = [
  // 环境变量
  {
    pattern: /process\.env\.VUE_APP_/g,
    replacement: 'import.meta.env.VITE_APP_',
    description: '环境变量前缀'
  },
  // Element UI 导入
  {
    pattern: /from ['"]element-ui['"]/g,
    replacement: 'from \'element-plus\'',
    description: 'Element UI 导入'
  },
  {
    pattern: /import\s+{\s*Message\s*}\s+from\s+['"]element-ui['"]/g,
    replacement: 'import { ElMessage } from \'element-plus\'',
    description: 'Message 导入'
  },
  {
    pattern: /import\s+{\s*MessageBox\s*}\s+from\s+['"]element-ui['"]/g,
    replacement: 'import { ElMessageBox } from \'element-plus\'',
    description: 'MessageBox 导入'
  },
  {
    pattern: /\bMessage\./g,
    replacement: 'ElMessage.',
    description: 'Message 使用'
  },
  {
    pattern: /\bMessageBox\./g,
    replacement: 'ElMessageBox.',
    description: 'MessageBox 使用'
  },
  // 生命周期钩子
  {
    pattern: /\bbeforeDestroy\b/g,
    replacement: 'beforeUnmount',
    description: 'beforeDestroy 生命周期'
  },
  {
    pattern: /\bdestroyed\b/g,
    replacement: 'unmounted',
    description: 'destroyed 生命周期'
  },
  // Store 相关
  {
    pattern: /this\.\$store\.dispatch\(['"]GenerateRoutes['"]\)/g,
    replacement: 'usePermissionStore().generateRoutes()',
    description: 'GenerateRoutes action'
  },
  {
    pattern: /this\.\$store\.dispatch\(['"]Login['"],\s*/g,
    replacement: 'useUserStore().login(',
    description: 'Login action'
  },
  {
    pattern: /this\.\$store\.dispatch\(['"]LogOut['"]\)/g,
    replacement: 'useUserStore().logOut()',
    description: 'LogOut action'
  },
  {
    pattern: /this\.\$store\.dispatch\(['"]GetInfo['"]\)/g,
    replacement: 'useUserStore().getInfo()',
    description: 'GetInfo action'
  },
  {
    pattern: /this\.\$store\.state\.user\.token/g,
    replacement: 'useUserStore().token',
    description: 'user.token state'
  },
  {
    pattern: /this\.\$store\.state\.user\.roles/g,
    replacement: 'useUserStore().roles',
    description: 'user.roles state'
  },
  {
    pattern: /this\.\$store\.getters\.roles/g,
    replacement: 'useUserStore().roles',
    description: 'roles getter'
  },
  {
    pattern: /this\.\$store\.getters\.permissions/g,
    replacement: 'useUserStore().permissions',
    description: 'permissions getter'
  },
  // Router
  {
    pattern: /router\.addRoutes\(/g,
    replacement: 'router.addRoute(',
    description: 'addRoutes 方法'
  }
]

// 统计信息
const stats = {
  filesProcessed: 0,
  filesModified: 0,
  replacementsMade: {}
}

// 初始化统计
replacements.forEach(r => {
  stats.replacementsMade[r.description] = 0
})

/**
 * 递归遍历目录
 */
function walkDir(dir, callback) {
  const files = fs.readdirSync(dir)
  
  files.forEach(file => {
    const filePath = path.join(dir, file)
    const stat = fs.statSync(filePath)
    
    if (stat.isDirectory()) {
      if (!excludeDirs.includes(file)) {
        walkDir(filePath, callback)
      }
    } else {
      callback(filePath)
    }
  })
}

/**
 * 处理单个文件
 */
function processFile(filePath) {
  const ext = path.extname(filePath)
  
  if (!extensions.includes(ext)) {
    return
  }
  
  stats.filesProcessed++
  
  let content = fs.readFileSync(filePath, 'utf8')
  let modified = false
  let originalContent = content
  
  replacements.forEach(({ pattern, replacement, description }) => {
    const matches = content.match(pattern)
    if (matches) {
      content = content.replace(pattern, replacement)
      stats.replacementsMade[description] += matches.length
      modified = true
    }
  })
  
  if (modified) {
    fs.writeFileSync(filePath, content, 'utf8')
    stats.filesModified++
    console.log(`✓ 已更新: ${filePath}`)
  }
}

/**
 * 主函数
 */
function main() {
  console.log('开始 Vue2 到 Vue3 迁移...\n')
  
  const srcDir = path.join(__dirname, 'src')
  
  if (!fs.existsSync(srcDir)) {
    console.error('错误: src 目录不存在')
    process.exit(1)
  }
  
  walkDir(srcDir, processFile)
  
  console.log('\n迁移完成！')
  console.log('='.repeat(50))
  console.log(`处理文件数: ${stats.filesProcessed}`)
  console.log(`修改文件数: ${stats.filesModified}`)
  console.log('\n替换统计:')
  
  Object.entries(stats.replacementsMade).forEach(([desc, count]) => {
    if (count > 0) {
      console.log(`  ${desc}: ${count} 处`)
    }
  })
  
  console.log('\n注意事项:')
  console.log('1. 请手动检查所有修改的文件')
  console.log('2. 某些复杂的 store 调用可能需要手动调整')
  console.log('3. 组件中的 $refs、$emit 等需要根据实际情况调整')
  console.log('4. 建议使用 Git 查看所有变更')
}

// 运行脚本
main()
