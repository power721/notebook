<template>
  <div class="ui left aligned fluid container">
    <div class="ui form">
      <div class="required field">
        <label>站点名称</label>
        <input type="text" v-model="siteConfig.siteName" name="siteName">
      </div>
      <div class="required field">
        <label>站点名称颜色</label>
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

      <div class="ui equal width grid">
        <div class="column">
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="showViews" v-model="siteConfig.showViews">
              <label>游客显示阅读量</label>
            </div>
          </div>
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="showWords" v-model="siteConfig.showWords">
              <label>笔记显示字数</label>
            </div>
          </div>
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableComment" v-model="siteConfig.enableComment">
              <label>笔记允许评论</label>
            </div>
          </div>
        </div>
        <div class="column">
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableFileUpload" v-model="siteConfig.enableFileUpload">
              <label>开启文件上传</label>
            </div>
          </div>
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableImageUpload" v-model="siteConfig.enableImageUpload">
              <label>开启图片上传</label>
            </div>
          </div>
        </div>
        <div class="column">
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableSignup" v-model="siteConfig.enableSignup">
              <label>开启用户注册</label>
            </div>
          </div>
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableHeartbeat" v-model="siteConfig.enableHeartbeat">
              <label>开启用户心跳</label>
            </div>
          </div>
        </div>
        <div class="column">
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableAudit" v-model="siteConfig.enableAudit">
              <label>开启操作日志</label>
            </div>
          </div>
          <div class="field">
            <div class="ui toggle checkbox">
              <input type="checkbox" name="enableEncrypt" v-model="siteConfig.enableEncrypt">
              <label>开启数据加密</label>
            </div>
          </div>
        </div>
      </div>

      <h4 class="ui dividing header" v-show="enableUpload">七牛云</h4>
      <div class="field" v-show="enableUpload">
        <div class="ui toggle checkbox">
          <input type="checkbox" name="enabled" v-model="siteConfig.qiniu.enabled">
          <label>启用</label>
        </div>
      </div>
      <div class="required field" v-show="enableUpload&&siteConfig.qiniu.enabled">
        <label>Access Key</label>
        <input type="text" name="accessKey" v-model="siteConfig.qiniu.accessKey">
      </div>
      <div class="required field" v-show="enableUpload&&siteConfig.qiniu.enabled">
        <label>Secret Key</label>
        <input type="text" name="secretKey" v-model="siteConfig.qiniu.secretKey">
      </div>
      <div class="required field" v-show="enableUpload&&siteConfig.qiniu.enabled">
        <label>空间</label>
        <input type="text" name="bucket" v-model="siteConfig.qiniu.bucket">
      </div>
      <div class="required field" v-show="enableUpload&&siteConfig.qiniu.enabled">
        <label>域名</label>
        <input type="url" name="domain" v-model="siteConfig.qiniu.domain">
      </div>

      <button class="ui button" @click.prevent="save">保存</button>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator'
import {SiteConfig} from '@/models/SiteConfig'
import configService from '@/services/config.service'
import axios from 'axios'
import {isValidHttpUrl} from '@/utils/utils'

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

  get enableUpload() {
    return this.siteConfig.enableFileUpload || this.siteConfig.enableImageUpload
  }

  mounted() {
    configService.setTitle('站点设置')
    axios.get('/admin/config').then(({data}) => {
      Object.assign(this.siteConfig, data)
    })
  }

  save() {
    const data = this.siteConfig.qiniu

    if (data.enabled && !data.bucket) {
      this.$toasted.error('七牛云 空间名 不能为空')
      return
    }
    if (data.enabled && !data.accessKey) {
      this.$toasted.error('七牛云 accessKey 不能为空')
      return
    }
    if (data.enabled && !data.secretKey) {
      this.$toasted.error('七牛云 secretKey 不能为空')
      return
    }
    const domain = data.domain
    if (data.enabled && !domain) {
      this.$toasted.error('七牛云 域名 不能为空')
      return
    }
    if (data.enabled && !isValidHttpUrl(domain)) {
      this.$toasted.error('七牛云 域名 不正确')
      return
    }

    if (domain.endsWith('/')) {
      this.siteConfig.qiniu.domain = domain.substr(0, domain.length - 1)
    }

    configService.updateSiteConfig(this.siteConfig).then((data) => {
      Object.assign(this.siteConfig, data)
    })
  }
}
</script>

<style scoped>

</style>
