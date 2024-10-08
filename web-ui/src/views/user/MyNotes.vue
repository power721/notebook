<template>
  <div class="ui left aligned fluid container">
    <router-link class="ui add icon primary button" data-tooltip="创建笔记" to="/notes/-/new">
      <i class="edit icon"></i>
    </router-link>

    <div class="ui raised segment">
      <Dropdown icon="bars" position="top right" :pointing="true">
        <a class="item" :class="{active:sort==='createdTime,desc'}" @click="sorted('createdTime,desc')">创建时间(最新)</a>
        <a class="item" :class="{active:sort==='createdTime,asc'}" @click="sorted('createdTime,asc')">创建时间(最早)</a>
        <a class="item" :class="{active:sort==='updatedTime,desc'}" @click="sorted('updatedTime,desc')">更新时间(最新)</a>
        <a class="item" :class="{active:sort==='updatedTime,asc'}" @click="sorted('updatedTime,asc')">更新时间(最早)</a>
        <a class="item" :class="{active:sort==='content.title,desc'}" @click="sorted('content.title,desc')">标题(降序)</a>
        <a class="item" :class="{active:sort==='content.title,asc'}" @click="sorted('content.title,asc')">标题(升序)</a>
      </Dropdown>
      <div class="ui divided items">
        <div class="ui warning message" v-if="notes.length===0">
          还没有笔记。
          <router-link to="/notes/-/new">创建</router-link>
          一个？
        </div>
        <div class="item" v-for="note in notes" :key="note.slug||note.id">
          <div class="content">
            <a class="link" data-tooltip="只有你可以访问" v-if="note.access==='PRIVATE'"><i class="lock icon"></i></a>
            <a class="link" data-tooltip="知道ID才能访问" v-if="note.access==='SECRET'"><i class="unlock alternate icon"></i></a>
            <router-link class="header" :to="'/notes/'+(note.slug?note.slug:note.id)">{{note.title}}</router-link>
            <div class="meta">
              <router-link :to="'/notebooks/'+note.notebook.id">@{{note.notebook.name}}</router-link>
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
        this.load()
      }
    }
  })
  export default class MyNotes extends Pageable {
    notes: Note[] = []

    mounted() {
      configService.setTitle('我的笔记')
      this.sort = configService.getNotesSortOrder()
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/users/-/notes?${this.query}`).then(({data}) => {
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
      console.log(this.$route)
      this.$router.push(this.$route.path + '?page=' + page)
    }
  }
</script>

<style scoped>
  .add.button {
    float: right;
    margin-top: -60px;
  }
</style>
