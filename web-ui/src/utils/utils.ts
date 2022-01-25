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

export function isValidHttpUrl(text: string): boolean {
  let url

  try {
    url = new URL(text)
  } catch (_) {
    return false
  }

  return url.protocol === "http:" || url.protocol === "https:"
}

export function createElement<K extends keyof HTMLElementTagNameMap>(tagName: K, options: any = {}, ...children: (Node | string)[]): HTMLElementTagNameMap[K] {
  const element = document.createElement(tagName)

  if (options.class) {
    element.classList.add(...options.class.split(' '))
    delete options['class']
  }

  if (options.style) {
    for (const style of options.style.split(';')) {
      const array = style.split(':')
      if (array.length == 2) {
        element.style[array[0].trim()] = array[1].trim()
      }
    }
    delete options['style']
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

export function createImg(src: string, options: any = {}, ...children: (Node | string)[]): HTMLElement {
  return createElement('img', {...options, src: src}, ...children)
}
