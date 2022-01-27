import Vue from 'vue'
import Vuex from 'vuex'
import {AppState} from '@/models/state'
import {Account, Role} from '@/models/Account'
import {SiteConfig} from '@/models/SiteConfig'
import configService from '@/services/config.service'
import {Menu} from '@/models/Menu'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: new Account(),
    authenticated: false,
    admin: false,
    siteConfig: new SiteConfig(),
    menus: [
      new Menu(1, '笔记', '/notes', 'home'),
      new Menu(2, '笔记本', '/notebooks', 'book'),
      new Menu(3, '分类', '/categories', 'idea'),
      new Menu(4, '标签', '/tags', 'tag'),
      new Menu(5, '关于', '/about', 'info'),
    ],
  } as AppState,
  mutations: {
    login(state: AppState, user: Account) {
      state.user = user
      state.authenticated = true
      state.admin = user.role == Role[Role.ROLE_ADMIN]
    },
    logout(state: AppState) {
      state.user = new Account()
      state.authenticated = false
      state.admin = false
    },
    authenticate(state: AppState, authenticated: boolean) {
      state.authenticated = authenticated
    },
    config(state: AppState, siteConfig: SiteConfig) {
      state.siteConfig = siteConfig
    },
    menus(state: AppState, menus: Menu[]) {
      if (menus.length) {
        state.menus = menus
      }
    },
  },
  actions: {
    getSiteConfig() {
      configService.getSiteConfig()
    },
    getMenus() {
      configService.getMenus()
    },
  },
  modules: {}
})
