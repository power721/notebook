import {Role} from '@/models/Account'

export class User {
  id: string = ''
  username: string = ''
  role: string = Role[Role.ROLE_USER]
  createdTime: string = ''
}
