import {Account} from './Account'
import {SiteConfig} from '@/models/SiteConfig'

export interface AppState {
  user: Account
  authenticated: boolean
  siteConfig: SiteConfig
}
