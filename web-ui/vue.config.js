const webpack = require('webpack')
const path = require('path')

const API = 'http://127.0.0.1:8080'

module.exports = {
  outputDir: path.resolve(__dirname, '../src/main/resources/static'),
  devServer: {
    proxy: {
      '/accounts': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/users': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/notes': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/notebooks': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/categories': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/images': {
        target: API,
        ws: true,
        changeOrigin: true
      },
      '/config': {
        target: API,
        ws: true,
        changeOrigin: true
      },
    }
  },
  css: { extract: false },
  // chainWebpack: config => {
  //   config
  //     .plugin('provide')
  //     .use(webpack.ProvidePlugin, [{
  //       Prism: 'Prism',
  //       'window.Prism': 'Prism',
  //     }])
  // },
}
