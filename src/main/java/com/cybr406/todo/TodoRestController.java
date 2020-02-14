package com.cybr406.todo;


import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {

        if (todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(inMemoryTodoRepository.create(todo), HttpStatus.CREATED);
        }
    }


    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTo(@PathVariable Long id) {

        Optional<Todo> list = inMemoryTodoRepository.find(id);

        if (list.isPresent()) {
            Todo test = list.get();
            return new ResponseEntity<>(test, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/todos")
    public ResponseEntity<List> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){

        return new ResponseEntity<>(inMemoryTodoRepository.findAll(page, size), HttpStatus.OK);

    }


    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity<Todo> addTask(@PathVariable(name = "id") Long todoId, @Valid @RequestBody Task task) {

       return new ResponseEntity<>(inMemoryTodoRepository.addTask(todoId,task), HttpStatus.CREATED);

    }


    @DeleteMapping("/todos/{id}")
    public ResponseEntity delete(@PathVariable Long id) {

        try {
            inMemoryTodoRepository.delete(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {


        try {
            inMemoryTodoRepository.deleteTask(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


}
