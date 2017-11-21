<template>

  <md-select v-model="classType">
    <md-option v-for="entry in classTypes" :value="entry.className">{{entry.displayName}}</md-option>
  </md-select>

</template>


<script>

  export default{

    props: {
      data: String,
    },

    created: function(){
      if(!this.data){
        this.$emit('update:data', 'java.lang.String');
      }
    },

    watch: {
      classType: function(){
          this.$emit('update:data', this.classType);
      }
    },

    data:function(){

      return {
        classType: this.data ? this.data : 'java.lang.String',
        classTypes: [
          {
            displayName: '문자열',
            className: 'java.lang.String'
          },
          {
            displayName: '정수형',
            className: 'java.lang.Integer'
          },
          {
            displayName: '정수형(Long)',
            className: 'java.lang.Long'
          },
          {
            displayName: '정수형(long)',
            className: 'long'
          },
          {
            displayName: '실수형',
            className: 'java.lang.double'
          },
          {
            displayName: '예 아니오',
            className: 'java.lang.Boolean'
          },
          {
            displayName: '날짜',
            className: 'java.util.Date'
          },
        ]
      };
    },

  }

</script>
