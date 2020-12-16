<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">回收站</div>
    </div>
    <div class="ui divider"></div>

    <a href="javascript:void(0)" class="ui trash icon button" data-tooltip="清空回收站" @click="confirm=true">
      <i class="trash icon"></i>
    </a>

    <div class="ui raised segment">
      <Dropdown icon="bars" position="top right" :pointing="true">
        <a class="item" :class="{active:sort==='createdTime,desc'}" @click="sorted('createdTime,desc')">创建时间(最新)</a>
        <a class="item" :class="{active:sort==='createdTime,asc'}" @click="sorted('createdTime,asc')">创建时间(最早)</a>
        <a class="item" :class="{active:sort==='updatedTime,desc'}" @click="sorted('updatedTime,desc')">更新时间(最新)</a>
        <a class="item" :class="{active:sort==='updatedTime,asc'}" @click="sorted('updatedTime,asc')">更新时间(最早)</a>
        <a class="item" :class="{active:sort==='content.title,desc'}" @click="sorted('content.title,desc')">标题(降序)</a>
        <a class="item" :class="{active:sort==='content.title,asc'}" @click="sorted('content.title,asc')">标题(升序)</a>
      </Dropdown>
      <div class="ui info message" v-if="notes.length===0">
        没有删除的笔记。
      </div>
      <div class="ui divided items">
        <div class="item" v-for="note in notes" :key="note.id">
          <div class="content">
            <router-link class="header" :to="'/notes/'+note.id">{{note.title}}</router-link>
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

    <Modal v-model="confirm" title="清空回收站">
      <p class="ui error message">
        是否清空回收站？<br>
        {{totalElements}}条笔记将会被永久删除。
      </p>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="cleanTrash" class="ui negative button">清空</button>
      </template>
    </Modal>

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
  import Modal from '@/components/Modal.vue'

  @Component<Pageable>({
    components: {
      Pagination,
      Dropdown,
      Modal
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.load()
      }
    }
  })
  export default class TrashNotes extends Pageable {
    notes: Note[] = []
    confirm: boolean = false

    mounted() {
      configService.setTitle('回收站')
      this.sort = configService.getNotesSortOrder()
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/users/-/trash?${this.query}`).then(({data}) => {
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
      this.$router.push('/trash-notes?page=' + page)
    }

    cleanTrash() {
      axios.delete(`/users/-/trash`).then(() => {
        this.confirm = false
        this.load()
      })
    }
  }
</script>

<style scoped>
  .trash.button {
    float: right;
    margin-top: -56px;
  }
</style>
