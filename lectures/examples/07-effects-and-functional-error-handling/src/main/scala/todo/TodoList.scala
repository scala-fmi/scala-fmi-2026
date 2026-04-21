package todo

enum Priority:
  case Low, Medium, High

opaque type TodoId = String
object TodoId:
  def apply(id: String): TodoId = id

case class Todo(id: TodoId, description: String, priority: Priority)

case class TodoList(todos: Map[TodoId, Todo], completedTodosCount: Int):
  def addTodo(todo: Todo): TodoList = this.copy(todos = todos + (todo.id -> todo))

  def completeTodo(todoId: TodoId): TodoList =
    TodoList(
      todos - todoId,
      completedTodosCount + todos.get(todoId).map(_ => 1).getOrElse(0)
    )

  def cancelTodo(todoId: TodoId): TodoList = this.copy(todos - todoId)

  def hasTodo(todoId: TodoId): Boolean = todos.contains(todoId)

object TodoList:
  def empty: TodoList = TodoList(Map.empty, 0)
