package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import repositories.TodoRepository
import views.TodoView

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject()(repository: TodoRepository) extends Controller {

  def index = Action.async { implicit request =>
    repository.getAllTodos map { todos =>
      //Ok(Json.toJson(todos.map(TodoView.fromModel)))
      Ok(views.html.index())
    }
  }

  def viewAll = Action.async { implicit request =>
    repository.getAllTodos map { todos =>
      Ok(Json.toJson(todos.map(TodoView.fromModel)))
      //Ok(views.html.index(Json.toJson(todos.map(TodoView.fromModel))))
    }
  }

  def get(todo_id: Long) = Action.async { implicit request =>
    repository.getTodo(todo_id) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }
  //case class Todo(todo_id: Long, todo_subject: String,todo_details:String,todo_status:String, todo_is_delete: String)
  def add = Action.async(BodyParsers.parse.json) { implicit request =>
    //val todo_id = (request.body \ "todo_id").as[String]
    val todo_subject = (request.body \ "todo_subject").as[String]
    val todo_details = (request.body \ "todo_details").as[String]
    //val todo_status = (request.body \ "todo_status").as[String]
    //val todo_is_delete = (request.body \ "todo_is_delete").as[String]

    repository.addTodo(todo_subject, todo_details) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => InternalServerError
    }
  }

  def removeAll() = Action.async {
    repository.removeAllTodos() map { _ => Ok("") }
  }

  def remove(todo_id: Long) = Action.async {
    repository.removeTodo(todo_id) map { _ => Ok("") }
  }

  def update(todo_id: Long) = Action.async(BodyParsers.parse.tolerantJson) { implicit request =>

    val todo_subject = (request.body \ "todo_subject").asOpt[String]
    val todo_details = (request.body \ "todo_details").asOpt[String]

    repository.updateTodo(todo_id,todo_subject, todo_details) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }

  def markDone = Action.async(BodyParsers.parse.json) { implicit request =>

    val todo_id = (request.body \ "todo_id").as[Long]
    val todo_status = (request.body \ "todo_status").as[String]
    repository.markToDoDone(todo_id,todo_status) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }

}
