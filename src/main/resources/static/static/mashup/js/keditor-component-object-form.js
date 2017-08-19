(function ($) {
    var KEditor = $.keditor;
    var flog = KEditor.log;

    KEditor.components['object-form'] = {
        init: function (contentArea, container, component, keditor) {
            flog('init "photo" component', component);

            var componentContent = component.children('.keditor-component-content');
            var img = componentContent.find('img');

            img.css('display', 'inline-block');
        },

        getContent: function (component, keditor) {

            var id = component[0].id;
            var elem = keditor.vueInstances[id].$children[0];
            var tagName = elem.$options._componentTag;
            var propDefs = Vue.options.components[tagName].options.props;

            var html =
                '<' + tagName

            for(var propName in propDefs){

                var propDef = propDefs[propName];

                html+=
                    ' ' + propName + "='" + elem[propName] + "'";

            }

            html += '/>';


            return html;
        },

        settingEnabled: true,

        settingTitle: 'Object Form Settings',

        initSettingForm: function (form, keditor) {
            flog('initSettingForm "photo" component');

            var self = this;
            var options = keditor.options;

            var id = keditor.getSettingComponent()[0].id;
            var elem = keditor.vueInstances[id].$children[0];
            var tagName = elem.$options._componentTag;
            var propDefs = Vue.options.components[tagName].options.props;

            var html =
            '<form class="form-horizontal">';

            for(var propName in propDefs){

                var propDef = propDefs[propName];

                html+=
                '   <div class="form-group">' +
                '       <label for="photo-width" class="col-sm-12">'+ propName +'</label>' +
                '       <div class="col-sm-12">' +
                '           <input id="' + propName + '" class="form-control" />' +
                '       </div>' +
                '   </div>';

            }

            html += '</form>';

            form.append(html);

            for(var propName in propDefs){

                var propDef = propDefs[propName];

                var input = form.find('#' + propName);

                input.on('change', function (e) {
                    var propName = e.originalEvent.srcElement.id;
                    elem[propName] = e.originalEvent.srcElement.value;
                });
            }

            //
            // var data = form.find('#data');
            // data.on('change', function () {
            //     elem.data = data.val();
            // });


        },

        showSettingForm: function (form, component, keditor) {
            flog('showSettingForm "photo" component', component);

            var self = this;

        }
    };

})(jQuery);
