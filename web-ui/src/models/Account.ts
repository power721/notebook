export enum Role {
  ROLE_ADMIN,
  ROLE_STAFF,
  ROLE_USER,
}

export class Account {
  id: string = ''
  username: string = ''
  email: string = ''
  signature: string = ''
  mdTheme: string = ''
  editorMode: string = ''
  role: string = Role[Role.ROLE_USER]
  createdTime: string = ''
}

export class AccountDto {
  password: string = ''
  email: string = ''
}
