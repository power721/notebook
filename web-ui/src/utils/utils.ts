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
