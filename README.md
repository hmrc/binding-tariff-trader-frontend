
# Binding Tariff Trader Front End

The Front-end microrservice for the BTI Application journey on GOV.UK


### Running

##### To run this Service you will need:

1) [Service Manager](https://github.com/hmrc/service-manager) Installed
2) [SBT](https://www.scala-sbt.org) Version `>0.13.13` Installed

##### Starting dependencies:

1) Start Mongo `sm --start MONGO`
2) Start Assets Frontend Using `sm --start ASSETS_FRONTEND -r 3.2.2`
3) Start [Binding Tariff Classification](https://github.com/hmrc/binding-tariff-classification) Using `sm --start BINDING_TARIFF_CLASSIFICATION -r`
4) Start Auth Using `sm --start AUTH -r`
5) Start Auth Login Stub `sm --start AUTH_LOGIN_STUB -r`
6) Start Auth Login API `sm --start AUTH_LOGIN_API -r`
7) Start Auth Identity verification `sm --start IDENTITY_VERIFICATION -r`
8) Start User Details `sm --start USER_DETAILS -r`
9) Start Pdf Generator Service `sm --start PDF_GENERATOR_SERVICE -r` (Requires first installing dependencies - see [below](#pdf-generator-service))
10) Start Feedback Frontend `sm --start FEEDBACK_FRONTEND -r`
11) Start Frontend Template Provider `sm --start FRONTEND_TEMPLATE_PROVIDER -r`
12) Start EMAIL Service `sm --start EMAIL -r && sm --start HMRC_TEMPLATE_RENDERER -r && sm --start MAILGUN_STUB -r`



##### Running With SBT

Run `sbt run` to boot the app

Go to http://localhost:9000/advance-tariff-application/

You will be redirected to the Auth Stub.

##### Running with Service Manager

This application runs on port 9582

Run `sm --start BINDING_TARIFF_TRADER_FRONTEND -r`

Go to http://localhost:9582/advance-tariff-application/

You will be redirected to the Auth Stub.

##### Authentication

The service uses the [HMRC Auth Client](https://github.com/hmrc/auth-client) for authentication with Gov Gateway as the provider. In non production environments you will be redirected to the Auth Stub, to authenticate you need an enrolment with properties:

Key: `HMRC-CUS-ORG`

Identifier name: `EORINumber`

Identifier value: any string or eori

### PDF Generator Service
This service requires the installation of some dependencies before it can be run using Service Manager.  See [Pdf Generator Service](https://github.com/hmrc/pdf-generator-service).

Running PDF Generator Service locally on Mac OSX (currently) requires running an older version.  

Run `sm --start PDF_GENERATOR_SERVICE -r 1.20.0`

### Testing

Run `./run_all_tests.sh`
or `sbt test it:test`

### Changes

This project uses [Scaffold](https://github.com/hmrc/hmrc-frontend-scaffold.g8) to create its pages.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
