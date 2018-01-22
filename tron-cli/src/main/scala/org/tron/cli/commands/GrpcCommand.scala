package org.tron.cli.commands

import org.tron.application.Application
import org.tron.grpc.GrpcServer

import scala.io.StdIn

case class GrpcCommand(port: Option[Int] = None) extends Command {

  def execute(app: Application, parameters: Array[String]): Unit = {

    val grpc = app.injector.getInstance(classOf[GrpcServer])

    grpc.start()
    println(s"Starting GRPC server on port ${grpc.apiServer.getPort}. Press ENTER to stop.")

    StdIn.readLine()

    grpc.stop()
    println("GRPC server stopped")
  }
}
