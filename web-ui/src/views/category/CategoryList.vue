<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">分类</div>
    </div>
    <div class="ui divider"></div>

    <a href="javascript:void(0)" class="ui add icon primary button" data-tooltip="创建分类" v-if="admin"
       @click="modal=true">
      <i class="add icon"></i>
    </a>

    <div class="ui divided items raised segment" :class="{loading: loading}">
      <div class="item" v-for="category in categories" :key="category.id">
        <div class="content">
          <router-link class="header" :to="'/categories/'+(category.slug?category.slug:category.id)">{{category.name}}</router-link>
          <div class="description">
            <p>{{category.description}}</p>
          </div>
          <div class="extra">
            创建于{{category.createdTime | fromNow}}({{category.createdTime | datetime}})
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="go"></Pagination>

    <Modal v-model="modal" title="创建分类" size="large" :closable="false">
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input type="text" name="title" autocomplete="off" v-model="category.name" placeholder="标题">
        </div>
        <div class="field">
          <label>slug</label>
          <input type="text" name="slug" autocomplete="off" v-model="category.slug" placeholder="slug">
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="category.description"></textarea>
        </div>
      </form>
      <template slot="actions">
        <button @click="modal=false" class="ui cancel button">取消</button>
        <button @click="submit" class="ui primary button" :disabled="!category.name">保存</button>
      </template>
    </Modal>
  </div>
</template>
<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'
  import Modal from '@/components/Modal.vue'
  import {Category} from '@/models/Category'
  import {goTop} from '@/utils/utils'
  import configService from '@/services/config.service'
  import {Role} from '@/models/Account'

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
  export default class CategoryList extends Pageable {
    modal: boolean = false
    loading: boolean = false
    category: Category = new Category()
    categories: Category[] = []

    get admin(): boolean {
      return this.$store.state.admin
    }

    mounted() {
      configService.setTitle('分类')
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      this.loading = true
      axios.get(`/categories?${this.query}`).then(({data}) => {
        this.categories = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
        this.loading = false
        goTop()
      }, () => {
        this.loading = false
      })
    }

    go(page: number) {
      this.$router.push(this.$route.path + '?page=' + page)
    }

    submit() {
      axios.post(`/categories`, this.category).then(({data}) => {
        this.category = new Category()
        this.categories.splice(0, 0, data)
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
