# LinProg

[ Title ]


## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```
sbt run
```

Play will start up on the HTTP port at http://localhost:9000/.   You don't need to reploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request. 

### Usage

[ tbd ]


### Developing

You need to install:

  * [sbt](http://www.scala-sbt.org/)
  * [nodejs](https://nodejs.org/)
  * [npm](https://www.npmjs.com/)
  * [bower](https://bower.io/)
  * [gulp](http://gulpjs.com/)
  * eclipse (optional)

Setup the environment running

    cd ui
    bower install
    npm install
    cd ..
    sbt compile

If you are using eclipse run

    sbt eclipse

To run application run

    sbt ~run

open browser at [http://localhost:9000/index.html](http://localhost:9000/index.html)


To run test

    sbt ~test

    