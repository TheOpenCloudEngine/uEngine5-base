const Essence = {
  install (Vue, opts = {}) {

    const files = require.context('.', true, /\.vue$/);
    const EssenceComponents = {}
    files.keys().forEach((key) => {
      if (key === './index.js') {
        return;
      }
      EssenceComponents[key.replace(/(\.\/|\.vue)/g, '')] = files(key);
    });

    if(Vue._components==null) Vue._components = {};

    for (var key in EssenceComponents) {
      Vue.component(EssenceComponents[key].name, EssenceComponents[key]);
      Vue._components[EssenceComponents[key].name] = EssenceComponents[key];
    }

    //bpmn 컴포넌트 검색용
    Vue.essenceComponents = EssenceComponents;

    //윈도우 전역변수 등록 (다른 인스톨 플러그인에서 거진 하긴 해주지만 혹시 모르니...)
    if (window && !window.Vue) {
      window.Vue = Vue;
    }
  }
}

export default Essence


