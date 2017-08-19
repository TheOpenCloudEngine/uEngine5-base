var template;
{
    var xhr = new XMLHttpRequest();
    xhr.open('GET', "http://rawgit.com/TheOpenCloudEngine/metaworks4/master/src/main/resources/static/iam-login.html", false);
    xhr.onload = function () {
        template = xhr.responseText
    }
    xhr.send();
}


Vue.component('iam-login', {
    template: template,
    props: {
        iamServer: String,
        id: String,
        password: String,
        listener: Object,
    },

    created: function(){
        if(!this.id) this.id = localStorage["iam-login.id"];
        if(!this.password) this.password = localStorage["iam-login.password"];
    },

    methods: {
       login: function (){
           localStorage["iam-login.id"] = this.id;
           localStorage["iam-login.password"] = this.password;

           var me = this;

           var iam = new IAM(this.iamServer);
           iam.setDefaultClient('e74a9505-a811-407f-b4f6-129b7af1c703','109cf590-ac67-4b8c-912a-913373ada046');
           iam.passwordCredentialsLogin(this.id,this.password,'uEngineSubscriptions/subscriptionsapi','JWT')
               .done(function(response){
                   console.log(response);

                   localStorage['access_token'] = response['access_token'];
                   localStorage['username'] = me.id;

                   var tenant = me.id.split("@")[1];
                   tenant = tenant.split(".")[0];
                   localStorage['tenant'] = tenant;

                   if(me.listener){
                       me.listener.onLoggedIn();
                   }
               });
        }

    }

})

