# decoder-ring

Small application to test using [Clojure Spec](https://clojure.org/guides/spec) and [Test Check](https://clojure.org/guides/test_check_beginner).

The Decoder Ring uses a starting point that defaults to "A", and the a list of instruction that move to the left or right.

To print `HELLO WORLD`
`lein run "R7;L3;R7;R0;R3;R12;L4;L8;R3;L6;L8"`

## Docker

### Building
`docker build -t decoder-ring . `

### Running
`docker run -it decoder-ring "R7;L3;R7;R0;R3;R12;L4;L8;R3;L6;L8"`