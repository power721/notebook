import {User} from '@/models/User'

export class Comment {
  id: number = 0
  user: User
  content: string = ''
  createdTime: string = ''
}
