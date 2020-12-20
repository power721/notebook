<template>
  <div class="ui left aligned container">
    <div class="ui breadcrumb">
      <router-link class="section" :exact="true" to="/">首页</router-link>
      <i class="right chevron icon divider"></i>
      <div class="section">管理中心</div>
      <i class="right chevron icon divider"></i>
      <div class="active section">菜单配置</div>
    </div>
    <div class="ui divider"></div>

    <button @click="addMenu" class="ui add primary icon button"><i class="add icon"></i></button>

    <div class="ui raised segment">
      <table class="ui selectable celled table">
        <thead>
        <tr>
          <th>ID</th>
          <th>标题</th>
          <th>链接</th>
          <th>图标</th>
          <th>顺序</th>
          <th>父菜单ID</th>
          <th>登录后显示</th>
          <th>管理员权限</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="menu of menus" :key="menu.id">
          <td>{{menu.id}}</td>
          <td>{{menu.title}}</td>
          <td>{{menu.uri}}</td>
          <td>{{menu.icon}}</td>
          <td>{{menu.order}}</td>
          <td>{{menu.parent}}</td>
          <td>{{menu.auth}}</td>
          <td>{{menu.admin}}</td>
          <td>
            <a href="javascript:void(0)" class="ui icon negative button" data-tooltip="删除菜单" @click="deleteMenu(menu)">
              <i class="close icon"></i>
            </a>
            <a href="javascript:void(0)" class="ui icon primary button" data-tooltip="编辑菜单" @click="editMenu(menu)">
              <i class="edit icon"></i>
            </a>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <Modal v-model="modal" :closable="false">
      <template slot="title">
        {{title}}
      </template>
      <form class="ui form">
        <div class="required field">
          <label>标题</label>
          <input name="title" placeholder="标题" type="text" autocomplete="off" v-model="menu.title">
        </div>
        <div class="required field">
          <label>链接</label>
          <input name="url" placeholder="URL" type="url" autocomplete="off" v-model="menu.uri">
        </div>
        <div class="required field">
          <label>顺序</label>
          <input name="order" placeholder="顺序" type="number" v-model="menu.order">
        </div>
        <div class="field">
          <label>图标</label>
          <input name="icon" placeholder="图标" type="text" autocomplete="off" v-model="menu.icon">
        </div>
        <div class="field">
          <label>父菜单</label>
          <input name="parent" placeholder="父菜单" type="number" v-model="menu.parent">
        </div>
        <div class="field">
          <div class="ui toggle checkbox">
            <input name="auth" placeholder="登录后显示" type="checkbox" v-model="menu.auth">
            <label>登录后显示</label>
          </div>
        </div>
        <div class="field">
          <div class="ui toggle checkbox">
            <input name="admin" placeholder="管理员权限" type="checkbox" v-model="menu.admin">
            <label>管理员权限</label>
          </div>
        </div>
      </form>
      <template slot="actions">
        <div @click="onCancel" class="ui cancel button">取消</div>
        <div @click="onSubmit" class="ui positive button">提交</div>
      </template>
    </Modal>

  </div>
</template>

<script lang="ts">
  import axios from 'axios'
  import {Component, Vue} from 'vue-property-decorator'
  import {Menu} from '@/models/Menu'
  import Modal from '@/components/Modal.vue'
  import configService from '@/services/config.service'

  @Component({
    components: {
      Modal
    }
  })
  export default class AdminMenu extends Vue {
    modal: boolean = false
    title: string = '添加菜单'
    menu: Menu = new Menu()
    menus: Menu[] = []

    mounted() {
      configService.setTitle('菜单配置')
      this.load()
    }

    load() {
      axios.get('/admin/menus').then(({data}) => {
        this.menus = data.content
      })
    }

    editMenu(menu: Menu) {
      Object.assign(this.menu, menu)
      this.title = '编辑菜单'
      this.modal = true
    }

    deleteMenu(menu: Menu) {
      axios.delete('/admin/menus/' + menu.id).then(() => {
        this.$store.dispatch('getMenus')
        this.$toasted.success('删除菜单成功')
        this.load()
      })
    }

    addMenu() {
      this.title = '添加菜单'
      this.modal = true
    }

    onCancel() {
      if (this.menu.id) {
        this.menu = new Menu()
      }
      this.modal = false
    }

    onSubmit() {
      if (!this.menu.title) {
        return
      }
      let api = '/admin/menus'
      if (this.menu.id) {
        api = '/admin/menus/' + this.menu.id
      }
      axios.post(api, this.menu).then(() => {
        this.menu = new Menu()
        this.modal = false
        this.$store.dispatch('getMenus')
        if (this.menu.id) {
          this.$toasted.success('更新菜单成功')
        } else {
          this.$toasted.success('添加菜单成功')
        }
        this.load()
      })
    }
  }
</script>

<style scoped>
  .add.button {
    float: right;
    margin-top: -60px;
  }
</style>
