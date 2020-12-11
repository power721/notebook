import {User} from '@/models/User'

export class Notebook {
  id: string = ''
  name: string = ''
  description: string = ''
  owner: User = new User()
  createdTime: string = ''
  updatedTime: string = ''
}
