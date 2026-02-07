<template>
  <div class="pdf-thumbnail-container">
    <canvas ref="pdfCanvas" class="pdf-canvas"></canvas>
    <div v-if="loading" class="loading-mask">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span class="loading-text">加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import * as pdfjsLib from 'pdfjs-dist';

// 设置 workerSrc。这里使用 unpkg 的 CDN 链接，或者指向本地 node_modules 
// 为了兼容性，在 Vite 中通常需要处理 worker。
// 简单起见，我们尝试直接指向 node_modules 中的文件（如果构建允许），或者使用 CDN。
// 鉴于本地开发，我们尝试使用 CDN 以避免复杂的 vite worker 配置问题，
// 或者使用 pdfjsLib.GlobalWorkerOptions.workerSrc = new URL('pdfjs-dist/build/pdf.worker.mjs', import.meta.url).toString();
// 尝试标准 Vite 方式：
pdfjsLib.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.js`;

const props = defineProps({
  url: {
    type: String,
    required: true
  }
});

const pdfCanvas = ref(null);
const loading = ref(true);

const renderPdf = async () => {
  if (!props.url) return;
  loading.value = true;
  
  try {
    // 异步下载 PDF
    const loadingTask = pdfjsLib.getDocument(props.url);
    const pdf = await loadingTask.promise;
    
    // 获取第一页
    const page = await pdf.getPage(1);
    
    const canvas = pdfCanvas.value;
    const context = canvas.getContext('2d');
    
    // 设置缩放比例。为了清晰度，可以稍微渲染大一点然后 CSS 缩小，或者直接按容器大小。
    // 这里我们设定一个固定宽度，比如 240px (2x preview width) 以保证清晰度
    const desiredWidth = 240; 
    const viewport = page.getViewport({ scale: 1 });
    const scale = desiredWidth / viewport.width;
    const scaledViewport = page.getViewport({ scale });

    canvas.height = scaledViewport.height;
    canvas.width = scaledViewport.width;

    const renderContext = {
      canvasContext: context,
      viewport: scaledViewport
    };
    
    await page.render(renderContext).promise;
  } catch (error) {
    console.error('Error rendering PDF thumbnail:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  renderPdf();
});

watch(() => props.url, () => {
  renderPdf();
});
</script>

<style scoped>
.pdf-thumbnail-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  background: #fff;
}

.pdf-canvas {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持比例 */
  display: block;
}

.loading-mask {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(255,255,255,0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}
.loading-text {
  margin-top: 5px;
}
</style>
