import {Component, Vue} from 'vue-property-decorator'

@Component
export class Pageable extends Vue {
  page: number = 1
  totalPages: number = 0
  totalElements: number = 0
  size: number = 10

  load() {
    return
  }
}
