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

  on(event: string | string[], handler: (event: Event) => void): number {
    const id = Math.random()
    if (Array.isArray(event)) {
      for (const e of event) {
        this.register(e, id, handler)
      }
    } else {
      this.register(event, id, handler)
    }
    return id
  }

  private register(event: string, id: number, handler: (event: Event) => void) {
    const handlers = this.map.get(event) || []
    handlers.push(new Handler(id, event, handler))
    this.map.set(event, handlers)
  }

  off(event: string | string[], id: number) {
    if (Array.isArray(event)) {
      for (const e of event) {
        this.unregister(e, id)
      }
    } else {
      this.unregister(event, id)
    }
  }

  private unregister(event: string, id: number) {
    const handlers = this.map.get(event) || []
    const index = handlers.findIndex(e => e.id === id)
    if (index > -1) {
      handlers.splice(index, 1)
    }
  }

  handle(event: Event) {
    const handlers = this.map.get(event.type) || []
    handlers.forEach(handler => {
      handler.handler(event)
    })
  }
}

const eventService = new EventService()

const events = ['click', 'touchend', 'scroll']
events.forEach(type => {
  document.addEventListener(type, (event) => {
    eventService.handle(event)
  }, false)
})

export default eventService
