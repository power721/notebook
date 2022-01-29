import themes from 'juejin-markdown-themes'
import {loadCss} from '../utils/utils'

export default function themeLoader() {
  return {
    viewerEffect: ({file}) => {
      const name = file.frontmatter?.theme ?? 'github'
      const theme = themes[name] ?? themes.github
      const $style = document.createElement('style')
      $style.innerHTML = theme.style
      document.head.appendChild($style)

      let highlight = theme.highlight ?? 'vs'
      highlight = highlight !== 'juejin' ? highlight : 'vs'
      const $highlight = loadCss('https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.4.0/build/styles/' + highlight + '.min.css')

      return () => {
        $style.remove()
        $highlight.remove()
      }
    }
  }
}
