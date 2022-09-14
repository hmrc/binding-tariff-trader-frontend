
# binding-tariff-trader-frontend

The public frontend for the Manage your Advance Tariff Rulings service, used to apply for Advance Tariff Rulings (ATaRs).

### Running

##### To run this Service you will need:

1) [Service Manager](https://github.com/hmrc/service-manager) installed
2) [SBT](https://www.scala-sbt.org) Version `>=1.x` installed
3) [MongoDB](https://www.mongodb.com/) version `>=3.6` installed and running on port 27017
4) [Localstack](https://github.com/localstack/localstack) installed and running on port 4572
5) Create an S3 bucket in localstack by using `awslocal s3 mb s3://digital-tariffs-local` within the localstack container

The easiest way to run MongoDB and Localstack for local development is to use [Docker](https://docs.docker.com/get-docker/).

##### To run MongoDB

```
> docker run --restart unless-stopped -d -p 27017-27019:27017-27019 --name mongodb mongo:3.6.13
```

##### To run Localstack and create the S3 bucket

```
> docker run -d --restart unless-stopped --name localstack -e SERVICES=s3 -p4572:4566 -p8080:8080 localstack/localstack
> docker exec -it localstack bash
> awslocal s3 mb s3://digital-tariffs-local
> exit
```

#### Starting the application:
 
1) Launch dependencies using `sm --start DIGITAL_TARIFF_DEPS -r`
2) Start the backend service [binding-tariff-classification](https://github.com/hmrc/binding-tariff-classification) using `sm --start BINDING_TARIFF_CLASSIFICATION -r`
3) Start the filestore service [binding-tariff-filestore](https://github.com/hmrc/binding-tariff-filestore) using `sm --start BINDING_TARIFF_FILESTORE -r`
5) On Mac OS you must start an older version of the [pdf-generator-service](https://github.com/hmrc/pdf-generator-service):
```
sm --restart PDF_GENERATOR_SERVICE -r 1.20.0
```

Use `sbt run` to boot the app or run it with Service Manager using `sm --start BINDING_TARIFF_TRADER_FRONTEND -r`.

This application runs on port 9582.

Open `http://localhost:9582/advance-tariff-application`.

You can also run the `DIGITAL_TARIFFS` profile using `sm --start DIGITAL_TARIFFS -r` and then stop the Service Manager instance of this service using `sm --stop BINDING_TARIFF_TRADER_FRONTEND` before running with sbt.

### Authentication

The service uses the HMRC [auth-client](https://github.com/hmrc/auth-client) for authentication with Government Gateway as the authentication provider. In non production environments you will be redirected to the auth-login-stub. You can log in using the following enrolment information:

Enrolment Key: `HMRC-ATAR-ORG`

Identifier Name: `EORINumber`

Identifier Value: `<any string>`

### PDF Generator Service

This service requires the installation of some dependencies before it can be run using Service Manager. See [pdf-generator-service](https://github.com/hmrc/pdf-generator-service).

Running the pdf-generator-service locally on Mac OSX (currently) requires running an older version.  

Run `sm --start PDF_GENERATOR_SERVICE -r 1.20.0`

### Testing

Run `./run_all_tests.sh`. This also runs Scalastyle and does coverage testing.

or `sbt test` to run the tests only.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
