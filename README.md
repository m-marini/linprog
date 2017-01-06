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

    
https://accounts.google.com/o/oauth2/v2/auth?scope=email%20profile&state=%2Fprofile&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Foauthcallback&response_type=token&client_id=356010545588-60gdq4m1us25ikg3cudppuaf0qioic1o.apps.googleusercontent.com

http://localhost:9000/oauthcallback#state=/profile&access_token=ya29.Ci_JA2TqmMlTcYeebcQqq3O-t8pgl5fxRmwUunFN0bzcM9vT__Q_lFVv7cYyY6MQwg&token_type=Bearer&expires_in=3600