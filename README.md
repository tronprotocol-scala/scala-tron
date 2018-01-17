<h1 align="center">
  scala-tron
  <br>
  <img src="docs/img/scala-tron.png">
  <br>
  <img src="https://travis-ci.org/Rovak/scala-tron.svg?branch=master">
</h1>

# What's TRON?
TRON is a block chain-based decentralized smart protocol and an application development platform. It allows each user to freely publish, store and own contents and data, and in the decentralized autonomous form, decides an incentive mechanism and enables application developers and content creators through digital asset distribution, circulation and transaction, thus forming a decentralized content entertainment ecosystem.

TRON is a product of Web 4.0 and the decentralized internet of next generation.

# Quick Start

This project requires SBT to build. 
Follow the instructions [installing SBT](http://www.scala-sbt.org/1.0/docs/Setup.html) to install SBT

```
git clone http://github.com/rovak/scala-tron
cd scala-tron
sbt "project cli" run
```

# Usage

The application has a web and cli interface


## Command Line

To get started first run

`sbt "project cli" run`

After that the following commands are available:

### `account`

Show account key

### `balance`

Show account balance

## Web API

Start the server using `sbt "project api" run`

The API will then be available on http://localhost:9000