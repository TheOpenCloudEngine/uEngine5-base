<template>

  <v-dialog v-model="dialog" width="600px">
    <v-btn primary dark slot="activator">Profile</v-btn>
    <v-card>
      <v-card-title>
        <span class="headline">User Profile</span>
      </v-card-title>
      <v-card-text>
        <v-flex xs12>
          <vue-img-inputer
            :maxSize='5120'
            placeholder="프로필 사진"
            accept="image/*"
            theme="material"
            size="large"
            :onChange="onFileChange"
          >
          </vue-img-inputer>
        </v-flex>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn class="blue--text darken-1" flat @click.native="dialog = false">Close</v-btn>
        <v-btn class="blue--text darken-1" flat @click.native="upload">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

</template>


<script>
  export default {
    props: {
      iam: Object,
    },

    watch: {
      taskId: function (val) {
        this.load();
      }
    },

    data: function () {
      return {
        dialog: false,
        file: null
      }
    },

    methods: {
      onFileChange: function (file, fileName) {
        this.file = file;
      },
      upload: function () {
        var me = this;
        this.iam.createUserAvatarByFormData(me.file, me.file.type, null, localStorage['username'])
          .done(function () {
            me.$root.$children[0].success('사진이 업로드 되었습니다.');
            me.dialog = false;
          })
          .fail(function () {
            me.$root.$children[0].error('사진을 업로드할 수 없습니다.');
          })
        ;
      }
    }
  }
</script>

