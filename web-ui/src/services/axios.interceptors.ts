import axios, {AxiosRequestConfig} from 'axios'
import auth from '@/services/account.service'
import router from '@/router'
import Vue from 'vue'
import CryptoJS from 'crypto-js'
import configService from '@/services/config.service'

const bypass = ['/accounts/info', '/config/site', '/config/menus']

function md5(data: string) {
  return btoa(CryptoJS.MD5(data).toString())
}

function getIv(config: AxiosRequestConfig, salt: string, time: number): string {
  const prefix = bypass.includes(config.url || '') ? 'Notebook' : configService.siteConfig.secretKey
  return prefix + '-' + salt + '-' + time
}

function encrypt(data: string, time: number): string {
  return CryptoJS.AES.encrypt(data, configService.siteConfig.secretKey + '-' + time).toString()
}

function decrypt(config: AxiosRequestConfig, data: string, sign: string): string {
  const hash = md5(configService.devConfig.developer + '-' + data)
  if (hash !== sign) {
    throw new Error('数据校验错误，请刷新页面！')
  }
  console.log(config.url)
  const salt = config.headers['sign']
  const time = +config.headers['time'] * 10 + 5
  const text = CryptoJS.AES.decrypt(data, getIv(config, salt, time)).toString(CryptoJS.enc.Utf8)
  const padding = text.substring(text.length - salt.length, text.length)
  if (salt !== padding) {
    throw new Error('数据校验错误！')
  }
  return text.substring(0, text.length - padding.length)
}

axios.interceptors.request.use(function (config) {
  if (auth.getToken()) {
    config.headers.common['X-ACCESS-TOKEN'] = auth.getToken()
  }
  const time = new Date().getTime()
  config.headers.common['time'] = time
  if (config.data && configService.siteConfig.secretKey && !(config.data instanceof FormData)) {
    const data = encrypt(JSON.stringify(config.data), time)
    config.data = {data}
    config.headers.common['sign'] = md5(time + '-' + data)
    config.headers.common['encrypted'] = 'true'
  } else {
    config.headers.common['sign'] = md5(time + '-' + config.url)
  }
  return config
}, function (error) {
  return Promise.reject(error)
})

axios.interceptors.response.use(function (response) {
  if (response.data && response.headers['sign']) {
    try {
      const data = JSON.parse(decrypt(response.config, response.data, response.headers['sign']))
      return Object.assign({}, response, {data})
    } catch (e) {
      console.error(e)
      throw new Error('数据解析错误，请刷新页面！')
    }
  }
  return response
}, function ({response}) {
  if (response.data && response.headers['sign']) {
    try {
      response.data = JSON.parse(decrypt(response.config, response.data, response.headers['sign']))
    } catch (e) {
      console.error(e)
      throw new Error('数据解析错误，请刷新页面！')
    }
  }

  const data = response.data
  if (response.status === 401 && data.code === 40100) {
    auth.clean()
    router.push('/?_t=' + (new Date().getTime()))
  } else {
    Vue.toasted.error(data.message)
  }
  return Promise.reject(data)
})