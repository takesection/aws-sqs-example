aws-sqs-example
===============

```
$ sbt "; clean; assembly"
$ cd cloudformation
$ AWS_PROFILE=<YOUR PROFILE> sls deploy -s <STAGE> -r ap-northeast-1 -v
$ AWS_PROFILE=<YOUR PROFILE> sbt receiverApp/run
```
