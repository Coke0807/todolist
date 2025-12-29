import axios from 'axios'

const client = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

const unwrap = (response) => response.data?.data ?? null

/**
 * 查询任务列表，供前端进行筛选排序。
 */
export const fetchTasks = async () => {
  const response = await client.get('/tasks')
  return unwrap(response) || []
}

/**
 * 创建任务记录。
 */
export const createTask = async (payload) => {
  const response = await client.post('/tasks', payload)
  return unwrap(response)
}

/**
 * 更新任务。
 */
export const updateTask = async (id, payload) => {
  const response = await client.put(`/tasks/${id}`, payload)
  return unwrap(response)
}

/**
 * 删除任务。
 */
export const deleteTask = async (id) => {
  await client.delete(`/tasks/${id}`)
}

/**
 * 切换完成状态。
 */
export const toggleTask = async (id) => {
  const response = await client.patch(`/tasks/${id}/toggle`)
  return unwrap(response)
}
