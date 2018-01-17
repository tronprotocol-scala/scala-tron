<h1 align="center">
  <img src="docs/img/scala-tron.png">
  <br>
  scala-tron
  <br>
</h1>

<h4 align="center">
    Scala implementation of the <a href="https://github.com/tronprotocol/wiki/blob/master/Home.md">Tron Protocol</a>
</h4>

<p align="center">
  <a href="https://travis-ci.org/Rovak/scala-tron" target="_blank">
    <img src="https://travis-ci.org/Rovak/scala-tron.svg?branch=master">
  </a>
</p>

<p align="center">
  <a href="#quick-start">Quick Start</a> •
  <a href="#how-to-use">How To Use</a>
</p>


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


# How To Use

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


### `/wallet/<public key>`

Shows the balance of the wallet for the given public key

__Response__

```json
{
  balance: 10
}
```

