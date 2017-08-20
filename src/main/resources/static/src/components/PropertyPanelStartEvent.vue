<template>

  <form novalidate @submit.stop.prevent="submit">
    <md-input-container>
      <label>액티비티 ID</label>
      <md-input v-model="data.tracingTag"></md-input>
    </md-input-container>
    <md-input-container>
      <label>액티비티 명</label>
      <md-input v-model="data.name.text"></md-input>
    </md-input-container>
  </form>

</template>


<script>
  export default {
    props: {
      data: Object,
    },

    created: function(){
      if(!data.name) data.name = {text: ''};
      if(!data.description) data.description = {text: ''};

    }
  }
</script>
