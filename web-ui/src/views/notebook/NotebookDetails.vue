<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/notebooks">笔记本</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">
        <i class="lock icon" v-if="notebook.access==='PRIVATE'"></i>
        <i class="unlock alternate icon" v-if="notebook.access==='SECRET'"></i>
        {{ notebook.name }}
      </div>
    </div>
    <div class="ui divider"></div>

    <router-link class="ui add icon primary button" data-tooltip="创建笔记"
                 :to="'/notes/-/new?notebook='+notebook.id" v-if="author&&notes.length">
      <i class="add icon"></i>
    </router-link>

    <div class="ui center aligned raised segment">
      <h1 class="ui header">{{ notebook.name }}</h1>
      <a class="ui top left attached label" data-tooltip="知道ID才能访问" v-if="notebook.access==='SECRET'">秘密</a>
      <a class="ui top left attached label" data-tooltip="只有你可以访问" v-if="notebook.access==='PRIVATE'">私有</a>
      <div class="metadata">
        <router-link :to="'/users/'+notebook.owner.id">@{{ notebook.owner.username }}</router-link>
        <span :data-tooltip="notebook.createdTime | datetime">
          创建于{{ notebook.createdTime | fromNow }}
        </span>
        <span :data-tooltip="notebook.updatedTime | datetime" v-if="notebook.updatedTime">
          更新于{{ notebook.updatedTime | fromNow }}
        </span>
        <a href="javascript:void(0)" data-tooltip="编辑笔记本" @click="edit" v-if="author">
          <i class="edit icon"></i>
        </a>
        <Dropdown icon="bars" position="top right" :pointing="true" v-if="author">
          <a class="item" @click="edit">编辑笔记本</a>
          <a class="item" @click="show=true" v-if="notebook.access!=='PRIVATE'">访问权限</a>
          <a class="item" @click="confirm=true">删除笔记本</a>
        </Dropdown>
      </div>
      <div class="ui info message" v-if="notebook.description">
        {{ notebook.description }}
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">加载中</div>
      </div>
      <div class="ui warning message" v-if="notes.length===0">
        还没有笔记。
        <template v-if="author">
          <router-link :to="'/notes/-/new?notebook='+notebook.id">创建</router-link>
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
        <div class="item" v-for="note in notes" :key="note.id">
          <div class="content">
            <a class="link" data-tooltip="只有你可以访问" v-if="note.access==='PRIVATE'"><i class="lock icon"></i></a>
            <a class="link" data-tooltip="知道ID才能访问" v-if="note.access==='SECRET'"><i class="unlock alternate icon"></i></a>
            <router-link class="header" :to="'/notes/'+(note.slug?note.slug:note.id)">{{ note.title }}</router-link>
            <div class="meta">
              <router-link :to="'/users/'+note.author.id">@{{ note.author.username }}</router-link>
              <router-link class="ui small label" :to="'/categories/'+note.category.id">{{ note.category.name }}
              </router-link>
            </div>
            <div class="extra" v-if="note.version>1">
              更新于{{ note.updatedTime | fromNow }}({{ note.updatedTime | datetime }})
            </div>
            <div class="extra" v-else>
              创建于{{ note.createdTime | fromNow }}({{ note.createdTime | datetime }})
            </div>
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="go"></Pagination>

    <Modal v-model="modal" title="更新笔记本" size="large" :closable="false">
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input type="text" name="title" autocomplete="off" v-model="nb.name" placeholder="标题">
        </div>
        <div class="field">
          <label>slug</label>
          <input type="text" name="slug" autocomplete="off" v-model="nb.slug" placeholder="slug">
        </div>
        <div class="required field">
          <label>访问权限</label>
          <el-radio-group v-model="nb.access">
            <el-radio label="PUBLIC" data-tooltip="所有人可以访问">公开</el-radio>
            <el-radio label="SECRET" data-tooltip="知道ID才能访问">秘密</el-radio>
            <el-radio label="PRIVATE" data-tooltip="只有你可以访问">私有</el-radio>
          </el-radio-group>
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

    <Modal v-model="confirm" title="删除笔记本">
      <div>
        <p class="ui error message">是否删除笔记本：{{ notebook.name }}？</p>
        <div class="ui checkbox" v-if="totalElements">
          <input type="checkbox" name="force" v-model="force">
          <label>强制删除所有{{ totalElements }}个笔记</label>
        </div>
      </div>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="deleteNotebook" class="ui negative button">删除</button>
      </template>
    </Modal>

    <Modal v-model="show" title="更新笔记访问权限">
      <form class="ui form">
        <p class="ui info message">更新当前笔记本所有笔记访问权限</p>
        <div class="required field">
          <label>访问权限</label>
          <el-radio-group v-model="access">
            <el-radio label="PUBLIC" data-tooltip="所有人可以访问" v-if="notebook.access==='PUBLIC'">公开</el-radio>
            <el-radio label="SECRET" data-tooltip="知道ID才能访问" v-if="notebook.access!=='PRIVATE'">秘密</el-radio>
            <el-radio label="PRIVATE" data-tooltip="只有你可以访问">私有</el-radio>
          </el-radio-group>
        </div>
      </form>
      <template slot="actions">
        <button @click="show=false" class="ui cancel button">取消</button>
        <button @click="updateAccess" class="ui positive button">更新</button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
import axios from 'axios'
import {Component} from 'vue-property-decorator'
import {Access, Note} from '@/models/Note'
import {Notebook} from '@/models/Notebook'
import Modal from '@/components/Modal.vue'
import Dropdown from '@/components/Dropdown.vue'
import Pagination from '@/components/Pagination.vue'
import {Pageable} from '@/components/Pageable'
import {goTop} from '@/utils/utils'
import accountService from '@/services/account.service'
import configService from '@/services/config.service'

@Component<Pageable>({
  components: {
    Modal,
    Pagination,
    Dropdown
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
  loading: boolean = false
  modal: boolean = false
  confirm: boolean = false
  force: boolean = false
  author: boolean = false
  show: boolean = false
  access: string = Access[Access.PRIVATE]
  notebook: Notebook = new Notebook()
  nb: Notebook = new Notebook()
  notes: Note[] = []

  mounted() {
    this.sort = configService.getNotesSortOrder()
    this.id = this.$route.params.id
    this.page = +this.$route.query.page || 1
    this.loadNotebook().then(() => {
      this.load()
    })
  }

  loadNotebook() {
    return axios.get(`/notebooks/${this.id}`).then(({data}) => {
      this.notebook = data
      this.author = accountService.account.id === this.notebook.owner.id
      configService.setTitle(this.notebook.name + ' - 笔记本')
    }, () => {
      this.$router.push('/')
    })
  }

  load() {
    this.loading = true
    axios.get(`/notebooks/${this.notebook.id}/notes?${this.query}`).then(({data}) => {
      this.notes = data.content
      this.totalPages = data.totalPages
      this.totalElements = data.totalElements
      this.loading = false
      goTop()
    }, () => {
      this.loading = false
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
    this.nb = Object.assign(this.nb, this.notebook)
    this.modal = true
  }

  submit() {
    axios.put(`/notebooks/${this.notebook.id}`, this.nb).then(({data}) => {
      this.notebook = data
      this.modal = false
    })
  }

  deleteNotebook() {
    axios.delete(`/notebooks/${this.notebook.id}?force=${this.force}`).then(() => {
      this.confirm = false
      this.$toasted.success('删除成功')
      this.$router.push('/user/notebooks')
    })
  }

  updateAccess() {
    axios.post(`/notebooks/${this.notebook.id}/access?access=${this.access}`).then(() => {
      this.show = false
      this.$toasted.success('更新成功')
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
