<template>

</template>


<script>
    export default {
        name: 'ServiceLocator',

        props: {
            host: String,
            path: String
        },

        methods: {

            getServiceHost: function () {
                if (this.host) {
                    return this.host;
                } else {
                    return "http://127.0.0.1:8080"
                }
            },

            invoke: function (context) {

              var path = context.path ? context.path : this.path;

              var queryString = "";
              if (context.query) {
                  queryString = "?";

                  for (var key in context.query) {
                      queryString += (key + "=" + context.query[key])
                  }

              }

              var xhr = new XMLHttpRequest()


              xhr.open(context.method ? context.method : 'GET', this.getServiceHost() + "/" + path + queryString);
              xhr.setRequestHeader("access_token", localStorage['access_token']);
              xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

              xhr.onload = function () {

                  var data = JSON.parse(xhr.responseText);
                  context.success(data);
              }
              xhr.send(context.data ? JSON.stringify(context.data) : null);
            }
        }
    }

</script>
