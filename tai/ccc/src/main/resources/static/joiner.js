let Joiner = {
    methods: {
        get_host: function () {
            return location.protocol + "//" + location.host;
        },
        initMemberAllInOne: function () {
            let url = this.get_host() + "/api/chain/initMemberAllInOne";
            let postData = {
                genesisJson: this.genesisJson,
                creator_account: this.creator_account,
                creator_enode: this.creator_enode,
                contract_address: this.contract_address
            };

            this.$http.post(url, JSON.stringify(postData)).then(res => {
                this.JoinAllInOneResult = res.bodyText;
            }, err => {

            });

        }
    },
    data: function () {
        return {
            JoinAllInOneResult:"",
            genesisJson:"",
            creator_account:"",
            creator_enode:"",
            contract_address:""
        };
    }
};
let Ctor = Vue.extend(Joiner);
new Ctor().$mount('#joiner');
