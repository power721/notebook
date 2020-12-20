export class Menu {
  id: number = 0
  title: string = ''
  uri: string = ''
  order: number = 0
  parent: number = 0
  icon: string = ''
  auth: boolean = false
  admin: boolean = false
  children: Menu[] = []

  constructor(id: number = 0, title: string = '', uri: string = '', icon: string = '') {
    this.id = id
    this.title = title
    this.uri = uri
    this.icon = icon
  }
}
