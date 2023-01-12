package controllers.v4

import akka.actor.{ActorRef, ActorSystem}
import controllers.BaseController
import org.apache.commons.lang3.StringUtils
import org.sunbird.utils.AssessmentConstants
import play.api.mvc.ControllerComponents
import utils.{ActorNames, ApiId, QuestionOperations}

import java.io.File
import java.nio.file.Paths
import javax.inject.{Inject, Named}
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.io.Source._

class QuestionController @Inject()(@Named(ActorNames.QUESTION_ACTOR) questionActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends BaseController(cc) {

	val objectType = "Question"
	val schemaName: String = "question"
	val version = "1.0"

	def create() = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, AnyRef]]
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.createQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.CREATE_QUESTION, questionActor, questionRequest)
	}

	def read(identifier: String, mode: Option[String], fields: Option[String]) = Action.async { implicit request =>
		val headers = commonHeaders()
		val question = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
		question.putAll(headers)
		question.putAll(Map("identifier" -> identifier, "fields" -> fields.getOrElse(""), "mode" -> mode.getOrElse("read")).asJava)
		val questionRequest = getRequest(question, headers, QuestionOperations.readQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.READ_QUESTION, questionActor, questionRequest)
	}

	def privateRead(identifier: String, mode: Option[String], fields: Option[String]) = Action.async { implicit request =>
		val headers = commonHeaders()
		val question = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
		question.putAll(headers)
		question.putAll(Map("identifier" -> identifier, "fields" -> fields.getOrElse(""), "mode" -> mode.getOrElse("read")).asJava)
		val questionRequest = getRequest(question, headers, QuestionOperations.readPrivateQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.READ_PRIVATE_QUESTION, questionActor, questionRequest)
	}

	def update(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.updateQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier)
		getResult(ApiId.UPDATE_QUESTION, questionActor, questionRequest)
	}

	def review(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.reviewQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier)
		getResult(ApiId.REVIEW_QUESTION, questionActor, questionRequest)
	}

	def publish(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.publishQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier)
		getResult(ApiId.PUBLISH_QUESTION, questionActor, questionRequest)
	}

	def retire(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val question = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.retireQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier)
		getResult(ApiId.RETIRE_QUESTION, questionActor, questionRequest)
	}

	def importQuestion() = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		body.putAll(headers)
		val questionRequest = getRequest(body, headers, QuestionOperations.importQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.IMPORT_QUESTION, questionActor, questionRequest)
	}

	def systemUpdate(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val content = body.getOrDefault(schemaName, new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		content.putAll(headers)
		val questionRequest = getRequest(content, headers, QuestionOperations.systemUpdateQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier);
		getResult(ApiId.SYSTEM_UPDATE_QUESTION, questionActor, questionRequest)
	}

	def list(fields: Option[String]) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("search", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		question.put("fields", fields.getOrElse(""))
		val questionRequest = getRequest(question, headers, QuestionOperations.listQuestions.toString)
		questionRequest.put("identifiers", questionRequest.get("identifier"))
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.LIST_QUESTIONS, questionActor, questionRequest)
	}

	def reject(identifier: String) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault(schemaName, new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.rejectQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		questionRequest.getContext.put("identifier", identifier)
		getResult(ApiId.REJECT_QUESTION, questionActor, questionRequest)
	}

	def copy(identifier: String, mode: Option[String]) = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()
		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
		question.putAll(headers)
		question.putAll(Map("identifier" -> identifier, "mode" -> mode.getOrElse(""), "copyType" -> AssessmentConstants.COPY_TYPE_DEEP).asJava)
		val questionRequest = getRequest(question, headers, QuestionOperations.copyQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.COPY_QUESTION, questionActor, questionRequest)
	}

	def bulkUpload() = Action.async { implicit request =>
		val headers = commonHeaders()
		val body = requestBody()


		val source = scala.io.Source.fromFile("/home/yogeshkumar/UPSMF/sample_questions.csv")
		val data = source.getLines.map(_.split("\t")).toArray
		println(data)
		source.close

		val question = body.getOrDefault("question", new java.util.HashMap()).asInstanceOf[java.util.Map[String, AnyRef]]
		question.putAll(headers)
		val questionRequest = getRequest(question, headers, QuestionOperations.createQuestion.toString)
		setRequestContext(questionRequest, version, objectType, schemaName)
		getResult(ApiId.CREATE_QUESTION, questionActor, questionRequest)
	}

	def parseCSV(file: File) = {
		val defaultQuestion = new java.util.HashMap().asInstanceOf[java.util.Map[String, AnyRef]]

		defaultQuestion.put("code", "question")
		defaultQuestion.put("mimeType", "application/vnd.sunbird.question")
		defaultQuestion.put("objectType", "Question")
		defaultQuestion.put("primaryCategory", "MTF Question")
		defaultQuestion.put("qType", "MTF")
		defaultQuestion.put("name", "Question")

		val lines = fromFile(file).getLines
		lines.map { s =>
			var question = new java.util.HashMap().asInstanceOf[java.util.Map[String, AnyRef]]
			question.putAll(defaultQuestion)
			val cols = s.split(",").map(_.trim)

			val questionText = cols(0)
			val option0 = cols(1)
			val option1 = cols(2)
			val option2 = cols(3)
			val option3 = cols(4)
			val answer = cols(5)
			val function = cols(6)
			val role = cols(7)
			val activity = cols(8)
			val competency = cols(9)
			val level = cols(10)
			val assessmentType = cols(11)

			question.put("body", questionText)
			question.put("editorState", Map (
				"options" -> Array(
					Map(
						"answer" -> StringUtils.equalsIgnoreCase(option0, answer),
						"value" -> Map(
							"body" -> option0,
							"value" -> 0
						)
					),
					Map(
						"answer" -> StringUtils.equalsIgnoreCase(option1, answer),
						"value" -> Map(
							"body" -> option1,
							"value" -> 1
						)
					),
					Map(
						"answer" -> StringUtils.equalsIgnoreCase(option2, answer),
						"value" -> Map(
							"body" -> option2,
							"value" -> 2
						)
					),
					Map(
						"answer" -> StringUtils.equalsIgnoreCase(option3, answer),
						"value" -> Map(
							"body" -> option3,
							"value" -> 3
						)
					)
				)
			)	)

			question
		}.toList
	}
	def upload() = Action(parse.multipartFormData) { request =>
		val questions = request.body
			.file("file")
			.map { filePart =>
				// only get the last part of the filename
				// otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
				val filename = Paths.get(filePart.filename).getFileName
				val fileSize = filePart.fileSize
				val contentType = filePart.contentType
				val absolutePath = filePart.ref.path.toAbsolutePath
				val realPath = filePart.ref.path.toRealPath()

				println("FileName :" + filename)
				println("FileSize :" + fileSize)
				println("ContentType :" + contentType)
				println("AbsolutePath :" + absolutePath)

				parseCSV(absolutePath.toFile).map( question => {

						val headers = commonHeaders(request.headers)
						question.putAll(headers)
						val questionRequest = getRequest(question, headers, QuestionOperations.createQuestion.toString)
						setRequestContext(questionRequest, version, objectType, schemaName)
						getResult(ApiId.CREATE_QUESTION, questionActor, questionRequest)
					}
				)  // List[Future[Result]]
			}
	}


}
