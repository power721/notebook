export function goTop(top = 60) {
  if (window.scrollY <= top) {
    return
  }
  window.scroll({
    top: top,
    left: 0,
    behavior: 'smooth'
  })
}

export function loadJs(url: string) {
  const script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  document.head.appendChild(script)
  console.log('load javascript file: ' + url)
}

export function createElement<K extends keyof HTMLElementTagNameMap>(tagName: K, options: any = {}, ...children: (Node | string)[]): HTMLElementTagNameMap[K] {
  const element = document.createElement(tagName)

  if (options.classList) {
    element.classList.add(...options.classList.split(' '))
    delete options['classList']
  }

  Object.assign(element, options)

  element.append(...children)

  return element
}

export function createLink(text: string = '', href: string = '', download: string = ''): HTMLAnchorElement {
  const a = document.createElement('a')
  a.href = href
  a.text = text
  a.download = download
  return a
}

export function createDiv(options: any = {}, ...children: (Node | string)[]): HTMLElement {
  return createElement('div', options, ...children)
}
