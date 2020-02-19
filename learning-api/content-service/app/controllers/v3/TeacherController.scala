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

    def notifyBatch()  = Action { implicit request =>
        val body = requestBody()
        val cmd = Seq("curl", "-X", "POST", s"https://onesignal.com/api/v1/notifications", "-H", "Content-Type: application/json", "-H", "authorization: Basic MDY4YjY1YTctNWRjMC00MjkxLWFiMTQtYzczMDg5MzI2Yzhm", "-d", "{\"app_id\":\"6e98f8cf-67fe-4798-93b9-97955e4858fc\",\"filters\":[{\"field\":\"tag\",\"key\":\"" + body.keySet().head +"\",\"relation\":\"=\",\"value\":\""+ body.get(body.keySet().head) +"\"}],\"headings\":{\"en\":\"Class Assessment\"},\"contents\":{\"en\":\"Learn Something New\"},\"data\":{\""+ body.keySet().head +"\":\""+ body.get(body.keySet().head) +"\"},\"large_icon\":\"https://i.ibb.co/x14wLrp/tha.png\"}")
        val result = cmd.!!
        Ok(result).as("application/json")
    }

    def getAssessmentScore(sessionId: String) = Action { implicit request =>
        val cmd = Seq("curl", "-X", "POST", s"http://50.1.0.12:8082/druid/v2", "-H", "Content-Type: application/json", "-H", "Content-Type: application/json", "-d", "{\n  \"queryType\": \"groupBy\",\n  \"dataSource\": \"devcon-events\",\n  \"dimensions\": [\n    \"profileId\",\n    \"profileName\", \"edata_profile_url\"  ],\n  \"aggregations\": [\n    {\n      \"fieldName\": \"edata_duration\",\n      \"fieldNames\": [\n        \"edata_duration\"\n      ],\n      \"type\": \"doubleSum\",\n      \"name\": \"SUM(edata_duration)\"\n    },\n    {\n      \"fieldName\": \"edata_score\",\n      \"fieldNames\": [\n        \"edata_score\"\n      ],\n      \"type\": \"doubleSum\",\n      \"name\": \"SUM(edata_score)\"\n    }\n  ],\n  \"granularity\": \"all\",\n  \"postAggregations\": [],\n  \"intervals\": \"2020-02-15T00:00:00+00:00/2020-02-23T00:00:00+00:00\",\n  \"filter\": {\n    \"type\": \"and\",\n    \"fields\": [\n      {\n        \"type\": \"selector\",\n        \"dimension\": \"sid\",\n        \"value\": \"" + sessionId + "\"\n      },\n      {\n        \"type\": \"selector\",\n        \"dimension\": \"eid\",\n        \"value\": \"DC_ASSESS\"\n      }\n    ]\n  },\n  \"limitSpec\": {\n    \"type\": \"default\",\n    \"limit\": 10000,\n    \"columns\": [\n      {\n        \"dimension\": \"SUM(edata_duration)\",\n        \"direction\": \"descending\"\n      }\n    ]\n  }\n}")
        val result = cmd.!!
        Ok(result).as("application/json")
    }

    def getAttendance(sessionId: String) = Action { implicit request =>
        val cmd = Seq("curl", "-X", "POST", s"http://50.1.0.12:8082/druid/v2", "-H", "Content-Type: application/json", "-H", "Content-Type: application/json", "-d", "{\"queryType\":\"groupBy\",\"dataSource\":\"devcon-events\",\"dimensions\":[\"profileId\",\"profileName\", \"edata_profile_url\"],\"aggregations\":[],\"granularity\":\"all\",\"postAggregations\":[],\"intervals\":\"2020-02-15T00:00:00+00:00/2020-02-23T00:00:00+00:00\",\"filter\":{\"type\":\"and\",\"fields\":[{\"type\":\"selector\",\"dimension\":\"sid\",\"value\":\"" + sessionId + "\"},{\"type\":\"selector\",\"dimension\":\"eid\",\"value\":\"DC_ATTENDANCE\"}]},\"limitSpec\":{\"type\":\"default\",\"limit\":10000,\"columns\":[{\"dimension\":\"profileId\",\"direction\":\"descending\"}]}}")
        val result = cmd.!!
        Ok(result).as("application/json")
    }
}
