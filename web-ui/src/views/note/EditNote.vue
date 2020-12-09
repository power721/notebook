<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <template v-if="notebook.name">
        <router-link class="section" to="/notebooks">笔记本</router-link>
        <i class="right chevron icon divider"></i>
        <router-link class="section" :to="'/notebooks/'+notebook.id">{{notebook.name}}</router-link>
        <i class="right chevron icon divider"></i>
      </template>
      <template v-if="category.name">
        <router-link class="section" to="/categories">分类</router-link>
        <i class="right chevron icon divider"></i>
        <router-link class="section" :to="'/categories/'+category.id">{{category.name}}</router-link>
        <i class="right chevron icon divider"></i>
      </template>
      <template v-if="id">
        <div class="section">笔记本</div>
        <i class="right chevron icon divider"></i>
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
        <!--        <Editor :options="options" :initialValue="note.content" ref="toastuiEditor"></Editor>-->
        <Editor :init="config" v-model="note.content"></Editor>
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
  /* eslint-disable @typescript-eslint/camelcase */
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import Dropdown from '@/components/Dropdown.vue'
  //import {Editor} from '@/components/vue-editor'
  import Editor from '@tinymce/tinymce-vue'
  import {Note} from '@/models/Note'
  import {Notebook} from '@/models/Notebook'
  import {Category} from '@/models/Category'

  @Component({
    components: {
      Editor,
      Dropdown
    }
  })
  export default class EditNote extends Vue {
    id: string = ''
    loading: boolean = false
    notebook: Notebook = new Notebook()
    category: Category = new Category()
    notebooks: Notebook[] = []
    categories: Category[] = []
    note: Note = new Note()
    options = {
      initialEditType: 'wysiwyg'
    }
    config = {
      height: 550,
      branding: false,
      plugins: [
        'autolink link media table advlist lists hr',
        'code codesample image preview fullscreen',
        'insertdatetime toc paste wordcount help'
      ],
      images_upload_url: '/images',
      codesample_global_prismjs: true,
      codesample_languages: [
        {text: 'Java', value: 'java'},
        {text: 'JavaStacktrace', value: 'javastacktrace'},
        {text: 'Kotlin', value: 'kotlin'},
        {text: 'JavaScript', value: 'javascript'},
        {text: 'Typescript', value: 'typescript'},
        {text: 'CSS', value: 'css'},
        {text: 'Docker', value: 'docker'},
        {text: 'Bash', value: 'bash'},
        {text: 'Regex', value: 'regex'},
        {text: 'Nginx', value: 'nginx'},
        {text: 'SQL', value: 'sql'},
        {text: 'JSON', value: 'json'},
        {text: 'XML', value: 'xml'},
        {text: 'YAML', value: 'yaml'},
      ],
      content_css: [
        'https://cdn.tiny.cloud/1/qagffr3pkuv17a8on1afax661irst1hbr4e6tbv888sz91jc/tinymce/5.5.1-99/skins/ui/oxide/content.min.css',
        'prism.css',
      ],
      toolbar:
        'undo redo | formatselect | bold italic backcolor | \
        alignleft aligncenter alignright alignjustify | link image media | \
        bullist numlist outdent indent | codesample | removeformat code preview fullscreen | help'
    }

    mounted() {
      document.title = '创建笔记'
      this.id = this.$route.params.id
      this.notebook.id = this.$route.query.notebook as string
      if (this.notebook.id) {
        this.note.notebookId = this.notebook.id
      }
      this.category.id = this.$route.query.category as string
      if (this.category.id) {
        this.note.categoryId = this.category.id
      }
      this.load()
      if (this.id) {
        this.loadNote()
      }
    }

    load() {
      axios.get('/users/-/notebooks').then(({data}) => {
        this.notebooks = data.content
        if (!this.id && !this.notebook.id) {
          this.note.notebookId = this.notebooks[0].id
        }
        if (this.notebook.id) {
          this.notebook.name = this.notebooks.find(e => e.id === this.notebook.id)?.name || ''
        }
      })
      axios.get('/categories').then(({data}) => {
        this.categories = data.content
        if (!this.id && !this.category.id) {
          this.note.categoryId = this.categories[0].id
        }
        if (this.category.id) {
          this.category.name = this.categories.find(e => e.id === this.category.id)?.name || ''
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
      } else if (this.notebook.id) {
        this.$router.push('/notebooks/' + this.notebook.id)
      } else if (this.category.id) {
        this.$router.push('/categories/' + this.category.id)
      } else {
        this.$router.push('/')
      }
    }

    submit() {
      // const editor: Editor = this.$refs.toastuiEditor as Editor
      // this.note.markdown = editor.invoke('isMarkdownMode')
      // this.note.content = this.note.markdown ? editor.invoke('getMarkdown') : editor.invoke('getHtml')
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
