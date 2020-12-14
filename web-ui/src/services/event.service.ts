class Handler {
  id: number
  event: string
  handler: (event: Event) => void

  constructor(id: number, event: string, handler: (event: Event) => void) {
    this.id = id
    this.event = event
    this.handler = handler
  }
}

class EventService {
  map: Map<string, Handler[]> = new Map<string, Handler[]>()

  on(event: string, handler: (event: Event) => void): number {
    const handlers = this.map.get(event) || []
    const id = new Date().getTime()
    handlers.push(new Handler(id, event, handler))
    this.map.set(event, handlers)
    return id
  }

  off(event: string, id: number) {
    const handlers = this.map.get(event) || []
    const index = handlers.findIndex(e => e.id === id)
    if (index > -1) {
      handlers.splice(index, 1)
    }
  }

  handle(name: string, event: Event) {
    const handlers = this.map.get(name) || []
    handlers.forEach(handler => {
      handler.handler(event)
    })
  }
}

const eventService = new EventService()

document.onclick = (event) => {
  eventService.handle('click', event)
}

document.onscroll = (event) => {
  eventService.handle('scroll', event)
}

export default eventService
