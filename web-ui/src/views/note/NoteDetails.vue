<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" :to="'/notebooks/'+note.notebook.id">{{note.notebook.name}}</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">{{note.title}}</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <h1 class="ui header">
        {{note.title}}<span class="ui label" v-if="note.access!=='PUBLIC'">{{note.access}}</span>
      </h1>
      <div class="metadata">
        <a>@{{note.author.username}}</a>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{note.createdTime | fromNow}}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.updatedTime">
          更新于{{note.updatedTime | fromNow}}
        </span>
        <router-link class="ui teal label" :to="'/categories/'+note.category.id">{{note.category.name}}</router-link>
        <span :data-tooltip="note.views+'阅读'" v-if="note.access!=='PRIVATE'">
          {{note.views}} <i class="eye icon"></i>
        </span>
        <Dropdown icon="bars" position="top right" :pointing="true" v-if="author">
          <router-link class="item" :to="'/notes/'+note.id+'/edit'">编辑笔记</router-link>
          <router-link class="item" :to="'/notes/'+note.id+'/history'">历史记录</router-link>
          <a class="item" @click="confirm=true">删除笔记</a>
          <a class="item" @click="modal=true">移动笔记</a>
        </Dropdown>
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui warning message" v-if="old">
        <i class="warning icon"></i>
        <template v-if="note.updatedTime">
          本文编写于 {{note.createdTime | fromNow}}，最后修改于 {{note.updatedTime | fromNow}}
          （{{note.updatedTime | date}}），其中某些信息可能已经过时。
        </template>
        <template v-else>本文编写于 {{note.createdTime | fromNow}}，其中某些信息可能已经过时。</template>
      </div>
      <div v-html="note.content"></div>
    </div>

    <Modal v-model="confirm" :title="note.title">
      <p>是否删除此笔记？</p>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="deleteNote" class="ui negative button">删除</button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import accountService from '@/services/account.service'
  import Dropdown from '@/components/Dropdown.vue'
  import Modal from '@/components/Modal.vue'
  import configService from '@/services/config.service'

  @Component({
    components: {
      Dropdown,
      Modal,
    }
  })
  export default class NoteDetails extends Vue {
    id: string = ''
    old: boolean = false
    author: boolean = false
    modal: boolean = false
    confirm: boolean = false
    note: Note = new Note()

    mounted() {
      this.id = this.$route.params.id
      this.load()
    }

    load() {
      axios.get(`/notes/${this.id}?view=true`).then(({data}) => {
        this.note = data
        configService.setTitle(this.note.title)
        const now = new Date().getTime()
        const diff = now - new Date(this.note.createdTime).getTime()
        this.old = diff > 1000 * 3600 * 24 * 360
        this.author = accountService.account.id === this.note.author.id
      }).then(() => {
        setTimeout(() => {
          document.querySelectorAll('pre[class*=language-]').forEach(e => e.classList.add('line-numbers'))
        }, 0)
        setTimeout(() => (window as any).Prism.highlightAll(), 0)
      })
    }

    deleteNote() {
      axios.delete(`/notes/${this.id}`).then(() => {
        this.confirm = false
        this.$toasted.success('删除成功')
        this.$router.push('/notebooks/' + this.note.notebook.id)
      })
    }
  }
</script>

<style scoped>
</style>
