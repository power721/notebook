<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/categories">分类</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">{{category.name}}</div>
    </div>
    <div class="ui divider"></div>

    <router-link class="ui add icon primary button" data-tooltip="创建笔记" :to="'/notes/-/new?category='+category.id"
                 v-if="authenticated&&notes.length">
      <i class="add icon"></i>
    </router-link>

    <div class="ui center aligned raised segment">
      <h1 class="ui header">{{category.name}}</h1>
      <div class="metadata">
        <span :data-tooltip="category.createdTime | datetime">
          创建于{{category.createdTime | fromNow}}
        </span>
        <a href="javascript:void(0)" data-tooltip="编辑分类" @click="edit" v-if="admin">
          <i class="edit icon"></i>
        </a>
      </div>
      <div class="ui info message" v-if="category.description">
        {{category.description}}
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui warning message" v-if="notes.length===0">
        还没有笔记。
        <template v-if="authenticated">
          <router-link :to="'/notes/-/new?category='+category.id">创建</router-link>
          一个？
        </template>
      </div>
      <Dropdown icon="bars" position="top right" :pointing="true" v-else>
        <a class="item" :class="{active:sort==='createdTime,desc'}" @click="sorted('createdTime,desc')">创建时间(最新)</a>
        <a class="item" :class="{active:sort==='createdTime,asc'}" @click="sorted('createdTime,asc')">创建时间(最早)</a>
        <a class="item" :class="{active:sort==='updatedTime,desc'}" @click="sorted('updatedTime,desc')">更新时间(最新)</a>
        <a class="item" :class="{active:sort==='updatedTime,asc'}" @click="sorted('updatedTime,asc')">更新时间(最早)</a>
        <a class="item" :class="{active:sort==='content.title,desc'}" @click="sorted('content.title,desc')">标题(降序)</a>
        <a class="item" :class="{active:sort==='content.title,asc'}" @click="sorted('content.title,asc')">标题(升序)</a>
      </Dropdown>
      <div class="ui divided items">
        <div class="item" v-for="note in notes" :key="note.slug||note.id">
          <div class="content">
            <i class="lock icon" v-if="note.access==='PRIVATE'"></i>
            <i class="unlock alternate icon" v-if="note.access==='SECRET'"></i>
            <router-link class="header" :to="'/notes/'+(note.slug?note.slug:note.id)">{{note.title}}</router-link>
            <div class="meta">
              <UserAvatar :user="note.author" :avatar="false" position="right center"></UserAvatar>
              发布于
              <router-link :to="'/notebooks/'+note.notebook.id">{{note.notebook.name}}</router-link>
            </div>
            <div class="extra" v-if="note.version>1">
              更新于{{note.updatedTime | fromNow}}({{note.updatedTime | datetime}})
            </div>
            <div class="extra" v-else>
              创建于{{note.createdTime | fromNow}}({{note.createdTime | datetime}})
            </div>
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="go"></Pagination>

    <Modal v-model="modal" title="更新分类" size="large" :closable="false">
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input type="text" name="title" autocomplete="off" v-model="nb.name" placeholder="标题">
        </div>
        <div class="field">
          <label>slug</label>
          <input type="text" name="slug" autocomplete="off" v-model="nb.slug" placeholder="slug">
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
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import Modal from '@/components/Modal.vue'
  import Dropdown from '@/components/Dropdown.vue'
  import Pagination from '@/components/Pagination.vue'
  import UserAvatar from '@/components/UserAvatar.vue'
  import {Pageable} from '@/components/Pageable'
  import {Category} from '@/models/Category'
  import {goTop} from '@/utils/utils'
  import configService from '@/services/config.service'

  @Component<Pageable>({
    components: {
      Modal,
      Pagination,
      Dropdown,
      UserAvatar,
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.load()
      }
    }
  })
  export default class CategoryDetails extends Pageable {
    id: string = ''
    modal: boolean = false
    category: Category = new Category()
    nb: Category = new Category()
    notes: Note[] = []

    get authenticated(): boolean {
      return this.$store.state.authenticated
    }

    get admin(): boolean {
      return this.$store.state.admin
    }

    mounted() {
      this.sort = configService.getNotesSortOrder()
      this.id = this.$route.params.id
      this.page = +this.$route.query.page || 1
      this.loadCategory().then(() => {
        this.load()
      })
    }

    loadCategory() {
      return axios.get(`/categories/${this.id}`).then(({data}) => {
        this.category = data
        configService.setTitle(this.category.name + ' - 分类')
      })
    }

    load() {
      axios.get(`/categories/${this.category.id}/notes?${this.query}`).then(({data}) => {
        this.notes = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
        goTop()
      })
    }

    sorted(sort: string) {
      configService.saveNotesSortOrder(sort)
      this.sort = sort
      this.load()
    }

    go(page: number) {
      this.$router.push(this.$route.path + '?page=' + page)
    }

    edit() {
      this.nb = Object.assign(this.nb, this.category)
      this.modal = true
    }

    submit() {
      axios.put(`/categories/${this.category.id}`, this.nb).then(({data}) => {
        this.category = data
        this.modal = false
      })
    }
  }
</script>

<style scoped>
  .add.button {
    float: right;
    margin-top: -56px;
  }
</style>
