var exports = module.exports = {

  requireGuest (to, from, next) {
    // will stop the routing if isAuthenticated
    next(!exports.isAuthenticated())
  },

  requireUser (to, from, next) {
    //isAuthenticated ? continue route
    //if not ? will login and come back
    next(exports.isAuthenticated() ? true : {
        path: '/login',
        query: {
          redirect: to.fullPath
        }
      })
  },
  isAuthenticated (){
    return true;
  }
};
