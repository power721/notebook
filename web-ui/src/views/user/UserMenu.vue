<template>
  <div class="login">
    <div class="ui pointing dropdown active" :class="{visible: show}" v-if="account.id">
      <div class="text" @click.stop="show=!show">{{account.username}}</div>
      <i class="dropdown icon" @click.stop="show=!show"></i>
      <div id="user-menu" class="menu transition" :class="{visible: show}">
        <router-link class="item" to="/notes/-/new">创建笔记</router-link>
        <router-link class="item" to="/user">用户中心</router-link>
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
  export default class UserMenu extends Vue {
    handler: number = 0
    show: boolean = false

    get admin(): boolean {
      return this.$store.state.user.role === Role[Role.ROLE_ADMIN]
    }

    get account(): Account {
      return this.$store.state.user
    }

    mounted() {
      this.handler = eventService.on(['click', 'touchend'], () => {
        this.show = false
      })
    }

    destroyed() {
      eventService.off(['click', 'touchend'], this.handler)
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

  .ui.dropdown>.dropdown.icon {
    margin-left: 6px;
  }

  #user-menu {
    margin-top: 6px;
  }

  @media only screen and (max-width: 767px) {
    #user-menu {
      right: -36px;
    }
  }
</style>
