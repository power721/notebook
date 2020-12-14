<template>
  <div class="ui left aligned container">
    <div class="ui form">
      <div class="required field">
        <label>站点名称</label>
        <input type="text" v-model="siteConfig.siteName" name="siteName">
      </div>
      <div class="field">
        <label>ICP备案</label>
        <input type="text" v-model="siteConfig.icpBeian" name="icpBeian">
      </div>
      <div class="field">
        <label>公安备案</label>
        <input type="text" v-model="siteConfig.govBeian" name="govBeian">
      </div>
      <div class="field">
        <div class="ui toggle checkbox">
          <input type="checkbox" name="enableSignup" v-model="siteConfig.enableSignup">
          <label>允许注册</label>
        </div>
      </div>
      <div class="field">
        <div class="ui toggle checkbox">
          <input type="checkbox" name="enableComment" v-model="siteConfig.enableComment">
          <label>允许评论</label>
        </div>
      </div>
      <button class="ui button" @click.prevent="save">保存</button>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import {SiteConfig} from '@/models/SiteConfig'
  import configService from '@/services/config.service'

  @Component
  export default class Admin extends Vue {
    siteConfig: SiteConfig = new SiteConfig()

    mounted() {
      configService.getSiteConfig().then((data) => {
        Object.assign(this.siteConfig, data)
      })
    }

    save() {
      configService.updateSiteConfig(this.siteConfig).then((data) => {
        Object.assign(this.siteConfig, data)
      })
    }
  }
</script>

<style scoped>

</style>
