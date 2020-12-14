<template>
  <div id="app" :class="{inverted:inverted}">
    <div class="ui menu" :class="{inverted:inverted}">
      <div class="ui container">
        <h3 class="ui header item" :class="[siteConfig.brandColor]">{{siteConfig.siteName}}</h3>
        <router-link class="item" to="/" exact><i class="home icon"></i>首页</router-link>
        <router-link class="item" to="/notebooks"><i class="book icon"></i>笔记本</router-link>
        <router-link class="item" to="/categories"><i class="idea icon"></i>分类</router-link>
        <router-link class="item" to="/about"><i class="info icon"></i>关于</router-link>
        <div class="right menu">
          <!--<div class="item">
            <div class="ui transparent icon input">
              <input type="text" placeholder="Search...">
              <i class="search link icon"></i>
            </div>
          </div>-->
          <UserAccount class="item"></UserAccount>
        </div>
      </div>
    </div>
    <div id="main" class="ui center aligned container" :class="{inverted:inverted}">
      <router-view/>
    </div>

    <div class="ui vertical footer raised segment" :class="{inverted:inverted}">
      <div class="ui left aligned container">
        <div class="ui stackable divided equal height stackable grid" :class="{inverted:inverted}">
          <div class="three wide column" v-if="siteConfig.qrCode">
            <div class="ui inverted link list">
              <div class="item">
                <img :src="siteConfig.qrCode" class="ui rounded image" alt="" style="width: 72px">
              </div>
            </div>
          </div>
          <div class="three wide column">
            <h4 class="ui header" :class="{inverted:inverted}">关于</h4>
            <div class="ui link list" :class="{inverted:inverted}">
              <router-link to="/about" class="item">关于</router-link>
              <a href="https://gitee.com/power/notebook" class="item" target="_blank">代码</a>
            </div>
          </div>
          <div class="three wide column">
            <h4 class="ui header" :class="{inverted:inverted}">服务</h4>
            <div class="ui link list" :class="{inverted:inverted}">
              <router-link to="/notebooks" class="item">笔记本</router-link>
              <router-link to="/categories" class="item">分类</router-link>
            </div>
          </div>
          <div class="six wide column" v-if="siteConfig.icpBeian">
            <h4 class="ui header" :class="{inverted:inverted}">备案</h4>
            <div class="ui link list" :class="{inverted:inverted}">
              <a href="http://beian.miit.gov.cn/" class="item" target="_blank">{{siteConfig.icpBeian}}</a>
              <a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo" class="item"
                 v-if="siteConfig.govBeian">
                <img src="beian.png" style="width: 16px;">
                {{siteConfig.govBeian}}
              </a>
            </div>
          </div>
        </div>
      </div>

      <Popup id="config" position="top right">
        <template slot="trigger">
          <div class="ui circular icon button" data-tooltip="页面设置" data-position="left center"
               :class="{inverted: inverted}">
            <i class="ui cog icon"></i>
          </div>
        </template>
        <div class="ui toggle checkbox">
          <input type="checkbox" name="inverted" v-model="inverted" @change="save">
          <label>夜间模式</label>
        </div>
      </Popup>

      <FloatingActions></FloatingActions>

    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import configService from '@/services/config.service'
  import store from '@/store'
  import {SiteConfig} from '@/models/SiteConfig'
  import Popup from '@/components/Popup.vue'
  import FloatingActions from '@/views/FloatingActions.vue'

  @Component({
    components: {
      Popup,
      FloatingActions
    }
  })
  export default class App extends Vue {
    inverted: boolean = false
    show: boolean = false

    get siteConfig(): SiteConfig {
      return store.state.siteConfig
    }

    mounted() {
      this.inverted = localStorage.getItem('invertedMode') === 'true'
      store.dispatch('getSiteConfig')
    }

    save() {
      localStorage.setItem('invertedMode', this.inverted + '')
    }
  }
</script>

<style>
  .right.floated {
    float: right;
  }

  .top.right.dropdown {
    position: absolute;
    top: 12px;
    right: 12px;
  }

  .footer {
    margin-top: 12px;
  }

  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    min-height: 100vh;
  }

  #main {
    margin-bottom: 12px;
    min-height: calc(100vh - 180px);
  }

  #main.inverted {
    margin-bottom: 12px;
    min-height: calc(100vh - 178px);
  }

  #config .ui.popup {
    margin-top: -16px;
    margin-right: 24px;
  }

  .trigger {
    position: absolute;
    bottom: 24px;
    right: 24px;
  }
</style>
