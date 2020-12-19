import {User} from '@/models/User'

export class Audit {
  id: number = 0
  type: string = ''
  content: string = ''
  operator: User = new User()
  target: number = 0
  clientIp: string = ''
  referer: string = ''
  userAgent: string = ''
  createdTime: string = ''
}
