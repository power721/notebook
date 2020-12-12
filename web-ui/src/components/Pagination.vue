<template>
  <div class="pagination">
    <div class="ui pagination menu" v-if="pages>1">
      <a class="item" @click="go(page-1)" v-if="page>1">上一页</a>
      <a class="item" @click="go(1)" v-if="page>4">1</a>
      <div class="disabled item" v-if="page>4">...</div>
      <a class="item" @click="go(page-2)" v-if="page>2">{{page-2}}</a>
      <a class="item" @click="go(page-1)" v-if="page>1">{{page-1}}</a>
      <div class="disabled active item">{{page}}</div>
      <a class="item" @click="go(page+1)" v-if="page+1<=pages">{{page+1}}</a>
      <a class="item" @click="go(page+2)" v-if="page+2<=pages">{{page+2}}</a>
      <div class="disabled item" v-if="page+3<pages">...</div>
      <a class="item" @click="go(pages)" v-if="page+3<pages">{{pages}}</a>
      <a class="item" @click="go(page+1)" v-if="page<pages">下一页</a>
    </div>
    <span class="total" v-if="total">共 {{total}} 条</span>
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
    @Prop() private pages!: number
    @Prop() private total!: number

    go(page: number) {
      this.$emit('change', page)
    }
  }
</script>

<style scoped>
  .ui.pagination.menu {
    margin-right: 12px;
  }
</style>
