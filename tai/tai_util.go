package tai

import (
	"github.com/ethereum/go-ethereum/log"
	"net/http"
	"io/ioutil"
	"encoding/json"
)

func ListFromUrl(url string) []string{
	resp, err := http.Get(url)
	if err != nil {
		return nil

	}
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil

	}

	list := []string{}
	if err := json.Unmarshal(body, &list); err != nil {
		log.Error("parse http response failed", "err:", err)
		return nil
	}

	return list
}

func ListFromFile(file_path string) []string {
	body, err := ioutil.ReadFile(file_path)
	if err != nil {
		log.Error("read file failed:" + file_path, "err:", err)
		return nil
	}

	list := []string{}
	if err := json.Unmarshal(body, &list); err != nil {
		log.Error("parse json failed:" + file_path, "err:", err)
		return nil
	}

	return list
}