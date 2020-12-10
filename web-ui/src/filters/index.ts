function toFix2(value: number) {
  const str = Math.floor(value) + ''
  if (str.length < 2) {
    return '0' + str
  }
  return str
}

export function datetime(value: number | string) {
  if (!value) {
    return ''
  }
  const dateObj = new Date(value)
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  const year = dateObj.getFullYear()
  const hour = dateObj.getHours()
  const minute = dateObj.getMinutes()
  const second = dateObj.getSeconds()
  return year + '-' + toFix2(month) + '-' + toFix2(day) + ' ' + toFix2(hour) + ':' + toFix2(minute)// + ':' + toFix2(second)
}

export function date(value: number | string) {
  const dateObj = new Date(value)
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  const year = dateObj.getFullYear()
  return year + '-' + toFix2(month) + '-' + toFix2(day)
}

export function time(value: number | string) {
  const dateObj = new Date(value)
  const year = dateObj.getFullYear()
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  const hour = dateObj.getHours()
  const minute = dateObj.getMinutes()

  const now = new Date()
  if (now.getFullYear() !== year) {
    return year + '-' + toFix2(month) + '-' + toFix2(day) + ' ' + toFix2(hour) + ':' + toFix2(minute)
  }
  if (now.getMonth() + 1 === month && now.getDate() === day) {
    return toFix2(hour) + ':' + toFix2(minute)
  }
  return toFix2(month) + '-' + toFix2(day) + ' ' + toFix2(hour) + ':' + toFix2(minute)
}

export function duration(value: number) {
  const second = value % 60
  const minute = value / 60 % 60
  const hour = value / 3600
  return toFix2(hour) + ':' + toFix2(minute) + ':' + toFix2(second)
}

export function fromNow(value: number | string) {
  const now = new Date().getTime()
  const diff = now - new Date(value).getTime()

  const second = 1000
  const minute = 60 * second
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day
  const year = 365 * day

  if (diff < 1000) {
    return '刚刚'
  } else if (diff < minute) {
    return Math.floor(diff / second) + '秒前'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < month) {
    return Math.floor(diff / day) + '天前'
  } else if (diff < year) {
    const m = Math.floor(diff / month)
    if (diff === m * month) {
      return m + '个月前'
    } else {
      return Math.floor(diff / day) + '天前'
    }
  } else {
    const y = Math.floor(diff / year)
    if (diff === y * year) {
      return y + '年前'
    } else {
      return Math.floor(diff / day) + '天前'
    }
  }
}
