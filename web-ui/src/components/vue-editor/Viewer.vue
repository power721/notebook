<template>
  <div class="viewer" ref="toastuiEditorViewer"></div>
</template>
<script>
  import 'highlight.js/styles/github.css'
  import codeSyntaxHighlight from '@toast-ui/editor-plugin-code-syntax-highlight'
  import Viewer from '@toast-ui/editor/dist/toastui-editor-viewer'
  import { optionsMixin } from './mixin/option'

  export default {
    name: 'ToastuiEditorViewer',
    mixins: [optionsMixin],
    props: {
      height: {
        type: String
      },
      initialValue: {
        type: String
      },
      options: {
        type: Object
      }
    },
    mounted () {
      if (this.initialValue) {
        this.createEditor()
      }
    },
    watch: {
      initialValue (val, preVal) {
        if (val !== preVal) {
          this.createEditor()
        }
      },
    },
    methods: {
      createEditor () {
        const options = {
          ...this.computedOptions,
          ...this.options,
          initialValue: this.initialValue,
          plugins: [[codeSyntaxHighlight]],
          el: this.$refs.toastuiEditorViewer
        }
        this.editor = new Viewer(options)
      },
      getRootElement () {
        return this.$refs.toastuiEditorViewer
      }
    }
  }
</script>
