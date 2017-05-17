package repositories

import java.sql.Connection
import java.util.concurrent.Executors
import javax.inject.{Inject, Singleton}

import anorm.SqlParser._
import anorm._
import models.Todo
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TodoRepository @Inject()(db: Database) {

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(30))

  val todoParser = long("todo_id") ~ str("todo_subject") ~ str("todo_details") ~ str("todo_status") ~ str("todo_is_delete") map {
    case todo_id ~ todo_subject ~ todo_details ~ todo_status ~ todo_is_delete => Todo(todo_id, todo_subject, todo_details, todo_status,todo_is_delete)
  }

  def withDb[T](body: Connection => T): Future[T] = Future(db.withConnection(body(_)))

  def getAllTodos = withDb { implicit conn =>
    SQL("SELECT * FROM tb_todolist WHERE todo_is_delete = '0' ORDER BY todo_id  ").as(todoParser.*)
  }

  def getTodo(todo_id: Long) = withDb { implicit conn => getSingleTodo(todo_id) }

  def addTodo(todo_subject: String, todo_details: String) = withDb { implicit conn =>

    val result: Option[Long] = SQL("INSERT INTO tb_todolist(todo_subject, todo_details, todo_status, todo_is_delete) values({todo_subject},{todo_details},{todo_status},{todo_is_delete})")
      .on("todo_subject" -> todo_subject,"todo_details"->todo_details, "todo_status" -> "PENDING", "todo_is_delete" -> "0")
      .executeInsert()

    result.flatMap(getSingleTodo)
  }

  def updateTodo(todo_id: Long,
                 todo_subject: Option[String] = None,
                 todo_details:Option[String] = None) =
    withDb { implicit conn =>
      getSingleTodo(todo_id).map { todo =>
        SQL("UPDATE tb_todolist SET todo_subject = {todo_subject},todo_details = {todo_details} WHERE todo_id = {todo_id}")
          .on(
            "todo_id" -> todo_id,
            "todo_subject" -> todo_subject.getOrElse(todo.todo_subject),
            
            "todo_details"-> todo_details.getOrElse(todo.todo_details)
          )
          .executeUpdate()
      } match {
        case Some(x: Int) if x > 0 => getSingleTodo(todo_id)
        case _ => None
      }
    }

  def markToDoDone(todo_id: Long,todo_status: String) =
    withDb { implicit conn =>
      getSingleTodo(todo_id).map { todo =>
        SQL("UPDATE tb_todolist SET todo_status = {todo_status} WHERE todo_id = {todo_id}")
          .on(
            "todo_status" -> todo_status,
            "todo_id" -> todo_id
          )
          .executeUpdate()
      } match {
        case Some(x: Int) if x > 0 => getSingleTodo(todo_id)
        case _ => None
      }
    }

  def removeAllTodos() = withDb { implicit conn =>
    SQL("DELETE FROM tb_todolist").execute()
  }

  def removeTodo(todo_id: Long) = withDb { implicit conn =>
    SQL("UPDATE tb_todolist SET todo_is_delete = '1' WHERE todo_id = {todo_id}").on("todo_id" -> todo_id).executeUpdate()
  }

  private def getSingleTodo(todo_id: Long)(implicit connection: Connection) =
    SQL("SELECT * FROM tb_todolist WHERE todo_id = {todo_id}")
      .on("todo_id" -> todo_id)
      .as(todoParser.singleOpt)
}
