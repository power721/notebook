<template>
  <div class="editor" ref="toastuiEditor"></div>
</template>
<script>
  import 'highlight.js/styles/github.css';
  import codeSyntaxHighlight from '@toast-ui/editor-plugin-code-syntax-highlight';
  import '@toast-ui/editor/dist/i18n/zh-cn'
  import Editor from '@toast-ui/editor'
  import { optionsMixin } from './mixin/option'

  export default {
    name: 'ToastuiEditor',
    mixins: [optionsMixin],
    props: {
      previewStyle: {
        type: String
      },
      height: {
        type: String
      },
      initialEditType: {
        type: String
      },
      initialValue: {
        type: String
      },
      options: {
        type: Object
      }
    },
    watch: {
      previewStyle (newValue) {
        this.editor.changePreviewStyle(newValue)
      },
      height (newValue) {
        this.editor.height(newValue)
      },
      initialValue (val, preVal) {
        if (val !== preVal) {
          this.createEditor()
        }
      },
    },
    mounted () {
      this.createEditor()
    },
    methods: {
      createEditor () {
        const options = {
          ...this.computedOptions,
          ...this.options,
          initialValue: this.initialValue,
          plugins: [[codeSyntaxHighlight]],
          el: this.$refs.toastuiEditor }
        this.editor = new Editor(options)
      },
      getRootElement () {
        return this.$refs.toastuiEditor
      }
    }
  }
</script>
