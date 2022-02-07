<template>
  <div id="comments" class="ui comments">
    <h3 class="ui dividing header">{{ total }} 评论</h3>
    <div class="comment" v-for="comment of comments" :key="comment.id">
      <a class="avatar">
        <img alt="avatar" :src="comment.user.avatar||'https://cn.gravatar.com/avatar/'">
      </a>
      <div class="content">
        <UserAvatar class="author" :user="comment.user" position="right center" :avatar="false"
                    :at="false"></UserAvatar>
        <div class="metadata">
          <a v-if="comment.user.id===note.author.id">作者</a>
          <a v-if="comment.sticky" class="ui blue basic mini label">置顶</a>
          <span class="date"
                :data-tooltip="comment.createdTime | datetime">{{ comment.createdTime | fromNow }}</span>
        </div>
        <div class="text markdown-body" v-html="comment.content"></div>
        <div class="actions">
          <a class="reply">回复</a>
          <a class="hide">隐藏</a>
          <a class="stick" v-if="author&&!comment.sticky" @click="stick(comment.id)">置顶</a>
          <a class="stick" v-if="author&&comment.sticky" @click="unstick(comment.id)">取消置顶</a>
          <a class="report">举报</a>
        </div>
      </div>
    </div>
    <Pagination :page="page" :pages="pages" :total="total" :simple="true" @change="loadComments"></Pagination>
    <form class="ui reply form" v-if="auth">
      <div class="field">
        <Popup trigger="hover" position="left center" style="float: right">
          <template slot="trigger">
            <a class="ui teal right ribbon label">
              <i class="help icon"></i>
            </a>
          </template>
          <div class="ui message">
            <ul class="list">
              <li>链接到笔记：[/notes/:id:]</li>
              <li>支持的标签：b, i, u, em, del, sub, sup, code</li>
            </ul>
          </div>
        </Popup>
        <Editor :init="config" v-model="comment"></Editor>
      </div>
      <div class="ui blue submit button" :class="{disabled:!comment||characters>2048}" @click.prevent="addComment">
        发表
      </div>
      <div class="error" v-show="characters>2048">
        评论内容太长 {{ characters }}字符
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import axios from 'axios'
import tinymce from 'tinymce'
import Editor from '@tinymce/tinymce-vue'
import {Component, Prop, Vue} from 'vue-property-decorator'
import {Note} from '@/models/Note'
import {Comment} from '@/models/Comment'
import {tinymceConfig} from '@/models/mceConfig'
import Pagination from '@/components/Pagination.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import Popup from '@/components/Popup.vue'

@Component({
  components: {
    Popup,
    Pagination,
    UserAvatar,
    Editor,
  },
  watch: {
    id() {
      this.load()
    }
  }
})
export default class NoteComments extends Vue {
  @Prop() private id!: string
  @Prop() private note!: Note
  comment: string = ''
  characters: number = 0
  page: number = 1
  total: number = 0
  pages: number = 0
  config = tinymceConfig
  stickyComments: Comment[] = []
  comments: Comment[] = []

  get auth(): boolean {
    return this.$store.state.authenticated
  }

  get author(): boolean {
    return this.$store.state.user.id == this.note.author.id
  }

  mounted() {
    this.load()
    if (this.auth) {
      tinymce.activeEditor.on('wordCountUpdate', this.wordCountUpdate)
    }
  }

  load() {
    if (!this.id) return
    this.getStickyComments().then(() => {
      this.loadComments()
    })
  }

  getStickyComments() {
    return axios.get(`/notes/${this.id}/stickyComments?sort=id,desc`).then(({data}) => {
      this.stickyComments.length = 0
      this.stickyComments.push(...data)
    })
  }

  loadComments(page: number = 0) {
    if (page) {
      this.page = page
    }
    axios.get(`/notes/${this.id}/comments?sort=id,desc&size=10&page=${this.page - 1}`).then(({data}) => {
      this.comments.length = 0
      this.comments.push(...this.stickyComments)
      this.stickyComments.forEach(c => {
        const index = data.content.findIndex(e => e.id === c.id)
        if (index >= 0) data.content.splice(index, 1)
      })
      this.comments.push(...data.content)
      this.total = data.totalElements
      this.pages = data.totalPages
      this.$emit('total', this.total)
    })
  }

  addComment() {
    axios.post(`/notes/${this.id}/comments`, {content: this.comment}).then(({data}) => {
      this.comment = ''
      if (this.comments.length > 15) {
        this.loadComments(1)
      } else {
        this.total++
        this.comments.splice(this.stickyComments.length, 0, data)
      }
    })
  }

  stick(id: number) {
    axios.post(`/comments/${id}/stick`).then(({data}) => {
      this.stickyComments.unshift(data)
      const index = this.comments.findIndex(e => e.id === data.id)
      if (index >= 0) this.comments.splice(index, 1)
      this.comments.unshift(data)
    })
  }

  unstick(id: number) {
    axios.delete(`/comments/${id}/stick`).then(() => {
      const index = this.stickyComments.findIndex(e => e.id === id)
      this.stickyComments.splice(index, 1)
      this.loadComments()
    })
  }

  wordCountUpdate(event) {
    this.characters = event.wordCount.characters
  }
}
</script>

<style scoped>
.ui.comments {
  margin: 1.5em 0;
  max-width: 100%;
}

.ui.comments .reply.form textarea {
  height: auto;
}

.ui.comments .comment .ui.mini.label {
  font-size: 12px;
  padding: 3px 4px;
}
</style>
