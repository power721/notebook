<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">关于</div>
    </div>
    <div class="ui divider"></div>
    <div class="ui vertical stripe segment">
      <div class="ui text container">
        <h3 class="ui header">我的笔记</h3>
        <p>本站使用Vue(TypeScript)+Spring(Kotlin)开发。</p>
        <p>分享关于Java，Kotlin，Spring，云计算，大数据，Vue，nodejs等技术学习笔记。</p>
        <a class="ui large button" href="https://gitee.com/power/notebook" target="_blank">Read More</a>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import configService from '@/services/config.service'

  @Component
  export default class About extends Vue {
    mounted() {
      configService.setTitle('关于')
    }
  }
</script>
