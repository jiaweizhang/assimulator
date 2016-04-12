import Vue from 'vue'
import App from './components/App.vue'
import Home from './components/Home.vue'
import Assembler from './components/Assembler.vue'
import Disassembler from './components/Disassembler.vue'
import Simulator from './components/Simulator.vue'
import AssemblerHistory from './components/AssemblerHistory.vue'
import About from './components/About.vue'
import Signup from './components/Signup.vue'
import Login from './components/Login.vue'
import VueRouter from 'vue-router'
import VueResource from 'vue-resource'
Vue.use(VueResource)
Vue.use(VueRouter)
import auth from './auth'

Vue.http.headers.common['Authorization'] = 'Bearer ' + localStorage.getItem('id_token');

// Check the user's auth status when the app starts
auth.checkAuth()

export var router = new VueRouter()

router.map({
  '/': {
    component: Home
  },
  '/assembler': {
    component: Assembler
  },
  '/disassembler': {
    component: Disassembler
  },
  '/simulator': {
    component: Simulator
  },
  '/assembler/history': {
    component: AssemblerHistory
  },
  '/about': {
    component: About
  },
  '/login': {
    component: Login
  },
  '/signup': {
    component: Signup
  }
})

router.redirect({
  '*': '/'
})

router.start(App, '#app')
