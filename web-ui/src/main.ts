import Vue from 'vue'
import axios from 'axios'
import {Option, Radio, RadioGroup, Select} from 'element-ui'
import Toasted from 'vue-toasted'
import App from './App.vue'
import './filters'
import router from './router'
import store from './store'
import auth from './services/account.service'
import UserMenu from '@/views/user/UserMenu.vue'

import 'tinymce/tinymce'
import 'tinymce/icons/default'
import 'tinymce/themes/silver'
import 'tinymce/plugins/paste'
import 'tinymce/plugins/code'
import 'tinymce/plugins/codesample'
import 'tinymce/plugins/emoticons'
import 'tinymce/plugins/lists'
import 'tinymce/plugins/advlist'
import 'tinymce/plugins/autolink'
import 'tinymce/plugins/insertdatetime'
import 'tinymce/plugins/media'
import 'tinymce/plugins/link'
import 'tinymce/plugins/hr'
import 'tinymce/plugins/image'
import 'tinymce/plugins/preview'
import 'tinymce/plugins/toc'
import 'tinymce/plugins/fullscreen'
import 'tinymce/plugins/help'
import 'tinymce/plugins/wordcount'
import 'tinymce/plugins/searchreplace'
import 'tinymce/plugins/charmap'
import 'tinymce/plugins/quickbars'
import '@npkg/tinymce-plugins/attachment'
import '@npkg/tinymce-plugins/table'
import '@npkg/tinymce-plugins/imagetools'
import '@npkg/tinymce-plugins/axupimgs'
import '@npkg/tinymce-plugins/upfile'

import '@/assets/langs/zh_CN'

import 'element-ui/lib/theme-chalk/index.css'
import 'semantic-ui-css/semantic.min.css'

Vue.config.productionTip = false

Vue.component(Select.name, Select)
Vue.component(Option.name, Option)
Vue.component(Radio.name, Radio)
Vue.component(RadioGroup.name, RadioGroup)

Vue.component('UserMenu', UserMenu)

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
  const data = error.response.data
  if (error.response.status === 401 && data.message === 'Token失效') {
    auth.clean()
    router.push('/?_t=' + (new Date().getTime()))
  } else {
    Vue.toasted.error(data.message)
  }
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

setInterval(() => {
  axios.post('/accounts/heartbeat').then()
}, 60000)

const info = '\n %c Notebook %c https://har01d.cn/ \n'
console.info(info, 'color: #fadfa3; background: #030307; padding:5px 0;', 'background: #fadfa3; padding:5px 0;')
