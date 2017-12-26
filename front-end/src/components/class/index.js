const ClassVue = {

  install (Vue, opts = {}) {

    const files = require.context('.', true, /\.vue$/);
    const Components = {}
    files.keys().forEach((key) => {
      if (key === './index.js') {
        return;
      }
      Components[key.replace(/(\.\/|\.vue)/g, '')] = files(key);
    });
    for (var key in Components) {
      Vue.component(Components[key].name, Components[key]);
    }

    //bpmn 컴포넌트 검색용
    Vue.classComponents = Components;

    //bpmn 용 이벤트 버스
    Vue.classBus = new Vue();

    //bpmn 활성 컴포넌트 저장소
    Vue.classLiveComponents = {};

    //윈도우 전역변수 등록 (다른 인스톨 플러그인에서 거진 하긴 해주지만 혹시 모르니...)
    if (window && !window.Vue) {
      window.Vue = Vue;
    }
  }
}

export default ClassVue


