<template>
  <div class="ui basic segment">
    <div class="ui raised card">
      <div class="content">
        <div class="header">用户注册</div>
      </div>
      <div class="content">
        <div class="description">
          <form class="ui form" :class="{error: error, success: success}">
            <div class="required field">
              <label>用户账号</label>
              <input type="text" name="username" autocomplete="username" v-model="account.username" placeholder="用户账号">
            </div>
            <div class="required field">
              <label>账号密码</label>
              <input type="password" name="password" autocomplete="new-password" v-model="account.password" placeholder="账号密码">
            </div>
            <div class="required field">
              <label>确认密码</label>
              <input type="password" name="confirm" v-model="account.confirm" placeholder="确认密码">
            </div>
            <div class="ui success message" v-if="success">
              注册成功
            </div>
            <div class="ui error message">
              <p>{{error}}</p>
            </div>
            <button class="ui primary button" :disabled="success" @click.prevent="submit">注册</button>
          </form>
        </div>
      </div>
      <div class="extra content">
        已有帐号？
        <router-link to="/login">登录</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import accountService from '@/services/account.service'
  import configService from '@/services/config.service'

  @Component
  export default class Signup extends Vue {
    error: string = ''
    success: boolean = false
    account = {
      username: '',
      password: '',
      confirm: '',
    }

    mounted() {
      configService.setTitle('用户注册')
      this.check()
    }

    check() {
      if (!configService.siteConfig.enableSignup) {
        this.$toasted.error('禁止用户注册')
        setTimeout(() => this.$router.push('/'), 100)
      }
    }

    submit() {
      this.error = ''
      if (this.account.username.length < 6) {
        this.error = '账号长度至少6位'
        return
      }
      if (this.account.password.length < 8) {
        this.error = '密码长度至少8位'
        return
      }
      if (this.account.password !== this.account.confirm) {
        this.error = '密码不匹配'
        return
      }
      accountService.signup(this.account.username, this.account.password).then(() => {
        this.success = true
        setTimeout(() => this.$router.push('/login'), 1000)
      }, ({response}) => {
        this.error = response.data.message
      })
    }
  }
</script>

<style scoped>
  .ui.card {
    max-width: 450px;
    margin: 96px auto;
  }
</style>
