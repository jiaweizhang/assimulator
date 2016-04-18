<template>
  <div class="row">
    <div class="col-sm-6">
      <div class='page-header'>
        <div class='btn-toolbar pull-right'>
          <div class='btn-group'>
            <button type="button" class="btn btn-primary center-block" v-on:click="assemble()">Assemble</button>
          </div>
        </div>
        <h2>Input (.asm)</h2>
      </div>

      <div id="editor"></div>
      
    <h3 v-show="processed" class="text-center">Assemble Errors</h3>
    <pre v-show="processed">{{errors}}</pre>
  </div>
  <div class="col-sm-6">
    <div class='page-header'>
      <div class='btn-toolbar pull-right'>
        <div class='btn-group'>
          <a v-show="processed" class="btn btn-primary" id="download-asm" download='file.asm' href="data:application/x-asm,">Download Asm</a>
          <a v-show="processed" class="btn btn-primary" id="download-imem" download='imem.mif' href="data:application/x-mif,">Download Imem</a>
          <a v-show="processed" class="btn btn-primary" id="download-dmem" download='dmem.mif' href="data:application/x-mif,">Download Dmem</a>
        </div>
      </div>
      <h2>Output (.mif)</h2>
    </div>

    <pre v-show="processed">{{imem}}</pre>
    <pre v-show="processed">{{dmem}}</pre>

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
        asm: 
        `.text
main: addi $1, $0, 1999
noop
halt
sw $r18, -640($r7)
bne $3, $2, middle
addi $6, $1, 65535
addi $7, $1, -65536
middle:
sub $8, $2, $1
and $9, $2, $1
j quit
dead: addi $r7, $r0, 0x0000DF00
quit:

.data
wow: .word 0x0000B504
mystring: .string ASDASDASDASDASDASD
var: .char Z
label: .char A
heapsize: .word 0x00000000
myheap: .word 0x00000000`,
        imem: '',
        dmem: '',
        errors: '',
        editor: {}
      }
    },

    ready: function() {
      this.editor = ace.edit("editor");
      this.editor.setTheme("ace/theme/github");
      //this.editor.getSession().setMode("ace/mode/javascript");
      this.editor.setValue(this.asm);
    },

    methods: {

      assemble() {
        //console.log(this.editor.getValue());
        this.$http
        .post('api/protected/assemble', this.editor.getValue(), (data) => {
          this.id = data.id;
          this.imem = data.imem;
          this.dmem = data.dmem;
          this.errors = data.errors;
          
          document.getElementById('download-asm').setAttribute('href', "data:application/x-asm," + encodeURI(this.asm));
          document.getElementById('download-imem').setAttribute('href', "data:application/x-mif," + encodeURI(this.imem));
          document.getElementById('download-dmem').setAttribute('href', "data:application/x-mif," + encodeURI(this.dmem));
          this.processed = true;
          console.log("finished processing");
        }, { 
          headers: [
          {'Content-Type': 'test/plain'},
          auth.getAuthHeader()
          ]
        })
        .error((err) => console.log(err))
      }
    },

    route: {
      canActivate() {
        //return auth.user.authenticated

        // can always activate because assembler is currently unlocked
        return true;
      }
    }

  }
</script>

