package test

import (
	"testing"
	"io/ioutil"
	"encoding/json"
	"github.com/open-falcon/common/model"
)

func TestDataUnmarshal(t *testing.T) {
	b, err := ioutil.ReadFile("data.json")
	var metrics []*model.MetricValue
	err = json.Unmarshal(b, &metrics)
	if err != nil {
		t.Error(err)
	}
	t.Log(metrics)
}