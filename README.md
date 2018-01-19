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
  <a href="#how-to-use">How To Use</a> •
  <a href="#cluster">Cluster</a> •
  <a href="#wiki">Wiki</a> •
  <a href="#contact">Contact</a>
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

### `cluster`
 
Start cluster as leader

### `cluster --join <address>`

Join cluster as client

## Cluster

Cluster is based on Akka. To start a cluster follow the following steps:

* Start tron-cli `sbt "project cli" run`
* Run `cluster` which starts the node as leader
* The console will show the following logs  
```
[INFO] [01/18/2018 22:59:28.399] [main] [akka.remote.Remoting] Starting remoting
[INFO] [01/18/2018 22:59:28.553] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://TronCluster@127.0.0.1:41795]
```
* Copy the address (`127.0.0.1:41795`)
* Start a second instance which will join the cluster. 
  The second client has to have a different database directory to prevent conflicts.
* Run `sbt -J-Ddatabase.directory=tron-data-client1 "project cli" run`.  
  `-J-Ddatabase.directory=tron-data-client1` specifies the database directory
* Run `cluster --join <address>` (example: `cluster --join 127.0.0.1:41795`)
* The client will now be joining the cluster

## Web API

Start the server using `sbt "project api" run`

The API will then be available on http://localhost:9000


### `/wallet/<public key>`

Shows the balance of the wallet for the given public key

__Response__

```json
{
  "address": "a9c030dfbfb83f6c9454b5e1da5cecdb8737d4af",
  "balance": 10
}
```

## Wiki

* [Introduction](https://github.com/tronprotocol/wiki/blob/master/Home.md)
* [ReadTheDocs](http://tron-wiki.readthedocs.io/introduction.html)

## Contact

* [Gitter](https://gitter.im/Rovak/scala-tron)
* [Telegram](https://t.me/joinchat/CP8XKBIjEc0RqzJdl61OlQ)

