import {User} from '@/models/User'
import {Notebook} from '@/models/Notebook'
import {Category} from '@/models/Category'

export enum Access {
  PUBLIC,
  SECRET,
  PRIVATE
}

export class Note {
  id: string = ''
  title: string = ''
  content: string = ''
  author: User = new User()
  notebookId: string = ''
  notebook: Notebook = new Notebook()
  categoryId: string = ''
  category: Category = new Category()
  markdown: boolean = false
  access: string = 'PUBLIC'
  version: number = 0
  views: number = 0
  createdTime: string = ''
  updatedTime: string = ''
}

export class NoteHistory {
  title: string = ''
  content: string = ''
  version: number = 0
  createdTime: string = ''
}