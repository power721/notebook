import tinymce, {Editor} from 'tinymce'

interface NotelinkData {
  id: string;
}

tinymce.PluginManager.add('notelink', function (editor: Editor) {
  const openDialog = function () {
    return editor.windowManager.open({
      title: '插入笔记链接',
      body: {
        type: 'panel',
        items: [
          {
            type: 'input',
            name: 'id',
            placeholder: '笔记ID',
            label: 'ID'
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
      onSubmit: function (api) {
        const data = api.getData() as NotelinkData
        if (data.id) {
          editor.insertContent('[/notes/' + data.id + ']')
        }
        api.close()
      }
    })
  }

  editor.ui.registry.addButton('notelink', {
    icon: 'link',
    tooltip: '插入笔记链接',
    onAction: function () {
      openDialog()
    }
  })

  editor.ui.registry.addMenuItem('notelink', {
    text: '笔记链接',
    icon: 'link',
    onAction: function () {
      openDialog()
    }
  })

  return {
    getMetadata: function () {
      return {
        name: 'Notelink Plugin',
        url: 'https://har01d.cn/',
      }
    }
  }
})
