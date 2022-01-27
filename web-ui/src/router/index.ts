import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import NoteList from '@/views/note/NoteList.vue'
import NoteDetails from '@/views/note/NoteDetails.vue'
import NoteHistory from '@/views/note/NoteHistory.vue'
import NotebookDetails from '@/views/notebook/NotebookDetails.vue'
import NotebookList from '@/views/notebook/NotebookList.vue'
import CategoryList from '@/views/category/CategoryList.vue'
import CategoryDetails from '@/views/category/CategoryDetails.vue'
import About from '@/views/About.vue'
import TagNotes from '@/views/tag/TagNotes.vue'
import TagList from '@/views/tag/TagList.vue'
import auth from '@/services/account.service'

Vue.use(VueRouter)

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'Home',
    redirect: '/notes',
    component: NoteList
  },
  {
    path: '/categories',
    name: 'CategoryList',
    component: CategoryList
  },
  {
    path: '/categories/:id',
    name: 'CategoryDetails',
    component: CategoryDetails
  },
  {
    path: '/notebooks',
    name: 'NotebookList',
    component: NotebookList
  },
  {
    path: '/notebooks/:id',
    name: 'NotebookDetails',
    component: NotebookDetails
  },
  {
    path: '/notes',
    name: 'NoteList',
    component: NoteList
  },
  {
    path: '/notes/:id',
    name: 'NoteDetails',
    component: NoteDetails
  },
  {
    path: '/articles/:id',
    name: 'ArticleDetails',
    component: NoteDetails
  },
  {
    path: '/notes/:id/history',
    name: 'NoteHistory',
    component: NoteHistory,
    meta: {auth: true}
  },
  {
    path: '/notes/-/new',
    name: 'CreateNote',
    component: () => import(/* webpackChunkName: "note" */ '@/views/note/EditNote.vue'),
    meta: {auth: true}
  },
  {
    path: '/notes/:id/edit',
    name: 'EditNote',
    component: () => import(/* webpackChunkName: "note" */ '@/views/note/EditNote.vue'),
    meta: {auth: true}
  },
  {
    path: '/signup',
    component: () => import(/* webpackChunkName: "user" */ '@/views/user/Signup.vue'),
    meta: {guest: true}
  },
  {
    path: '/login',
    component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login.vue'),
    meta: {guest: true}
  },
  {
    path: '/user',
    name: 'UserHome',
    component: () => import(/* webpackChunkName: "user" */ '@/views/user/UserHome.vue'),
    redirect: '/user/notes',
    meta: {auth: true},
    children: [
      {
        path: 'notes',
        name: 'MyNotes',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/MyNotes.vue'),
        meta: {auth: true}
      },
      {
        path: 'notebooks',
        name: 'MyNotebooks',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/MyNotebooks.vue'),
        meta: {auth: true}
      },
      {
        path: 'trash',
        name: 'TrashNotes',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/TrashNotes.vue'),
        meta: {auth: true}
      },
      {
        path: 'info',
        name: 'UserInfo',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/UserInfo.vue'),
        meta: {auth: true}
      },
    ]
  },
  {
    path: '/users/:id',
    name: 'UserNotes',
    component: () => import(/* webpackChunkName: "user" */ '@/views/user/UserNotes.vue'),
    meta: {auth: true}
  },
  {
    path: '/tags',
    name: 'TagList',
    component: TagList,
  },
  {
    path: '/tags/:tag',
    name: 'TagNotes',
    component: TagNotes,
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminHome.vue'),
    redirect: '/admin/info',
    meta: {admin: true},
    children: [
      {
        path: 'info',
        name: 'AdminInfo',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminInfo.vue'),
        meta: {admin: true},
      },
      {
        path: 'audit',
        name: 'AdminAudit',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminAudit.vue'),
        meta: {admin: true},
      },
      {
        path: 'stats',
        name: 'AdminStats',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminStats.vue'),
        meta: {admin: true},
      },
      {
        path: 'menu',
        name: 'AdminMenu',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminMenu.vue'),
        meta: {admin: true},
      },
      {
        path: 'config',
        name: 'AdminConfig',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/admin/AdminConfig.vue'),
        meta: {admin: true},
      },
    ]
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = new VueRouter({
  routes,
  linkActiveClass: 'active'
})

export default router

router.beforeEach((to, from, next) => {
  if (to.meta.admin && !auth.isAdmin()) {
    if (auth.isAuth()) {
      Vue.toasted.error('无权操作')
      next('/')
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    }
  } else if (to.meta.auth && !auth.getToken()) {
    next({
      path: '/login',
      query: {redirect: to.fullPath}
    })
  } else if (to.meta.guest && auth.isAuth()) {
    next('/')
  } else {
    next()
  }
})
