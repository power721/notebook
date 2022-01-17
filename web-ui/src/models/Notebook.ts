import {User} from '@/models/User'
import {Access} from '@/models/Note'

export class Notebook {
  id: string = ''
  name: string = ''
  slug: string = ''
  description: string = ''
  owner: User = new User()
  access: string = Access[Access.PUBLIC]
  createdTime: string = ''
  updatedTime: string = ''
}
