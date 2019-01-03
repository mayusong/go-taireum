package tai

import "github.com/ethereum/go-ethereum/log"

var EnodeConfigUrl = "" //"http://localhost:8080/api/getEnode"
var RpcConfigUrl = "" //"http://localhost:8080/api/getRpc"
var AuthoritiesConfigUrl = "" //"http://localhost:8080/api/getMiner"
var ContractConfigUrl = "" //"http://localhost:8080/api/getContract"
const configName = "config.json"

func GetEnodeConfigUrl() string {
	if EnodeConfigUrl == "" {
		config := JsonFromFile(configName)
		if config != nil {
			EnodeUrl := config["EnodeUrl"].(string)
			if EnodeUrl != "" {
				EnodeConfigUrl = EnodeUrl
			}
		}
	}
	log.Error(EnodeConfigUrl)
	return EnodeConfigUrl
}
func GetRpcConfigUrl() string {
	if RpcConfigUrl == "" {
		config := JsonFromFile(configName)
		if config != nil {
			RpcUrl := config["RpcUrl"].(string)
			if RpcUrl != "" {
				RpcConfigUrl = RpcUrl
			}
		}
	}
	log.Error(RpcConfigUrl)
	return RpcConfigUrl
}

func GetAuthoritiesConfigUrl() string {
	if AuthoritiesConfigUrl == "" {
		config := JsonFromFile(configName)
		if config != nil {
			MinerUrl := config["MinerUrl"].(string)
			if MinerUrl != "" {
				AuthoritiesConfigUrl = MinerUrl
			}
		}
	}
	log.Error(AuthoritiesConfigUrl)
	return AuthoritiesConfigUrl
}

func GetContractConfigUrl() string {
	if ContractConfigUrl == "" {
		config := JsonFromFile(configName)
		if config != nil {
			ContractUrl := config["ContractUrl"].(string)
			if ContractUrl != "" {
				ContractConfigUrl = ContractUrl
			}
		}
	}
	log.Error(ContractConfigUrl)
	return ContractConfigUrl
}
