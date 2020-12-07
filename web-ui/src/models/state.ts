import {Account} from './Account'

export interface AppState {
  user: Account;
  authenticated: boolean;
}
