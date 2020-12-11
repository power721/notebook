import {Component, Vue} from 'vue-property-decorator'

@Component
export class Pageable extends Vue {
  page: number = 1
  totalPages: number = 0
  totalElements: number = 0
  size: number = 10
  sort: string = 'id,desc'

  get query(): string {
    return `page=${this.page - 1}&size=${this.size}&sort=${this.sort}`
  }

  load() {
    return
  }

  sorted(sort: string) {
    this.sort = sort
    this.load()
  }
}
