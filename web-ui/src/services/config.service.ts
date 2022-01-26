import axios from 'axios'
import {DveConfig, SiteConfig} from '@/models/SiteConfig'
import store from '@/store'

export class ConfigService {
  siteConfig: SiteConfig = new SiteConfig()
  devConfig: DveConfig = new DveConfig()

  getSiteConfig() {
    return axios.get('/config/site').then(({data}) => {
      this.siteConfig = data
      store.commit('config', data)
      return data
    })
  }

  updateSiteConfig(siteConfig: SiteConfig) {
    return axios.put('/admin/config', siteConfig).then(({data}) => {
      this.siteConfig = data
      store.commit('config', data)
      return data
    })
  }

  getMenus() {
    axios.get('/config/menus').then(({data}) => {
      store.commit('menus', data)
    })
  }

  setTitle(title: string) {
    document.title = title + ' - ' + this.siteConfig.siteName
  }

  getNotesSortOrder(): string {
    return localStorage.getItem('notes_sort_order') || 'id,desc'
  }

  saveNotesSortOrder(order: string) {
    localStorage.setItem('notes_sort_order', order)
  }

  getNotebooksSortOrder(): string {
    return localStorage.getItem('notebooks_sort_order') || 'id,desc'
  }

  saveNotebooksSortOrder(order: string) {
    localStorage.setItem('notebooks_sort_order', order)
  }

}

const configService = new ConfigService()
export default configService
