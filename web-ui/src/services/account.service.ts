import axios from 'axios'
import {Account, AccountDto, Role} from '@/models/Account'
import store from '@/store'

class AccountService {
  account = new Account()

  isAuth() {
    return this.account.id != ''
  }

  isAdmin() {
    return this.account.role === Role[Role.ROLE_ADMIN]
  }

  getToken() {
    return localStorage.getItem('token')
  }

  getInfo() {
    const token = localStorage.getItem('token')
    if (token) {
      axios.get('/accounts/info').then(({data}) => {
        this.account = data
        store.commit('login', data)
        return data
      }, () => {
        console.log('get account info failed.')
        this.clean()
      })
    }
  }

  update(account: AccountDto) {
    return axios.post('/accounts/info', account).then(({data}) => {
      Object.assign(this.account, data)
      store.commit('login', data)
    })
  }

  signup(username: string, password: string) {
    return axios.post('/accounts/signup', {
      username,
      password
    })
  }

  login(username: string, password: string, rememberMe: boolean) {
    return axios.post('/accounts/login', {
      username,
      password,
      rememberMe
    }).then(({data}) => {
      localStorage.setItem('token', data.token)
      this.getInfo()
    })
  }

  logout() {
    axios.post('/accounts/logout').then(() => {
      store.commit('logout')
      this.clean()
    }, () => {
      store.commit('logout')
      this.clean()
    })
  }

  clean() {
    this.account = new Account()
    localStorage.removeItem('token')
  }
}

const accountService = new AccountService()

export default accountService
