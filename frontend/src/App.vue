<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Moon, Sunny } from '@element-plus/icons-vue'
import TaskForm from './components/TaskForm.vue'
import TaskList from './components/TaskList.vue'
import { createTask, deleteTask, fetchTasks, toggleTask, updateTask } from './api/taskService'

const tasks = ref([])
const loading = ref(false)
const creating = ref(false)
const updating = ref(false)
const editorVisible = ref(false)
const editingTask = ref(null)

const statusFilter = ref('all')
const sortMode = ref('created_desc')

const THEME_KEY = 'todo-theme'
const theme = ref(localStorage.getItem(THEME_KEY) || 'light')

const filterOptions = [
  { label: '全部任务', value: 'all' },
  { label: '进行中', value: 'active' },
  { label: '已完成', value: 'done' },
]

const sortOptions = [
  { label: '创建时间：最新优先', value: 'created_desc' },
  { label: '截止时间：最早优先', value: 'due_asc' },
  { label: '优先级：高到低', value: 'priority_desc' },
]

const stats = computed(() => ({
  total: tasks.value.length,
  done: tasks.value.filter((item) => item.completed).length,
}))

const filteredTasks = computed(() => {
  const list = [...tasks.value]
  const filtered = list.filter((item) => {
    if (statusFilter.value === 'active') return !item.completed
    if (statusFilter.value === 'done') return item.completed
    return true
  })

  return filtered.sort((a, b) => {
    switch (sortMode.value) {
      case 'due_asc':
        return new Date(a.dueDate || '9999-12-31') - new Date(b.dueDate || '9999-12-31')
      case 'priority_desc':
        const order = { HIGH: 3, MEDIUM: 2, LOW: 1 }
        return (order[b.priority] || 0) - (order[a.priority] || 0)
      case 'created_desc':
      default:
        return new Date(b.createdAt) - new Date(a.createdAt)
    }
  })
})

const applyTheme = () => {
  document.documentElement.dataset.theme = theme.value
}

watch(
  theme,
  (value) => {
    localStorage.setItem(THEME_KEY, value)
    applyTheme()
  },
  { immediate: true }
)

onMounted(async () => {
  await loadTasks()
})

watch(editorVisible, (opened) => {
  if (!opened) {
    editingTask.value = null
  }
})

/**
 * 拉取任务列表并更新本地状态。
 */
const loadTasks = async () => {
  loading.value = true
  try {
    tasks.value = await fetchTasks()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载任务失败')
  } finally {
    loading.value = false
  }
}

/**
 * 清洗即将提交到后端的任务数据，确保字段安全。
 */
const normalizePayload = (payload) => {
  return {
    ...payload,
    description: payload.description?.trim() || '',
    dueDate: payload.dueDate || null,
    completed: Boolean(payload.completed),
  }
}

/**
 * 创建任务后刷新列表。
 */
const handleCreate = async (payload) => {
  creating.value = true
  try {
    await createTask(normalizePayload(payload))
    ElMessage.success('任务已创建')
    await loadTasks()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '创建任务失败')
  } finally {
    creating.value = false
  }
}

const showEditor = (task) => {
  editingTask.value = { ...task }
  editorVisible.value = true
}

/**
 * 更新任务信息，并保持弹窗状态与数据一致。
 */
const handleUpdate = async (payload) => {
  if (!editingTask.value) return
  updating.value = true
  try {
    await updateTask(editingTask.value.id, normalizePayload(payload))
    ElMessage.success('任务已更新')
    editorVisible.value = false
    editingTask.value = null
    await loadTasks()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新任务失败')
  } finally {
    updating.value = false
  }
}

/**
 * 删除任务前弹出二次确认，避免误操作。
 */
const handleDelete = async (task) => {
  try {
    await ElMessageBox.confirm(`确认删除任务「${task.title}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await deleteTask(task.id)
    ElMessage.success('任务已删除')
    await loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除任务失败')
    }
  }
}

/**
 * 切换任务完成状态。
 */
const handleToggle = async (task) => {
  try {
    await toggleTask(task.id)
    await loadTasks()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '切换状态失败')
  }
}

const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
}

const themeIcon = computed(() => (theme.value === 'light' ? Moon : Sunny))
const themeLabel = computed(() => (theme.value === 'light' ? '切换暗色' : '切换亮色'))
const drawerSize = computed(() => {
  if (typeof window === 'undefined') return '480px'
  return window.innerWidth < 768 ? '100%' : '480px'
})
</script>

<template>
  <div class="page">
    <header class="hero glass-card">
      <div>
        <p class="eyebrow">Focus & Finish</p>
        <h1>智能待办看板</h1>
        <p class="subtitle">使用现代化设计与暗色模式，轻松管理所有任务。</p>
      </div>
      <div class="hero-actions">
        <el-statistic title="任务总数" :value="stats.total" />
        <el-divider direction="vertical" />
        <el-statistic title="已完成" :value="stats.done" />
        <el-button class="theme-btn" circle @click="toggleTheme">
          <el-icon><component :is="themeIcon" /></el-icon>
        </el-button>
        <span>{{ themeLabel }}</span>
      </div>
    </header>

    <section class="toolbar glass-card">
      <el-select v-model="statusFilter" placeholder="筛选任务" size="large">
        <el-option v-for="option in filterOptions" :key="option.value" :label="option.label" :value="option.value" />
      </el-select>
      <el-select v-model="sortMode" placeholder="排序方式" size="large">
        <el-option v-for="option in sortOptions" :key="option.value" :label="option.label" :value="option.value" />
      </el-select>
      <el-button type="primary" plain @click="loadTasks">刷新数据</el-button>
    </section>

    <section class="content">
      <div class="form-column">
        <TaskForm :loading="creating" submit-text="创建任务" @submit="handleCreate" />
      </div>
      <div class="list-column">
        <TaskList :tasks="filteredTasks" :loading="loading" @toggle="handleToggle" @edit="showEditor" @remove="handleDelete" />
      </div>
    </section>

    <el-drawer v-model="editorVisible" title="编辑任务" :size="drawerSize">
      <TaskForm v-if="editorVisible" :task="editingTask" :loading="updating" submit-text="保存修改" show-completed @submit="handleUpdate" />
    </el-drawer>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  padding: 40px;
  background: var(--app-gradient);
  color: var(--text-primary);
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.hero h1 {
  font-size: 2.8rem;
  margin: 4px 0;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.eyebrow {
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-size: 0.9rem;
}

.subtitle {
  color: var(--text-secondary);
  max-width: 520px;
}

.toolbar {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
}

.content {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
}

.form-column,
.list-column {
  width: 100%;
}

.glass-card {
  background: var(--card-bg);
  border-radius: 24px;
  padding: 24px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--card-border);
  backdrop-filter: blur(16px);
}

.theme-btn {
  border-radius: 50%;
}

@media (max-width: 1024px) {
  .content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 20px;
  }

  .hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
