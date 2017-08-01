Vue.component('service-locator', {
    template: "",
    props: {
        host: String,
        path: String
    },

    methods: {

        getServiceHost: function () {
            if (this.serviceLocator) {
                if (this.serviceLocator.host) {
                    return this.serviceLocator.host;
                } else if (this.$root.$refs[this.serviceLocator]) {
                    return this.$root.$refs[this.serviceLocator].host;
                } else {
                    return this.serviceLocator;
                }

            } else {
                return "http://127.0.0.1:8080"
            }
        },

        invoke: function (context) {

            var queryString = "";
            if(context.query){
                queryString = "?";

                for(key in context.query){
                    queryString += (key + "=" + context.query[key])
                }

            }

            xhr.open(context.method ? context.method : 'GET', this.getServiceHost() + "/" + this.path + queryString);
            xhr.setRequestHeader("access_token", localStorage['access_token']);
            xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

            xhr.onload = function () {

                var data = JSON.parse(xhr.responseText);
                context.success(data);
            }
            xhr.send(context.payload ? JSON.stringify(context.payload) : null);
        }
    }
});
