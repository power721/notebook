import {Account} from './Account'
import {SiteConfig} from '@/models/SiteConfig'
import {Menu} from '@/models/Menu'

export interface AppState {
  user: Account
  authenticated: boolean
  siteConfig: SiteConfig,
  menus: Menu[],
}
