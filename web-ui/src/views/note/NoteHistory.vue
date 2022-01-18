<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/trash-notes" v-if="note.deleted">回收站</router-link>
      <i class="right chevron icon divider" v-if="note.deleted"></i>
      <router-link class="section" :to="'/notebooks/'+(note.notebook.slug?note.notebook.slug:note.notebook.id)">{{note.notebook.name}}</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" :to="'/notes/'+(note.slug?note.slug:note.id)">{{note.title}}</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">编辑历史</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <router-link :to="'/notes/'+(note.slug?note.slug:note.id)" class="ui large header" v-if="note.deleted">
        <del>{{note.title}}</del>
      </router-link>
      <router-link :to="'/notes/'+(note.slug?note.slug:note.id)" class="ui large header" v-else>{{note.title}}</router-link>
      <div class="metadata">
        <router-link :to="'/users/'+note.author.id">@{{note.author.username}}</router-link>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{note.createdTime | fromNow}}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.version>1">
          更新于{{note.updatedTime | fromNow}}
        </span>
        <router-link data-tooltip="编辑笔记" v-if="author&&!note.deleted" :to="'/notes/'+note.id+'/edit'">
          <i class="edit icon"></i>
        </router-link>
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui link divided items">
        <div class="item" v-for="item in list" :key="item.version" @click.prevent="show(item)">
          <div class="content">
            <a class="ui right floated red label" v-if="note.version===item.version">当前版本</a>
            <a class="header">版本{{item.version}}： {{item.title}}</a>
            <div class="extra">
              创建于{{item.createdTime | fromNow}}({{item.createdTime | datetime}})
            </div>
          </div>
        </div>
      </div>
    </div>

    <Modal v-model="modal" size="large">
      <template slot="title">
        版本{{history.version}}： {{history.title}}
      </template>
      <div class="article content" v-html="history.content"></div>
      <template slot="actions">
        <button @click="modal=false" class="ui cancel button">取消</button>
        <button @click="deleteVersion" class="ui negative button" data-tooltip="删除此版本" v-if="note.version!==history.version">
          删除
        </button>
        <button @click="revert" class="ui primary button" data-tooltip="恢复到此版本" v-if="note.version!==history.version&&!note.deleted">
          恢复
        </button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import {Note, NoteHistory} from '@/models/Note'
  import {ResponseError} from '@/models/ResponseError'
  import accountService from '@/services/account.service'
  import Modal from '@/components/Modal.vue'
  import configService from '@/services/config.service'

  @Component({
    components: {
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

    mounted() {
      this.id = this.$route.params.id
      this.load()
    }

    load() {
      axios.get(`/notes/${this.id}`).then(({data}) => {
        this.note = data
        configService.setTitle(this.note.title + ' - 编辑历史')
        this.author = accountService.account.id === this.note.author.id
      })
      axios.get(`/notes/${this.id}/history`).then(({data}) => {
        this.list = data
      }, (error: ResponseError) => {
        if (error.status === 403) {
          this.$router.push('/')
        }
      })
    }

    show(history: NoteHistory) {
      if (history.content) {
        this.history = history
        this.parse()
        this.modal = true
      } else {
        axios.get(`/notes/${this.note.id}/content/${history.version}`).then(({data}) => {
          history.content = data.content
          this.history = data
        }).then(() => {
          this.parse()
          this.modal = true
        })
      }
    }

    private parse() {
      setTimeout(() => {
        document.querySelectorAll('pre[class*=language-]').forEach(e => e.classList.add('line-numbers'))
        document.querySelectorAll('.article').forEach(node => {
          (window as any).twemoji.parse(node, {'size': 72})
        });
        (window as any).Prism.highlightAll()
      }, 0)
    }

    revert() {
      if (this.note.version != this.history.version) {
        axios.post(`/notes/${this.note.id}/content/${this.history.version}`).then(({data}) => {
          this.note = data
          this.modal = false
        })
      }
    }

    deleteVersion() {
      if (this.note.version != this.history.version) {
        axios.delete(`/notes/${this.note.id}/content/${this.history.version}`).then(() => {
          this.modal = false
          this.$toasted.success(`版本${this.history.version}删除成功`)
          const index = this.list.findIndex(e => e.version === this.history.version)
          this.list.splice(index, 1)
        })
      }
    }
  }
</script>

<style scoped>

</style>
