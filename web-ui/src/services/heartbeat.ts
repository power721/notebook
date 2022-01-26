import axios from 'axios'
import configService from '@/services/config.service'
import eventService from '@/services/event.service'
import store from '@/store'

let idle: number = 0

setInterval(() => {
  if (idle < 1 && configService.siteConfig.enableHeartbeat) {
    axios.post('/accounts/heartbeat').then(({data}) => {
      Object.assign(configService.siteConfig, data)
      store.commit('config', data)
    })
  }
  idle++
}, 60000)

eventService.on('mousemove', () => {
  idle = 0
})

eventService.on('keypress', () => {
  idle = 0
})
