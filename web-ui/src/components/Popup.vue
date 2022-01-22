<template>
  <div ref="container" class="popup-wrapper" @mouseenter="onMouseenter" @mouseleave="onMouseleave">
    <div ref="trigger" class="trigger" @click.stop="onClick">
      <slot name="trigger"></slot>
    </div>
    <div ref="popup" class="ui popup transition" :class="[{visible:show,active:show},size,position]" :style="style"
         @click.stop="">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator'
import eventService from '@/services/event.service'

interface Style {
  position: string;
  top: string | number;
  bottom: string | number;
  left: string | number;
  right: string | number;
}

function mapStyle(style: Style): Style {
  if (typeof style.top === 'number') {
    style.top = style.top + 'px'
  }
  if (typeof style.bottom === 'number') {
    style.bottom = style.bottom + 'px'
  }
  if (typeof style.left === 'number') {
    style.left = style.left + 'px'
  }
  if (typeof style.right === 'number') {
    style.right = style.right + 'px'
  }
  return style
}

@Component
export default class Popup extends Vue {
  @Prop() private size!: string
  @Prop() private position!: string
  @Prop({default: 'click'}) private trigger!: string
  private show: boolean = false
  private handler: number = 0
  private style: Style = {position: 'fixed', top: 'auto', bottom: 'auto', left: 'auto', right: 'auto'}

  onClick() {
    if (this.trigger === 'click') {
      if (this.show) {
        this.show = false
      } else {
        this.onShow()
      }
    }
  }

  onMouseenter() {
    if (this.trigger === 'hover') {
      this.onShow()
    }
  }

  onMouseleave() {
    if (this.trigger === 'hover') {
      this.show = false
    }
  }

  onShow() {
    this.show = true
    this.computePopupStyle(this.position)
  }

  computePopupStyle(positions: string) {
    const style: Style = {position: 'fixed', top: 'auto', bottom: 'auto', left: 'auto', right: 'auto'}
    const {clientWidth, clientHeight} = document.documentElement
    const trigger = this.$refs.trigger as HTMLElement
    const triggerCoords = trigger.getBoundingClientRect()
    const popup = this.$refs.popup as HTMLElement
    const popupCoords = popup.getBoundingClientRect()

    if (positions.includes('right')) {
      style.right = Math.round(clientWidth - triggerCoords.right)
    } else if (positions.includes('left')) {
      style.left = Math.round(triggerCoords.left)
    } else {
      // if not left nor right, we are horizontally centering the element
      const xOffset = (popupCoords.width - triggerCoords.width) / 2
      style.left = Math.round(triggerCoords.left - xOffset)
    }

    if (positions.includes('top')) {
      style.bottom = Math.round(clientHeight - triggerCoords.top) + 4
    } else if (positions.includes('bottom')) {
      style.top = Math.round(triggerCoords.bottom) + 4
    } else {
      // if not top nor bottom, we are vertically centering the element
      const yOffset = (popupCoords.height) / 2
      style.top = Math.round(triggerCoords.bottom - yOffset)

      const xOffset = triggerCoords.width;
      if (positions.includes('right')) {
        style.right = Math.round(style.right as number - xOffset + 4)
      } else {
        style.left = Math.round(style.left as number - xOffset - 4)
      }
    }

    if (positions.includes('center')) {
      const xOffset = (popupCoords.width - triggerCoords.width);
      if (positions.includes('right')) {
        style.right = Math.round(style.right as number - xOffset - 6)
      } else if (positions.includes('left')) {
        style.left = Math.round(style.left as number - xOffset - 2)
      }
    }

    this.style = mapStyle(style)
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
.popup-wrapper, .trigger {
  display: inline;
}

.popup-wrapper .ui.popup {
  display: block;
  visibility: hidden;
}
</style>
