package views

import models.Todo
import play.api.libs.json.Json
import play.api.mvc.Request

object TodoView {
  implicit val todoWriter = Json.writes[TodoView]
  implicit val todoReader = Json.reads[TodoView]

  def fromModel(todo: Todo)(implicit request: Request[_]): TodoView = new TodoView(
  	todo_id = todo.todo_id,
    todo_subject = todo.todo_subject,
    todo_details = todo.todo_details,
    todo_status = todo.todo_status,
    todo_is_delete = todo.todo_is_delete,
    url = s"${if (request.secure) "https" else "http"}://${request.host}/todo/${todo.todo_id.toString}"
  )
}

//case class TodoView(title: String, order: Int, completed: Boolean, url: String)
case class TodoView(todo_id:Long,todo_subject: String,todo_details:String,todo_status:String, todo_is_delete: String,url: String)