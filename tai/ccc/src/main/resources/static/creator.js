let Creator = {
    methods: {
        get_host: function () {
            return location.protocol + "//" + location.host;
        },
        initAllInOne: function () {
            let url = this.get_host() + "/api/chain/initAllInOne";
            let postData = {
                companyName: this.companyName,
                email: this.email,
                remark: this.remark
            };
            this.$http.post(url, JSON.stringify(postData)).then(res => {
                this.AllInOneResult = res.bodyText;
            }, err => {

            });
        }
    },
    data: function () {
        return {
            AllInOneResult: "",
            companyName: "",
            email: "",
            remark: ""
        };
    }
};
let Ctor = Vue.extend(Creator);
new Ctor().$mount('#creator');
