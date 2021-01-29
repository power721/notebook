<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">笔记</div>
    </div>
    <div class="ui divider"></div>

    <router-link class="ui add icon primary button" data-tooltip="创建笔记" to="/notes/-/new">
      <i class="edit icon"></i>
    </router-link>

    <div class="ui raised segment" :class="{loading: loading}">
      <Dropdown icon="bars" position="top right" :pointing="true">
        <a class="item" :class="{active:sort==='createdTime,desc'}" @click="sorted('createdTime,desc')">创建时间(最新)</a>
        <a class="item" :class="{active:sort==='createdTime,asc'}" @click="sorted('createdTime,asc')">创建时间(最早)</a>
        <a class="item" :class="{active:sort==='updatedTime,desc'}" @click="sorted('updatedTime,desc')">更新时间(最新)</a>
        <a class="item" :class="{active:sort==='updatedTime,asc'}" @click="sorted('updatedTime,asc')">更新时间(最早)</a>
        <a class="item" :class="{active:sort==='content.title,desc'}" @click="sorted('content.title,desc')">标题(降序)</a>
        <a class="item" :class="{active:sort==='content.title,asc'}" @click="sorted('content.title,asc')">标题(升序)</a>
      </Dropdown>
      <div class="ui divided items">
        <div class="ui warning message" v-if="q&&notes.length===0">
          没有找到包含“{{q}}”的笔记。
        </div>
        <div class="item" v-for="note in notes" :key="note.id">
          <div class="content">
            <a class="link" data-tooltip="只有你可以访问" v-if="note.access==='PRIVATE'"><i class="lock icon"></i></a>
            <a class="link" data-tooltip="知道ID才能访问" v-if="note.access==='SECRET'"><i class="unlock alternate icon"></i></a>
            <router-link class="header" :to="'/notes/'+(note.slug?note.slug:note.id)">{{note.title}}</router-link>
            <div class="meta">
              <router-link :to="'/users/'+note.author.id">@{{note.author.username}}</router-link>
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
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'
  import {goTop} from '@/utils/utils'
  import Dropdown from '@/components/Dropdown.vue'
  import configService from '@/services/config.service'

  @Component<Pageable>({
    components: {
      Pagination,
      Dropdown
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.q = to.query.q || ''
        this.load()
      }
    }
  })
  export default class NoteList extends Pageable {
    loading: boolean = false
    notes: Note[] = []

    mounted() {
      configService.setTitle('笔记')
      this.sort = configService.getNotesSortOrder()
      this.page = +this.$route.query.page || 1
      this.q = (this.$route.query.q || '') as string
      this.load()
    }

    load() {
      this.loading = true
      axios.get(`/notes?${this.query}`).then(({data}) => {
        this.notes = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
        this.loading = false
        goTop()
      })
    }

    sorted(sort: string) {
      configService.saveNotesSortOrder(sort)
      this.sort = sort
      this.load()
    }

    go(page: number) {
      this.$router.push(this.$route.path + '?q=' + this.q + '&page=' + page)
    }
  }
</script>

<style scoped>
  .add.button {
    float: right;
    margin-top: -56px;
  }
</style>
