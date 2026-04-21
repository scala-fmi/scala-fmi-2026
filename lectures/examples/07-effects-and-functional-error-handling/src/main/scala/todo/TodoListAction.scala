package todo

enum TodoListAction:
  case AddTodo(todo: Todo)
  case CompleteTodo(todoId: TodoId)
  case CancelTodo(todoId: TodoId)

def applyTodoListAction(todoList: TodoList)(action: TodoListAction): TodoList =
  action match
    case TodoListAction.AddTodo(todo) => todoList.addTodo(todo)
    case TodoListAction.CompleteTodo(todoId) => todoList.completeTodo(todoId)
    case TodoListAction.CancelTodo(todoId) => todoList.cancelTodo(todoId)
