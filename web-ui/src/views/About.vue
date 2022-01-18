<template>
  <div class="ui left aligned fluid container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">关于</div>
    </div>
    <div class="ui divider"></div>
    <div class="ui vertical stripe segment">
      <div class="ui text container">
        <h3 class="ui header">我的笔记</h3>
        <p>支持多用户的笔记系统。</p>
        <p>本站使用<a href="https://vuejs.org/" target="_blank">Vue</a>(TypeScript) +
          <a href="https://spring.io/" target="_blank">Spring</a>(Kotlin)开发。</p>
        <p>UI框架：
          <a href="https://semantic-ui.com/" target="_blank">Semantic UI</a>，
          <a href="https://element.eleme.cn/" target="_blank">Element</a>
        </p>
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
