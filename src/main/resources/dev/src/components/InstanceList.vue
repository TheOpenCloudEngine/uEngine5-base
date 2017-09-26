<template>
  <!--<v-data-table-->
    <!--v-bind:headers="headers"-->
    <!--:items="items"-->
    <!--hide-actions-->
    <!--class="elevation-1"-->
  <!--&gt;-->
    <!--<template slot="items" scope="props">-->
      <!--<td>{{ props.item.instId }}</td>-->
      <!--<td class="text-xs-right">-->
        <!--<v-btn outline class="indigo&#45;&#45;text" v-on:click="move(props.item.instId)">진행상황 보기</v-btn>-->
      <!--</td>-->
      <!--<td class="text-xs-right">{{ props.item.defName }}</td>-->
      <!--<td class="text-xs-right">{{ props.item.defId }}</td>-->
      <!--<td class="text-xs-right">{{ props.item.isSubProcess }}</td>-->
      <!--<td class="text-xs-right">{{ props.item.startedDate }}</td>-->
      <!--<td class="text-xs-right">{{ props.item.finishedDate }}</td>-->
    <!--</template>-->
  <!--</v-data-table>-->
</template>
<script>
  export default {
    data () {
      return {
        headers: [
          {
            text: 'ID',
            align: 'left',
            sortable: false,
            value: 'instId'
          },
          {text: '모니터링', value: ''},
          {text: '프로세스 이름', value: 'defName'},
          {text: '프로세스 아이디', value: 'defId'},
          {text: '서브 프로세스', value: 'isSubProcess'},
          {text: '시작일', value: 'startedDate'},
          {text: '종료일', value: 'finishedDate'}
        ],
        items: [
          {
            instId: 1,
            defName: 1,
            defId: 1,
            isSubProcess: 1,
            startedDate: 1,
            finishedDate: 1
          }
        ]
      }
    },
    mounted() {
      var me = this;
      this.$root.codi('instances').get()
        .then(function (response) {
          var instances = [];
          if (response.data._embedded && response.data._embedded.instances && response.data._embedded.instances.length) {
            $.each(response.data._embedded.instances, function (i, instance) {
              let split = instance._links.self.href.split('/');
              instance['instId'] = split[split.length - 1];
              instances.push(instance);
            });
            me.items = instances;
          }
        })
    },
    methods: {
      move: function (instId) {
        this.$router.push({
          path: 'instance/' + instId
        })
      }
    }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .mt-100 {
    margin-top: 50px;
  }
</style>
