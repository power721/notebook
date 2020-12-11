class Handler {
  id: number
  event: string
  handler: (event: Event) => any

  constructor(id: number, event: string, handler: (event: Event) => any) {
    this.id = id
    this.event = event
    this.handler = handler
  }
}

class EventService {
  map: Map<string, Handler[]> = new Map<string, Handler[]>()

  on(event: string, handler: (event: Event) => any): number {
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

  onclick(event: MouseEvent) {
    const handlers = this.map.get('click') || []
    handlers.forEach(handler => {
      handler.handler(event)
    })
  }
}

const eventService = new EventService()

document.onclick = (event) => {
  eventService.onclick(event)
}

export default eventService
