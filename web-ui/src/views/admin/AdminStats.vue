<template>
  <div class="ui left aligned fluid container">
    <div class="ui raised segment">
      <div class="ui statistics">
        <div class="statistic">
          <div class="value">
            {{stats.note.total}}
          </div>
          <div class="label">
            笔记
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.note.deleted}}
          </div>
          <div class="label">
            回收站
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.note.views}}
          </div>
          <div class="label">
            阅读
          </div>
        </div>
      </div>
      <div class="ui statistics">
        <div class="statistic">
          <div class="value">
            {{stats.note.public}}
          </div>
          <div class="label">
            公开笔记
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.note.secret}}
          </div>
          <div class="label">
            秘密笔记
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.note.private}}
          </div>
          <div class="label">
            私有笔记
          </div>
        </div>
      </div>
      <div class="ui statistics">
        <div class="statistic">
          <div class="value">
            {{stats.user.total}}
          </div>
          <div class="label">
            注册用户
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.user.online}}
          </div>
          <div class="label">
            在线用户
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.user.online - stats.user.anonymous}}
          </div>
          <div class="label">
            登录用户
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.user.anonymous}}
          </div>
          <div class="label">
            匿名用户
          </div>
        </div>
      </div>
      <div class="ui statistics">
        <div class="statistic">
          <div class="value">
            {{stats.notebooks}}
          </div>
          <div class="label">
            笔记本
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.categories}}
          </div>
          <div class="label">
            分类
          </div>
        </div>
        <div class="statistic">
          <div class="value">
            {{stats.tags}}
          </div>
          <div class="label">
            标签
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import configService from '@/services/config.service'
  import {SystemStats} from '@/models/SystemStats'

  @Component
  export default class AdminStats extends Vue {
    stats: SystemStats = new SystemStats()

    mounted() {
      configService.setTitle('统计信息')
      this.load()
    }

    load() {
      axios.get('/admin/stats').then(({data}) => {
        this.stats = data
      })
    }
  }
</script>

<style scoped>

</style>
