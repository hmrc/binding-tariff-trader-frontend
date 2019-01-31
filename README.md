
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


##### Running With SBT

Run `sbt run` to boot the app

Go to http://localhost:9000/binding-tariff-application/
You will be redirected to the Auth Stub. Make sure you pick `Affinity=Organisation` and Submit.

##### Running with Service Manager

Run `sm --start BINDING_TARIFF_TRADER_FRONTEND -r`

Go to http://localhost:9582/binding-tariff-application/
You will be redirected to the Auth Stub. Make sure you pick `Affinity=Organisation` and Submit.

### Testing

Run `./run_all_tests.sh`
or `sbt test it:test`

### Changes

This project uses [Scaffold](https://github.com/hmrc/hmrc-frontend-scaffold.g8) to create its pages.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
