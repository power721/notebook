import gfm from '@bytemd/plugin-gfm'
import zhGfm from '@bytemd/plugin-gfm/lib/locales/zh_Hans.json'
import breaks from '@bytemd/plugin-breaks'
import frontmatter from '@bytemd/plugin-frontmatter'
import highlight from '@bytemd/plugin-highlight'
import gemoji from '@bytemd/plugin-gemoji'
import math from '@bytemd/plugin-math'
import zhMath from '@bytemd/plugin-math/lib/locales/zh_Hans.json'
import mermaid from '@bytemd/plugin-mermaid'
import zhMermaid from '@bytemd/plugin-mermaid/lib/locales/zh_Hans.json'

import externalLink from '@/plugins/externalLink'
import themeLoader from '@/plugins/themeLoader'

export const plugins = [
  gfm({locale: zhGfm}),
  breaks(),
  frontmatter(),
  highlight(),
  gemoji(),
  math({locale: zhMath}),
  mermaid({locale: zhMermaid}),
  externalLink(),
  themeLoader(),
]
