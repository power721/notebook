<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <router-link class="section" to="/trash-notes" v-if="note.deleted">回收站</router-link>
      <i class="right chevron icon divider" v-if="note.deleted"></i>
      <router-link class="section" :to="'/notebooks/'+(note.notebook.slug?note.notebook.slug:note.notebook.id)">{{note.notebook.name}}</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">
        <i class="lock icon" v-if="note.access==='PRIVATE'"></i>
        <i class="unlock alternate icon" v-if="note.access==='SECRET'"></i>
        {{note.title}}</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui center aligned raised segment">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">加载中</div>
      </div>
      <h1 class="ui header" v-if="note.deleted">
        <del>{{note.title}}</del>
      </h1>
      <h1 class="ui header" v-else>
        {{note.title}}
      </h1>
      <a class="ui top left attached label" data-tooltip="知道ID才能访问" v-if="note.access==='SECRET'">秘密</a>
      <a class="ui top left attached label" data-tooltip="只有你可以访问" v-if="note.access==='PRIVATE'">私有</a>
      <div class="metadata">
        <router-link :to="'/users/'+note.author.id">@{{note.author.username}}</router-link>
        <span :data-tooltip="note.createdTime | datetime">
          创建于{{note.createdTime | fromNow}}
        </span>
        <span :data-tooltip="note.updatedTime | datetime" v-if="note.version>1">
          更新于{{note.updatedTime | fromNow}}
        </span>
        <router-link class="ui teal small label" :to="'/categories/'+(note.category.slug?note.category.slug:note.category.id)">
          {{note.category.name}}
        </router-link>&nbsp;
        <span v-if="showWords">字数：{{note.words}}</span>&nbsp;&nbsp;
        <span v-if="showViews&&note.access!=='PRIVATE'">阅读：{{note.views}}</span>
        <router-link :to="'/notes/'+(note.slug?note.slug:note.id)+'/edit'" class="edit" data-tooltip="编辑笔记" v-if="author&&!note.deleted">
          <i class="edit icon"></i>
        </router-link>
        <Dropdown icon="bars" position="top right" :pointing="true" v-if="author">
          <router-link class="item" :to="'/notes/'+(note.slug?note.slug:note.id)+'/edit'" v-if="!note.deleted">编辑笔记</router-link>
          <router-link class="item" :to="'/notes/'+(note.slug?note.slug:note.id)+'/history'" v-if="note.version>1">历史记录</router-link>
          <a class="item" @click="confirm=true">删除笔记</a>
          <a class="item" @click="showMove" v-if="!note.deleted">移动笔记</a>
          <a class="item" @click="revert=true" v-if="note.deleted">恢复笔记</a>
        </Dropdown>
      </div>
    </div>

    <div class="ui left aligned raised segment">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">加载中</div>
      </div>
      <div class="ui warning message" v-if="old">
        <i class="warning icon"></i>
        <template v-if="note.updatedTime">
          本文编写于 {{note.createdTime | fromNow}}，最后修改于 {{note.updatedTime | fromNow}}
          （{{note.updatedTime | date}}），其中某些信息可能已经过时。
        </template>
        <template v-else>本文编写于 {{note.createdTime | fromNow}}，其中某些信息可能已经过时。</template>
      </div>
      <div class="article content" v-html="note.content"></div>
      <div class="footer">
        <div class="ui divider"></div>
        <div class="ui blue tag labels">
          <router-link :to="'/tags/'+tag.name" class="ui label" :key="tag.name" v-for="tag of note.tags">
            {{tag.name}}
          </router-link>
        </div>
      </div>
    </div>

    <Modal v-model="confirm" title="删除笔记">
      <p class="ui error message" v-if="note.deleted">是否永久删除笔记：{{note.title}}？</p>
      <p class="ui warning message" v-else>是否删除笔记：{{note.title}}？</p>
      <template slot="actions">
        <button @click="confirm=false" class="ui cancel button">取消</button>
        <button @click="deleteNote" class="ui negative button">删除</button>
      </template>
    </Modal>

    <Modal v-model="revert" title="恢复笔记">
      <p class="ui success message">是否恢复笔记：{{note.title}}？</p>
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
                     placeholder="请选择">
            <el-option
              v-for="item in notebooks"
              :key="item.id"
              :label="item.name"
              :value="item.id">
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
  import {Component} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import accountService from '@/services/account.service'
  import Dropdown from '@/components/Dropdown.vue'
  import Modal from '@/components/Modal.vue'
  import configService from '@/services/config.service'
  import {Notebook} from '@/models/Notebook'
  import {EntityView} from "@/components/EntityView";

  @Component<EntityView>({
    components: {
      Dropdown,
      Modal,
    },
    watch: {
      '$route'(to) {
        this.id = to.params.id
        this.load()
      }
    }
  })
  export default class NoteDetails extends EntityView {
    notebookId: string = ''
    old: boolean = false
    loading: boolean = false
    author: boolean = false
    modal: boolean = false
    confirm: boolean = false
    revert: boolean = false
    note: Note = new Note()
    notebooks: Notebook[] = []

    get showViews(): boolean {
      return this.$store.state.siteConfig.showViews || this.$store.state.authenticated
    }

    get showWords(): boolean {
      return this.$store.state.siteConfig.showWords
    }

    mounted() {
      this.id = this.$route.params.id
      this.load()
    }

    load() {
      this.loading = true
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
        setTimeout(() => {
          document.querySelectorAll('pre[class*=language-]').forEach(e => e.classList.add('line-numbers'))
          document.querySelectorAll('.article').forEach(node => {
            // eslint-disable-next-line
            (window as any).twemoji.parse(node, {'size': 72})
          });
          // eslint-disable-next-line
          (window as any).Prism.highlightAll()
        }, 0)
      })
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
  }
</script>

<style scoped>
  .edit {
    margin-left: 3px;
  }
</style>
