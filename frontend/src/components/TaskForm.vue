<script setup>
import { reactive, ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '创建任务',
  },
  submitText: {
    type: String,
    default: '保存任务',
  },
  loading: Boolean,
  task: {
    type: Object,
    default: null,
  },
  showCompleted: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['submit'])

const formRef = ref()
const formModel = reactive({
  title: '',
  description: '',
  priority: 'MEDIUM',
  dueDate: '',
  completed: false,
})

const rules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 2, max: 120, message: '标题长度需在2-120个字符之间', trigger: 'blur' },
  ],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  dueDate: [
    {
      validator: (_, value, callback) => {
        if (value && value < new Date().toISOString().split('T')[0]) {
          callback(new Error('截止日期不能早于今天'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

const priorityOptions = [
  { label: '高优先级', value: 'HIGH', tag: 'danger' },
  { label: '中优先级', value: 'MEDIUM', tag: 'warning' },
  { label: '低优先级', value: 'LOW', tag: 'success' },
]

const resetForm = () => {
  formModel.title = ''
  formModel.description = ''
  formModel.priority = 'MEDIUM'
  formModel.dueDate = ''
  formModel.completed = false
  formRef.value?.clearValidate()
}

watch(
  () => props.task,
  (task) => {
    if (!task) {
      resetForm()
      return
    }
    formModel.title = task.title ?? ''
    formModel.description = task.description ?? ''
    formModel.priority = task.priority ?? 'MEDIUM'
    formModel.dueDate = task.dueDate ?? ''
    formModel.completed = Boolean(task.completed)
  },
  { immediate: true }
)

/**
 * 触发表单校验并向父组件回传整理好的数据。
 */
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    emit('submit', { ...formModel })
  })
}
</script>

<template>
  <el-card class="glass-card">
    <template #header>
      <div class="card-header">
        <span>{{ title }}</span>
        <el-tag round type="info" effect="plain">{{ formModel.priority }}</el-tag>
      </div>
    </template>
    <el-form ref="formRef" :model="formModel" :rules="rules" label-position="top" class="task-form" size="large">
      <el-form-item label="任务标题" prop="title">
        <el-input v-model.trim="formModel.title" placeholder="例如：撰写接口文档" maxlength="120" show-word-limit />
      </el-form-item>
      <el-form-item label="任务描述">
        <el-input
          v-model.trim="formModel.description"
          type="textarea"
          :rows="4"
          maxlength="1000"
          show-word-limit
          placeholder="补充任务细节、验收标准或相关链接"
        />
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="formModel.priority" placeholder="选择优先级">
          <el-option v-for="option in priorityOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="截止日期" prop="dueDate">
        <el-date-picker
          v-model="formModel.dueDate"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
          :disabled-date="(date) => date.getTime() < Date.now() - 86400000"
        />
      </el-form-item>
      <el-form-item v-if="showCompleted" label="完成状态">
        <el-switch v-model="formModel.completed" active-text="已完成" inactive-text="进行中" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">{{ submitText }}</el-button>
        <el-button v-if="props.task" @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.task-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}
</style>
