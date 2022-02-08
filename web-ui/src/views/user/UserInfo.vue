<template>
  <div class="ui left aligned container">
    <div class="ui form">
      <div class="required field">
        <label>编辑器模式</label>
        <el-radio-group v-model="editorMode">
          <el-radio label="html" data-tooltip="TinyMCE">HTML</el-radio>
          <el-radio label="markdown" data-tooltip="ByteMD">Markdown</el-radio>
        </el-radio-group>
      </div>
      <div class="required field">
        <label>Markdown主题</label>
        <el-select v-model="mdTheme" filterable>
          <el-option key="" label="默认" value="">
          </el-option>
          <el-option
            v-for="item in themes"
            :key="item"
            :label="item"
            :value="item">
          </el-option>
        </el-select>
      </div>
      <div class="field">
        <label>个性签名</label>
        <input type="text" placeholder="输入个性签名" maxlength="100" v-model="signature">
        <div class="ui segment">
          <MdViewer :content="content"></MdViewer>
        </div>
      </div>
      <button class="ui primary button" @click.prevent="submit">更新</button>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator'
import axios from 'axios'
import store from '@/store'
import configService from '@/services/config.service'
import MdViewer from '@/components/MdViewer.vue'
import {themes} from '@/models/themes'

@Component({
  components: {MdViewer},
  computed: {
    content: function () {
      const theme = this.mdTheme ? `---\ntheme: ${this.mdTheme}\n---\n` : ''
      return theme
        + '## Markdown主题预览\n'
        + '这是一个 **粗体文本**， 这是一个 *斜体文本*。'
        + '打开~~CMD~~ Terminal， 输入命令： `nano`。\n'
        + '```ts\n' +
        'export function loadJs(url: string) {\n' +
        '  const script = document.createElement(\'script\')\n' +
        '  script.type = \'text/javascript\'\n' +
        '  script.src = url\n' +
        '  document.head.appendChild(script)\n' +
        '}\n' +
        '```\n'
        + '1. 前端开发\n'
        + '2. 后端开发\n'
        + '3. 全栈开发\n'
        + '[Google](https://google.com/)\n'
        + (this.signature ? '>' + this.signature : '')
    }
  }
})
export default class UserInfo extends Vue {
  editorMode = this.$store.getters.editorMode
  mdTheme = this.$store.state.user.mdTheme || ''
  signature = this.$store.state.user.signature || ''
  themes = themes

  mounted() {
    configService.setTitle('用户设置')
  }

  submit() {
    const data = {editorMode: this.editorMode, mdTheme: this.mdTheme, signature: this.signature}
    axios.post('/users/-/info', data).then(({data}) => {
      store.commit('user', data)
      this.$toasted.show('更新成功')
    }).catch((error) => {
      console.log(error)
    })
  }
}
</script>

<style>
</style>
