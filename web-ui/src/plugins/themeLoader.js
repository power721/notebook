import themes from 'juejin-markdown-themes'
import {loadCss} from '../utils/utils'

export default function themeLoader() {
  return {
    viewerEffect: ({file}) => {
      const name = file.frontmatter?.theme ?? 'github'
      if (document.getElementById('code-style-flag')) {
        return () => {
          document.querySelectorAll('.dynamic-code-style').forEach(e => e.remove())
        }
      }

      const elements = loadThemeStyles(name)
      return () => {
        elements.forEach(e => e.remove())
      }
    }
  }
}

export function loadThemeStyles(name) {
  const $flag = document.createElement('div')
  $flag.id = 'code-style-flag'
  $flag.classList.add('dynamic-code-style')
  document.body.appendChild($flag)

  const theme = themes[name] ?? themes.github
  const $style = document.createElement('style')
  $style.classList.add('dynamic-code-style')
  $style.innerHTML = theme.style
  document.head.appendChild($style)

  let highlight = theme.highlight ?? 'vs'
  highlight = highlight !== 'juejin' ? highlight : 'vs'
  const $highlight = loadCss('https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.4.0/build/styles/' + highlight + '.min.css', 'dynamic-code-style')

  return [$style, $highlight, $flag]
}
