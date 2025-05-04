package com.mts.backend.api.common;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateException.class)
    public ErrorResponse handleDuplicateException(DuplicateException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

        return ErrorResponse.builder(e, detail)
                .title("Trùng lặp tài nguyên")
                .build();
        
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());

        return ErrorResponse.builder(e, detail)
                .title("Không tìm thấy tài nguyên")
                .build();
    }
    
    @ExceptionHandler(DomainException.class)
    public ErrorResponse handleDomainException(DomainException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

        return ErrorResponse.builder(e, detail)
                .title("Dữ liệu đầu vào không hợp lệ")
                .build();
    }
    
    @ExceptionHandler(DomainBusinessLogicException.class)
    public ErrorResponse handleDomainBusinessLogicException(DomainBusinessLogicException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

        return ErrorResponse.builder(e, detail)
                .title("Không đáp ứng yêu cầu nghiệp vụ")
                .build();   
    }
    
    @ExceptionHandler(NullPointerException.class)
    public void handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException: ", e);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

        return ErrorResponse.builder(e, detail)
                .title("Lỗi ràng buộc dữ liệu")
                .build();
    }
    
    @ExceptionHandler(JpaSystemException.class)
    public ErrorResponse handleJpaSystemException(JpaSystemException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMostSpecificCause().getMessage());
        
        return ErrorResponse.builder(e, detail)
                .title("Lỗi truy vấn dữ liệu")
                .build();
    }
    
    @ExceptionHandler(TransactionSystemException.class)
    public ErrorResponse handleTransactionalException(TransactionSystemException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        
        return ErrorResponse.builder(e, detail)
                .title("Giao tác không thành công")
                .build();
    }
    
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        
        return ErrorResponse.builder(e, detail)
                .title("Lỗi xác thực dữ liệu")
                .build();
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Không tìm thấy tài khoản");
        
        return ErrorResponse.builder(e, detail)
                .title(e.getMessage())
                .build();
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không chính xác");
        return ErrorResponse.builder(e, detail)
                .title(e.getMessage())
                .build();
    }
    

}
