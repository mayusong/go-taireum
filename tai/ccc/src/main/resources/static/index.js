var Main = {
    methods: {
        get_host:function( ){
            return location.protocol + "//" + location.host + "/";
        },
        handleClick: function (row) {
            console.log(row);
        },
        updateTableData: function () {
            var newdata = [{
                CompanyId: '1',
                CompanyName: '测试公司1',
                email: 'test1@example.com',
                enode: 'enode://xxx1',
                stat: '1'
            }];
            console.log(newdata);

            this.tableData = newdata;
        },
        ShowAllCompany: function () {
            this.updateTableData()
        },
        NewAccount: function () {
            var url = this.get_host() + "/api/chain/newAccount";
            var postData = {"password": this.NewAccountPassword};

            this.$http.post(url, JSON.stringify(postData)).then(response => {
                console.log(response.body);
                this.NewAccountResult = response.body;
            }, err => {

            });
        },
        initTai: function () {
            var url = this.get_host() + "/api/chain/initTai";
            this.$http.get(url).then(response => {
                console.log(response.body);
                this.initTaiResult = response.body;
            }, err => {

            });
        },
        addGenesisJson: function () {
            var url = this.get_host() + "/api/chain/addGenesis";
            this.$http.post(url, this.genesisJson).then(response => {
                console.log(response.body);
                this.genesisJsonResult = response.body;
            }, err => {

            })
        },
        delGenesisJson: function () {
            var url = this.get_host() + "/api/chain/delGenesis";
            this.$http.get(url).then(response => {
                console.log(response.body);
                this.genesisJsonResult = response.body;
            }, err => {

            });
        },
        startTai: function () {
            if (this.unlockAccount === "" || this.unlockAccountPassword === ""
                ||this.port === "" || this.rpcPort === "") {
                alert("账户地址和密码, port, rpcport不能为空");
                return;
            }
            var url = this.get_host() + "/api/chain/startTai";
            var postData = {
                unlockAccount: this.unlockAccount,
                password: this.unlockAccountPassword,
                port: this.port,
                rpcPort: this.rpcPort
            }
            this.$http.post(url, JSON.stringify(postData)).then(response => {
                this.startTaiResult = "startTai:" + response.body;
            }, err => {

            });
        },
        stopTai: function () {
            var url = this.get_host() + "/api/chain/stopTai";
            this.$http.get(url).then(response => {
                this.startTaiResult = "stopTai:" + response.body;
            }, err => {

            });
        },
        addMiner: function () {
            var url = this.get_host() + "/api/addMiner";
            this.$http.post(url, this.minerAddress).then(res => {
                this.addResult = res.body;
            },err=>{

            });
        },
        addEnode: function () {
            var url = this.get_host() + "/api/addEnode";
            this.$http.post(url, this.enodeAddress).then(res => {
                this.addResult = res.body;
            },err=>{

            });
        },
        addContract: function () {
            var url = this.get_host() + "/api/addContract";
            this.$http.post(url, this.contractAddress).then(res => {
                this.addResult = res.body;
            },err=>{

            });
        },
        getMiner: function () {
            var url = this.get_host() + "/api/getMiner";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        getEnode: function () {
            var url = this.get_host() + "/api/getEnode";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        getContract: function () {
            var url = this.get_host() + "/api/getContract";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        delMiner: function () {
            var url = this.get_host() + "/api/delMiner";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        delEnode: function () {
            var url = this.get_host() + "/api/delEnode";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        delContract: function () {
            var url = this.get_host() + "/api/delContract";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, err => {

            });
        },
        addPeer: function () {
            var url = this.get_host() + "/api/admin/addPeer";
            this.$http.post(url, this.addPeerAddress).then(res => {
                this.enodeResult = res.body;
            },err=>{

            });
        },
        getOwnenode: function () {
            var url = this.get_host() + "/api/admin/getOwnenode";
            this.$http.get(url).then(res => {
                this.enodeResult = res.bodyText;
            }, err => {

            });
        },
        initAllInOne: function () {
            //newAccount
            //addGenesis.json
            //initTai
            //addMiner
            //startTai
            //addEnode
            //getOwnEnode
        },
        addPeerAllInOne: function () {
            
        }
    },
    data: function () {
        return {
            genesisJson: "",
            tableData: [],
            NewAccountPassword: "",
            NewAccountResult: "",
            initTaiResult: "",
            genesisJsonResult: "",
            unlockAccount: "",
            unlockAccountPassword: "",
            port:"",
            rpcPort:"",
            getResult: "",
            startTaiResult: "",
            addResult: "",
            minerAddress:"",
            enodeAddress:"",
            contractAddress:"",
            addPeerAddress:"",
            enodeResult:"",
            enodeResult_AllInOne:"",
            addPeer_AllInOne:""
        }
    }
};

var Ctor = Vue.extend(Main);
new Ctor().$mount('#app');
