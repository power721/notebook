<template>
  <div class="ui left aligned fluid container">
    <a href="javascript:void(0)" class="ui add icon primary button" data-tooltip="创建笔记本" v-if="authenticated"
       @click="modal=true">
      <i class="add icon"></i>
    </a>

    <div class="ui raised segment">
      <Dropdown icon="bars" position="top right" :pointing="true">
        <a class="item" :class="{active:sort==='createdTime,desc'}" @click="sorted('createdTime,desc')">创建时间(最新)</a>
        <a class="item" :class="{active:sort==='createdTime,asc'}" @click="sorted('createdTime,asc')">创建时间(最早)</a>
        <a class="item" :class="{active:sort==='updatedTime,desc'}" @click="sorted('updatedTime,desc')">更新时间(最新)</a>
        <a class="item" :class="{active:sort==='updatedTime,asc'}" @click="sorted('updatedTime,asc')">更新时间(最早)</a>
        <a class="item" :class="{active:sort==='name,desc'}" @click="sorted('name,desc')">标题(降序)</a>
        <a class="item" :class="{active:sort==='name,asc'}" @click="sorted('name,asc')">标题(升序)</a>
      </Dropdown>
      <div class="ui divided items">
        <div class="item" v-for="notebook in notebooks" :key="notebook.id">
          <div class="content">
            <a class="link" data-tooltip="只有你可以访问" v-if="notebook.access==='PRIVATE'"><i class="lock icon"></i></a>
            <a class="link" data-tooltip="知道ID才能访问" v-if="notebook.access==='SECRET'"><i class="unlock alternate icon"></i></a>
            <router-link class="header" :to="'/notebooks/'+notebook.id">{{notebook.name}}</router-link>
            <div class="description">
              <p>{{notebook.description}}</p>
            </div>
            <div class="extra" v-if="notebook.updatedTime">
              更新于{{notebook.updatedTime | fromNow}}({{notebook.updatedTime | datetime}})
            </div>
            <div class="extra" v-else>
              创建于{{notebook.createdTime | fromNow}}({{notebook.createdTime | datetime}})
            </div>
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="go"></Pagination>

    <Modal v-model="modal" title="创建笔记本" size="large" :closable="false">
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input type="text" name="title" v-model="notebook.name" placeholder="标题">
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="notebook.description"></textarea>
        </div>
      </form>
      <template slot="actions">
        <button @click="modal=false" class="ui cancel button">取消</button>
        <button @click="submit" class="ui primary button" :disabled="!notebook.name">保存</button>
      </template>
    </Modal>
  </div>
</template>
<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Notebook} from '@/models/Notebook'
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'
  import Modal from '@/components/Modal.vue'
  import Dropdown from '@/components/Dropdown.vue'
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
  export default class MyNotebooks extends Pageable {
    modal: boolean = false
    notebook: Notebook = new Notebook()
    notebooks: Notebook[] = []

    get authenticated(): boolean {
      return this.$store.state.authenticated
    }

    mounted() {
      configService.setTitle('我的笔记本')
      this.sort = configService.getNotebooksSortOrder()
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/users/-/notebooks?${this.query}`).then(({data}) => {
        this.notebooks = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
      })
    }

    sorted(sort: string) {
      configService.saveNotebooksSortOrder(sort)
      this.sort = sort
      this.load()
    }

    go(page: number) {
      this.$router.push(this.$route.path + '?page=' + page)
    }

    submit() {
      axios.post(`/notebooks`, this.notebook).then(({data}) => {
        this.notebook = new Notebook()
        this.notebooks.splice(0, 0, data)
        this.modal = false
      })
    }
  }
</script>

<style scoped>
  .add.button {
    float: right;
    margin-top: -60px;
  }
</style>
