<template>
  <div class="ui fluid container">

    <div class="ui pointing secondary menu">
      <router-link class="item" to="/user/notes">我的笔记</router-link>
      <router-link class="item" to="/user/notebooks">我的笔记本</router-link>
      <router-link class="item" to="/user/trash">回收站</router-link>
      <router-link class="item" to="/user/info">用户设置</router-link>
    </div>

    <transition name="fade" mode="out-in">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'

  @Component
  export default class UserHome extends Vue {
  }
</script>

<style scoped>

</style>
