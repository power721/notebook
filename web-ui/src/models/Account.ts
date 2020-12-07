
export enum Role {
  ROLE_ADMIN,
  ROLE_STAFF,
  ROLE_USER,
}

export class Account {
  id: string = ''
  username: string = ''
  role: Role = Role.ROLE_USER
  createdTime: string = ''
}
