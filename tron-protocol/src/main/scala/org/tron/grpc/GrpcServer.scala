package org.tron.grpc

import io.grpc.ServerBuilder
import org.tron.api.api.WalletGrpc
import org.tron.application.Service
import org.tron.wallet.WalletApi

import scala.concurrent.ExecutionContext.Implicits.global

class GrpcServer(port: Int) extends Service {

  val apiServer = ServerBuilder.forPort(port)
    // TODO enable service
//    .addService(WalletGrpc.bindService(new WalletApi(), global))
    .build()

  def start() = apiServer.start()

  def stop() = apiServer.shutdown()
}
