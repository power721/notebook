<template>
  <div class="ui fluid container">

    <div class="ui pointing secondary menu" v-if="wide">
      <router-link class="item" to="/user/notes">我的笔记</router-link>
      <router-link class="item" to="/user/notebooks">我的笔记本</router-link>
      <router-link class="item" to="/user/trash">回收站</router-link>
      <router-link class="item" to="/user/security">账号安全</router-link>
      <router-link class="item" to="/user/info">用户设置</router-link>
    </div>
    <div class="ui pointing secondary menu" v-else>
      <router-link class="item" to="/user/notes" title="我的笔记">
        <i class="file alternate outline icon"></i>
      </router-link>
      <router-link class="item" to="/user/notebooks" title="我的笔记本"><i class="book icon"></i></router-link>
      <router-link class="item" to="/user/trash" title="回收站"><i class="trash icon"></i></router-link>
      <router-link class="item" to="/user/security" title="账号安全"><i class="shield alternate icon"></i></router-link>
      <router-link class="item" to="/user/info" title="用户设置"><i class="setting icon"></i></router-link>
    </div>

    <transition name="fade" mode="out-in">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import eventService from '@/services/event.service'

  @Component
  export default class UserHome extends Vue {
    wide = window.innerWidth > 720
    handler: number = 0

    mounted() {
      this.handler = eventService.on('resize', () => {
        this.wide = window.innerWidth > 720
      })
    }

    destroyed() {
      eventService.off('resize', this.handler)
    }
  }
</script>

<style scoped>

</style>
