import {router} from '../index'

const API_URL = '/'
const LOGIN_URL = API_URL + 'api/login'
const SIGNUP_URL = API_URL + 'api/signup'

export default {

  user: {
    authenticated: false
  },

  login(context, creds, redirect) {
    context.$http.post(LOGIN_URL, creds, (data) => {
      localStorage.setItem('id_token', data.id_token)
      console.log(data.id_token);

      if (data.id_token) {
        this.user.authenticated = true

        if(redirect) {
          router.go(redirect)        
        }
      } else {
        context.error = "Invalid username or password";
      }

    }).error((err) => {
      context.error = err
    })
  },

  signup(context, creds, redirect) {
    context.$http.post(SIGNUP_URL, creds, (data) => {
      localStorage.setItem('id_token', data.id_token)
      console.log(data.id_token);

      if (data.id_token) {
        this.user.authenticated = true

        if(redirect) {
          router.go(redirect)        
        }
      } else {
        context.error = "Username taken";
      }

    }).error((err) => {
      context.error = err
    })
  },

  logout() {
    localStorage.removeItem('id_token')
    this.user.authenticated = false
  },

  checkAuth() {
    var jwt = localStorage.getItem('id_token')
    if(jwt) {
      this.user.authenticated = true
    }
    else {
      this.user.authenticated = false      
    }
  },


  getAuthHeader() {
    return {
      'Authorization': 'Bearer ' + localStorage.getItem('id_token')
    }
  }
}
