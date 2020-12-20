<template>
  <div class="ui container">

    <div class="ui pointing secondary menu">
      <router-link class="item" to="/admin/info">系统信息</router-link>
      <router-link class="item" to="/admin/stats">统计信息</router-link>
      <router-link class="item" to="/admin/audit">操作日志</router-link>
      <router-link class="item" to="/admin/menu">菜单配置</router-link>
      <router-link class="item" to="/admin/config">站点设置</router-link>
    </div>

    <transition name="fade" mode="out-in">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'

  @Component
  export default class AdminHome extends Vue {
    mounted() {
      //
    }
  }
</script>

<style scoped>

</style>
