<template>
  <div class="popup">
    <div class="trigger" @click.stop="show=!show">
      <slot name="trigger">
      </slot>
    </div>
    <div class="ui active popup transition" :class="[{visible: show}, size, position]" @click.stop="">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'
  import eventService from '@/services/event.service'

  @Component
  export default class Popup extends Vue {
    @Prop() private size!: string
    @Prop() private position!: string
    private show: boolean = false
    private handler: number = 0

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
