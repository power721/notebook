import rehypeExternalLinks from 'rehype-external-links'

export default function externalLink() {
  return {
    rehype: (u) => u.use(rehypeExternalLinks),
  }
}
