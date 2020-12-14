import Vue from 'vue'
import Vuex from 'vuex'
import {AppState} from '@/models/state'
import {Account} from '@/models/Account'
import {SiteConfig} from '@/models/SiteConfig'
import configService from '@/services/config.service'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: new Account(),
    authenticated: false,
    siteConfig: new SiteConfig(),
  } as AppState,
  mutations: {
    login(state: AppState, user: Account) {
      state.user = user
      state.authenticated = true
    },
    logout(state: AppState) {
      state.user = new Account()
      state.authenticated = false
    },
    authenticate(state: AppState, authenticated: boolean) {
      state.authenticated = authenticated
    },
    config(state: AppState, siteConfig: SiteConfig) {
      state.siteConfig = siteConfig
    },
  },
  actions: {
    getSiteConfig() {
      configService.getSiteConfig()
    },
  },
  modules: {}
})
