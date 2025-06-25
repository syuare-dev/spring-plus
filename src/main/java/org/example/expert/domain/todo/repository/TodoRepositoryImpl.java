package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;

import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

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
