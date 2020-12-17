import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import NoteList from '@/views/note/NoteList.vue'
import NoteDetails from '@/views/note/NoteDetails.vue'
import EditNote from '@/views/note/EditNote.vue'
import NoteHistory from '@/views/note/NoteHistory.vue'
import NotebookDetails from '@/views/notebook/NotebookDetails.vue'
import NotebookList from '@/views/notebook/NotebookList.vue'
import CategoryList from '@/views/category/CategoryList.vue'
import CategoryDetails from '@/views/category/CategoryDetails.vue'
import About from '@/views/About.vue'
import Signup from '@/views/user/Signup.vue'
import Login from '@/views/user/Login.vue'
import UserHome from '@/views/user/UserHome.vue'
import MyNotes from '@/views/user/MyNotes.vue'
import MyNotebooks from '@/views/user/MyNotebooks.vue'
import TrashNotes from '@/views/user/TrashNotes.vue'
import UserNotes from '@/views/user/UserNotes.vue'
import UserInfo from '@/views/user/UserInfo.vue'
import AdminHome from '@/views/admin/AdminHome.vue'
import AdminAudit from '@/views/admin/AdminAudit.vue'
import AdminConfig from '@/views/admin/AdminConfig.vue'
import AdminStats from '@/views/admin/AdminStats.vue'
import AdminInfo from '@/views/admin/AdminInfo.vue'

Vue.use(VueRouter)

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'NoteList',
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
    path: '/notes/:id',
    name: 'NoteDetails',
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
    component: EditNote,
    meta: {auth: true}
  },
  {
    path: '/notes/:id/edit',
    name: 'EditNote',
    component: EditNote,
    meta: {auth: true}
  },
  {
    path: '/signup',
    component: Signup,
    meta: {guest: true}
  },
  {
    path: '/login',
    component: Login,
    meta: {guest: true}
  },
  {
    path: '/user',
    name: 'UserHome',
    component: UserHome,
    redirect: '/user/notes',
    meta: {auth: true},
    children: [
      {
        path: 'notes',
        name: 'MyNotes',
        component: MyNotes,
        meta: {auth: true}
      },
      {
        path: 'notebooks',
        name: 'MyNotebooks',
        component: MyNotebooks,
        meta: {auth: true}
      },
      {
        path: 'trash',
        name: 'TrashNotes',
        component: TrashNotes,
        meta: {auth: true}
      },
      {
        path: 'info',
        name: 'UserInfo',
        component: UserInfo,
        meta: {auth: true}
      },
    ]
  },
  {
    path: '/users/:id',
    name: 'UserNotes',
    component: UserNotes,
    meta: {auth: true}
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: AdminHome,
    redirect: '/admin/info',
    meta: {admin: true},
    children: [
      {
        path: 'info',
        name: 'AdminInfo',
        component: AdminInfo,
        meta: {admin: true},
      },
      {
        path: 'audit',
        name: 'AdminAudit',
        component: AdminAudit,
        meta: {admin: true},
      },
      {
        path: 'stats',
        name: 'AdminStats',
        component: AdminStats,
        meta: {admin: true},
      },
      {
        path: 'config',
        name: 'AdminConfig',
        component: AdminConfig,
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
