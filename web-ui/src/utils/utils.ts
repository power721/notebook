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
