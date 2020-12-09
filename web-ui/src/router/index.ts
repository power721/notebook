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
    component: MyNotebooks
  },
  {
    path: '/my-notes',
    name: 'MyNotes',
    component: MyNotes
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
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  routes,
  linkActiveClass: 'active'
})

export default router
