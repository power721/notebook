<template>
  <div id="app" class="ui center aligned container">
    <div class="ui pointing menu">
      <router-link class="item" to="/" exact>首页</router-link>
      <router-link class="item" to="/notebooks">笔记本</router-link>
      <router-link class="item" to="/categories">分类</router-link>
      <router-link class="item" to="/about">关于</router-link>
      <div class="right menu">
        <!--        <div class="item">-->
        <!--          <div class="ui transparent icon input">-->
        <!--            <input type="text" placeholder="Search...">-->
        <!--            <i class="search link icon"></i>-->
        <!--          </div>-->
        <!--        </div>-->
        <UserAccount></UserAccount>
      </div>
    </div>
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

.top.right.dropdown {
  position: absolute;
  top: 12px;
  right: 12px;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  padding-bottom: 16px;
}
</style>
