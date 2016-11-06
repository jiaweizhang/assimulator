<template>
  <div>
    <div class="wrap">
      <nav class="navbar navbar-default">
        <div class="container">
          <div class="navbar-header">
            <router-link class="navbar-brand" to="/">ADS</router-link>
          </div>
          <ul class="nav navbar-nav">
            <li>
              <router-link to="/assembler">Assembler</router-link>
            </li>
            <li>
              <router-link to="/simulator">Simulator</router-link>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li>
              <router-link to="/about">About</router-link>
            </li>
          </ul>
        </div>
      </nav>
      <div class="container">
        <router-view></router-view>
      </div>
    </div>
    <div class="footer">
      <div class="container text-center">
        <div>
          <a href="https://github.com/jiaweizhang/assembler-disassembler-simulator" target="_blank"><i
            class="fa fa-github fa-3x"></i></a>
        </div>
        <p>&copy; Jiawei Zhang, 2016. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>


<script>

export default {
}




</script>
