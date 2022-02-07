<template>
  <div id="top" class="ui left aligned fluid container">
    <div id="breadcrumb" class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/trash-notes" v-if="note.deleted">回收站</router-link>
      <i class="right chevron icon divider" v-if="note.deleted"></i>
      <router-link class="section" :to="'/notebooks/'+(note.notebook.slug?note.notebook.slug:note.notebook.id)">
        {{ note.notebook.name }}
      </router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">
        <i class="lock icon" v-if="note.access==='PRIVATE'"></i>
        <i class="unlock alternate icon" v-if="note.access==='SECRET'"></i>
        {{ note.title }}
      </div>
    </div>
    <div class="ui divider"></div>

    <div id="note_title" class="ui center aligned raised segment">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">加载中</div>
      </div>
      <h1 class="ui header" v-if="note.deleted">
        <del>{{ note.title }}</del>
      </h1>
      <h1 class="ui header" v-else>
        {{ note.title }}
      </h1>
      <a class="ui top left attached label" data-tooltip="知道ID才能访问" v-if="note.access==='SECRET'">秘密</a>
      <a class="ui top left attached label" data-tooltip="只有你可以访问" v-if="note.access==='PRIVATE'">私有</a>
      <div class="metadata">
        <UserAvatar :user="note.author"></UserAvatar>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{ note.createdTime | fromNow }}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.version>1">
          更新于{{ note.updatedTime | fromNow }}
        </span>
        <router-link class="ui teal small label"
                     :to="'/categories/'+(note.category.slug?note.category.slug:note.category.id)">
          {{ note.category.name }}
        </router-link>
        <span class="ui basic label" v-if="showWords">字数：{{ note.words }}</span>
        <span class="ui basic label" v-if="showViews&&note.access!=='PRIVATE'">阅读：{{ note.views }}</span>
        <a class="ui basic label" v-if="enableComment" @click="goAnchor('comments')">
          评论：{{ total }}
        </a>
        <Popup position="bottom right" trigger="hover">
          <template slot="trigger">
            <i class="share link blue icon"></i>
          </template>
          <div class="share">
            <p style="margin: 0">微信扫一扫分享</p>
            <canvas id="qrcode"></canvas>
          </div>
        </Popup>
        <router-link :to="'/notes/'+(note.slug?note.slug:note.id)+'/edit'" class="edit" data-tooltip="编辑笔记"
                     v-if="author&&!note.deleted">
          <i class="edit icon"></i>
        </router-link>
        <Dropdown icon="bars" position="top right" class="icon" :pointing="true" v-if="author">
          <div class="item" @click="exportPng"><i class="images icon"></i>存为图片</div>
          <router-link class="item" :to="'/notes/'+(note.slug?note.slug:note.id)+'/edit'" v-if="!note.deleted">
            <i class="edit icon"></i>编辑笔记
          </router-link>
          <router-link class="item" :to="'/notes/'+(note.slug?note.slug:note.id)+'/history'" v-if="note.version>1">
            <i class="history icon"></i>历史记录
          </router-link>
          <div class="divider"></div>
          <div class="item" @click="showMove" v-if="!note.deleted"><i class="move icon"></i>移动笔记</div>
          <div class="item" @click="revert=true" v-if="note.deleted"><i class="redo icon"></i>恢复笔记</div>
          <div class="item" @click="confirm=true"><i class="trash icon"></i>删除笔记</div>
        </Dropdown>
      </div>
    </div>

    <div id="content" class="ui left aligned raised segment">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">加载中</div>
      </div>
      <div class="ui warning message" v-if="old">
        <i class="warning icon"></i>
        <template v-if="note.updatedTime">
          本文编写于 {{ note.createdTime | fromNow }}，最后修改于 {{ note.updatedTime | fromNow }}
          （{{ note.updatedTime | date }}），其中某些信息可能已经过时。
        </template>
        <template v-else>本文编写于 {{ note.createdTime | fromNow }}，其中某些信息可能已经过时。</template>
      </div>
      <MdViewer v-if="note.markdown" :content="content"></MdViewer>
      <div v-else class="article content markdown-body" v-html="note.content"></div>
      <div id="footer" class="footer">
        <div class="ui divider" v-if="note.tags.length"></div>
        <div id="tags" class="ui blue tag labels" v-if="note.tags.length">
          <router-link :to="'/tags/'+tag.name" class="ui label" :key="tag.name" v-for="tag of note.tags">
            {{ tag.name }}
          </router-link>
        </div>
        <div class="ui divider" v-if="note.author.signature"></div>
        <MdViewer :content="signature"></MdViewer>
      </div>
    </div>

    <div class="ui left aligned raised segment" v-if="enableComment">
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
              <a v-if="comment.user.id===note.author.id" class="ui blue basic mini label">作者</a>
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
    </div>

    <Modal v-model="confirm" title="删除笔记">
      <p class="ui error message" v-if="note.deleted">是否永久删除笔记：{{ note.title }}？</p>
      <p class="ui warning message" v-else>是否删除笔记：{{ note.title }}？</p>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="deleteNote" class="ui negative button">删除</button>
      </template>
    </Modal>

    <Modal v-model="revert" title="恢复笔记">
      <p class="ui success message">是否恢复笔记：{{ note.title }}？</p>
      <template slot="actions">
        <button @click="revert=false" class="ui cancel button">取消</button>
        <button @click="revertNote" class="ui positive button">恢复</button>
      </template>
    </Modal>

    <Modal v-model="modal" title="移动笔记">
      <form class="ui form">
        <div class="required field">
          <label>笔记本</label>
          <el-select v-model="notebookId"
                     filterable
                     size="medium"
                     placeholder="请选择">
            <el-option
              v-for="item in notebooks"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.access }}</span>
            </el-option>
          </el-select>
        </div>
      </form>
      <template slot="actions">
        <button @click="modal=false" class="ui cancel button">取消</button>
        <button @click="moveNote" class="ui positive button" :disabled="!notebookId">移动</button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
import axios from 'axios'
import {toCanvas} from 'qrcode'
import Viewer from 'viewerjs'
import 'viewerjs/dist/viewer.css'
import {toPng} from 'html-to-image'
import {Component, Vue} from 'vue-property-decorator'
import Editor from '@tinymce/tinymce-vue'
import hljs from 'highlight.js'
import {Note} from '@/models/Note'
import {Notebook} from '@/models/Notebook'
import {Comment} from '@/models/Comment'
import {tinymceConfig} from '@/models/mceConfig'
import {Emoji} from '@/types/twemoji'
import Dropdown from '@/components/Dropdown.vue'
import Modal from '@/components/Modal.vue'
import Popup from '@/components/Popup.vue'
import Pagination from '@/components/Pagination.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import MdViewer from '@/components/MdViewer.vue'
import accountService from '@/services/account.service'
import configService from '@/services/config.service'
import {createDiv, createElement, createLink} from '@/utils/utils'
import tinymce from 'tinymce'

declare const twemoji: Emoji

@Component<NoteDetails>({
  components: {
    Dropdown,
    Modal,
    Popup,
    Pagination,
    UserAvatar,
    Editor,
    MdViewer,
  },
  watch: {
    '$route'(to, from) {
      if (to.params.id != from.params.id) {
        this.load()
      } else {
        const anchor = to.query.anchor as string || ''
        this.goAnchor(anchor)
      }
    }
  }
})
export default class NoteDetails extends Vue {
  id: string = ''
  notebookId: string = ''
  comment: string = ''
  characters: number = 0
  page: number = 1
  total: number = 0
  pages: number = 0
  old: boolean = false
  loading: boolean = false
  author: boolean = false
  modal: boolean = false
  confirm: boolean = false
  revert: boolean = false
  note: Note = new Note()
  notebooks: Notebook[] = []
  stickyComments: Comment[] = []
  comments: Comment[] = []
  config = tinymceConfig

  get auth(): boolean {
    return this.$store.state.authenticated
  }

  get admin(): boolean {
    return this.$store.state.admin
  }

  get enableComment(): boolean {
    return this.$store.state.siteConfig.enableComment
  }

  get showViews(): boolean {
    return this.$store.state.siteConfig.showViews || this.$store.state.authenticated
  }

  get showWords(): boolean {
    return this.$store.state.siteConfig.showWords
  }

  get signature(): string {
    const signature = this.note.author.signature
    if (signature) {
      const theme = this.note.author.mdTheme ? `---\ntheme: ${this.note.author.mdTheme}\n---\n` : ''
      return theme + '>' + signature
    } else return ''
  }

  get content(): string {
    return (this.note.author.mdTheme ? `---\ntheme: ${this.note.author.mdTheme}\n---\n` : '') + this.note.content
  }

  mounted() {
    this.load()
    if (this.enableComment) {
      this.getStickyComments().then(() => {
        this.loadComments()
      })
      if (this.auth) {
        tinymce.activeEditor.on('wordCountUpdate', this.wordCountUpdate)
      }
    }
  }

  goAnchor(anchor: string) {
    if (anchor) {
      const element = document.getElementById(anchor)
      if (element) {
        element.scrollIntoView()
      }
    }
  }

  load() {
    this.loading = true
    this.id = this.$route.params.id
    axios.get(`/notes/${this.id}?view=true`).then(({data}) => {
      this.note = data
      configService.setTitle(this.note.title)
      const now = new Date().getTime()
      const diff = now - new Date(this.note.createdTime).getTime()
      this.old = diff > 1000 * 3600 * 24 * 360
      this.author = accountService.account.id === this.note.author.id
      this.loading = false
    }, () => {
      this.loading = false
      this.$router.push('/')
    }).then(() => {
      this.$nextTick(() => {
        document.querySelectorAll('.article').forEach(node => {
          twemoji.parse(node as HTMLElement, {size: 72})
        })

        if (!this.note.markdown) {
          document.querySelectorAll('pre[class*=language-] code').forEach(el => {
            el.classList.add(el.parentElement.className)
            hljs.highlightElement(el as HTMLElement)
          })
        }

        // eslint-disable-next-line
        (window as any).highlightJsBadge({
          copyIconClass: 'copy outline icon',
          checkIconClass: 'check circle icon'
        })

        const qrcode = document.getElementById('qrcode') as HTMLElement
        toCanvas(qrcode, window.location.href, {width: 150})

        const toc = document.querySelector('.mce-toc')
        if (toc) {
          const origin = window.location.origin + '/#'
          const url = window.location.origin + '/#/notes/' + (this.note.slug || this.note.id)
          const li = createElement('li', {}, createLink('[' + this.note.title + ']', url + '?anchor=top'))
          const ul = toc.querySelector('ul')
          ul.insertBefore(li, ul.firstChild)
          if (this.note.tags.length) {
            ul.append(createElement('li', {}, createLink('[标签]', url + '?anchor=tags')))
          }
          if (this.enableComment) {
            ul.append(createElement('li', {}, createLink('[评论]', url + '?anchor=comments')))
          }
          for (const a of toc.querySelectorAll('a')) {
            if (!a.href.includes('?anchor=')) {
              a.href = url + '?anchor=' + a.href.replace(origin, '')
            }
          }
          const width: number = document.getElementById('main')?.clientWidth || 0
          this.adjustToc(toc, width)
          window.addEventListener('resize', () => {
            this.adjustToc(toc, width)
          })
        }

        const el = document.getElementById('content') as HTMLElement
        new Viewer(el, {
          navbar: false, title: false, filter(image: HTMLElement) {
            return image.className !== 'emoji'
          }
        })

        setTimeout(() => {
          const anchor = this.$route.query.anchor as string || ''
          this.goAnchor(anchor)
        }, 500)
      })
    })
  }

  adjustToc(toc: Element, width: number) {
    const offset = (document.body.clientWidth - width) / 2
    toc.classList.remove('rail0', 'rail1', 'rail2')
    if (offset < 350) {
      //
    } else if (offset < 450) {
      toc.classList.add('rail0')
    } else if (offset < 550) {
      toc.classList.add('rail1')
    } else {
      toc.classList.add('rail2')
    }
  }

  showMove() {
    axios.get('/users/-/notebooks').then(({data}) => {
      this.modal = true
      this.notebooks = data.content.filter((e: Notebook) => e.id !== this.note.notebook.id)
    })
  }

  revertNote() {
    axios.post(`/notes/${this.note.id}/revert`).then(({data}) => {
      this.$toasted.success('恢复笔记成功')
      this.revert = false
      this.note = data
    })
  }

  moveNote() {
    axios.post(`/notes/${this.note.id}/move?notebookId=${this.notebookId}`).then(({data}) => {
      this.modal = false
      this.$toasted.success('移动笔记成功')
      this.note = data
    })
  }

  deleteNote() {
    axios.delete(`/notes/${this.note.id}`).then(() => {
      this.confirm = false
      this.$toasted.success('删除笔记成功')
      this.$router.push('/notebooks/' + this.note.notebook.id)
    })
  }

  exportPng() {
    const toc = document.querySelector('.mce-toc')
    const qrcode = createElement('canvas', {style: 'position: absolute;right: 5px;bottom: 0;'})
    toCanvas(qrcode, window.location.href, {width: 90})
    const link = window.location.origin + '/#/notes/' + (this.note.slug || this.note.id)
    const info = createDiv({},
      createDiv({}, '标题：', this.note.title),
      createDiv({}, '作者：', createLink(this.note.author.username)),
      createDiv({}, '链接：', createLink(link, link))
    )
    const divider = this.note.tags.length ? createDiv({class: 'ui divider'}) : createElement('p')
    const div = createDiv({}, divider, info, qrcode)
    const content = document.getElementById('content') as HTMLElement
    content.append(div)

    if (toc) toc.classList.add('hide')

    toPng(content)
      .then((dataUrl) => {
        div.remove()
        if (toc) toc.classList.remove('hide')
        const link = createLink('', dataUrl, this.note.title + '.png')
        link.click()
        link.remove()
      })
  }

  getStickyComments() {
    return axios.get(`/notes/${this.id}/stickyComments?sort=id,desc`).then(({data}) => {
      this.stickyComments.length = 0
      this.stickyComments.push(...data)
    })
  }

  loadComments(page: number = 1) {
    this.page = page
    axios.get(`/notes/${this.id}/comments?sort=id,desc&size=10&page=${page - 1}`).then(({data}) => {
      this.comments.length = 0
      this.comments.push(...this.stickyComments)
      this.stickyComments.forEach(c => {
        const index = data.content.findIndex(e => e.id === c.id)
        if (index >= 0) data.content.splice(index, 1)
      })
      this.comments.push(...data.content)
      this.total = data.totalElements
      this.pages = data.totalPages
    })
  }

  addComment() {
    axios.post(`/notes/${this.id}/comments`, {content: this.comment}).then(() => {
      this.comment = ''
      this.loadComments(1)
    })
  }

  stick(id: number) {
    axios.post(`/comments/${id}/stick`).then(({data}) => {
      this.stickyComments.push(data)
      this.comments.unshift(data)
    })
  }

  unstick(id: number) {
    axios.delete(`/comments/${id}/stick`).then(() => {
      let index = this.stickyComments.findIndex(e => e.id === id)
      this.stickyComments.splice(index, 1)
      index = this.comments.findIndex(e => e.id === id)
      this.comments.splice(index, 1)
    })
  }

  wordCountUpdate(event) {
    this.characters = event.wordCount.characters
  }
}
</script>

<style scoped>
.edit {
  margin-left: 3px;
}

.article {
  word-break: break-word;
}

.share {
  margin-left: 8px;
}

#content {
  margin-top: 0;
}

.ui.segment .ui.comments {
  margin: 1.5em 0;
  max-width: 100%;
}

.ui.segment .ui.comments .reply.form textarea {
  height: auto;
}

.ui.segment .ui.comments .comment .ui.mini.label {
  font-size: 12px;
  padding: 3px 4px;
}

.error {
  display: inline-block;
  background-color: #fff6f6;
  color: #9f3a38;
  box-shadow: 0 0 0 1px #e0b4b4 inset, 0 0 0 0 transparent;
  font-size: 1em;
  padding: 0.568em 1em;
  margin-left: 6px;
}

#qrcode {
  width: 150px;
  height: 150px;
}
</style>
