import axios from 'axios'
import {SiteConfig} from '@/models/SiteConfig'

export class ConfigService {
  siteConfig: SiteConfig = new SiteConfig()

  getSiteConfig() {
    return axios.get('/config/site').then(({data}) => {
      this.siteConfig = data
      return data
    })
  }

  setTitle(title: string) {
    document.title = title + ' - ' + this.siteConfig.siteName
  }
}

const configService = new ConfigService()
export default configService
