<template>
  <div class="ui left aligned container">
    <router-link class="ui right floated icon primary button" data-tooltip="创建笔记" to="/notes/-/new">
      <i class="edit icon"></i>
    </router-link>
    <div class="ui divided items">
      <div class="item" v-for="note in notes" :key="note.id">
        <div class="content">
          <router-link class="header" :to="'/notes/'+note.id">{{note.title}}</router-link>
          <div class="meta">
            <a>{{note.author.username}}</a>
            <router-link :to="'/notebooks/'+note.notebook.id">@{{note.notebook.name}}</router-link>
          </div>
          <div class="extra">
            创建于{{note.createdTime | fromNow}}({{note.createdTime | datetime}})
          </div>
        </div>
      </div>
    </div>

    <Pagination v-model="page" :totalPages="totalPages" @change="go"></Pagination>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Note} from '@/models/Note'
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'

  @Component<Pageable>({
    components: {
      Pagination
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.load()
      }
    }
  })
  export default class NoteList extends Pageable {
    notes: Note[] = []

    mounted() {
      document.title = '笔记'
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/notes?page=${this.page - 1}&size=${this.size}`).then(({data}) => {
        this.notes = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
      })
    }

    go(page: number) {
      this.$router.push('/?page=' + page)
    }
  }
</script>
