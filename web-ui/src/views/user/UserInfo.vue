<template>
  <div class="ui container">
    <div class="ui left aligned container">
      <div class="ui breadcrumb">
        <router-link class="section" :exact="true" to="/">首页</router-link>
        <i class="right chevron icon divider"></i>
        <div class="section">用户中心</div>
        <i class="right chevron icon divider"></i>
        <div class="active section">用户设置</div>
      </div>
    </div>
    <div class="ui divider"></div>

    <div class="ui cards">
      <div class="card">
        <div class="content">
          <div class="header">用户信息</div>
          <div class="description">
            <form class="ui form" :class="{error: error, success: success}">
              <div class="field">
                <label>用户账号</label>
                <input type="text" name="username" v-model="account.username" readonly>
              </div>
              <div class="field">
                <label>用户邮箱</label>
                <input type="email" name="email" v-model="account.email" placeholder="邮箱">
              </div>
              <div class="field">
                <label>原密码</label>
                <input type="password" name="password" v-model="account.password" placeholder="账号密码">
              </div>
              <div class="field">
                <label>新密码</label>
                <input type="password" name="newPassword" v-model="account.newPassword" placeholder="新密码">
              </div>
              <div class="field">
                <label>确认密码</label>
                <input type="password" name="confirm" v-model="account.confirm" placeholder="确认密码">
              </div>
              <div class="ui success message" v-if="success">
                更新成功
              </div>
              <div class="ui error message">
                <p>{{error}}</p>
              </div>
              <button class="ui primary button" :disabled="success" @click.prevent="submit">更新</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import accountService from '@/services/account.service'
  import {Account} from '@/models/Account'
  import configService from '@/services/config.service'

  @Component
  export default class UserInfo extends Vue {
    error: string = ''
    success: boolean = false
    account = {
      email: this.user.email || '',
      username: this.user.username,
      password: '',
      newPassword: '',
      confirm: ''
    }

    get user(): Account {
      return this.$store.state.user
    }

    mounted() {
      configService.setTitle('用户设置')
    }

    submit() {
      this.error = ''
      if (this.account.newPassword) {
        if (this.account.newPassword.length < 8) {
          this.error = '密码长度至少8位'
          return
        }
        if (this.account.newPassword !== this.account.confirm) {
          this.error = '密码不匹配'
          return
        }
      }

      const account = {
        email: this.account.email,
        password: this.account.password,
        newPassword: this.account.newPassword,
      }
      accountService.update(account).then(() => {
        this.success = true
        this.account.password = ''
        this.account.newPassword = ''
        this.account.confirm = ''
        this.$toasted.success('更新成功')
        setTimeout(() => this.success = false, 2000)
      }, (error) => {
        this.error = error.message
      })
    }
  }
</script>

<style scoped>
  .ui.cards {
    max-width: 450px;
    margin: 96px auto;
  }
</style>
