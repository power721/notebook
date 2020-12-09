
import Vue from 'vue'
import axios from 'axios'
import {Select, Option, Radio, RadioGroup} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import Toasted from 'vue-toasted'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import auth from './services/account.service'
import {date, datetime, duration, fromNow, time} from './filters'
import UserAccount from '@/views/user/UserAccount.vue'

import 'tinymce/tinymce'
import 'tinymce/icons/default'
import 'tinymce/themes/silver'
import 'tinymce/plugins/paste'
import 'tinymce/plugins/code'
import 'tinymce/plugins/codesample'
import 'tinymce/plugins/lists'
import 'tinymce/plugins/advlist'
import 'tinymce/plugins/autolink'
import 'tinymce/plugins/insertdatetime'
import 'tinymce/plugins/media'
import 'tinymce/plugins/link'
import 'tinymce/plugins/hr'
import 'tinymce/plugins/image'
import 'tinymce/plugins/table'
import 'tinymce/plugins/preview'
import 'tinymce/plugins/toc'
import 'tinymce/plugins/fullscreen'
import 'tinymce/plugins/help'
import 'tinymce/plugins/wordcount'

import 'tinymce/skins/ui/oxide/skin.min.css'
import 'tinymce/skins/content/default/content.min.css'

import 'semantic-ui-css/semantic.min.css'

Vue.config.productionTip = false

Vue.filter('datetime', datetime)
Vue.filter('date', date)
Vue.filter('time', time)
Vue.filter('duration', duration)
Vue.filter('fromNow', fromNow)

Vue.component(Select.name, Select)
Vue.component(Option.name, Option)
Vue.component(Radio.name, Radio)
Vue.component(RadioGroup.name, RadioGroup)

Vue.component('UserAccount', UserAccount)

Vue.use(Toasted, {
  theme: 'outline',
  position: 'top-right',
  duration: 5000
})

axios.interceptors.request.use(function (config) {
  if (auth.getToken()) {
    config.headers.common['X-ACCESS-TOKEN'] = auth.getToken()
  }
  return config
}, function (error) {
  return Promise.reject(error)
})

axios.interceptors.response.use(function (response) {
  return response
}, function (error) {
  if (error.response.status === 401 && auth.getToken()) {
    auth.clean()
    Vue.toasted.error('登录失效')
    router.push('/')
  }
  const data = error.response.data
  Vue.toasted.error(data.message)
  return Promise.reject(data)
})

auth.getInfo()

router.beforeEach((to, from, next) => {
  // if (from.fullPath !== '/login' && from.fullPath !== '/signup') {
  //   config.back = from.fullPath;
  // }
  if (to.meta.admin && !auth.isAdmin()) {
    if (auth.isAuth()) {
      Vue.toasted.error('无权操作')
      next('/')
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    }
  } else if (to.meta.auth && !auth.getToken()) {
    next({
      path: '/login',
      query: {redirect: to.fullPath}
    })
  } else if (to.meta.guest && auth.isAuth()) {
    next('/')
  } else {
    next()
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
