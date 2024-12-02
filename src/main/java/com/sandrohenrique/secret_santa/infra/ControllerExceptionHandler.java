package com.sandrohenrique.secret_santa.infra;

import com.sandrohenrique.secret_santa.dtos.ExceptionDTO;
import com.sandrohenrique.secret_santa.exceptions.EntityNotFoundException;
import com.sandrohenrique.secret_santa.exceptions.GroupAlreadyDrawnException;
import com.sandrohenrique.secret_santa.exceptions.InsufficientFriendsException;
import com.sandrohenrique.secret_santa.exceptions.UserAlreadyInGroupException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleEntityNotFound(EntityNotFoundException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), "404");
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(UserAlreadyInGroupException.class)
    public ResponseEntity<ExceptionDTO> handleUserAlreadyInGroup(UserAlreadyInGroupException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), "400");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(GroupAlreadyDrawnException.class)
    public ResponseEntity<ExceptionDTO> handleGroupAlreadyDrawn(GroupAlreadyDrawnException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), "400");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InsufficientFriendsException.class)
    public ResponseEntity<ExceptionDTO> handleInsufficientFriends(InsufficientFriendsException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), "400");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleGeneralException(Exception exception) {
        ExceptionDTO response = new ExceptionDTO("Erro interno no servidor", "500");
        return ResponseEntity.internalServerError().body(response);
    }
}
