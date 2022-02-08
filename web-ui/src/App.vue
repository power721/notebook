<template>
  <div id="app" :class="{inverted:inverted}" :data-font-size="fontSize">
    <div class="ui stackable menu" :class="{inverted:inverted}">
      <div class="ui container">
        <h3 class="ui header item" :class="[siteConfig.brandColor]">{{siteConfig.siteName}}</h3>

        <template v-for="menu of menus">
          <template v-if="menu.children&&menu.children.length">
            <div class="ui simple dropdown item" :key="menu.id" v-if="(menu.admin&&admin)||(menu.auth&&!menu.admin&&auth)||(!menu.auth&&!menu.admin)">
              {{menu.title}}
              <i class="dropdown icon"></i>
              <div class="menu">
                <AppMenu :menu="item" :key="item.id" v-for="item of menu.children"/>
              </div>
            </div>
          </template>
          <template v-else>
            <AppMenu :menu="menu" :key="menu.id" v-if="(menu.admin&&admin)||(menu.auth&&!menu.admin&&auth)||(!menu.auth&&!menu.admin)"/>
          </template>
        </template>

        <div class="right menu">
          <div class="item">
            <div class="ui icon input">
              <input type="text" placeholder="搜索笔记" v-model="text" @keypress.enter="search">
              <i class="search link icon" @click="search"></i>
            </div>
          </div>
          <UserMenu class="item"/>
        </div>
      </div>
      <a v-if="siteConfig.github" v-show="wide" id="github" :href="siteConfig.github" target="_blank">
        <i class="large github link icon" :class="{inverted:inverted}"></i>
      </a>
    </div>

    <div id="main" class="ui center aligned container" :class="{inverted:inverted,fluid:fluid}">
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
            <h4 class="ui header" :class="{inverted:inverted}">内容</h4>
            <div class="ui link list" :class="{inverted:inverted}">
              <a href="/home.html" class="item" target="_blank">静态版</a>
              <router-link to="/" class="item">动态版</router-link>
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
        <div class="actions">
          <div class="font" v-if="note">
            <button class="ui icon tiny basic button" data-tooltip="减小字体" @click="decFontSize"><i class="font icon"></i>-
            </button>
            <span class="size" data-tooltip="笔记内容字体大小">{{fontSize}}</span>
            <button class="ui icon tiny basic button" data-tooltip="增大字体" @click="incFontSize"><i class="font icon"></i>+
            </button>
          </div>
          <div class="ui toggle checkbox">
            <input type="checkbox" name="inverted" v-model="inverted" @change="save">
            <label>夜间模式</label>
          </div>
          <div class="ui toggle checkbox">
            <input type="checkbox" name="fluid" v-model="fluid" @change="save">
            <label>宽屏模式</label>
          </div>
        </div>
      </Popup>

      <FloatingActions/>

    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import {SiteConfig} from '@/models/SiteConfig'
  import Popup from '@/components/Popup.vue'
  import FloatingActions from '@/views/FloatingActions.vue'
  import {Menu} from '@/models/Menu'
  import AppMenu from '@/components/AppMenu.vue'
  import UserMenu from '@/views/user/UserMenu.vue'

  @Component({
    components: {
      Popup,
      AppMenu,
      UserMenu,
      FloatingActions
    }
  })
  export default class App extends Vue {
    text: string = ''
    inverted: boolean = false
    fluid: boolean = false
    show: boolean = false
    fontSize: number = 16
    wide = window.innerWidth > 1080

    get auth(): boolean {
      return this.$store.state.authenticated
    }

    get admin(): boolean {
      return this.$store.state.admin
    }

    get note(): boolean {
      return this.$route.name === 'NoteDetails' || this.$route.name === 'ArticleDetails'
    }

    get menus(): Menu[] {
      return this.$store.state.menus
    }

    get siteConfig(): SiteConfig {
      return this.$store.state.siteConfig
    }

    created() {
      this.inverted = localStorage.getItem('invertedMode') === 'true'
      this.fluid = localStorage.getItem('fluid') === 'true'
      this.fontSize = +(localStorage.getItem('fontSize') || '16')
      this.$store.dispatch('getSiteConfig')
      this.$store.dispatch('getMenus')
      this.$store.dispatch('getUserInfo')
    }

    mounted() {
      window.addEventListener('resize', () => {
        this.wide = window.innerWidth > 1080
      })
    }

    search() {
      if (this.text) {
        this.$router.push('/notes?q=' + this.text + '&t=' + new Date().getTime())
      } else {
        this.$router.push('/')
      }
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
      localStorage.setItem('fluid', this.fluid + '')
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
.font {
  margin-bottom: 6px;
}

.font .ui.icon.button {
  margin: 0;
}

.font .size {
  margin: 0 6px;
}

#github {
  position: fixed;
  top: 12px;
  right: 8px;
  color: #A0A0A0;
}

#github:hover {
  color: #505050;
}

#config .trigger {
  position: absolute;
  bottom: 24px;
  right: 24px;
}

#config .toggle {
  margin-top: 6px;
}

@media only screen and (max-width: 767px) {
  #config .ui.popup {
    top: auto;
    bottom: 60px;
  }
}
</style>
