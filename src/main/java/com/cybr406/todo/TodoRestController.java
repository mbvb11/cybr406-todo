package com.cybr406.todo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.lang.Integer.min;

@RestController
public class TodoRestController {

    @Autowired
    InMemoryTodoRepository inMemoryTodoRepository;
    @Autowired
    TodoJpaRepository todoJpaRepository;
    @Autowired
    TaskJpaRepository taskJpaRepository;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {

        if (todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(todoJpaRepository.save(todo), HttpStatus.CREATED);
        }
    }


    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTo(@PathVariable Long id) {

        Optional<Todo> list = todoJpaRepository.findById(id);

        if (list.isPresent()) {
            Todo test = list.get();
            return new ResponseEntity<>(test, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/todos")
    public Page<Todo> findAll(Pageable page){

        return (Page<Todo>) todoJpaRepository.findAll(page);

    }


    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity<Todo> addTask(@PathVariable(name = "id") Long todoId, @Valid @RequestBody Task task) {
        return todoJpaRepository.findById(todoId)
                .map(todo -> {
                    todo.getTasks().add(task);
                    task.setTodo(todo);
                    taskJpaRepository.save(task);
                    return new ResponseEntity<>(todo, HttpStatus.CREATED);
                }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("/todos/{id}")
    public ResponseEntity delete(@PathVariable Long id) {

        if (todoJpaRepository.existsById(id)){
            todoJpaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {

        if (taskJpaRepository.existsById(id)){
            taskJpaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


}
