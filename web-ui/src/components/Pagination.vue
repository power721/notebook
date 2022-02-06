<template>
  <div class="pagination">
    <div class="ui pagination horizontal" :class="{menu:!simple,list:simple}" v-if="pages>1">
      <a class="item" @click="go(page-1)" v-if="!simple&&page>1">上一页</a>
      <a class="item" @click="go(1)" v-if="page>=4">1</a>
      <div class="disabled item" v-if="page>4">...</div>
      <a class="item" @click="go(page-2)" v-if="page>2">{{page-2}}</a>
      <a class="item" @click="go(page-1)" v-if="page>1">{{page-1}}</a>
      <div class="disabled active item">{{page}}</div>
      <a class="item" @click="go(page+1)" v-if="page+1<=pages">{{page+1}}</a>
      <a class="item" @click="go(page+2)" v-if="page+2<=pages">{{page+2}}</a>
      <div class="disabled item" v-if="page+3<pages">...</div>
      <a class="item" @click="go(pages)" v-if="page+3<=pages">{{pages}}</a>
      <a class="item" @click="go(page+1)" v-if="!simple&&page<pages">下一页</a>
    </div>
    <div class="ui action input" v-if="!simple&&pages>9">
      <input type="number" min="1" :max="pages" class="ui input" v-model="current">
      <button class="ui button" @click="jump">跳转</button>
    </div>
    <span class="total" v-if="!simple&&total">共 {{total}} 条</span>
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
    @Prop({default: false}) private simple!: boolean
    current: number = this.page

    go(page: number) {
      this.current = page
      this.$emit('change', page)
    }

    jump() {
      if (this.current == this.page) {
        return
      }
      if (this.current > this.pages) {
        this.current = this.pages
      }
      this.$emit('change', this.current)
    }
  }
</script>

<style scoped>
  .ui.pagination.menu {
    margin-right: 6px;
  }

  .ui.action.input {
    margin-left: 0;
    margin-right: 6px;
  }

  .ui.action.input input {
    width: 5.25em;
  }
  .total {
    font-size: 1.25em;
  }
</style>
