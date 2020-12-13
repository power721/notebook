<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="active section">关于</div>
    </div>
    <div class="ui divider"></div>
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
