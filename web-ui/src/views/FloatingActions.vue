<template>
  <span class="top" data-tooltip="回到顶部" data-position="left center" v-show="show">
    <i class="arrow circle up link orange icon" @click="goTop"></i>
  </span>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import eventService from '@/services/event.service'

  @Component
  export default class FloatingActions extends Vue {
    private show: boolean = false
    private handler: number = 0

    mounted() {
      this.handler = eventService.on('scroll', () => {
        this.show = window.scrollY > (window.innerHeight / 2)
      })
    }

    destroyed() {
      eventService.off('scroll', this.handler)
    }

    goTop() {
      window.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth'
      })
    }
  }
</script>

<style scoped>
  .top .icon {
    font-size: 38px;
  }

  .top {
    position: fixed;
    right: 20px;
    bottom: 58px;
  }
</style>
