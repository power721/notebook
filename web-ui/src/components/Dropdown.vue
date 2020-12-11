<template>
  <div class="ui active dropdown" :class="[{visible: show, pointing: pointing}, position]" tabindex="0"
       @click.stop="show=true">
    <i class="icon" :class="iconClass" @click.stop="show=!show"></i>
    <div class="menu transition" :class="{visible: show}" tabindex="-1">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'
  import eventService from '@/services/event.service'

  @Component
  export default class Dropdown extends Vue {
    @Prop({default: false}) private pointing!: boolean
    @Prop() private position!: string
    @Prop({default: 'dropdown'}) private icon!: string
    handler: number = 0
    show: boolean = false

    get iconClass() {
      return {
        [this.icon]: true
      }
    }

    mounted() {
      this.handler = eventService.on('click', () => {
        this.show = false
      })
    }

    destroyed() {
      eventService.off('click', this.handler)
    }
  }
</script>

<style scoped>

</style>
