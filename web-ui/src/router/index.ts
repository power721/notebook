import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import Signup from '@/views/user/Signup.vue'
import Login from '@/views/user/Login.vue'
import NoteList from '@/views/note/NoteList.vue'
import NoteDetails from '@/views/note/NoteDetails.vue'
import EditNote from '@/views/note/EditNote.vue'
import NoteHistory from '@/views/note/NoteHistory.vue'
import NotebookDetails from '@/views/notebook/NotebookDetails.vue'
import NotebookList from '@/views/notebook/NotebookList.vue'
import MyNotebooks from '@/views/notebook/MyNotebooks.vue'
import MyNotes from '@/views/note/MyNotes.vue'
import CategoryList from '@/views/category/CategoryList.vue'
import CategoryDetails from '@/views/category/CategoryDetails.vue'
import About from '@/views/About.vue'
import UserInfo from '@/views/user/UserInfo.vue'
import Admin from '@/views/admin/Admin.vue'
import TrashNotes from '@/views/note/TrashNotes.vue'
import UserNotes from '@/views/user/UserNotes.vue'

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
    path: '/my-notebooks',
    name: 'MyNotebooks',
    component: MyNotebooks,
    meta: {auth: true}
  },
  {
    path: '/my-notes',
    name: 'MyNotes',
    component: MyNotes,
    meta: {auth: true}
  },
  {
    path: '/users/:id',
    name: 'UserNotes',
    component: UserNotes,
    meta: {auth: true}
  },
  {
    path: '/trash-notes',
    name: 'TrashNotes',
    component: TrashNotes,
    meta: {auth: true}
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
    path: '/info',
    component: UserInfo,
    meta: {auth: true}
  },
  {
    path: '/admin',
    component: Admin,
    meta: {auth: true}
  },
  {
    path: '/about',
    name: 'About',
    component: About
  }
]

const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active'
})

export default router
