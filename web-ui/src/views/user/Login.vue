<template>
  <div class="ui basic segment">
    <div class="ui raised card">
      <div class="content">
        <div class="header">用户登录</div>
      </div>
      <div class="content">
        <div class="description">
          <form class="ui form" :class="{error: error, success: success}">
            <div class="required field">
              <label>用户账号</label>
              <input type="text" name="username" v-model="account.username" placeholder="用户账号">
            </div>
            <div class="required field">
              <label>账号密码</label>
              <input type="password" name="password" v-model="account.password" placeholder="账号密码">
            </div>
            <div class="field">
              <div class="ui checkbox">
                <input type="checkbox" name="remember" v-model="account.rememberMe">
                <label>记住登录</label>
              </div>
            </div>
            <div class="ui success message" v-if="success">
              登录成功
            </div>
            <div class="ui error message">
              <p>{{error}}</p>
            </div>
            <button class="ui primary button" :disabled="success" @click.prevent="submit">登录</button>
          </form>
        </div>
      </div>
      <div class="extra content" v-if="!disableSignup">
        没有帐号？
        <router-link to="/signup">注册</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import accountService from '@/services/account.service'

  @Component
  export default class Login extends Vue {
    error: string = ''
    success: boolean = false
    disableSignup: boolean = false
    account = {
      username: '',
      password: '',
      rememberMe: localStorage.getItem('rememberMe') === 'true',
    }

    mounted() {
      document.title = '用户登录'
      this.load()
    }

    load() {
      axios.get('/config/disable_signup').then(({data}) => {
        if (data) {
          this.disableSignup = data.value === 'true'
        }
      })
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
      localStorage.setItem('rememberMe', this.account.rememberMe + '')
      accountService.login(this.account.username, this.account.password, this.account.rememberMe).then(() => {
        this.success = true
        //const back = this.$route.query.redirect || '/'
        setTimeout(() => this.$router.push('/'), 500)
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
