<template>
  <div class="ui pagination menu" v-if="totalPages>1">
    <a class="item" @click="go(page-1)" v-if="page>1">上一页</a>
    <a class="item" @click="go(1)" v-if="page>4">1</a>
    <span class="item" v-if="page>4">...</span>
    <a class="item" @click="go(page-2)" v-if="page>2">{{page-2}}</a>
    <a class="item" @click="go(page-1)" v-if="page>1">{{page-1}}</a>
    <span class="active item">{{page}}</span>
    <a class="item" @click="go(page+1)" v-if="page+1<=totalPages">{{page+1}}</a>
    <a class="item" @click="go(page+2)" v-if="page+2<=totalPages">{{page+2}}</a>
    <span class="item" v-if="page+3<totalPages">...</span>
    <a class="item" @click="go(totalPages)" v-if="page+3<totalPages">{{totalPages}}</a>
    <a class="item" @click="go(page+1)" v-if="page<totalPages">下一页</a>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'

  @Component({
    model: {
      prop: 'page',
      event: 'change'
    }
  })
  export default class Pagination extends Vue {
    @Prop() private page!: number
    @Prop() private totalPages!: number

    go(page: number) {
      this.$emit('change', page)
    }
  }
</script>
