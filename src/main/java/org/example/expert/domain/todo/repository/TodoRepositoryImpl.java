package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<TodoSearchResponse> searchTodos(String title, LocalDateTime start, LocalDateTime end, String nickname, Pageable pageable) {

        QTodo todo = QTodo.todo;
        QUser user = QUser.user;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> results = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class, todo.title, manager.id.countDistinct(), comment.id.count()))
                .from(todo)
                .leftJoin(manager).on(manager.todo.eq(todo))
                .leftJoin(comment).on(comment.todo.eq(todo))
                .leftJoin(manager.user, user)
                .where(title != null ? todo.title.contains(title) : null,
                        start != null && end != null ? todo.createdAt.between(start, end) : null,
                        nickname != null ? manager.user.nickname.contains(nickname) : null)
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        QTodo todo = QTodo.todo; // Q타입 클래스 -> Todo
        QUser user = QUser.user; // Q타입 클래스 -> User

        Todo result = jpaQueryFactory
                .selectFrom(todo) // select * from todos
                .join(todo.user, user) // join users on todo.user_id = users.id
                .fetchJoin() // join -> join fetch 변경 -> N+1 이슈 방지
                .where(todo.id.eq(todoId)) // where todo.id =?
                .fetchOne(); // limit 1 -> 단건 조회


        return Optional.ofNullable(result); // Optional -> null 가능성이 있어서 Optional 로 감싼 후 반환
    }
}
