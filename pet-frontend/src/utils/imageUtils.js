/**
 * 图片资源处理工具类
 * 用于统一处理图片URL构建和错误处理
 */

import store from '@/store'

/**
 * 获取完整的图片URL
 * @param {string} imgPath - 图片路径
 * @returns {string} 完整的图片URL
 */
export function getImageUrl(imgPath) {
  // 如果路径为空，返回默认占位图
  if (!imgPath) {
    return getDefaultImage()
  }
  
  // 如果已经是完整URL，直接返回
  if (imgPath.startsWith('http://') || imgPath.startsWith('https://')) {
    return imgPath
  }
  
  // 拼接后端基础URL
  return store.state.baseApi + imgPath
}

/**
 * 获取默认占位图
 * @returns {string} 默认图片路径
 */
export function getDefaultImage() {
  try {
    return require('@/assets/404.png')
  } catch (error) {
    // 如果找不到默认图片，返回一个空的SVG占位符
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2Y1ZjdmYSIvPjx0ZXh0IHg9IjUwIiB5PSI1MCIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjEyIiBmaWxsPSIjOTA5Mzk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5peg5Zu+54mHPC90ZXh0Pjwvc3ZnPg=='
  }
}

/**
 * 检查图片URL是否有效
 * @param {string} url - 图片URL
 * @returns {Promise<boolean>} 是否有效
 */
export function isImageValid(url) {
  return new Promise((resolve) => {
    const img = new Image()
    img.onload = () => resolve(true)
    img.onerror = () => resolve(false)
    img.src = url
  })
}

/**
 * 获取图片预览列表
 * @param {Array<string>} imgPaths - 图片路径数组
 * @returns {Array<string>} 完整URL数组
 */
export function getPreviewImageList(imgPaths) {
  if (!Array.isArray(imgPaths)) {
    return []
  }
  
  return imgPaths
    .filter(path => path) // 过滤空值
    .map(path => getImageUrl(path))
}

/**
 * 创建图片错误处理的Vue组件选项
 * @returns {Object} Vue组件选项
 */
export function createImageErrorHandler() {
  return {
    data() {
      return {
        imageLoadErrors: new Set()
      }
    },
    methods: {
      handleImageError(imgPath, event) {
        console.warn(`图片加载失败: ${imgPath}`)
        this.imageLoadErrors.add(imgPath)
        
        // 替换为默认图片
        if (event && event.target) {
          event.target.src = getDefaultImage()
        }
      },
      
      isImageError(imgPath) {
        return this.imageLoadErrors.has(imgPath)
      }
    }
  }
}