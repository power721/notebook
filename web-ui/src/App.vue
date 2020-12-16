<template>
  <div id="app" :class="{inverted:inverted}" :data-font-size="fontSize">
    <div class="ui stackable menu" :class="{inverted:inverted}">
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
        <div>
          <div class="font">
            <button class="ui icon tiny basic button" data-tooltip="减小字体" @click="decFontSize"><i class="font icon"></i>-</button>
            <span class="size" data-tooltip="笔记内容字体大小">{{fontSize}}</span>
            <button class="ui icon tiny basic button" data-tooltip="增大字体" @click="incFontSize"><i class="font icon"></i>+</button>
          </div>
          <div class="ui toggle checkbox">
            <input type="checkbox" name="inverted" v-model="inverted" @change="save">
            <label>夜间模式</label>
          </div>
        </div>
      </Popup>

      <FloatingActions></FloatingActions>

    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
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
    fontSize: number = 16

    get siteConfig(): SiteConfig {
      return this.$store.state.siteConfig
    }

    created() {
      this.inverted = localStorage.getItem('invertedMode') === 'true'
      this.fontSize = +(localStorage.getItem('fontSize') || '16')
      this.$store.dispatch('getSiteConfig')
    }

    save() {
      // document.querySelectorAll('.segment').forEach(e => {
      //   if (this.inverted) {
      //     e.classList.add('inverted')
      //   } else {
      //     e.classList.remove('inverted')
      //   }
      // })
      localStorage.setItem('invertedMode', this.inverted + '')
    }

    incFontSize() {
      if (this.fontSize >= 22) {
        this.$toasted.error('字体已经是最大了')
      } else {
        this.fontSize += 2
        localStorage.setItem('fontSize', this.fontSize + '')
      }
    }

    decFontSize() {
      if (this.fontSize <= 12) {
        this.$toasted.error('字体已经是最小了')
      } else {
        this.fontSize -= 2
        localStorage.setItem('fontSize', this.fontSize + '')
      }
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

  img.emoji {
    height: 1.25em;
    width: 1.25em;
  }

  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    min-height: 100vh;
  }

  #app[data-font-size='12'] .ui.segment .article.content {
    font-size: 12px;
  }

  #app[data-font-size='14'] .ui.segment .article.content {
    font-size: 14px;
  }

  #app[data-font-size='16'] .ui.segment .article.content {
    font-size: 16px;
  }

  #app[data-font-size='18'] .ui.segment .article.content {
    font-size: 18px;
  }

  #app[data-font-size='20'] .ui.segment .article.content {
    font-size: 20px;
  }

  #app[data-font-size='22'] .ui.segment .article.content {
    font-size: 22px;
  }

  .font {
    margin-bottom: 6px;
  }

  .font .ui.icon.button {
    margin: 0;
  }

  .font .size {
    margin: 0 6px;
  }

  /*.inverted {*/
  /*  background: #1b1c1d;*/
  /*  color: rgba(255,255,255,.9);*/
  /*}*/

  #main {
    margin-bottom: 12px;
    min-height: calc(100vh - 180px);
  }

  #main.inverted {
    margin-bottom: 12px;
    min-height: calc(100vh - 178px);
  }

  #config .ui.popup {
    margin-top: -52px;
    margin-right: 24px;
  }

  .trigger {
    position: absolute;
    bottom: 24px;
    right: 24px;
  }

  @media only screen and (max-width: 767px) {
    #config .ui.popup {
      top: auto;
      bottom: 60px;
    }
  }
</style>
