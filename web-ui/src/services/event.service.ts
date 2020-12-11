export type Handler = (event: Event) => any

class EventService {
  id: number = 0
  map: Map<number, Handler> = new Map<number, Handler>()
  eventMap: Map<string, Handler[]> = new Map<string, Handler[]>()

  onclick(event: MouseEvent) {
    const handlers = this.eventMap.get('click') || []
    handlers.forEach(handler => {
      handler(event)
    })
  }

  on(event: string, handler: Handler): number {
    const handlers = this.eventMap.get(event) || []
    handlers.push(handler)
    this.eventMap.set(event, handlers)
    this.map.set(this.id, handler)
    return this.id++
  }

  off(event: string, id: number) {
    const handlers = this.eventMap.get(event) || []
    const handler = this.map.get(id)
    if (handler) {
      const index = handlers.indexOf(handler)
      if (index > -1) {
        this.eventMap.set(event, handlers.splice(index))
      }
    }
  }
}

const eventService = new EventService()

document.onclick = (event) => {
  eventService.onclick(event)
}

export default eventService
