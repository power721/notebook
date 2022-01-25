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
  qiniu: QiniuProperties = new QiniuProperties()
}
