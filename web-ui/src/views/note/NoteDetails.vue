<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <!--      <div class="section">笔记本</div>-->
      <!--      <i class="right chevron icon divider"></i>-->
      <router-link class="section" :to="'/notebooks/'+note.notebook.id">{{note.notebook.name}}</router-link>
      <i class="right chevron icon divider"></i>
      <!--      <div class="section">笔记</div>-->
      <!--      <i class="right chevron icon divider"></i>-->
      <div class="active section">{{note.title}}</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <h1 class="ui header">{{note.title}}</h1>
      <div class="metadata">
        <a>@{{note.author.username}}</a>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{note.createdTime | fromNow}}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.updatedTime">
          编辑于{{note.updatedTime | fromNow}}
        </span>
        {{note.views}} <i class="eye icon"></i>
        <router-link data-tooltip="历史记录" v-if="author&&note.version>1" :to="'/notes/'+note.id+'/history'">
          <i class="list icon"></i>
        </router-link>
        <router-link data-tooltip="编辑笔记" v-if="author" :to="'/notes/'+note.id+'/edit'">
          <i class="edit icon"></i>
        </router-link>
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
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import accountService from '@/services/account.service'

  @Component
  export default class NoteDetails extends Vue {
    id: string = ''
    old: boolean = false
    author: boolean = false
    note: Note = new Note()

    mounted() {
      this.id = this.$route.params.id
      this.load()
    }

    load() {
      axios.get(`/notes/${this.id}?view=true`).then(({data}) => {
        this.note = data
        document.title = this.note.title
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
  }
</script>

<style scoped>

</style>
