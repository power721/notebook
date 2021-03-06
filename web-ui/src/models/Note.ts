import {User} from '@/models/User'
import {Notebook} from '@/models/Notebook'
import {Category} from '@/models/Category'

export enum Access {
  PUBLIC,
  SECRET,
  PRIVATE
}

export class Tag {
  id: number = 0
  name: string = ''
}

export class Note {
  id: string = ''
  title: string = ''
  slug: string = ''
  content: string = ''
  author: User = new User()
  notebookId: string = ''
  notebook: Notebook = new Notebook()
  categoryId: string = ''
  category: Category = new Category()
  tags: Tag[] = []
  markdown: boolean = false
  access: string = Access[Access.PUBLIC]
  version: number = 0
  views: number = 0
  words: number = 0
  deleted: boolean = false
  createdTime: string | number = ''
  updatedTime: string | number = ''
}

export class NoteHistory {
  title: string = ''
  content: string = ''
  version: number = 0
  createdTime: string = ''
  markdown: boolean = false
}
