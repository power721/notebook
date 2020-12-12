export class Account {
  id: string = ''
  username: string = ''
  email: string = ''
  role: string = 'ROLE_USER'
  createdTime: string = ''
}

export class AccountDto {
  password: string = ''
  email: string = ''
}
