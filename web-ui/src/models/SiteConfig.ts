export class QiniuProperties {
  enabled: boolean = false
  accessKey: string = ''
  secretKey: string = ''
  bucket: string = ''
  domain: string = ''
}

export class SiteConfig {
  siteName: string = 'Notebook'
  brandColor: string = 'teal'
  qrCode: string = ''
  icpBeian: string = ''
  govBeian: string = ''
  showViews: boolean = false
  showWords: boolean = false
  enableAudit: boolean = true
  enableComment: boolean = true
  enableFileUpload: boolean = true
  enableImageUpload: boolean = true
  enableSignup: boolean = true
  enableHeartbeat: boolean = true
  enableEncrypt: boolean = false
  secretKey: string = ''
  qiniu: QiniuProperties = new QiniuProperties()
}

export class DveConfig {
  developer: string = 'Har01d'
  version: string = '0.1.0'
}
