package test

import (
	"testing"
	"os/exec"
	"encoding/json"
	"github.com/open-falcon/common/model"
	"bytes"
)

func TestCatalinaMonitorUnmarshal(t *testing.T) {
	out, err := exec.Command("groovy", "-cp", "..\\src", "CatalinaMonitorTest.groovy", "dump").Output()
	if err != nil {
		t.Error(err)
	}

	/*
	 first line of out is json string
	 second line is junit out put, like "Unit 4 Runner, Tests: 11, Failures: 0, Time: 1869"
	 so we need get rid of it
	*/
	json_bytes := bytes.Split(out, []byte{'\n'})[0]
	var metrics []*model.MetricValue
	err = json.Unmarshal(json_bytes, &metrics)
	if err != nil {
		t.Error(err)
	}
	t.Log(metrics)
}
