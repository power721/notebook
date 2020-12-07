import {Role} from '@/models/Account'

export class User {
  id: string = ''
  username: string = ''
  role: Role = Role.ROLE_USER
  createdTime: string = ''
}
