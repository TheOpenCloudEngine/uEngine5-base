<template>
    <md-card>

        <md-card-header>
            <div class="md-title">Login</div>
            <div class="md-subhead">Please Login</div>
        </md-card-header>
        <md-card-content>

            <form novalidate @submit.stop.prevent="submit">
                <md-input-container>
                    <label>E-Mail</label>
                    <md-input v-model="id"></md-input>
                </md-input-container>

                <md-input-container md-has-password>
                    <label>Password</label>
                    <md-input v-model="password" type="password"></md-input>
                </md-input-container>
            </form>
        </md-card-content>


        <md-card-actions>
            <md-button class="md-primary" @click.native="login()">로그인</md-button>
        </md-card-actions>

    </md-card>
</template>

<script>
export default {
    name: 'IAMLogin',
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
                    localStorage['iam.photo_url'] = response['photo_url'];

                    var tenant = me.id.split("@")[1];
                    tenant = tenant.split(".")[0];
                    localStorage['tenant'] = tenant;

                    if(me.listener){
                        me.listener.onLoggedIn();
                    }
                });
        }

    }


}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
