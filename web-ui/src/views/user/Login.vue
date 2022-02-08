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
              <input type="text" name="username" autocomplete="username" v-model="account.username" placeholder="用户账号">
            </div>
            <div class="required field">
              <label>账号密码</label>
              <input type="password" name="password" autocomplete="current-password" v-model="account.password"
                     placeholder="账号密码">
            </div>
            <div class="required field" v-if="captcha">
              <label>验证码</label>
              <div class="ui input">
                <input type="text" name="captcha" v-model="account.captcha" placeholder="验证码">
                <a title="点击刷新验证码" @click="t=new Date().getTime()">
                  <img alt="验证码" :src="'/captcha?name='+account.username+'&t='+t">
                </a>
              </div>
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
              <p>{{ error }}</p>
            </div>
            <button class="ui primary button" :disabled="success||blocked" @click.prevent="submit">登录</button>
          </form>
        </div>
      </div>
      <div class="extra content" v-if="enableSignup">
        没有帐号？
        <router-link to="/signup">注册</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator'
import accountService from '@/services/account.service'
import configService from '@/services/config.service'

@Component({
  watch: {
    '$store.state.authenticated': function (value) {
      if (value) {
        const back = (this.$route.query.redirect as string) || '/'
        this.$router.push(back)
      }
    }
  }
})
export default class Login extends Vue {
  error: string = ''
  success: boolean = false
  blocked: boolean = false
  captcha: boolean = false
  t: number = 0
  account = {
    username: localStorage.getItem('username') || '',
    password: '',
    captcha: '',
    rememberMe: localStorage.getItem('rememberMe') === 'true',
  }

  mounted() {
    if (this.$store.state.authenticated) {
      const back = (this.$route.query.redirect as string) || '/'
      this.$router.push(back)
    }
    configService.setTitle('用户登录')
  }

  get enableSignup() {
    return this.$store.getters.enableSignup
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
    if (this.captcha && !this.account.captcha) {
      this.error = '请填写验证码'
      return
    }

    localStorage.setItem('username', this.account.username)
    localStorage.setItem('rememberMe', this.account.rememberMe + '')
    accountService.login(this.account.username, this.account.password, this.account.rememberMe, this.account.captcha).then(() => {
      this.success = true
      const back = (this.$route.query.redirect as string) || '/'
      setTimeout(() => this.$router.push(back), 500)
    }, (error) => {
      if (error.code === 10003) {
        this.blocked = true
        this.error = error.message
        setTimeout(() => this.$router.push('/'), 2000)
        return
      }

      if (this.captcha && error.code !== 10002) {
        this.t = new Date().getTime()
        this.account.captcha = ''
      }
      if (error.code === 10001) {
        this.captcha = true
      } else if (error.code === 10002) {
        this.captcha = true
      }
      this.error = error.message
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
