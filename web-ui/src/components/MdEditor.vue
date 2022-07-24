<template>
  <Editor class="editor"
          :plugins="plugins"
          :locale="locale"
          :value="content"
          :uploadImages="uploadImages"
          @change="onTextChanged"></Editor>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator'
import axios from 'axios'
import {Editor} from '@bytemd/vue'
import zhHans from 'bytemd/locales/zh_Hans.json'
import {plugins} from '@/plugins/bytemd-plugins'

@Component({
  components: {
    Editor,
  },
  model: {
    prop: 'content',
    event: 'changed'
  },
})
export default class MdEditor extends Vue {
  @Prop() private content!: string

  plugins = plugins
  locale = zhHans

  onTextChanged(value: string) {
    this.$emit('changed', value)
  }

  uploadImages(files: File[]) {
    const formData = new FormData()
    for (const file of files) {
      formData.append('file', file)
    }

    return axios.post('/images/multiple', formData).then(({data}) => {
      return data
    })
  }
}
</script>

<style scoped>

</style>
