<template>
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
</template>

<script>
  import auth from '../auth'

  export default {

    data() {
      return {
        mif: '',
        asm: '',
        asmId: '',
        mifId: '',
      }
    },

    methods: {
      disassemble() {
        this.$http
        .post('api/disassemble', this.mif, (data) => {
          console.log(response);
          this.asm = data.asm;
          this.asmId = data.asmId;
          this.mifId = data.mifId;
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