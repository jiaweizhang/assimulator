<template>
  <nav class="navbar navbar-default">
    <div class="container">
      <div class="navbar-header">
        <li><a class="navbar-brand" v-link="{path: '/'}">ADS</a></li>
      </div>
      <ul class="nav navbar-nav">
        <li><a v-link="{path: '/assembler'}" v-if="user.authenticated">Assembler</a></li>
        <li><a v-link="{path: '/disassembler'}" v-if="user.authenticated">Disassembler</a></li>
        <li><a v-link="{path: '/simulator'}" v-if="user.authenticated">Simulator</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a class="dropdown-toggle" data-toggle="dropdown" href="#">History
            <span class="caret"></span></a>
            <ul class="dropdown-menu">
              <li><a v-link="{path: '/assembler/history'}" v-if="user.authenticated">Assembler</a></li>
              <li><a href="/disassembler/history">Disassembler</a></li>
              <li><a href="/simulator">Simulator</a></li> 
            </ul>
          </li>
          <li><a v-link="{path: '/about'}">About</a></li>
          <li><a v-link="{path: '/login'}" v-if="!user.authenticated">Login</a></li>
          <li><a v-link="{path: '/signup'}" v-if="!user.authenticated">Sign Up</a></li>
          <li><a v-link="'login'" v-if="user.authenticated" @click="logout()">Logout</a></li>
        </ul>
      </div>
    </nav>
  <div class="container">
    <router-view></router-view>
  </div>
  <div class="footer">
    <div class="container text-center">
      <p>
        <a href="https://github.com/jiaweizhang/assembler-disassembler-simulator" target="_blank"><i class="fa fa-github fa-3x"></i></a>
      </p>

      <p>&copy; Jiawei Zhang, 2016. All rights reserved.</p>
    </div>
  </div>
</template>

<script>
import auth from '../auth'

export default {
  data() {
    return {
      user: auth.user
    }
  },

  methods: {

    logout() {
      auth.logout()
    }
  }
}
</script>