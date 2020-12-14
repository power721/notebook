<template>
  <div class="login">
    <div class="ui dropdown active" :class="{visible: show}" v-if="account.id">
      <div class="text" @click.stop="show=!show">{{account.username}}</div>
      <i class="dropdown icon" @click.stop="show=!show"></i>
      <div id="user-menu" class="menu transition" :class="{visible: show}">
        <router-link class="item" to="/notes/-/new">创建笔记</router-link>
        <router-link class="item" to="/my-notebooks">我的笔记本</router-link>
        <router-link class="item" to="/my-notes">我的笔记</router-link>
        <router-link class="item" to="/info">用户设置</router-link>
        <router-link class="item" to="/admin" v-if="admin">管理中心</router-link>
        <div class="item" @click.stop="logout">退出登录</div>
      </div>
    </div>
    <router-link v-else to="/login">登录</router-link>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import accountService from '@/services/account.service'
  import {Account, Role} from '@/models/Account'
  import eventService from '@/services/event.service'

  @Component
  export default class UserAccount extends Vue {
    handler: number = 0
    show: boolean = false

    get admin(): boolean {
      return this.$store.state.user.role === Role[Role.ROLE_ADMIN]
    }

    get account(): Account {
      return this.$store.state.user
    }

    mounted() {
      this.handler = eventService.on('click', () => {
        this.show = false
      })
      console.log('UserAccount mounted', this.handler)
    }

    destroyed() {
      eventService.off('click', this.handler)
      console.log('UserAccount destroyed', this.handler)
    }

    logout() {
      accountService.logout()
      if (this.$route.meta.auth) {
        setTimeout(() => this.$router.push('/'), 200)
      }
    }
  }
</script>

<style scoped>
  .login {
    margin-right: 12px;
  }

  @media only screen and (max-width: 767px) {
    #user-menu {
      right: -36px;
    }
  }
</style>
