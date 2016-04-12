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
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">Input (.asm)</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="asm"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="assemble()">Assemble</button>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output (.mif)</h2>
      <pre>{{mif}}</pre>
      <a class="btn btn-primary" href="/files/assembler/asm/{{asmId}}/file.asm" download>Download Asm</a>
      <a class="btn btn-primary" href="/files/assembler/mif/{{mifId}}/imem.mif" download>Download Mif</a>
    </div>
  </div>
  `,
  props:['asm', 'mif', 'asmId', 'mifId'],
  methods: {
    assemble: function() {
        this.$http({url: 'api/assemble', 
          method: 'POST', 
          data: this.asm,
          headers: {
            'Content-Type': 'test/plain'
          }
        }).then(function (response) {
          // success callback
          console.log(response);
          this.asmId = response.data.asmId;
          this.mifId = response.data.mifId;
          this.mif = response.data.mif;
        }, function (response) {
          // error callback
        })
      }
    }
});

var Disassembler = Vue.extend({
  template: `
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">Input (.mif)</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="mif"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="disassemble()">Disassemble</button>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output (.asm)</h2>
      <pre>{{asm}}</pre>
      <a class="btn btn-primary" href="/files/disassembler/asm/{{asmId}}/file.asm" download>Download Asm</a>
      <a class="btn btn-primary" href="/files/disassembler/mif/{{mifId}}/imem.mif" download>Download Mif</a>
    </div>
  </div>
  `,
  props:['asm', 'mif', 'mifId', 'asmId'],
  methods: {
    disassemble: function() {
        this.$http({url: 'api/disassemble', 
          method: 'POST', 
          data: this.mif,
          headers: {
            'Content-Type': 'test/plain'
          }
        }).then(function (response) {
          // success callback
          console.log(response);
          this.asm = response.data.asm;
          this.asmId = response.data.asmId;
          this.mifId = response.data.mifId;
        }, function (response) {
          // error callback
        })
      }
    }
});

var Simulator = Vue.extend({
  template: `
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">Input (.asm)</h2>
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
        this.$http({url: 'api/simulate', 
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
