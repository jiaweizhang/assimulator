<template>
  <div class="row">
    <div class="col-sm-6">
      <div class='page-header'>
        <div class='btn-toolbar pull-right'>
          <div class='btn-group'>
            <button type="button" class="btn btn-primary center-block" @click="clear()">Clear</button>
            <button type="button" class="btn btn-primary center-block" @click="assemble()">Assemble</button>
          </div>
        </div>
        <h2>Input (.asm)</h2>
      </div>

      <div id="editor"></div>

      <br/>
    </div>
    <div class="col-sm-6">
      <div class='page-header'>
        <h2>Output</h2>
      </div>

      <div>
        <div class='btn-toolbar' v-show="processed">
          <div class='btn-group'>
            <a class="btn btn-primary" id="download-asm" download='file.asm'
               href="data:application/x-asm,">Download Asm</a>
            <a class="btn btn-primary" id="download-imem" download='imem.mif'
               href="data:application/x-mif,">Download Imem</a>
            <a class="btn btn-primary" id="download-dmem" download='dmem.mif'
               href="data:application/x-mif,">Download Dmem</a>
          </div>
        </div></div>

      <div v-show="processed">
        <h3 class="text-center">Output Status</h3>
        <pre>{{message}}</pre>
        <h3 class="text-center">Assembly Errors</h3>
        <code v-for="error in errors">
          {{ error }}<br/>
        </code>
        <br/>
      </div>
    </div>
  </div>
</template>

<script>

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
sw $18, -640($7)
bne $3, $2, middle
addi $6, $1, 65535
addi $7, $1, -65536

middle:
sub $8, $2, $1
and $9, $2, $1
j quit
dead: addi $7, $0, 0x0000DF00
quit:
halt

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
        message: '',
        editor: {}
      }
    },

/*
ready deprecated: https://github.com/vuejs/vue/issues/2873
*/
    mounted: function() {
      this.editor = ace.edit("editor");
      this.editor.$blockScrolling = Infinity;
      this.editor.setTheme("ace/theme/github");
      //this.editor.getSession().setMode("ace/mode/javascript");
      this.editor.setValue(this.asm, -1);
    },

    methods: {

      assemble() {
        // generate object
        var postData = {
          instructions: this.editor.getValue().split("\n"),
          instructionGrammars: [],
          macroGrammars: []
        }

        this.$http.post('http://localhost:8080/api/v3/assemble', JSON.stringify(postData))
        .then((response) => {
          var body = response.body.body;
          // success
          this.imem = body.binary;
          this.dmem = body.dmem;
          this.errors = body.errors;
          this.message = response.body.message;

          document.getElementById('download-asm').setAttribute('href', "data:application/x-asm," + encodeURI(this.asm));
          document.getElementById('download-imem').setAttribute('href', "data:application/x-mif," + encodeURI(this.imem));
          document.getElementById('download-dmem').setAttribute('href', "data:application/x-mif," + encodeURI(this.dmem));
          this.processed = true;
        }, (response) => {
          // fail
          this.message = 'Server cannot be accessed';
        });
      },
      clear() {
        this.asm = '';
        this.editor.setValue(this.asm, -1);
      }
    }
  }

</script>
