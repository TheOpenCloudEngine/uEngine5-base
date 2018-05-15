<template>

    <md-switch v-model="value" @change="change">Yes</md-switch>

</template>

<script>

  export default {
    name: 'object-form-boolean',

    created(){
      if(!this.value)
        this.value = false;

      this.$emit("input", this.value);
    },

    props: {
      fieldDescriptor: Object,
      value: Object
    },

    methods: {

      onChange(value){

        this.$emit("input", this.value);

      }
    }
  }

</script>

