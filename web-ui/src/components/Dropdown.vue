<template>
  <div class="ui active dropdown" :class="[{visible: show, pointing: pointing, inline: inline}, position]" tabindex="0"
       @click.stop="show=true">
    <i class="icon" :class="[icon]" @click.stop="show=!show"></i>
    <div class="menu transition" :class="{visible: show}" tabindex="-1" @click.stop="show=!show">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'
  import eventService from '@/services/event.service'

  @Component
  export default class Dropdown extends Vue {
    @Prop({default: false}) private inline!: boolean
    @Prop({default: false}) private pointing!: boolean
    @Prop() private position!: string
    @Prop({default: 'dropdown'}) private icon!: string
    private handler: number = 0
    private show: boolean = false

    mounted() {
      this.handler = eventService.on(['click', 'touchend'], () => {
        this.show = false
      })
    }

    destroyed() {
      eventService.off(['click', 'touchend'], this.handler)
    }
  }
</script>

<style scoped>

</style>
