package test

import (
	"os"
	"path"
	"bytes"
	"testing"
	"strings"
	"os/exec"
	"path/filepath"
	"encoding/json"
	"github.com/open-falcon/common/model"
)

func TestCatalinaMonitorUnmarshal(t *testing.T) {
	/*
	 go1.8 go test use a temp dir for testing
	 wo shold set TMPDIR environment variable before running "go test".
	 the working path will be like $TMPDIR/go-build879888304/command-line-arguments/_test
	*/
	
	// get test working dir
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	if err != nil {
		t.Error(err)
	}
	slice := strings.Split(dir, "/")
	length := len(slice)
	tmpdir := strings.Join(slice[0: length - 3], "/")
	// t.Log(tmpdir)
	
	out, err := exec.Command("groovy", "-cp", path.Join(tmpdir, "src"), path.Join(tmpdir, "test", "CatalinaMonitorTest.groovy"), "dump").Output()
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
