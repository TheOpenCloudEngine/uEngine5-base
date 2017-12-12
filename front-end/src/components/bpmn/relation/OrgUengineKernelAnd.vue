<template>

  <ul>
    <div v-for="childCondition in data.conditionsVt">

      <component :is="childCondition._type.replace('.', '-')" :data="childCondition"></component>

    </div>
  </ul>


</template>

<script>

  export default {
      name: 'org-uengine-kernel-And',

      props: {

          data: Object
      }

  }

</script>
