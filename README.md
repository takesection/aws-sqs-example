aws-sqs-example
===============

# Prepare

* Cognito UserPool

# Build

```
$ sbt "; clean; assembly"
```

# Settings

```
$ cd cloudformation
$ AWS_PROFILE=<YOUR PROFILE> sls deploy -s <STAGE> -r ap-northeast-1 --userpool=<YOUR USERPOOL Arn> -v
$ cd ../webapp
$ cp .env .env.development.local
```

webapp/.env.development.local を vim 等で編集します。

# Sender App

```
$ cd webapp
$ npm start
```

# Receiver App

```
$ AWS_PROFILE=<YOUR PROFILE> sbt receiverApp/run
```
