var Vue = require('vue');
Vue.use(require('vue-resource'));
// TODO npm vue-router is broken
//Vue.use(require('vue-router'));
Vue.use(VueRouter);

var Home = Vue.extend({
  template: `
    <h1>Assembler Disassembler Simulator</h1>
    <p>Check out Github for instructions</p>
  `
})

var Assembler = Vue.extend({
  template: `
  <div class="jumbotron text-center"><h1>Assembler</h1></div>
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">.asm goes here</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="asm"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="assemble()">Send</button>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output</h2>
      <pre>{{mif}}</pre>
    </div>
  </div>
  `,
  props:['asm', 'mif'],
  methods: {
    assemble: function() {
        this.$http({url: 'http://localhost/api/assemble', 
          method: 'POST', 
          data: this.asm,
          headers: {
            'Content-Type': 'test/plain'
          }
        }).then(function (response) {
          // success callback
          console.log(response);
          this.mif = response.data;
        }, function (response) {
          // error callback
        })
      }
    }
});

var Disassembler = Vue.extend({
  template: `
  <div class="jumbotron text-center"><h1>Disassembler</h1></div>
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">.mif goes here</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="mif"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="disassemble()">Send</button>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output</h2>
      <pre>{{asm}}</pre>
    </div>
  </div>
  `,
  props:['asm', 'mif'],
  methods: {
    disassemble: function() {
        this.$http({url: 'http://localhost/api/disassemble', 
          method: 'POST', 
          data: this.mif,
          headers: {
            'Content-Type': 'test/plain'
          }
        }).then(function (response) {
          // success callback
          console.log(response);
          this.asm = response.data;
        }, function (response) {
          // error callback
        })
      }
    }
});

var Simulator = Vue.extend({
  template: `
  <div class="jumbotron text-center"><h1>Simulator</h1></div>
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">.asm goes here</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="asm"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="simulate()">Send</button>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output</h2>
      <pre>{{states}}</pre>
    </div>
  </div>
  `,
  props:['asm', 'states'],
  methods: {
    simulate: function() {
        this.$http({url: 'http://localhost/api/simulate', 
          method: 'POST', 
          data: this.asm,
          headers: {
            'Content-Type': 'test/plain'
          }
        }).then(function (response) {
          // success callback
          console.log(response);
          this.states = response.data;
        }, function (response) {
          // error callback
        })
      }
    }
});

var About = Vue.extend({
  template: `
    <div>Fun project I'm doing</div>
  `
})

var Login = Vue.extend({
  template: `
    <div>NetID authentication will be added eventually</div>
  `
})

var App = Vue.extend({});

var router = new VueRouter();

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
  '/about': {
    component: About
  },
  '/login': {
    component: Login
  }
})

router.start(App, '#app')
