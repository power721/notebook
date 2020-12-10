<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">我的笔记本</div>
    </div>
    <div class="ui divider"></div>

    <a href="javascript:void(0)" class="ui add icon primary button" data-tooltip="创建笔记本" v-if="authenticated"
       @click="modal=true">
      <i class="add icon"></i>
    </a>

    <div class="ui divided items raised segment">
      <div class="item" v-for="notebook in notebooks" :key="notebook.id">
        <div class="content">
          <router-link class="header" :to="'/notebooks/'+notebook.id">{{notebook.name}}</router-link>
          <div class="meta">
            <a>@{{notebook.owner.username}}</a>
          </div>
          <div class="description">
            <p>{{notebook.description}}</p>
          </div>
          <div class="extra">
            创建于{{notebook.createdTime | fromNow}}({{notebook.createdTime | datetime}})
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :totalPages="totalPages" @change="go"></Pagination>

    <Modal v-model="modal" title="创建笔记本" size="large">
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
  export default class MyNotebooks extends Pageable {
    modal: boolean = false
    notebook: Notebook = new Notebook()
    notebooks: Notebook[] = []

    get authenticated(): boolean {
      return this.$store.state.authenticated
    }

    mounted() {
      document.title = '我的笔记本'
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/users/-/notebooks?page=${this.page - 1}&size=${this.size}&sort=id,desc`).then(({data}) => {
        this.notebooks = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
      })
    }

    go(page: number) {
      this.$router.push('/my-notebooks?page=' + page)
    }

    submit() {
      axios.post(`/notebooks`, this.notebook).then(({data}) => {
        this.notebook = new Notebook()
        this.notebooks.push(data)
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
