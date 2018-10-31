
# Binding Tariff Trader Front End

The Front End for the BTI Application journey on GOV.UK


### Running

##### To run this Service you will need:

1) [Service Manager](https://github.com/hmrc/service-manager) Installed
2) [SBT](https://www.scala-sbt.org) Version `>0.13.13` Installed

##### Starting dependencies:

1) Start Assets Frontend Using `sm --start ASSETS_FRONTEND -r 3.2.2`
2) Start [Binding Tariff Classification](https://github.com/hmrc/binding-tariff-classification) Using `sm --start BINDING_TARIFF_CLASSIFICATION -f`
3) Start Auth Using `sm --start AUTH -f`
4) Start Auth Login Stub `sm --start AUTH_LOGIN_STUB -f`
5) Start Auth Login API `sm --start AUTH_LOGIN_API -f`
6) Start User Details `sm --start USER_DETAILS -f`

##### Running With SBT

Run `sbt run` to boot the app

Go to the [Start Page](http://localhost:9000/binding-tariff-trader-frontend/registeredAddressForEori).
You will be redirected to the Auth Stub. Make sure you pick `Affinity=Organisation` and Submit.

##### Running with Service Manager

Run `sm --start BINDING_TARIFF_TRADER_FRONTEND -r`

Go to the [Start Page](http://localhost:9582/binding-tariff-trader-frontend/registeredAddressForEori).
You will be redirected to the Auth Stub. Make sure you pick `Affinity=Organisation` and Submit.

### Testing

Run `sbt test`

### Changes

This project uses [Scaffold](https://github.com/hmrc/hmrc-frontend-scaffold.g8) to create its pages.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
