import axios from 'axios'
import {SiteConfig} from '@/models/SiteConfig'
import store from '@/store'

export class ConfigService {
  siteConfig: SiteConfig = new SiteConfig()

  getSiteConfig() {
    return axios.get('/config/site').then(({data}) => {
      this.siteConfig = data
      store.commit('config', data)
      return data
    })
  }

  updateSiteConfig(siteConfig: SiteConfig) {
    return axios.put('/config/site', siteConfig).then(({data}) => {
      this.siteConfig = data
      store.commit('config', data)
      return data
    })
  }

  setTitle(title: string) {
    document.title = title + ' - ' + this.siteConfig.siteName
  }
}

const configService = new ConfigService()
export default configService
