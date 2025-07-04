package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    // 조건이 없을 경우 - 전체 조회 (최신 수정일 순 정렬)
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);


    // QueryDSL 로 대체
//    @Query("SELECT t FROM Todo t LEFT JOIN t.user WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    // weather, start, end 조건이 있을 경우
    @Query("SELECT t FROM Todo t JOIN FETCH t.user WHERE t.weather = :weather AND t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByWeatherAndModifiedAtBetween(@Param("weather") String weather,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 Pageable pageable);

    // weather 조건만 있을 경우
    @Query("SELECT t FROM Todo t JOIN FETCH t.user WHERE t.weather = : weatehr")
    Page<Todo> findByWeather(@Param("weather") String weather, Pageable pageable);

    // 수정일 (시작 - 종료) 조건만 있을 경우
    @Query("SELECT t FROM Todo t JOIN FETCH t.user WHERE t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findByModifiedAtBetween(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       Pageable pageable);
}
