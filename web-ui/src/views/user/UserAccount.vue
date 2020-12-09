<template>
  <div class="login">
    <div class="ui dropdown active" :class="{visible: visible}" v-if="account.id">
      <div class="text" @click.stop="visible=!visible">{{account.username}}</div>
      <i class="dropdown icon" @click.stop="visible=!visible"></i>
      <div class="menu transition" :class="{visible: visible}">
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
  import {Account} from '@/models/Account'

  @Component
  export default class UserAccount extends Vue {
    visible: boolean = false

    get admin(): boolean {
      return this.$store.state.user.role === 'ROLE_ADMIN'
    }

    get account(): Account {
      return this.$store.state.user
    }

    mounted() {
      document.onclick = () => {
        this.visible = false
      }
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
    position: absolute;
    right: 30px;
    top: 6px;
  }
</style>
