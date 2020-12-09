<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">首页</router-link> |
      <router-link to="/notebooks">笔记本</router-link> |
      <router-link to="/categories">分类</router-link>
      |
      <router-link to="/about">关于</router-link>
    </div>
    <UserAccount></UserAccount>
    <router-view/>
  </div>
</template>

<style>
.viewer .tui-editor-contents, .editor .tui-editor-contents {
  font-size: medium;
}

.right.floated {
  float: right;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  padding-bottom: 16px;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
