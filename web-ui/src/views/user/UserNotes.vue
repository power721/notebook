<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <span class="section">用户</span>
      <i class="right chevron icon divider"></i>
      <div class="active section">{{user.username}}</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <h2 class="ui header">"{{user.username}}"发布的笔记</h2>
      <div class="metadata">
        <span :data-tooltip="user.createdTime | datetime">
          加入于{{user.createdTime | fromNow}}
        </span>
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui warning message" v-if="notes.length===0">
        还没有笔记。
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
            <i class="lock icon" v-if="note.access==='PRIVATE'"></i>
            <i class="unlock alternate icon" v-if="note.access==='SECRET'"></i>
            <router-link class="header" :to="'/notes/'+(note.slug?note.slug:note.id)">{{note.title}}</router-link>
            <div class="meta">
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
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import {User} from '@/models/User'
  import Pagination from '@/components/Pagination.vue'
  import {Pageable} from '@/components/Pageable'
  import Dropdown from '@/components/Dropdown.vue'
  import configService from '@/services/config.service'
  import {goTop} from '@/utils/utils'

  @Component<Pageable>({
    components: {
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
  export default class UserNotes extends Pageable {
    id: string = ''
    user: User = new User()
    notes: Note[] = []

    get authenticated(): boolean {
      return this.$store.state.authenticated
    }

    mounted() {
      configService.setTitle('用户发布的笔记')
      this.sort = configService.getNotesSortOrder()
      this.id = this.$route.params.id
      this.page = +this.$route.query.page || 1
      this.loadUser()
      this.load()
    }

    loadUser() {
      axios.get(`/users/${this.id}`).then(({data}) => {
        this.user = data
        configService.setTitle(this.user.username + '发布的笔记')
      })
    }

    load() {
      axios.get(`/users/${this.id}/notes?${this.query}`).then(({data}) => {
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
  }
</script>

<style scoped>
</style>
