<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">标签</div>
    </div>
    <div class="ui divider"></div>

    <div class="ui divided items raised segment">
      <div class="ui blue tag labels">
        <router-link :to="'/tags/'+tag.name" class="ui label" :key="tag.name" v-for="tag of tags">
          {{tag.name}}
        </router-link>
      </div>
    </div>

    <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="go"></Pagination>
  </div>
</template>
<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'
  import Modal from '@/components/Modal.vue'
  import {goTop} from '@/utils/utils'
  import configService from '@/services/config.service'
  import {Tag} from '@/models/Note'

  @Component<Pageable>({
    components: {
      Modal,
      Pagination
    },
    watch: {
      '$route'(to) {
        this.page = +to.query.page || 1
        this.load()
      }
    }
  })
  export default class TagList extends Pageable {
    modal: boolean = false
    tags: Tag[] = []

    get admin(): boolean {
      return this.$store.state.admin
    }

    mounted() {
      configService.setTitle('标签')
      this.size = 100
      this.page = +this.$route.query.page || 1
      this.load()
    }

    load() {
      axios.get(`/tags?${this.query}`).then(({data}) => {
        this.tags = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
        goTop()
      })
    }

    go(page: number) {
      this.$router.push(this.$route.path + '?page=' + page)
    }
  }
</script>

<style scoped>
</style>
