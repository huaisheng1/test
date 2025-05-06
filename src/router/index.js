import { createRouter, createWebHistory } from 'vue-router'
import login from '../views/login.vue'
import register from '@/views/register.vue'
import forget from '@/views/forget.vue'
import Home from '@/views/Home.vue'
import History from '@/views/history.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: login
    },
    {
      path: '/register',
      name: 'register',
      component: register
    },
    {
      path: '/forget',
      name:'forget',
      component: forget
    },
    {
      path: '/index',
      name: 'index',
      component: () => import('../views/indexView.vue'),
      children: [
        {
          path: '/home',
          name: 'home',
          component: Home
        },
        {
          path: '/chat',
          name: 'chat',
          component: () => import('../views/chat.vue')
        },
        {
          path: '/history',
          name: 'history',
          component: History
        },
        {
          path: '/history/:id',
          name: 'storyDetail',
          component: History,
          props: true
        }
      ]  
    }
  ],
})

export default router
