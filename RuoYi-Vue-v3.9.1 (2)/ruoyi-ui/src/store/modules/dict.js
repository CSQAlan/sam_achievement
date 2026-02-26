import { defineStore } from 'pinia'

export const useDictStore = defineStore('dict', {
  state: () => ({
    dict: []
  }),

  actions: {
    // 设置字典
    setDict(data) {
      const { key, value } = data
      if (key !== null && key !== "") {
        this.dict.push({
          key: key,
          value: value
        })
      }
    },
    // 删除字典
    removeDict(key) {
      try {
        for (let i = 0; i < this.dict.length; i++) {
          if (this.dict[i].key == key) {
            this.dict.splice(i, 1)
            return true
          }
        }
      } catch (e) {
      }
    },
    // 清空字典
    cleanDict() {
      this.dict = []
    }
  }
})
