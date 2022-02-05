export class CacheItem<V> {
  time: number = new Date().getTime()
  value: V

  constructor(value: V) {
    this.value = value
  }
}

export class CacheValueIterator<V> implements IterableIterator<V> {
  private readonly it: IterableIterator<CacheItem<V>>

  constructor(it: IterableIterator<CacheItem<V>>) {
    this.it = it
  }

  [Symbol.iterator](): IterableIterator<V> {
    return new CacheValueIterator<V>(this.it)
  }

  next(...args: [] | [undefined]): IteratorResult<V, V> {
    const inext = this.it.next(...args)
    return {
      done: inext.done,
      value: inext?.value?.value
    }
  }
}

export class CacheEntryIterator<K, V> implements IterableIterator<[K, V]> {
  private readonly it: IterableIterator<[K, CacheItem<V>]>

  constructor(it: IterableIterator<[K, CacheItem<V>]>) {
    this.it = it
  }

  [Symbol.iterator](): IterableIterator<[K, V]> {
    return new CacheEntryIterator<K, V>(this.it)
  }

  next(...args: [] | [undefined]): IteratorResult<[K, V], [K, V]> {
    const inext = this.it.next(...args)
    return {
      done: inext.done,
      value: inext.done ? undefined : [inext.value[0], inext.value[1].value]
    }
  }
}

export class Cache<K, V> {
  private map: Map<K, CacheItem<V>> = new Map<K, CacheItem<V>>()
  readonly expire: number = 0

  get size(): number {
    return this.map.size
  }

  constructor(expire: number = 0) {
    this.expire = expire
  }

  private isExpired(key: K): boolean {
    if (this.expire <= 0) {
      return false
    }
    const item = this.map.get(key)
    const now = new Date().getTime()
    return item.time + this.expire < now
  }

  get(key: K): V | undefined {
    if (!this.has(key)) {
      return undefined
    }
    return this.map.get(key)?.value
  }

  has(key: K): boolean {
    return this.map.has(key) && !this.isExpired(key)
  }

  set(key: K, value: V): this {
    this.map.set(key, new CacheItem<V>(value))
    return this
  }

  delete(key: K): boolean {
    return this.map.delete(key)
  }

  clear(): void {
    this.map.clear()
  }

  keys(): IterableIterator<K> {
    return this.map.keys()
  }

  values(): IterableIterator<V> {
    return new CacheValueIterator<V>(this.map.values())
  }

  entries(): IterableIterator<[K, V]> {
    return new CacheEntryIterator<K, V>(this.map.entries())
  }
}
