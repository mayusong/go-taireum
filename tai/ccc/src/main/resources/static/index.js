var Main = {
    methods: {
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
            var url = "http://localhost:8080/api/chain/newAccount";
            var postData = {"password": this.NewAccountPassword};

            this.$http.post(url, JSON.stringify(postData)).then(response => {
                console.log(response.body);
                this.NewAccountResult = response.body;
            }, response => {

            });
        },
        initTai: function () {
            var url = "http://localhost:8080/api/chain/initTai";
            this.$http.get(url).then(response => {
                console.log(response.body);
                this.initTaiResult = response.body;
            }, response => {

            });
        },
        addGenesisJson: function () {
            var url = "http://localhost:8080/api/chain/addGenesis";
            this.$http.post(url, this.genesisJson).then(response => {
                console.log(response.body);
                this.genesisJsonResult = response.body;
            }, response => {

            })
        },
        delGenesisJson: function () {
            var url = "http://localhost:8080/api/chain/delGenesis";
            this.$http.get(url).then(response => {
                console.log(response.body);
                this.genesisJsonResult = response.body;
            }, response => {

            });
        },
        startTai: function () {
            if (this.unlockAccount === "" || this.unlockAccountPassword === "") {
                alert("账户地址和密码不能为空");
                return;
            }
            var url = "http://localhost:8080/api/chain/startTai";
            var postData = {
                unlockAccount: this.unlockAccount,
                password: this.unlockAccountPassword
            }
            this.$http.post(url, JSON.stringify(postData)).then(response => {
                this.startTaiResult = "startTai:" + response.body;
            }, response => {

            });
        },
        stopTai: function () {
            var url = "http://localhost:8080/api/chain/stopTai";
            this.$http.get(url).then(response => {
                this.startTaiResult = "stopTai:" + response.body;
            }, response => {

            });
        },
        addMiner: function () {
            var url = "http://localhost:8080/api/addMiner";
            this.$http.post(url, this.minerAddress).then(res => {
                this.addResult = res.body;
            },res=>{

            });
        },
        addEnode: function () {
            var url = "http://localhost:8080/api/addEnode";
            this.$http.post(url, this.enodeAddress).then(res => {
                this.addResult = res.body;
            },res=>{

            });
        },
        addContract: function () {
            var url = "http://localhost:8080/api/addContract";
            this.$http.post(url, this.contractAddress).then(res => {
                this.addResult = res.body;
            },res=>{

            });
        },
        getMiner: function () {
            var url = "http://localhost:8080/api/getMiner";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
        },
        getEnode: function () {
            var url = "http://localhost:8080/api/getEnode";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
        },
        getContract: function () {
            var url = "http://localhost:8080/api/getContract";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
        },
        delMiner: function () {
            var url = "http://localhost:8080/api/delMiner";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
        },
        delEnode: function () {
            var url = "http://localhost:8080/api/delEnode";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
        },
        delContract: function () {
            var url = "http://localhost:8080/api/delContract";
            this.$http.get(url).then(response => {
                this.getResult = response.body;
            }, response => {

            });
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
            getResult: "",
            startTaiResult: "",
            addResult: "",
            minerAddress:"",
            enodeAddress:"",
            contractAddress:""
        }
    }
};

var Ctor = Vue.extend(Main);
new Ctor().$mount('#app');

