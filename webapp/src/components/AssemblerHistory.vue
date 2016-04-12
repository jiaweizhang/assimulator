
<template>
  <div>
    <li v-for="id in ids">
      <a class="btn btn-primary" href="/files/assembler/{{id}}/file.asm" download>Download Asm</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/imem.mif" download>Download ImemMif</a>
      <a class="btn btn-primary" href="/files/assembler/{{id}}/dmem.mif" download>Download DmemMif</a>
    </li>
  </div>
</template>

<script>
  import auth from '../auth'

  export default {

    data() {
      return {
        ids: []
      }
    },

    ready() {
      this.ids = JSON.parse(localStorage.getItem("ids"));
    },

    route: {
      canActivate() {
        return auth.user.authenticated
      }
    }

  }
</script>
