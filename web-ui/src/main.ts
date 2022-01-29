import Vue from 'vue'
import {Option, Radio, RadioGroup, Select} from 'element-ui'
import Toasted from 'vue-toasted'
import App from './App.vue'
import './filters'
import router from './router'
import store from './store'
import auth from '@/services/account.service'
import UserMenu from '@/views/user/UserMenu.vue'
import '@/services/heartbeat'
import '@/services/axios.interceptors'

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

import 'bytemd/dist/index.min.css'

import 'juejin-markdown-themes'

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

auth.getInfo()

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

const info = '\n %c Notebook %c https://har01d.cn/ \n'
console.info(info, 'color: #fadfa3; background: #030307; padding:5px 0;', 'background: #fadfa3; padding:5px 0;')
