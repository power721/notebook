<template>
  <div class="ui left aligned container">
    <div class="ui raised segment">
      <table class="ui selectable celled table">
        <thead>
        <tr>
          <th>ID</th>
          <th>类型</th>
          <th>内容</th>
          <th>操作人</th>
          <th>目标ID</th>
          <th>客户端IP</th>
          <th>Referer</th>
          <th>客户端浏览器</th>
          <th>时间</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="audit of audits" :key="audit.id">
          <td>{{audit.id}}</td>
          <td>{{audit.type}}</td>
          <td>{{audit.content}}</td>
          <td>
            <router-link :to="'/users/'+audit.operator.id">{{audit.operator.username}}</router-link>
          </td>
          <td>{{audit.target}}</td>
          <td>
            <a :href="'http://ip-api.com/json/'+audit.clientIp+'?lang=zh-CN'" target="_blank">{{audit.clientIp}}</a>
          </td>
          <td>{{audit.referer}}</td>
          <td>{{audit.userAgent}}</td>
          <td>{{audit.createdTime | datetime}}</td>
        </tr>
        </tbody>
      </table>

      <Pagination v-model="page" :pages="totalPages" :total="totalElements" @change="load"></Pagination>
    </div>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component} from 'vue-property-decorator'
  import configService from '@/services/config.service'
  import {Audit} from '@/models/Audit'
  import {Pageable} from '@/components/Pageable'
  import Pagination from '@/components/Pagination.vue'

  @Component({
    components: {
      Pagination,
    },
  })
  export default class AdminAudit extends Pageable {
    audits: Audit[] = []

    mounted() {
      configService.setTitle('操作日志')
      this.load()
    }

    load() {
      axios.get(`/admin/audit?${this.query}`).then(({data}) => {
        this.audits = data.content
        this.totalPages = data.totalPages
        this.totalElements = data.totalElements
      })
    }
  }
</script>

<style scoped>

</style>
