package org.sunbird.managers

import org.sunbird.common.dto.{Request, Response, ResponseHandler}

import scala.concurrent.{ExecutionContext, Future}

object SessionManager {

    val schemaName: String = "collection"
    val schemaVersion: String = "1.0"

    def addSession(request: Request)(implicit ec: ExecutionContext): Future[Response] =  {
        Future(ResponseHandler.OK())
    }

    def removeSession(request: Request)(implicit ec: ExecutionContext): Future[Response] = {
        Future(ResponseHandler.OK())
    }

    def updateSession(request: Request)(implicit ec: ExecutionContext): Future[Response] = {
        Future(ResponseHandler.OK())
    }

}
