# api-aggregator-spring

Things to consider: 
- current design is for single node, usage of a centralized message broker with pubsub (redis or GCP)  
- Testing - no unit tests, integration tests but only end-to-end tests that serve as proof of concept.
- -- test percentiles ..etc to achieve what is expected (max 10 seconds response..etc)


todos:
- handle 503 
- 