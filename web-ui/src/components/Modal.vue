<template>
  <div class="ui dimmer modals page" :class="{active: show, visible: show}" @click="onDimmer">
    <div :class="sizeClass" class="ui visible active modal" v-show="show">
      <i @click="close" class="close icon"></i>
      <div class="header">
        <slot name="title">
          {{ title }}
        </slot>
      </div>
      <div class="scrolling content">
        <slot></slot>
      </div>
      <div class="actions">
        <slot name="actions">
          <div @click="onCancel" class="ui cancel button">{{cancelText}}</div>
          <div @click="onSubmit" class="ui positive button">{{okText}}</div>
        </slot>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'

  @Component
  export class Super extends Vue {
    show: boolean = false
  }

  @Component<Super>({
    model: {
      prop: 'open',
      event: 'changed'
    },
    watch: {
      open(newVal) {
        this.show = newVal
      }
    }
  })
  export default class Modal extends Super {
    @Prop() private open!: boolean
    @Prop() private title!: string
    @Prop() private size!: string
    @Prop({default: '确认'}) private okText!: string
    @Prop({default: '取消'}) private cancelText!: string

    get sizeClass() {
      return {
        [this.size]: true
      }
    }

    close() {
      this.show = false
      this.$emit('changed', false)
    }

    onDimmer(event: Event) {
      if (this.show && event.target === event.currentTarget) {
        this.close()
      }
    }

    onSubmit() {
      this.$emit('submit')
      this.close()
    }

    onCancel() {
      this.$emit('cancel')
      this.close()
    }
  }
</script>

<style scoped>

</style>
