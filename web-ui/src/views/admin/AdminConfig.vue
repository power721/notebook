<template>
  <div class="ui left aligned container">
    <div class="ui form">
      <div class="required field">
        <label>站点名称</label>
        <input type="text" v-model="siteConfig.siteName" name="siteName">
      </div>
      <div class="required field">
        <label>品牌颜色</label>
        <el-select v-model="siteConfig.brandColor">
          <el-option v-for="item in colors" :key="item.id" :label="item.name" :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;height: 24px;margin-top: 4px" class="ui label" :class="[item.id]"></span>
          </el-option>
        </el-select>
      </div>
      <div class="field">
        <label>二维码</label>
        <input type="text" v-model="siteConfig.qrCode" name="qrCode">
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
          <input type="checkbox" name="enableAudit" v-model="siteConfig.enableAudit">
          <label>开启审计</label>
        </div>
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
  export default class AdminConfig extends Vue {
    siteConfig: SiteConfig = new SiteConfig()
    colors = [
      {id: 'red', name: '红色'},
      {id: 'orange', name: '橙色'},
      {id: 'yellow', name: '黄色'},
      {id: 'olive', name: '橄榄色'},
      {id: 'green', name: '绿色'},
      {id: 'teal', name: '蓝绿色'},
      {id: 'blue', name: '蓝色'},
      {id: 'violet', name: '紫罗兰'},
      {id: 'purple', name: '紫色'},
      {id: 'pink', name: '粉色'},
      {id: 'brown', name: '棕色'},
    ]

    mounted() {
      configService.setTitle('站点设置')
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
