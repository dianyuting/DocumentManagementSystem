import { createStore } from 'vuex'

export default createStore({
  state: {
    friendMid:null,
    user: localStorage.getItem("user") ? localStorage.getItem("user") : null,
    token: localStorage.getItem("token") ? localStorage.getItem("token") : null,
  },
  getters: {
    getToken(state){
      return state.token || localStorage.getItem("token") || "";
    },
    getUser(state){
      return state.user || localStorage.getItem("user") || "";
    },
  },
  mutations: {
    setUser(state, user){
      state.user = JSON.stringify(user)
      localStorage.setItem('user', JSON.stringify(user))
    },
    setToken(state, token){
      localStorage.setItem('token', token)
      state.token = token
    },
    setFriendMid(state,id){
      state.friendMid = id;
    },
    logout(state){
      localStorage.removeItem('token')
      state.token = null
      localStorage.removeItem('user')
      state.user = null
    }
  },
  actions: {
  },
  modules: {
  }
})
