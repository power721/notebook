import {User} from '@/models/User'

export class Comment {
  id: number = 0
  user: User
  content: string = ''
  sticky: boolean = false
  createdTime: string = ''
}
