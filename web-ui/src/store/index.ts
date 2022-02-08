import Vue from 'vue'
import Vuex from 'vuex'
import {AppState} from '@/models/state'
import {Menu} from '@/models/Menu'
import {SiteConfig} from '@/models/SiteConfig'
import {Account, Role} from '@/models/Account'
import accountService from '@/services/account.service'
import configService from '@/services/config.service'

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
  getters: {
    admin(state: AppState): boolean {
      return state.user.role == Role[Role.ROLE_ADMIN]
    },
    showViews(state: AppState): boolean {
      return state.siteConfig.showViews || state.authenticated
    },
    showWords(state: AppState): boolean {
      return state.siteConfig.showWords
    },
    enableSignup(state: AppState): boolean {
      return state.siteConfig.enableSignup
    },
    enableComment(state: AppState): boolean {
      return state.siteConfig.enableComment
    },
    editorMode(state: AppState): string {
      return state.user.editorMode || 'markdown'
    },
  },
  mutations: {
    user(state: AppState, user: Account) {
      state.user = user
    },
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
    getUserInfo() {
      accountService.getInfo()
    },
  },
  modules: {}
})
