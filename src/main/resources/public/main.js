var Vue = require('vue');
Vue.use(require('vue-resource'));

//window.Vue = Vue;

new Vue({
  el: '#app',
  data: {
    asm: ''
  },
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
        }, function (response) {
          // error callback
        })
      }
    }
  });
