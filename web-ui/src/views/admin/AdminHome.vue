<template>
  <div class="ui fluid container">

    <div class="ui pointing secondary menu" v-if="wide">
      <router-link class="item" to="/admin/info">系统信息</router-link>
      <router-link class="item" to="/admin/stats">统计信息</router-link>
      <router-link class="item" to="/admin/audit">操作日志</router-link>
      <router-link class="item" to="/admin/menu">菜单管理</router-link>
      <router-link class="item" to="/admin/config">站点设置</router-link>
    </div>
    <div class="ui pointing secondary menu" v-else>
      <router-link class="item" to="/admin/info" title="系统信息"><i class="info icon"></i></router-link>
      <router-link class="item" to="/admin/stats" title="统计信息"><i class="database icon"></i></router-link>
      <router-link class="item" to="/admin/audit" title="操作日志"><i class="bell icon"></i></router-link>
      <router-link class="item" to="/admin/menu" title="菜单管理"><i class="list icon"></i></router-link>
      <router-link class="item" to="/admin/config" title="站点设置"><i class="setting icon"></i></router-link>
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
    wide = window.innerWidth > 720
    mounted() {
      window.addEventListener('resize', () => {
        this.wide = window.innerWidth > 720
      })
    }
  }
</script>

<style scoped>

</style>
