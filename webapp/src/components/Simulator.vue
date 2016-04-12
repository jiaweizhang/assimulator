<template>
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
</template>

<script>
  import auth from '../auth'

  export default {

    data() {
      return {
        asm: '',
        states: ''
      }
    },

    methods: {
      simulate() {
        this.$http
        .post('api/simulate', this.asm, (data) => {
          console.log(response);
          this.states = response.data;
        }, { 
          headers: {
            'Content-Type': 'test/plain',
            'Authorization': auth.getAuthHeader()
          }
        })
        .error((err) => console.log(err))
      }
    },

    route: {
      canActivate() {
        return auth.user.authenticated
      }
    }

  }
</script>
