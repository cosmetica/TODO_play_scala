package models

import play.api.libs.json.Json

object Todo {
  implicit val todoWriter = Json.writes[Todo]
}

case class Todo(todo_id: Long, todo_subject: String,todo_details:String,todo_status:String, todo_is_delete: String)
