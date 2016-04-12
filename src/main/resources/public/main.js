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
      <h3 class="text-center">Assemble Errors</h3>
      <pre>{{errors}}</pre>
    </div>
    <div class="col-sm-6">
      <h2 class="text-center">Output (.mif)</h2>
      <pre>{{imem}}</pre>
      <pre>{{dmem}}</pre>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/file.asm" download>Download Asm</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/imem.mif" download>Download ImemMif</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/dmem.mif" download>Download DmemMif</a>
    </div>
  </div>
  `,
  props:['asm', 'imem', 'dmem', 'id', 'errors'],
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
          this.id = response.data.id;
          this.imem = response.data.imem;
          this.dmem = response.data.dmem;
          this.errors = response.data.errors;
          var arr = localStorage.getItem("ids");
          if (arr) {
            var a = [];
            a = JSON.parse(arr);
            a.push(this.id);
            localStorage.setItem('ids', JSON.stringify(a));
          } else {
            var a = [];
            a.push(this.id);
            localStorage.setItem('ids', JSON.stringify(a));
          }
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

var AssemblerHistory = Vue.extend({
  template: `
  <div>
    <li v-for="id in ids">
      <a class="btn btn-primary" href="/files/assembler/{{id}}/file.asm" download>Download Asm</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/imem.mif" download>Download ImemMif</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/dmem.mif" download>Download DmemMif</a>
    </li>
  </div>
  `,
  props:['ids'],
  ready: function() {
    this.ids = JSON.parse(localStorage.getItem("ids"));
  }
})

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
  '/assembler/history': {
    component: AssemblerHistory
  },
  '/about': {
    component: About
  },
  '/login': {
    component: Login
  }
})

router.start(App, '#app')
