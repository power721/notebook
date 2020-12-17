export class NoteStats {
  total: number = 0
  views: number = 0
  deleted: number = 0
  public: number = 0
  secret: number = 0
  private: number = 0
}

export class UserStats {
  total: number = 0
  online: number = 0
}

export class SystemStats {
  user: UserStats = new UserStats()
  note: NoteStats = new NoteStats()
  notebooks: number = 0
  categories: number = 0
  tags: number = 0
}
