<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <template v-if="id">
        <!--        <div class="section">笔记本</div>-->
        <!--        <i class="right chevron icon divider"></i>-->
        <router-link class="section" :to="'/notebooks/'+note.notebook.id">{{note.notebook.name}}</router-link>
        <i class="right chevron icon divider"></i>
        <!--        <div class="section">笔记</div>-->
        <!--        <i class="right chevron icon divider"></i>-->
        <router-link class="section" :to="'/notes/'+id">{{note.title}}</router-link>
        <i class="right chevron icon divider"></i>
        <div class="active section">编辑笔记</div>
      </template>
      <template v-else>
        <div class="active section">创建笔记</div>
      </template>
    </div>
    <div class="ui divider"></div>

    <form class="ui form">
      <div class="required field">
        <label>标题</label>
        <input type="text" name="title" v-model="note.title" placeholder="标题">
      </div>
      <div class="fields">
        <div class="required field">
          <label>笔记本</label>
          <el-select v-model="note.notebookId"
                     filterable
                     remote
                     :remote-method="search"
                     :loading="loading"
                     placeholder="请选择">
            <el-option
              v-for="item in notebooks"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="required field">
          <label>分类</label>
          <el-select v-model="note.categoryId" filterable placeholder="请选择">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="required field">
          <label>访问权限</label>
          <el-radio-group v-model="note.access">
            <el-radio label="PUBLIC" data-tooltip="所有人可以访问">公开</el-radio>
            <el-radio label="SECRET" data-tooltip="知道ID可以访问">秘密</el-radio>
            <el-radio label="PRIVATE" data-tooltip="只有你可以访问">私有</el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="required field">
        <label>内容</label>
        <Editor :options="options" :initialValue="note.content" ref="toastuiEditor"></Editor>
      </div>
      <div class="right floated">
        <button class="ui button" @click="cancel">取消</button>
        <button class="ui primary button" @click.prevent="submit"
                :disabled="!note.title||!note.notebookId||!note.categoryId">保存
        </button>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import 'codemirror/lib/codemirror.css'
  import '@toast-ui/editor/dist/toastui-editor.css'
  import Dropdown from '@/components/Dropdown.vue'
  import {Editor} from '@/components/vue-editor'
  import {Category, Note} from '@/models/Note'
  import {Notebook} from '@/models/Notebook'

  @Component({
    components: {
      Editor,
      Dropdown
    }
  })
  export default class EditNote extends Vue {
    id: string = ''
    loading: boolean = false
    notebookId: string = ''
    notebooks: Notebook[] = []
    categories: Category[] = []
    note: Note = new Note()
    options = {
      initialEditType: 'wysiwyg'
    }

    mounted() {
      document.title = '创建笔记'
      this.id = this.$route.params.id
      this.notebookId = this.$route.query.notebook as string
      if (this.notebookId) {
        this.note.notebookId = this.notebookId
      }
      this.load()
      if (this.id) {
        this.loadNote()
      }
    }

    load() {
      axios.get('/users/-/notebooks').then(({data}) => {
        this.notebooks = data.content
        if (!this.id && !this.notebookId) {
          this.note.notebookId = this.notebooks[0].id
        }
      })
      axios.get('/categories').then(({data}) => {
        this.categories = data.content
        if (!this.id) {
          this.note.categoryId = this.categories[0].id
        }
      })
    }

    search(query: string) {
      this.loading = true
      axios.get('/users/-/notebooks?q=' + query).then(({data}) => {
        this.notebooks = data.content
        this.loading = false
      }, () => {
        this.loading = false
      })
    }

    loadNote() {
      axios.get(`/notes/${this.id}`).then(({data}) => {
        this.options.initialEditType = data.markdown ? 'markdown' : 'wysiwyg'
        this.note = data
        this.note.notebookId = this.note.notebook.id
        this.note.categoryId = this.note.category.id
        document.title = this.note.title + ' - 编辑'
      })
    }

    cancel() {
      if (this.id) {
        this.$router.push('/notes/' + this.id)
      } else if (this.notebookId) {
        this.$router.push('/notebooks/' + this.notebookId)
      } else {
        this.$router.push('/')
      }
    }

    submit() {
      const editor: Editor = this.$refs.toastuiEditor as Editor
      this.note.markdown = editor.invoke('isMarkdownMode')
      this.note.content = this.note.markdown ? editor.invoke('getMarkdown') : editor.invoke('getHtml')
      if (this.id) {
        axios.put(`/notes/${this.id}`, this.note).then(({data}) => {
          this.note = data
          this.$toasted.success('更新成功')
          this.$router.push('/notes/' + data.id)
        })
      } else {
        axios.post(`/notes`, this.note).then(({data}) => {
          this.note = data
          this.$toasted.success('创建成功')
          this.$router.push('/notes/' + data.id)
        })
      }
    }
  }
</script>

<style scoped>

</style>
