export function goTop(top = 60) {
  window.scroll({
    top: top,
    left: 0,
    behavior: 'smooth'
  })
}
