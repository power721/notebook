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

tinymce.PluginManager.add('remoteimage', function(editor: Editor) {
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

  editor.ui.registry.addIcon('remoteimage', '<svg width="24px" height="24px" viewBox="0 0 60 60"><g><g><g><rect x="1" y="4.5" style="fill:#ECF0F1;" width="55" height="42"></rect><path style="fill:#545E73;" d="M57,47.5H0v-44h57V47.5z M2,45.5h53v-40H2V45.5z"></path></g><g><rect x="5" y="8.5" style="fill:#545E73;" width="47" height="34"></rect><path style="fill:#ECF0F1;" d="M53,43.5H4v-36h49V43.5z M6,41.5h45v-32H6V41.5z"></path></g><circle style="fill:#F3D55A;" cx="15" cy="17.069" r="4.569"></circle><polygon style="fill:#11A085;" points="51,32.611 50,31.5 38,20.5 27.5,32 32.983,37.483 37,41.5 51,41.5"></polygon><polygon style="fill:#26B999;" points="6,41.5 37,41.5 32.983,37.483 22.017,26.517 6,40.5"></polygon></g><g><g><path style="fill:#48A0DC;" d="M55.045,45.611c-0.05-3.935-3.162-7.111-6.999-7.111c-2.568,0-4.806,1.426-6.025,3.546 c-0.421-0.141-0.87-0.22-1.337-0.22c-2.063,0-3.785,1.492-4.208,3.484c-1.754,0.865-2.975,2.706-2.975,4.831 c0,2.947,2.343,5.359,5.208,5.359h10.775c0.061,0,0.119-0.007,0.18-0.009c0.06,0.002,0.119,0.009,0.18,0.009h4.31 c2.667,0,4.849-2.245,4.849-4.989C59,48.081,57.288,46.046,55.045,45.611z"></path><path style="fill:#B1D3EF;" d="M54.151,56.5h-4.31c-0.063,0-0.126-0.004-0.188-0.008c-0.048,0.004-0.109,0.008-0.172,0.008 H38.708c-3.423,0-6.208-2.853-6.208-6.358c0-2.262,1.209-4.372,3.116-5.503c0.686-2.235,2.746-3.813,5.066-3.813 c0.296,0,0.592,0.025,0.884,0.076c1.495-2.116,3.914-3.402,6.479-3.402c4.102,0,7.524,3.225,7.954,7.332 c2.358,0.806,4,3.079,4,5.679C60,53.813,57.376,56.5,54.151,56.5z M49.614,54.491l0.186,0.006l4.352,0.003 c2.122,0,3.849-1.79,3.849-3.989c0-1.917-1.323-3.564-3.146-3.919l-0.799-0.155l-0.011-0.813 c-0.044-3.376-2.734-6.123-5.999-6.123c-2.135,0-4.063,1.139-5.158,3.045l-0.409,0.711l-0.777-0.261 c-0.332-0.112-0.675-0.169-1.019-0.169c-1.54,0-2.898,1.133-3.229,2.692l-0.102,0.475l-0.435,0.214 c-1.469,0.725-2.417,2.269-2.417,3.935c0,2.403,1.888,4.358,4.208,4.358L49.614,54.491z"></path></g></g></g></svg>')

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
    getMetadata: function () {
      return {
        name: 'Remote Image Plugin',
        url: 'https://har01d.cn/',
      }
    }
  }
})
