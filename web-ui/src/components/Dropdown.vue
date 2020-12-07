<template>
  <div class="ui fluid selection dropdown" :class="{active: show, visible: show}" tabindex="0" @click="show=true">
    <i class="dropdown icon"></i>
    <!--    <input class="search" autocomplete="off" v-model="selectedValue" @focus="show=true" @focusout="show=false">-->
    <div class="text" v-if="selectedText">{{selectedText}}</div>
    <div class="default text" v-else>{{placeholder}}</div>
    <div class="menu" :class="{transition: show, visible: show, hidden: !show}" tabindex="-1">
      <div class="item"
           :class="{active: item.value===selectedValue, selected: item.value===selectedValue}"
           v-for="item of options"
           :key="item.key"
           @click="select(item)">
        {{ item.text }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'

  @Component({
    model: {
      prop: 'value',
      event: 'changed'
    }
  })
  export default class Dropdown extends Vue {
    @Prop() private placeholder!: string
    @Prop() private value!: string | number
    @Prop() private options!: Item[]

    show: boolean = false
    selected!: Item | undefined
    selectedValue: string | number = ''
    selectedText: string | number = ''

    select(item: Item) {
      this.selected = item
      this.selectedValue = item.value
      this.selectedText = item.text
      this.show = false
      this.$emit('changed', item.value)
    }
  }

  export class Item {
    key: string | number = ''
    value: string | number = ''
    text: string = ''
  }
</script>

<style scoped>

</style>
