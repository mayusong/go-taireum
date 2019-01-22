let Joiner = {
    methods: {
        get_host: function () {
            return location.protocol + "//" + location.host;
        }
    },
    data: function () {
        return {
            JoinAllInOneResult:""
        };
    }
};
let Ctor = Vue.extend(Joiner);
new Ctor().$mount('#joiner');
