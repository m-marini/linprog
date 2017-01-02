# LinProg

[ Title ]


## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

    sbt run

Play will start up on the HTTP port at http://localhost:9000/.   You don't need to reploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request. 

### Usage

[ tbd ]

### Developing

To develope you need the following software be installed

  - git
  - sbt
  - eclipse (optional)

To setup the eclipse project run

    sbt compile
    sbt eclipse

To run application

    sbt ~run


### Testing

To run continuous test run

    sbt ~test

To run just once test run

    sbt test
