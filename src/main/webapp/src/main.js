import Vue from "vue";
import App from "./App.vue";
import Home from "./components/Home.vue";
import Assembler from "./components/Assembler.vue";
import Simulator from "./components/Simulator.vue";
import About from "./components/About.vue";
import VueRouter from "vue-router";
import VueResource from "vue-resource";
Vue.use(VueResource);
Vue.use(VueRouter);

const routes = [
  {path: '/', component: Home},
  {path: '/assembler', component: Assembler},
  {path: '/simulator', component: Simulator},
  {path: '/about', component: About},
  {path: '*', redirect: '/'}
];

const router = new VueRouter({routes});

/*
 https://github.com/vuejs/vue-router/blob/dev/examples/auth-flow/app.js
 */
const app = new Vue({
  el: '#app',
  router,
  render: h => h(App)
});
