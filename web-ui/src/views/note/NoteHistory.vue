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
      <router-link class="section" :to="'/notes/'+id">{{note.title}}</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">编辑历史</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <router-link :to="'/notes/'+id" class="ui large header">{{note.title}}</router-link>
      <div class="metadata">
        <a>@{{note.author.username}}</a>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{note.createdTime | fromNow}}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.updatedTime">
          编辑于{{note.updatedTime | fromNow}}
        </span>
        <router-link data-tooltip="编辑笔记" v-if="author" :to="'/notes/'+note.id+'/edit'">
          <i class="edit icon"></i>
        </router-link>
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui link divided items">
        <div class="item" v-for="history in list" :key="history.version" @click.prevent="show(history)">
          <a class="ui top right attached label" v-if="note.version===history.version">当前版本</a>
          <div class="content">
            <a class="header">版本{{history.version}}： {{history.title}}</a>
            <div class="extra">
              创建于{{history.createdTime | fromNow}}({{history.createdTime | datetime}})
            </div>
          </div>
        </div>
      </div>
    </div>

    <Modal v-model="modal" size="large">
      <template slot="title">
        版本{{history.version}}： {{history.title}}
      </template>
      <Viewer :options="options" :initialValue="history.content"></Viewer>
    </Modal>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import {Note, NoteHistory} from '@/models/Note'
  import {Viewer} from '@/components/vue-editor'
  import accountService from '@/services/account.service'
  import Modal from '@/components/Modal.vue'

  @Component({
    components: {
      Viewer,
      Modal
    }
  })
  export default class NoteHistoryList extends Vue {
    id: string = ''
    author: boolean = false
    modal: boolean = false
    list: NoteHistory[] = []
    history: NoteHistory = new NoteHistory()
    note: Note = new Note()
    options = {
      initialEditType: 'wysiwyg'
    }

    mounted() {
      this.id = this.$route.params.id
      this.load()
    }

    load() {
      axios.get(`/notes/${this.id}`).then(({data}) => {
        this.options.initialEditType = data.markdown ? 'markdown' : 'wysiwyg'
        this.note = data
        document.title = this.note.title + ' - 编辑历史'
        this.author = accountService.account.id === this.note.author.id
      })
      axios.get(`/notes/${this.id}/history`).then(({data}) => {
        this.list = data
      })
    }

    show(history: NoteHistory) {
      if (history.content) {
        this.history = history
        this.modal = true
      } else {
        axios.get(`/notes/${this.id}/content/${history.version}`).then(({data}) => {
          history.content = data.content
          this.history = data
          this.modal = true
        })
      }
    }
  }
</script>

<style scoped>

</style>
