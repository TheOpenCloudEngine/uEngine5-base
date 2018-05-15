<template>
  <md-select>
    <md-option v-for="(value, index) in fieldDescriptor.values" value="value">{{fieldDescriptor.options[index] ? fieldDescriptor.options[index] : value}}</md-option>
  </md-select>
</template>

<script>

  export default {
    name: 'object-form-select',

    props: {
      fieldDescriptor: Object
    }
  }

</script>

