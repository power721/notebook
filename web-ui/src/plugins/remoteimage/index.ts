import tinymce, {Editor} from 'tinymce'
import axios from 'axios'

interface ImageData {
  url: string;
}

const getSelectedImage = (editor: Editor): HTMLElement | null => {
  const imgElm = editor.selection.getNode()
  const figureElm = editor.dom.getParent<HTMLElement>(imgElm, 'figure.image')

  if (figureElm) {
    return editor.dom.select('img', figureElm)[0]
  }

  if (imgElm && (imgElm.nodeName !== 'IMG')) {
    return null
  }

  return imgElm as HTMLElement
}

tinymce.PluginManager.add('remoteimage', (editor: Editor) => {
  const openDialog = () => {
    return editor.windowManager.open({
      title: '上传远程图片',
      body: {
        type: 'panel',
        items: [
          {
            type: 'input',
            name: 'url',
            placeholder: '图片url',
            label: 'url'
          }
        ]
      },
      buttons: [
        {
          type: 'cancel',
          text: 'Close'
        },
        {
          type: 'submit',
          text: 'Insert',
          primary: true
        }
      ],
      initialData: {
        url: getSelectedImage(editor)?.getAttribute('src') ?? ''
      },
      onSubmit: (api) => {
        const image = api.getData() as ImageData
        if (image.url) {
          axios.post('/images/remote?url=' + image.url).then(({data}) => {
            editor.insertContent(`<img alt="remote" src="${data.url}">`)
          })
        }
        api.close()
      }
    })
  }

  editor.ui.registry.addIcon('remoteimage', '<svg width="24px" height="21px" viewBox="0 0 23 21" version="1.1"> <g id="surface1"> <path style=" stroke:none;fill-rule:nonzero;fill:rgb(0%,0%,0%);fill-opacity:1;" d="M 22.105469 16.859375 C 22.105469 17.851562 21.316406 18.667969 20.351562 18.667969 L 2.808594 18.667969 C 1.84375 18.667969 1.054688 17.851562 1.054688 16.859375 L 1.054688 3.117188 C 1.054688 2.125 1.84375 1.308594 2.808594 1.308594 L 20.351562 1.308594 C 21.316406 1.308594 22.105469 2.125 22.105469 3.117188 Z M 2.808594 2.757812 C 2.625 2.757812 2.460938 2.925781 2.460938 3.117188 L 2.460938 16.859375 C 2.460938 17.050781 2.625 17.21875 2.808594 17.21875 L 20.351562 17.21875 C 20.539062 17.21875 20.703125 17.050781 20.703125 16.859375 L 20.703125 3.117188 C 20.703125 2.925781 20.539062 2.757812 20.351562 2.757812 Z M 5.96875 8.542969 C 4.804688 8.542969 3.863281 7.570312 3.863281 6.371094 C 3.863281 5.175781 4.804688 4.203125 5.96875 4.203125 C 7.128906 4.203125 8.070312 5.175781 8.070312 6.371094 C 8.070312 7.570312 7.128906 8.542969 5.96875 8.542969 Z M 19.300781 15.773438 L 3.863281 15.773438 L 3.863281 13.605469 L 7.371094 9.988281 L 9.125 11.796875 L 14.738281 6.011719 L 19.300781 10.710938 Z M 19.300781 15.773438 "/> </g> </svg>')

  editor.ui.registry.addButton('remoteimage', {
    icon: 'remoteimage',
    tooltip: '上传远程图片',
    onAction: () => {
      openDialog()
    }
  })

  editor.ui.registry.addMenuItem('remoteimage', {
    text: '远程图片',
    icon: 'remoteimage',
    onAction: () => {
      openDialog()
    }
  })

  editor.ui.registry.addContextMenu('remoteimage', {
    update: () => {
      return 'remoteimage'
    }
  })

  editor.ui.registry.addContextToolbar('remoteimage', {
    predicate: (node) => {
      return node.nodeName.toLowerCase() === 'img'
    },
    items: 'remoteimage',
    position: 'node',
    scope: 'node'
  })

  return {
    getMetadata: () => {
      return {
        name: 'Remote Image Plugin',
        url: 'https://har01d.cn/',
      }
    }
  }
})
