<template>
  <a v-if="menu.uri.startsWith('http')" class="item" :href="menu.uri" target="_blank">
    <i class="icon" :class="[menu.icon]" v-if="menu.icon"></i>{{menu.title}}
  </a>
  <router-link v-else class="item" :to="menu.uri">
    <i class="icon" :class="[menu.icon]" v-if="menu.icon"></i>{{menu.title}}
  </router-link>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator'
  import {Menu} from '@/models/Menu'

  @Component
  export default class AppMenu extends Vue {
    @Prop() menu!: Menu
  }
</script>
