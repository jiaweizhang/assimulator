<template>
  <div class="row">
    <div class="col-sm-6">
      <h2 class="text-center">Input (.asm)</h2>
      <textarea class="center-block" cols="40" rows="20" v-model="asm"></textarea>
      <button type="button" class="btn btn-primary center-block" v-on:click="assemble()">Assemble</button>
      <h3 v-show="processed" class="text-center">Assemble Errors</h3>
      <pre v-show="processed">{{errors}}</pre>
    </div>
    <div class="col-sm-6">

      <h2 class="text-center">Output (.mif)</h2>
      <pre v-show="processed">{{imem}}</pre>
      <pre v-show="processed">{{dmem}}</pre>
      
      <a v-show="processed" class="btn btn-primary" id="download-asm" download='file.asm' href="data:application/x-asm,">Download Asm</a>
      <a v-show="processed" class="btn btn-primary" id="download-imem" download='imem.mif' href="data:application/x-mif,">Download Imem</a>
      <a v-show="processed" class="btn btn-primary" id="download-dmem" download='dmem.mif' href="data:application/x-mif,">Download Dmem</a>
    </div>
  </div>
</template>

<script>
  import auth from '../auth'

  export default {

    data() {
      return {
        processed: false,
        id: '',
        asm: '',
        imem: '',
        dmem: '',
        errors: ''
      }
    },

    methods: {

      assemble() {
        this.$http
        .post('api/assemble', this.asm, (data) => {
          this.id = data.id;
          this.imem = data.imem;
          this.dmem = data.dmem;
          this.errors = data.errors;
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
          document.getElementById('download-asm').setAttribute('href', "data:application/x-asm," + encodeURI(this.asm));
          document.getElementById('download-imem').setAttribute('href', "data:application/x-mif," + encodeURI(this.imem));
          document.getElementById('download-dmem').setAttribute('href', "data:application/x-mif," + encodeURI(this.dmem));
          this.processed = true;
          console.log("finished processing");
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

