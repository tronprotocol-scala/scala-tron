package org.tron.grpc

import io.grpc.ServerBuilder
import org.tron.api.api.WalletGrpc
import org.tron.application.Service
import scala.concurrent.ExecutionContext.Implicits.global

class GrpcServer(port: Int) extends Service {

  val apiServer = ServerBuilder.forPort(port)
    .addService(WalletGrpc.bindService(new WalletRpc(), global))
    .build()

  def start() = apiServer.start()

  def stop() = apiServer.shutdown()
}
