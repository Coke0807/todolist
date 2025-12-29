<script setup>
import dayjs from 'dayjs'
import { computed } from 'vue'
import { Check, Close, Delete, Edit } from '@element-plus/icons-vue'

const props = defineProps({
  tasks: {
    type: Array,
    default: () => [],
  },
  loading: Boolean,
})

const emit = defineEmits(['toggle', 'edit', 'remove'])

const priorityMeta = {
  HIGH: { text: '高', type: 'danger' },
  MEDIUM: { text: '中', type: 'warning' },
  LOW: { text: '低', type: 'success' },
}

const formatDate = (value) => (value ? dayjs(value).format('YYYY-MM-DD') : '未设置')

/**
 * 格式化日期时间，用于显示创建和更新时间。
 */
const formatDateTime = (value) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '未设置')

/**
 * 根据优先级返回配色方案，保持列表色彩一致。
 */
const priorityType = (priority) => priorityMeta[priority]?.type || 'info'
</script>

<template>
  <div class="task-list" v-loading="loading">
    <transition-group name="list" tag="div" class="task-grid">
      <el-card v-for="item in tasks" :key="item.id" class="task-card" shadow="hover">
        <div class="task-card__header">
          <div class="title">
            <el-tag :type="item.completed ? 'success' : 'info'" effect="dark" round>
              {{ item.completed ? '已完成' : '进行中' }}
            </el-tag>
            <h3>{{ item.title }}</h3>
          </div>
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">操作</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="emit('toggle', item)">
                  {{ item.completed ? '标记未完成' : '快速完成' }}
                </el-dropdown-item>
                <el-dropdown-item @click="emit('edit', item)">编辑</el-dropdown-item>
                <el-dropdown-item divided @click="emit('remove', item)" style="color: #f56c6c">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <p class="desc">{{ item.description || '暂无描述' }}</p>
        <div class="meta">
          <el-tag :type="priorityType(item.priority)" effect="plain">优先级：{{ priorityMeta[item.priority]?.text }}</el-tag>
          <el-tag type="info" effect="plain">截止：{{ formatDate(item.dueDate) }}</el-tag>
        </div>
        <footer>
          <small>创建：{{ formatDateTime(item.createdAt) }}</small>
          <small>更新：{{ formatDateTime(item.updatedAt) }}</small>
        </footer>
        <div class="actions">
          <el-button :type="item.completed ? 'warning' : 'success'" plain @click="emit('toggle', item)">
            <el-icon><component :is="item.completed ? Close : Check" /></el-icon>
            {{ item.completed ? '标记未完成' : '标记完成' }}
          </el-button>
          <el-button text type="primary" @click="emit('edit', item)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button text type="danger" @click="emit('remove', item)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </el-card>
    </transition-group>
    <el-empty v-if="!tasks.length && !loading" description="暂无任务，开始创建吧" />
  </div>
</template>

<style scoped>
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.task-card {
  backdrop-filter: blur(12px);
  border-radius: 18px;
}

.task-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.task-card__header h3 {
  margin: 6px 0 0;
  font-size: 1.1rem;
}

.desc {
  min-height: 60px;
  color: var(--text-secondary);
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

footer {
  display: flex;
  justify-content: space-between;
  color: var(--text-tertiary);
  font-size: 0.85rem;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
