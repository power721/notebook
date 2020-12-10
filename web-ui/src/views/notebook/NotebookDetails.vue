<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/notebooks">笔记本</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">{{notebook.name}}</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <h1 class="ui header">{{notebook.name}}</h1>
      <div class="metadata">
        <a>@{{notebook.owner.username}}</a>
        <span :data-tooltip="notebook.createdTime | datetime">
          创建于{{notebook.createdTime | fromNow}}
        </span>
        <a href="javascript:void(0)" data-tooltip="删除笔记" @click="confirm=true" v-if="author">
          <i class="delete red icon"></i>
        </a>
        <a href="javascript:void(0)" data-tooltip="编辑笔记本" @click="edit" v-if="author">
          <i class="edit icon"></i>
        </a>
      </div>
      <div class="ui info message">
        {{notebook.description}}
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui warning message" v-if="notes.length===0">
        还没有笔记。
        <template v-if="author">
          <router-link :to="'/notes/-/new?notebook='+id">创建</router-link>一个？
        </template>
      </div>
      <router-link class="ui right floated icon primary button" data-tooltip="创建笔记" :to="'/notes/-/new?notebook='+id" v-if="author&&notes.length">
        <i class="add icon"></i>
      </router-link>
      <div class="ui divided items">
        <div class="item" v-for="note in notes" :key="note.id">
          <div class="content">
            <router-link class="header" :to="'/notes/'+note.id">{{note.title}}</router-link>
            <div class="meta">
              <a>@{{note.author.username}}</a>
            </div>
            <div class="extra">
              创建于{{note.createdTime | fromNow}}({{note.createdTime | datetime}})
            </div>
          </div>
        </div>
      </div>
    </div>
    <Pagination v-model="page" :totalPages="totalPages" @change="go"></Pagination>

    <Modal v-model="modal" title="更新笔记本" size="large">
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input type="text" name="title" v-model="nb.name" placeholder="标题">
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="nb.description"></textarea>
        </div>
      </form>
      <template slot="actions">
        <button @click="modal=false" class="ui cancel button">取消</button>
        <button @click="submit" class="ui primary button" :disabled="!nb.name">保存</button>
      </template>
    </Modal>

    <Modal v-model="confirm" :title="notebook.name">
      <div>
        <p>是否删除此笔记本？</p>
        <div class="ui checkbox">
          <input type="checkbox" name="force" v-model="force">
          <label>强制删除所有笔记</label>
        </div>
      </div>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="deleteNotebook" class="ui negative button">删除</button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import {Notebook} from '@/models/Notebook'
  import Modal from '@/components/Modal.vue'
  import accountService from '@/services/account.service'
  import Pagination from '@/components/Pagination.vue'
  import {Pageable} from '@/components/Pageable'
  import {goTop} from '@/utils/utils'

  @Component<Pageable>({
    components: {
      Modal,
      Pagination
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.load()
      }
    }
  })
  export default class NotebookDetails extends Pageable {
    id: string = ''
    modal: boolean = false
    confirm: boolean = false
    force: boolean = false
    author: boolean = false
    notebook: Notebook = new Notebook()
    nb: Notebook = new Notebook()
    notes: Note[] = []

    mounted() {
      this.id = this.$route.params.id
      this.page = +this.$route.query.page || 1
      this.loadNotebook()
      this.load()
    }

    loadNotebook() {
      axios.get(`/notebooks/${this.id}`).then(({data}) => {
        this.notebook = data
        this.author = accountService.account.id === this.notebook.owner.id
      })
    }

    load() {
      axios.get(`/notebooks/${this.id}/notes?page=${this.page-1}&size=${this.size}`).then(({data}) => {
        this.notes = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
        goTop()
      })
    }

    go(page: number) {
      this.$router.push(`/notebooks/${this.id}?page=${page}`)
    }

    edit() {
      this.nb = Object.assign(this.nb, this.notebook)
      this.modal = true
    }

    submit() {
      axios.put(`/notebooks/${this.id}`, this.nb).then(({data}) => {
        this.notebook = data
        this.modal = false
      })
    }

    deleteNotebook() {
      axios.delete(`/notebooks/${this.id}?force=${this.force}`).then(() => {
        this.confirm = false
        this.$toasted.success('删除成功')
        this.$router.push('/my-notebooks')
      })
    }
  }
</script>
