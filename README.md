# Getting Started with the URL Shortener project

## Overall structure

The structure of this project is heavily influenced by 
[the clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html):

* A `core` module where we define the domain entities and the functionalities
  (also known as uses cases, business rules, etc.). They do not know that this application 
  has a web interface or that data is stored in relational databases.
* A `repositories` module that knows how to store domain entities in a relational database.
* A `delivery` module that knows how to expose in the Web the functionalities. 
* An `app` module that contains the main, the configuration (i.e. it links `core`, `delivery` and `repositories`), 
  and the static assets (i.e. html files, JavaScript files, etc. )

Usually, if you plan to add a new feature, usually:

* You will add a new use case to the `core` module.
* If required, you will modify the persistence model in the `repositories` module.
* You will implement a web-oriented solution to expose to clients in the `delivery` module.

Sometimes, your feature will not be as simple, and it would require:

* To connect a third party (e.g. an external server). 
  In this case you will add a new module named `gateway` responsible for such task.
* An additional application.  
  In this case you can create a new application module (e.g. `app2`) with the appropriate configuration to run this second server.

Features that require the connection to a third party or having more than a single app will be rewarded. 

## RabbitMQ required

Need install RabbitMQ and start it in your computer.

If you have not installed it yet:
* Follow this [guide](https://www.rabbitmq.com/install-windows.html) if you use windows.
* Follow this [guide](https://www.rabbitmq.com/install-debian.html) if you use debian.

Once Rabbit is installed, it will run automatically with the default settings, but you can enable management server with this command: 

```shell
rabbitmq-plugins enable rabbitmq_management
```
You need to restart rabbit or restart the computer to work.

For the management dashboard, open browser at http://localhost:15672/ and login.

* user : guest
* pass : guest

## Run

The application can be run as follows:

```shell
./gradlew :app:bootRun
```

Now you have a shortener service running at port 8080. You can test that it works as follows:

```shell
$ curl -v -d "url=http://www.unizar.es/" http://localhost:8080/api/link
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> POST /api/link HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.71.1
> Accept: */*
> Content-Length: 25
> Content-Type: application/x-www-form-urlencoded
> 
* upload completely sent off: 25 out of 25 bytes
* Mark bundle as not supporting multiuse
< HTTP/1.1 201 
< Location: http://localhost:8080/tiny-6bb9db44
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Tue, 28 Sep 2021 17:06:01 GMT
< 
* Connection #0 to host localhost left intact
{"url":"http://localhost:8080/tiny-6bb9db44","properties":{"safe":true}}%   
```

And now, we can navigate to the shortened URL.

```shell
$ curl -v http://localhost:8080/tiny-6bb9db44
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> GET /tiny-6bb9db44 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.71.1
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 307 
< Location: http://www.unizar.es/
< Content-Length: 0
< Date: Tue, 28 Sep 2021 17:07:34 GMT
< 
* Connection #0 to host localhost left intact
```

## Build and Run 

The uberjar can be built and then run with:

```shell
./gradlew build
java -jar app/build/libs/app.jar
```
## Functionalities

The project offers a minimum set of functionalities:

* **Create a short URL**. 
  See in `core` the use case `CreateShortUrlUseCase` and in `delivery` the REST controller `UrlShortenerController`.

* **Redirect to a URL**.
  See in `core` the use case `RedirectUseCase` and in `delivery` the REST controller `UrlShortenerController`.

* **Log redirects**.
  See in `core` the use case `LogClickUseCase` and in `delivery` the REST controller `UrlShortenerController`.

* **Generate a QR**
  See in `core` the use case `QRGeneratorUseCase` and in `delivery` the REST controller `QRControler`

* **Validate URL security** 
  See in `delivery` the service `SecurityServiceImpl`

* **Validate URL Reacheability**
  See in `delivery` the service `ReachabilityServiceImpl`

* **Expired time to short ULS**
  See in `core` the use case `ExpiredUseCaseImpl`


The objects in the domain are:

* `ShortUrl`: the minimum information about a short url
* `Redirection`:  the remote URI and the redirection mode
* `ShortUrlProperties`: a handy way to extend data about a short url
* `Click`: the minimum data captured when a redirection is logged
* `ClickProperties`: a handy way to extend data about a click
* `QRCode`: the minimum information about QR
* `QRFormat`: the format used to generate QR

## Delivery

The above functionality is available through a simple API:

* `POST /api/link` which creates a short URL from data send by a form, It suport 3 parameters:
  * `url` : url that you want shorter
  * `days` : how many days ulr is active
  * `qr` : true if you want generate qr, false if you dont want it 
* `GET /tiny-{id}` where `id` identifies the short url, deals with redirects, and logs use (i.e. clicks).
* `GET /getQR/{id}` where `id` identifies the qr, deals with qr. The `id` of the qr is the same as that of the corresponding shortened page


In addition, `GET /` returns the landing page of the system. 



## Repositories

All the data is stored in a relational database. 
There are only two tables.

* **shorturl** that represents short url and encodes in each row `ShortUrl` related data 
* **click** that represents clicks and encodes in each row `Click` related data
* **QRCode** that represents  QR and encodes in each row `QRCode` related data

## RabbitMQ Queues

* `VALIDITY_queue` : get url and check that it is safe and reacheability

* `QRCODE_queue` : get QR format and url and generate a new QR code


## Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.5/gradle-plugin/reference/html/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [RabbitMQ](https://www.rabbitmq.com/documentation.html)
## Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a Messages Queue with Rabbit](https://spring.io/guides/gs/messaging-rabbitmq/)

