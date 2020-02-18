package controllers.v3

import java.util

import akka.actor.{ActorRef, ActorSystem}
import controllers.BaseController
import javax.inject.{Inject, Named}
import play.api.mvc.ControllerComponents
import utils.ActorNames

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.sys.process._

class TeacherController @Inject()(@Named(ActorNames.SCHOOL_ACTOR) schoolActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends BaseController(cc) {

    val objectType = "Teacher"
    val schemaName: String = "teacher"
    val version = "1.0"


    def create() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val content = body.getOrElse("teacher", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        content.putAll(headers)
        val contentRequest = getRequest(content, headers, "create")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult("api.teacher.create", schoolActor, contentRequest)
    }


    def read(identifier: String) = Action.async { implicit request =>
        val headers = commonHeaders()
        val content = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
        content.putAll(headers)
        content.putAll(Map("identifier" -> identifier, "mode" -> "read", "fields" -> new util.ArrayList[String]()))
        val readRequest = getRequest(content, headers, "read")
        setRequestContext(readRequest, version, objectType, schemaName)
        getResult("api.teacher.read", schoolActor, readRequest)
    }

    def addUserProfile() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val content = body.getOrElse("teacher", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        content.putAll(headers)
        val contentRequest = getRequest(content, headers, "addUserData")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult("api.teacher.profile", schoolActor, contentRequest)
    }

    def getUserProfile(identifier: String) = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val content = body.getOrElse("teacher", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]]
        content.putAll(headers)
        content.putAll(Map("userId" -> identifier))
        val contentRequest = getRequest(content, headers, "getUserData")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult("api.teacher.profile", schoolActor, contentRequest)
    }

    def notifyBatch()  = Action {
        val cmd = Seq("curl", "-X", "POST", s"https://onesignal.com/api/v1/notifications", "-H", "Content-Type: application/json", "-H", "authorization: Basic MDY4YjY1YTctNWRjMC00MjkxLWFiMTQtYzczMDg5MzI2Yzhm", "-d", "{\"app_id\":\"6e98f8cf-67fe-4798-93b9-97955e4858fc\",\"filters\":[{\"field\":\"tag\",\"key\":\"teachers\",\"relation\":\"=\",\"value\":\"true\"}],\"headings\":{\"en\":\"My Teacher Image notification title\"},\"contents\":{\"en\":\"My Teacher notification content\"},\"data\":{\"task\":\"Sent through api Teacher\"},\"big_picture\":\"https://img.onesignal.com/n/37326fcc-2baa-45da-891c-ca9454a64957.png\",\"large_icon\":\"https://img.onesignal.com/n/b5cadcf0-1297-4489-b865-545b421d8c5d.png\"}")
        val result = cmd.!!
        Ok(result).as("application/json")
    }
}
