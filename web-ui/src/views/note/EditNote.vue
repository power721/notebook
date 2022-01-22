<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <template v-if="notebook.name">
        <router-link class="section" to="/notebooks">笔记本</router-link>
        <i class="right chevron icon divider"></i>
        <router-link class="section" :to="'/notebooks/'+(notebook.slug?notebook.slug:notebook.id)">
          {{ notebook.name }}
        </router-link>
        <i class="right chevron icon divider"></i>
      </template>
      <template v-if="category.name">
        <router-link class="section" to="/categories">分类</router-link>
        <i class="right chevron icon divider"></i>
        <router-link class="section" :to="'/categories/'+(category.slug?category.slug:category.id)">
          {{ category.name }}
        </router-link>
        <i class="right chevron icon divider"></i>
      </template>
      <template v-if="id">
        <router-link class="section" :to="'/notebooks/'+(note.notebook.slug?note.notebook.slug:note.notebook.id)">
          {{ note.notebook.name }}
        </router-link>
        <i class="right chevron icon divider"></i>
        <router-link class="section" :to="'/notes/'+(note.slug?note.slug:note.id)">{{ title }}</router-link>
        <i class="right chevron icon divider"></i>
        <div class="active section">编辑笔记</div>
      </template>
      <template v-else>
        <div class="active section">创建笔记</div>
      </template>
    </div>
    <div class="ui divider"></div>

    <form class="ui form">
      <div class="ui active inverted dimmer" v-if="noteLoading">
        <div class="ui text loader">加载中</div>
      </div>
      <div class="required field">
        <label>标题</label>
        <input type="text" name="title" autocomplete="off" v-model="note.title" @change="clear" placeholder="标题">
      </div>
      <div class="field">
        <label>slug</label>
        <input type="text" name="slug" autocomplete="off" v-model="note.slug" @change="clear" placeholder="slug">
      </div>
      <div class="fields">
        <div class="required field">
          <label>笔记本</label>
          <el-select v-model="note.notebookId"
                     filterable
                     remote
                     :remote-method="searchNotebooks"
                     :loading="loading">
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
          <el-select v-model="note.categoryId"
                     filterable
                     remote
                     :remote-method="searchCategories"
                     :loading="loading">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="field">
          <label>标签</label>
          <multiselect
            v-model="note.tags"
            :multiple="true"
            :taggable="true"
            :searchable="true"
            :options="tags"
            label="name"
            track-by="name"
            tag-placeholder="添加为标签"
            placeholder="搜索或者添加标签"
            deselectLabel=""
            selectLabel=""
            :max="5"
            :limit="5"
            @tag="addTag"
            @keypress.enter="addTag"
            @search-change="loadTags"
          ></multiselect>
        </div>
        <div class="required field">
          <label>访问权限</label>
          <el-radio-group v-model="note.access">
            <el-radio label="PUBLIC" data-tooltip="所有人可以访问" v-if="notebook.access==='PUBLIC'">公开</el-radio>
            <el-radio label="SECRET" data-tooltip="知道ID才能访问" v-if="notebook.access!=='PRIVATE'">秘密</el-radio>
            <el-radio label="PRIVATE" data-tooltip="只有你可以访问">私有</el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="required field">
        <label>内容</label>
        <Editor :init="config" v-model="note.content" @change="clear"></Editor>
      </div>
      <div class="right floated">
        <button class="ui button" @click.prevent="cancel">取消</button>
        <button class="ui primary button" @click.prevent="saveDraft" :disabled="!note.title||!note.content">
          存草稿
        </button>
        <button class="ui primary button" @click.prevent="submit"
                :disabled="!note.title||!note.content||!note.notebookId||!note.categoryId">
          保存
        </button>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
/* eslint-disable @typescript-eslint/camelcase */
import axios from 'axios'
import {Component, Vue} from 'vue-property-decorator'
import Editor from '@tinymce/tinymce-vue'
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'
import {Note, Tag} from '@/models/Note'
import {Notebook} from '@/models/Notebook'
import {Category} from '@/models/Category'
import accountService from '@/services/account.service'
import configService from '@/services/config.service'
import {ToastObject} from "vue-toasted";

const DRAFT_KEY = 'noteDraft-'

interface BlobInfo {
  id: () => string;
  name: () => string;
  filename: () => string;
  blob: () => Blob;
  base64: () => string;
  blobUri: () => string;
  uri: () => string | undefined;
}

@Component({
  components: {
    Editor,
    Multiselect
  }
})
export default class EditNote extends Vue {
  id: string = ''
  title: string = ''
  loading: boolean = false
  noteLoading: boolean = false
  notebook: Notebook = new Notebook()
  category: Category = new Category()
  notebooks: Notebook[] = []
  categories: Category[] = []
  tags: Tag[] = []
  note: Note = new Note()
  noteCache: Note = new Note()
  draftHandler: number = 0
  config = {
    height: document.body.clientHeight - 530,
    branding: false,
    language: 'zh_CN',
    plugins: [
      'autolink link media table advlist lists hr upfile attachment',
      'code codesample charmap image imagetools quickbars preview fullscreen',
      'insertdatetime toc paste wordcount help searchreplace emoticons'
    ],
    relative_urls : false,
    emoticons_database_url: '/emojis.js',
    default_link_target: '_blank',
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
    content_css: [],
    toolbar:
      'formatselect | bold italic backcolor | \
      alignleft aligncenter alignright alignjustify | link image media upfile attachment | \
      bullist numlist outdent indent | charmap emoticons codesample | removeformat code preview fullscreen | help',
    file_callback: function (file: File, callback: (url: string, details: unknown) => void) {
      const formData = new FormData();
      formData.append("file", file);
      axios.post('/files', formData).then((res) => {
        callback(res.data.url, {text: res.data.name})
      }).catch((error) => {
        console.error(error)
      })
    },
    attachment_max_size: 104857600,
    attachment_upload_handler: function (file: File, succFun: (url: string) => void, failFun: (error: string) => void, progressCallback: (progress: number) => void) {
      const formData = new FormData()
      formData.append("file", file)
      axios.post('/files', formData, {
        onUploadProgress: function (progressEvent) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          progressCallback(percent)
        }
      }).then((res) => {
        succFun(res.data.url)
      }).catch((error) => {
        console.log(error)
        failFun(error.message)
      })
    },
    images_upload_handler: function (blobInfo: BlobInfo, success: (url: string) => void, failure: (error: string) => void, progress: (progress: number) => void) {
      const formData = new FormData()
      formData.append('file', blobInfo.blob(), blobInfo.filename())
      axios.post('/images', formData, {
        onUploadProgress: function (progressEvent) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          progress(percent)
        }
      }).then((res) => {
        success(res.data.url)
      }).catch((error) => {
        console.log(error)
        failure(error.message)
      })
    }
  }

  mounted() {
    this.id = this.$route.params.id || ''
    this.notebook.id = this.$route.query.notebook as string
    if (this.notebook.id) {
      this.note.notebookId = this.notebook.id
      this.loadNotebook()
    }
    this.category.id = this.$route.query.category as string
    if (this.category.id) {
      this.note.categoryId = this.category.id
    }
    this.load()
    if (this.id) {
      configService.setTitle('编辑笔记')
      this.loadNote().then(() => this.loadDraft())
    } else {
      configService.setTitle('创建笔记')
      this.loadDraft()
    }
    this.draftHandler = setInterval(() => this.saveDraft(), 30000)
  }

  destroyed() {
    clearInterval(this.draftHandler)
    this.$toasted.clear()
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
    this.loadTags('')
  }

  loadNotebook() {
    axios.get(`/notebooks/${this.notebook.id}`).then(({data}) => {
      this.notebook = data
      this.note.access = this.notebook.access
    }, () => {
      this.$router.push('/')
    })
  }

  addTag(name: string) {
    const tag = {id: 0, name: name}
    axios.post('/tags?size=10', tag).then(() => {
      this.tags.push(tag)
      this.note.tags.push(tag)
    })
  }

  loadTags(query: string) {
    this.loading = true
    axios.get('/tags?size=10&q=' + query).then(({data}) => {
      this.tags = data.content
      this.loading = false
    }, () => {
      this.loading = false
    })
  }

  searchNotebooks(query: string) {
    this.loading = true
    axios.get('/users/-/notebooks?q=' + query).then(({data}) => {
      this.notebooks = data.content
      this.loading = false
    }, () => {
      this.loading = false
    })
  }

  searchCategories(query: string) {
    this.loading = true
    axios.get('/categories?q=' + query).then(({data}) => {
      this.categories = data.content
      this.loading = false
    }, () => {
      this.loading = false
    })
  }

  loadNote() {
    this.noteLoading = true
    return axios.get(`/notes/${this.id}`).then(({data}) => {
      Object.assign(this.note, data)
      this.title = this.note.title
      this.note.notebookId = this.note.notebook.id
      this.note.categoryId = this.note.category.id
      this.noteLoading = false
      if (accountService.account.id !== this.note.author.id) {
        this.$toasted.error('用户无权操作')
        this.$router.push('/')
      }
      configService.setTitle(this.note.title + ' - 编辑')
    }, () => {
      this.noteLoading = false
    })
  }

  cancel() {
    if (this.id) {
      this.$router.push('/notes/' + this.id)
    } else if (this.notebook.id) {
      this.$router.push('/notebooks/' + (this.notebook.slug ? this.notebook.slug : this.notebook.id))
    } else if (this.category.id) {
      this.$router.push('/categories/' + (this.category.slug ? this.category.slug : this.category.id))
    } else {
      this.$router.push('/')
    }
  }

  submit() {
    if (this.id) {
      axios.put(`/notes/${this.note.id}`, this.note).then(({data}) => {
        this.note = data
        this.$toasted.success('更新成功')
        this.$router.push('/notes/' + (this.note.slug ? this.note.slug : this.note.id))
        this.cleanDraft()
      })
    } else {
      axios.post(`/notes`, this.note).then(({data}) => {
        this.note = data
        this.$toasted.success('创建成功')
        this.$router.push('/notes/' + (this.note.slug ? this.note.slug : this.note.id))
        this.cleanDraft()
      })
    }
  }

  clear() {
    this.$toasted.clear()
  }

  loadDraft() {
    this.noteCache = Object.assign({}, this.note)
    const draft = localStorage.getItem(DRAFT_KEY + this.note.id)
    if (draft) {
      this.$toasted.info('您有未保存的草稿，是否恢复？', {
        duration: 30000,
        action: [
          {
            text: '恢复', onClick: (_, toastObject: ToastObject) => {
              this.restoreDraft()
              toastObject.goAway(0)
            }
          },
          {
            text: '清除', onClick: (_, toastObject: ToastObject) => {
              this.cleanDraft()
              toastObject.goAway(0)
            }
          },
          {
            text: '关闭', onClick: (_, toastObject: ToastObject) => toastObject.goAway(0)
          }
        ]
      })
    }
  }

  cleanDraft() {
    localStorage.removeItem(DRAFT_KEY + this.note.id)
  }

  restoreDraft() {
    const draft = localStorage.getItem(DRAFT_KEY + this.note.id)
    if (draft) {
      this.note = JSON.parse(draft)
    }
  }

  saveDraft() {
    if (this.noteCache.title !== this.note.title
      || this.noteCache.slug !== this.note.slug
      || this.noteCache.content !== this.note.content) {
      console.log(new Date(), '草稿保存中')
      const note = Object.assign({}, this.note, {updatedTime: new Date().getTime()})
      localStorage.setItem(DRAFT_KEY + this.note.id, JSON.stringify(note))
      this.$toasted.success('草稿保存中。。。', {duration: 1000})
      this.noteCache = Object.assign({}, this.note)
    }
  }
}
</script>

<style scoped>
.ui.form {
  padding-bottom: 33px;
}
</style>
