<template>
  <Viewer :plugins="plugins" :value="content"></Viewer>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator'
import {Viewer} from '@bytemd/vue'
import {plugins} from '@/plugins/bytemd-plugins'

@Component({
  components: {
    Viewer,
  },
})
export default class MdViewer extends Vue {
  @Prop() private content!: string

  plugins = plugins
}
</script>

<style scoped>

</style>
