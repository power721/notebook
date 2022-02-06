/* eslint-disable */
import configService from '@/services/config.service'
import axios from 'axios'

interface BlobInfo {
  id: () => string;
  name: () => string;
  filename: () => string;
  blob: () => Blob;
  base64: () => string;
  blobUri: () => string;
  uri: () => string | undefined;
  file?: File;
}

const fileUpload = configService.siteConfig.enableFileUpload ? ' upfile attachment' : ''
const imageUpload = configService.siteConfig.enableImageUpload ? '  axupimgs' : ''

export const mceConfig: any = {
  height: document.body.clientHeight - 530,
  branding: false,
  language: 'zh_CN',
  plugins: [
    'autolink link media table advlist lists hr' + fileUpload + imageUpload,
    'code codesample charmap image imagetools quickbars preview fullscreen',
    'insertdatetime toc paste wordcount help searchreplace emoticons textpattern notelink'
  ],
  menu: {
    insert: {
      title: 'Insert',
      items: 'image axupimgs link media upfile attachment template codesample inserttable | notelink charmap emoticons hr | pagebreak nonbreaking anchor toc | insertdatetime'
    },
  },
  relative_urls: false,
  image_uploadtab: configService.siteConfig.enableImageUpload,
  emoticons_database_url: 'https://cdn.jsdelivr.net/npm/tinymce@5.10.2/plugins/emoticons/js/emojis.min.js',
  default_link_target: '_blank',
  codesample_global_prismjs: true,
  codesample_languages: (window as any).hljs.listLanguages().map(e => ({text: e, value: e})),
  skin_url: 'https://cdn.jsdelivr.net/npm/tinymce@5.10.2/skins/ui/oxide',
  content_css: [],
  toolbar:
    'formatselect | bold italic backcolor | \
    alignleft aligncenter alignright alignjustify | link image axupimgs media upfile attachment | \
    bullist numlist outdent indent | charmap emoticons codesample | removeformat code preview fullscreen | help',
  textpattern_patterns: [
    {start: '*', end: '*', format: 'italic'},
    {start: '**', end: '**', format: 'bold'},
    {start: '`', end: '`', format: 'code'},
    {start: '~~', end: '~~', format: 'strikethrough'},
    {start: '#', format: 'h1'},
    {start: '##', format: 'h2'},
    {start: '###', format: 'h3'},
    {start: '####', format: 'h4'},
    {start: '#####', format: 'h5'},
    {start: '######', format: 'h6'},
    // The following text patterns require the `lists` plugin
    {start: '1. ', cmd: 'InsertOrderedList'},
    {start: '* ', cmd: 'InsertUnorderedList'},
    {start: '- ', cmd: 'InsertUnorderedList' }
  ],
  file_callback: function (file: File, callback: (url: string, details: unknown) => void) {
    const formData = new FormData()
    formData.append('file', file)
    axios.post('/files', formData).then((res) => {
      callback(res.data.url, {text: res.data.name})
    }).catch((error) => {
      console.error(error)
    })
  },
  attachment_max_size: 104857600,
  attachment_upload_handler: function (file: File, success: (url: string) => void, failure: (error: string) => void, progress: (progress: number) => void) {
    const formData = new FormData()
    formData.append('file', file)
    axios.post('/files', formData, {
      onUploadProgress: function (progressEvent) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        progress(percent)
      }
    }).then((res) => {
      success(res.data.url)
    }).catch((error) => {
      console.log(error)
      failure(error.message)
    })
  },
  images_upload_handler: function (blobInfo: BlobInfo, success: (url: string) => void, failure: (error: string) => void, progress: (progress: number) => void) {
    const formData = new FormData()
    let name: string
    if (blobInfo.file) {
      name = blobInfo.file.name
    } else {
      name = blobInfo.filename()
    }
    formData.append('file', blobInfo.blob(), name)
    axios.post('/images', formData, {
      onUploadProgress: function (progressEvent) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        progress && progress(percent)
      }
    }).then((res) => {
      success(res.data.url)
    }).catch((error) => {
      console.log(error)
      failure(error.message)
    })
  }
}

export const tinymceConfig: any = {
  height: 180,
  branding: false,
  language: 'zh_CN',
  plugins: ['charmap code emoticons textpattern wordcount notelink fullscreen'],
  formats: {
    underline: {inline: 'u'},
    strikethrough: {inline: 'del'}
  },
  textpattern_patterns: [
    {start: '*', end: '*', format: 'italic'},
    {start: '**', end: '**', format: 'bold'},
    {start: '`', end: '`', format: 'code'},
    {start: '~~', end: '~~', format: 'strikethrough'},
    {start: '#', format: 'h1'},
    {start: '##', format: 'h2'},
    {start: '###', format: 'h3'},
    {start: '####', format: 'h4'},
    {start: '#####', format: 'h5'},
    {start: '######', format: 'h6'},
    {start: '1. ', cmd: 'InsertOrderedList'},
    {start: '* ', cmd: 'InsertUnorderedList'},
    {start: '- ', cmd: 'InsertUnorderedList' }
  ],
  toolbar: 'bold italic underline strikethrough subscript superscript notelink charmap emoticons removeformat code fullscreen',
  menubar: false,
  contextmenu: false,
  emoticons_database_url: 'https://cdn.jsdelivr.net/npm/tinymce@5.10.2/plugins/emoticons/js/emojis.min.js',
  skin_url: 'https://cdn.jsdelivr.net/npm/tinymce@5.10.2/skins/ui/oxide',
  content_css: [],
}
